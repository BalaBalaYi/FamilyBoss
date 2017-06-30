package com.cty.family.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cty.family.util.NetUtil;

/**
 * 过滤器实现
 * @WebFilter将一个实现了javax.servlet.Filter接口的类定义为过滤器
 * 属性filterName声明过滤器的名称,可选
 * 属性urlPatterns指定要过滤 的URL模式,也可使用属性value来声明.(指定要过滤的URL模式是必选属性)
 * 
 * @author 陈天熠
 */
@WebFilter(filterName="sessionFilter", urlPatterns="*.do")
public class SessionFilter implements Filter {
	
	private Logger logger = LoggerFactory.getLogger(SessionFilter.class);
	
	/**
	 * 封装，不需要过滤的list列表
	 */
	private List<String> urls = new CopyOnWriteArrayList<String>();

	@Override
	public void destroy() {
		logger.info("SessionFilter destroyed");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		
		String url = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
		if (url.startsWith("/") && url.length() > 1) {
			url = url.substring(1);
		}
		
		if (isInclude(url)){
			chain.doFilter(httpRequest, httpResponse);
			return;
		} else {
			HttpSession session = httpRequest.getSession();
			if (session.getAttribute("userInfo") != null){
				// session存在
				chain.doFilter(httpRequest, httpResponse);
				return;
			} else {
				String ip = NetUtil.getRemoteHost(httpRequest);
				logger.warn("ip为：" + ip + " 的用户非法连接，强制重新登录！");
				// session不存在 准备跳转失败
				RequestDispatcher dispatcher = servletRequest.getRequestDispatcher("/toLogin.do");
				dispatcher.forward(servletRequest, servletResponse);
				return;
			}
		}
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("SessionFilter initiated");
		// 排除路径
		this.urls.add("login/");
		this.urls.add("toLogin.do");
		this.urls.add("test");
	}
	
	/**
	 * 判断url是否需要过滤
	 * @param url
	 * @return
	 */
	private boolean isInclude(String url) {
		for (String nUrl : urls) {
			int i = url.indexOf(nUrl);
			if(i == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		
		List<String> urls = new ArrayList<String>();
		String url1 = "/";
		urls.add(url1);
		
		String url = "/user/toLogin.do";
		
		for (String nUrl : urls) {
			int i = url.indexOf(nUrl);
			System.out.println(i);
			if(i == 0) {
				System.out.println(true + url);
			}
		}
		System.out.println(false);
	}

}
