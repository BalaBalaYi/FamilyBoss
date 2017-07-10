package com.cty.family.socket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 离线消息处理类
 * 
 * 1.存储离线消息
 * 2.负责离线消息的发送
 * 
 * @author 陈天熠
 *
 */
public class OfflineMessageHandler {
	
	private static Logger logger = LoggerFactory.getLogger(OfflineMessageHandler.class);
	
	// 单个用户的最大离线消息数
	private static final int MAX_OFFLINE_MSG = 1000;

	// concurrent包的线程安全Map，用来存放每个客户端对应的离线消息
	private static ConcurrentHashMap<String, LinkedList<String>> offlineMsgMap = new ConcurrentHashMap<String, LinkedList<String>>();

	// 默认的get/set方法
	public static ConcurrentHashMap<String, LinkedList<String>> getOfflineMsgMap() {
		return offlineMsgMap;
	}
	public static void setOfflineMsgMap(ConcurrentHashMap<String, LinkedList<String>> offlineMsgMap) {
		OfflineMessageHandler.offlineMsgMap = offlineMsgMap;
	}
	
	/**
	 * 向消息存储map体添加消息
	 * @param key 即userId
	 * @param offlineMsg
	 */
	public static void saveOfflineMsg(String key, String offlineMsg) {
		
		LinkedList<String> offlineMsgList = offlineMsgMap.get(key);
		
		if(null == offlineMsgList) { // 如果没有则创建
			offlineMsgList = new LinkedList<String>();
			offlineMsgList.add(offlineMsg);
			offlineMsgMap.put(key, offlineMsgList);
		} else {
			if(offlineMsgList.size() >= MAX_OFFLINE_MSG) { // 如果超过 MAX_OFFLINE_MSG 条，则删除头一条再在尾部添加
				offlineMsgList.removeFirst();
			}
			offlineMsgList.add(offlineMsg);
		}
	}
	
	/**
	 * 获取某个用户的离线消息，并通过socket发送
	 * @param key 即userId
	 */
	public static void sendOfflineMsg(String userId) {
		
		LinkedList<String> offlineMsgList = offlineMsgMap.get(userId);
		if(null != offlineMsgList && offlineMsgList.size() > 0) {
			logger.info("用户id：" + userId + "的离线消息有" + offlineMsgList.size() + "条");
			try {
				// 获取对应用户的socket对象
				MyWebSocket reciever = MyWebSocket.getWebSocket(userId);
			
				while(offlineMsgList.size() > 0) {
					reciever.sendMessageSync(offlineMsgList.poll());
				}
			} catch (IOException e) {
				logger.error("FamiliBoss-IM 发送离线消息异常！", e);
			}
		}
	}
	
}
