package com.cty.family.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Service;

/**
 * 系统业务类
 * @author 陈天熠
 *
 */
@Service
public class SystemService {
	
	private Logger logger = LoggerFactory.getLogger(SystemService.class);
	
	private static final String[] OS_PROP_LIST = {"os.name|操作系统名称", "os.arch|操作系统构架", "os.version|操作系统版本" , "java.version|Java 运行时环境版本"
			, "java.vendor|Java 运行时环境供应商", "java.vendor.url|Java 供应商的 URL", "java.home|Java 安装目录", "java.vm.specification.version|Java 虚拟机规范版本"
			, "java.vm.specification.vendor|Java 虚拟机规范供应商", "java.vm.specification.name|Java 虚拟机规范名称", "java.vm.version|Java 虚拟机实现版本"
			, "java.vm.vendor|Java 虚拟机实现供应商", "java.vm.name|Java 虚拟机实现名称", "java.specification.version|Java 运行时环境规范版本","java.specification.vendor|Java 运行时环境规范供应商"
			, "java.specification.name|Java 运行时环境规范名称", "java.class.version|Java 类格式版本号", "java.class.path|Java 类路径", "java.library.path|Java 加载库路径"
			, "java.io.tmpdir|Java 默认的临时文件路径", "java.compiler|Java JIT编译器的名称", "java.ext.dirs|Java 扩展目录路径"};

	/**
	 * 获取操作系统信息
	 * @return
	 */
	public List<Map<String, String>> getOsInfo(){
		
		List<Map<String, String>> osInfoList = new ArrayList<Map<String, String>>();
		Properties props = System.getProperties(); //获得系统属性集
		
		for(String prop : OS_PROP_LIST){
			String[] propArray = prop.split("\\|");
			Map<String, String> propMap = new HashMap<String, String>();
			propMap.put("name", propArray[0]);
			propMap.put("value", props.getProperty(propArray[0]));
			propMap.put("desc", propArray[1]);
			osInfoList.add(propMap);
		}
		return osInfoList;
	}
	
	/**
	 * 获取网络信息
	 * @return
	 */
	public List<Map<String, String>> getNetInfo(){
		
		List<Map<String, String>> netInfoList = new ArrayList<Map<String, String>>();
		
		// 方式一
		try {
			Process pro = Runtime.getRuntime().exec("cmd /c ipconfig /all");
			InputStreamReader isr = new InputStreamReader(pro.getInputStream(), "GBK");
			BufferedReader br = new BufferedReader(isr);
			String str = br.readLine();
			while(str != null){
				Map<String, String> propMap = new HashMap<String, String>();
				if(str.indexOf("IPv4") != -1) {
					propMap.put("name", "host.ipv4.address");
					propMap.put("value", str.split(":")[1].trim().split("\\(")[0].trim());
					propMap.put("desc", "主机ipv4地址");
				} else if(str.indexOf("物理地址") != -1) {
					propMap.put("name", "host.mac.address");
					propMap.put("value", str.split(":")[1].trim());
					propMap.put("desc", "主机MAC地址");
				} else if(str.indexOf("子网掩码") != -1) {
					propMap.put("name", "host.ipv4.submask.address");
					propMap.put("value", str.split(":")[1].trim());
					propMap.put("desc", "子网掩码地址");
				} else if(str.indexOf("默认网关") != -1) {
					propMap.put("name", "gateway.address");
					propMap.put("value", str.split(":")[1].trim());
					propMap.put("desc", "默认网关地址");
				} else if(str.indexOf("DNS 服务器") != -1) {
					propMap.put("name", "dns.address");
					propMap.put("value", str.split(":")[1].trim());
					propMap.put("desc", "DNS服务器地址");
				} else {
					str = br.readLine();
					continue;
				}
				netInfoList.add(propMap);
				str = br.readLine();
			}
			br.close();
			isr.close();
			return netInfoList;
		} catch (Exception e) {
			logger.error("获取本机网络信息异常！", e);
			netInfoList.clear();
		}
		
		// 方式二
		InetAddress address = null;
		try {
			// 主机ip地址
			address = InetAddress.getLocalHost();
			Map<String, String> map1 = new HashMap<String, String>();
			map1.put("name", "host.ipv4.address");
			map1.put("value", address.getHostAddress().toString());
			map1.put("desc", "主机ipv4地址");
			netInfoList.add(map1);
		} catch (Exception e) {
			logger.error("获取本机网络信息异常！", e);
			return null;
		}
		
		// mac地址
		byte[] mac = null;
		try {
			mac = NetworkInterface.getByInetAddress(address).getHardwareAddress();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer macAddress = new StringBuffer("");
		for(int i = 0; i<mac.length; i++) {
			if(i != 0) {
				macAddress.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length() == 1) {
				macAddress.append("0" + str);
			}else {
				macAddress.append(str);
			}
		}
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "host.mac.address");
		map2.put("value", macAddress.toString().toUpperCase());
		map2.put("desc", "主机MAC地址");
		netInfoList.add(map2);
		
		// 子网掩码
		String subMask = "";
		try {
			for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis != null && nis.hasMoreElements();) {
				NetworkInterface ni = nis.nextElement();
				for (InterfaceAddress ifAddr : ni.getInterfaceAddresses()) {
					if(address.toString().equals(ifAddr.getAddress().getHostAddress())){
						subMask = getMask(ifAddr.getNetworkPrefixLength());
						break;
					}
				}
			}
			Map<String, String> map3 = new HashMap<String, String>();
			map3.put("name", "host.ipv4.submask");
			map3.put("value", subMask);
			map3.put("desc", "主机ipv4子网掩码");
			netInfoList.add(map3);
		} catch (Exception e) {
			logger.error("获取本机网络信息异常！", e);
		}
		
		return netInfoList;
	}
	
	/**
	 * 获取应用信息
	 * @return
	 */
	public List<Map<String, String>> getAppInfo(){
		
		List<Map<String, String>> appInfoList = new ArrayList<Map<String, String>>();
		
		// spring 
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("name", "spring.boot.version");
		map1.put("value", SpringBootVersion.getVersion());
		map1.put("desc", "spring boot版本号");
		appInfoList.add(map1);
		
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("name", "spring.framework.version");
		map2.put("value", SpringVersion.getVersion());
		map2.put("desc", "spring framework版本号");
		appInfoList.add(map2);
		
		return appInfoList;
	}
	
	// 获得子网掩码
	private static String getMask(int maskLength){
		StringBuffer maskStr = new StringBuffer();
		int mask = 0xFFFFFFFF << 32 - maskLength ;
		for(int i = 3;i >= 0;i--){
			maskStr.append( (  mask  >> (8*i) ) & 0xFF);
			if(i > 0){
				maskStr.append(".");
			}
		}
		return maskStr.toString();
	}
	
	
}

