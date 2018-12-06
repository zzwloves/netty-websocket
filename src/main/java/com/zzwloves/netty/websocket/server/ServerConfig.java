package com.zzwloves.netty.websocket.server;

import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptor;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptorChain;

/**
 * 服务配置类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class ServerConfig {
	
	private static final int DEFAULT_PORT = 80;
	private static final String DEFAULT_CONTEXT_PATH = "/";

	private int port;
	private String contextPath;
	private WebSocketHandler webSocketHandler;
	private HandshakeInterceptorChain interceptorChain;
	
	public ServerConfig(WebSocketHandler webSocketHandler, HandshakeInterceptor[] interceptors) {
		this(webSocketHandler, new HandshakeInterceptorChain(interceptors));
	}
	
	public ServerConfig(WebSocketHandler webSocketHandler, HandshakeInterceptorChain handshakeInterceptorChain) {
		this(DEFAULT_PORT, DEFAULT_CONTEXT_PATH, webSocketHandler, handshakeInterceptorChain);
	}

	public ServerConfig(int port, String contextPath, WebSocketHandler webSocketHandler, HandshakeInterceptor[] interceptors) {
		this(port, contextPath, webSocketHandler, new HandshakeInterceptorChain(interceptors));
	}
	
	public ServerConfig(int port, String contextPath, WebSocketHandler webSocketHandler, HandshakeInterceptorChain handshakeInterceptorChain) {
		this.port = port;
		this.contextPath = contextPath;
		this.webSocketHandler = webSocketHandler;
		this.interceptorChain = handshakeInterceptorChain;
	}

	//*************************** get/set **************************************************
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public WebSocketHandler getWebSocketHandler() {
		return webSocketHandler;
	}

	public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	public HandshakeInterceptorChain getInterceptorChain() {
		return interceptorChain;
	}

	public void setInterceptorChain(HandshakeInterceptorChain interceptorChain) {
		this.interceptorChain = interceptorChain;
	}

	// ***************************** toString ************************************************
	@Override
	public String toString() {
		return "ServerConfig {port=" + port + ", contextPath="
				+ contextPath + "}";
	}
		
		
}
