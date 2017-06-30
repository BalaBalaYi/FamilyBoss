package com.cty.family.socket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.cty.family.config.ApplicationContextRegister;
import com.cty.family.entity.UserEntity;
import com.cty.family.service.UserService;

import net.sf.json.JSONObject;

@ServerEndpoint(value = "/websocket/{userId}")
@Component
public class MyWebSocket {
	
	private Logger logger = LoggerFactory.getLogger(MyWebSocket.class);
	
	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象
	private static ConcurrentHashMap<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<String, MyWebSocket>();

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;
	
	/**
	 * 连接建立成功调用的方法
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam("userId")String userId, Session session) {
		this.session = session;
		
		// 判断当前用户id是否已有会话连接，如果有则删除之前的会话连接
		if(null != webSocketMap.get(userId)){
			logger.info("存在该用户的历史会话连接，放弃该连接的维护！");
			webSocketMap.remove(userId);
		}
		// 添加socket
		webSocketMap.put(userId, this); // 加入map中
		logger.info("有新连接加入！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("userId")String userId) {
		webSocketMap.remove(userId, this); // 从map中删除
//		subOnlineCount(); // 在线数减1
		logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @throws IOException 
	 * 
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		
		logger.info("来自客户端的消息:" + message);
		
		if (message != null && !"".equals(message)) {
			JSONObject jsonObject = JSONObject.fromObject(message);
			
			String msgType = (String) jsonObject.get("type");
			if("sys".equals(msgType)) {
				logger.info("系统消息:" + message); 
				
				// 首次连接，处理该用户的离线消息
				String key = (String) jsonObject.get("key");
				OfflineMessageHandler.sendOfflineMsg(key);
				return;
				
			} else if ("chat".equals(msgType)) {
				JSONObject data = null;
				JSONObject src = null;
				JSONObject dist = null;
				try {
					data = (JSONObject) jsonObject.get("data");
					src = (JSONObject) data.get("mine");
					dist = (JSONObject) data.get("to");
					
					// 详细参数校验
					boolean verifyResult = messageVerify(src, dist);
					if(!verifyResult){
						logger.error("消息发送格式校验失败！");
						return;
					}
					
				} catch (Exception e) {
					logger.error("消息发送格式校验失败！", e);
					return;
				}
				
				if(null != data && null != src && null != dist){
					boolean sendResult = false;
					try {
						sendResult = sendMessageHandler(src, dist);
						if(!sendResult){
							logger.error("FamiliBoss-IM 存在失败消息！");
						}
					} catch (IOException e) {
						logger.error("FamiliBoss-IM 发送消息异常！", e);
					}
				}
			} else {
				logger.warn("出现不支持的消息类型！type:" + msgType);
				return;
			}
			
		} else {
			logger.warn("消息为空！");
			return;
		}
	}

	/**
	 * 错误处理
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {

		if(null == error.getCause()){
			logger.error("MyWebSocket 发生错误!");
			error.printStackTrace();
			return;
		}
		String reason = error.getCause().getMessage();
		if("你的主机中的软件中止了一个已建立的连接。".equals(reason)){
			// 忽略
			logger.warn("发生客户端二次连接，socket关闭连接，并重新建立连接");
		} else {
			logger.error("MyWebSocket 发生错误!");
			error.printStackTrace();
		}
	}

	/**
	 * 消息处理
	 * @param src
	 * @param dist
	 * @return
	 * @throws IOException 
	 */
	public boolean sendMessageHandler(JSONObject src, JSONObject dist) throws IOException {
		
		String distId = (String) dist.get("id"); // 接收方id
		String type = (String) dist.get("type"); // 聊天窗口类型
		
		// 返回消息固话参数
		String id = (String) src.get("id"); // 发送方id
		String username = (String) src.get("username"); // 发送方name
		String content = (String) src.get("content"); // 消息
		String avatar = (String) src.get("avatar"); // 消息来源用户头像
		JSONObject returnMsgJson = new JSONObject();
		returnMsgJson.put("id", id);
		returnMsgJson.put("username", username);
		returnMsgJson.put("content", content);
		returnMsgJson.put("avatar", avatar);
		returnMsgJson.put("type", type);
		returnMsgJson.put("timestamp", System.currentTimeMillis());
		
		if("friend".equals(type)){ // 单人消息发送
			// 获取接收方会话
			MyWebSocket reciever = webSocketMap.get(distId);
			
			// 返回信息参数加工
			boolean mine = false; // 是否为我发送的消息，如果为true，则会显示在右方
			if(distId.equals(id)){
				mine = true;
			}
			returnMsgJson.put("mine", mine);
			
			// 发送信息
			if(null != reciever){
				reciever.sendMessage(returnMsgJson.toString());
			} else { // 添加离线消息
				OfflineMessageHandler.saveOfflineMsg(distId, returnMsgJson.toString());
			}
			
		} else if ("group".equals(type)) { // 群组消息发送
			
			// 获取当前web应用的上下文环境
			ApplicationContext ctx = ApplicationContextRegister.getApplicationContext();
			UserService userService = ctx.getBean(UserService.class);
			
			// 根据接收组id查询组内所有用户id，并依次发送信息
			Integer groupId = Integer.parseInt(distId);
			List<UserEntity> groupUserList = null;
			if(groupId == 0){ // 如果是默认群组，则获取全部用户
				groupUserList = userService.getAllUsersInfo();
			} else {
				groupUserList = userService.getAllUsersIdAndNameByGroupId(groupId);
			}
			
			for(UserEntity groupUser : groupUserList){
				// 获取接收方会话
				MyWebSocket reciever = webSocketMap.get(groupUser.getId().toString());
				// 返回信息参数加工
				returnMsgJson.put("id", distId); // 设定为群组id（某人通过群组发送信息，则信息源认为是群组）
				returnMsgJson.put("mine", false);
				if(groupUser.getId().toString().equals(id)){ // 群组内的自己发言无需再次发送给自己
					continue;
				}
				// 发送信息
				if(null != reciever){
					reciever.sendMessage(returnMsgJson.toString());
				} else { // 添加离线消息
					OfflineMessageHandler.saveOfflineMsg(groupUser.getId().toString(), returnMsgJson.toString());
				}
			}
		} else {
			logger.warn("出现不支持的聊天窗口类型！type:" + type);
			return false;
		}
		return true;
	}
	
	/**
	 * 发送异步消息
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		this.session.getAsyncRemote().sendText(message);
	}
	
	/**
	 * 发送同步消息
	 * @param message
	 * @throws IOException
	 */
	public void sendMessageSync(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}
	
	/**
	 * 消息参数校验
	 * @param src
	 * @param dist
	 * @return
	 */
	public boolean messageVerify(JSONObject src, JSONObject dist){
		
		// 必要参数非空判断
		if(StringUtils.isEmpty((String) src.get("id")) || StringUtils.isEmpty((String) src.get("username")) || StringUtils.isEmpty((String) src.get("avatar")) 
				|| StringUtils.isEmpty((String) dist.get("id")) || StringUtils.isEmpty((String) dist.get("type"))){
			return false;
		}
		return true;
	}
	
	/**
	 * 统计在线人数
	 * @return
	 */
	public static synchronized int getOnlineCount() {
		return webSocketMap.size();
	}
	
	/**
	 * 增加在线人数
	 */
	public static synchronized void addOnlineCount() {
		MyWebSocket.onlineCount++;
	}
	
	/**
	 * 减少在线人数
	 */
	public static synchronized void subOnlineCount() {
		MyWebSocket.onlineCount--;
	}
	
	/**
	 * 获取对应用户的socket对象
	 * @param key
	 */
	public static synchronized MyWebSocket getWebSocket(String key) {
		return webSocketMap.get(key);
	}
	
}
