package com.zzwloves.netty.websocket.server;

import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptor;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptorChain;

import javax.annotation.PostConstruct;

/**
 * websocket服务实体类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public final class WebScoketServer {

	private ServerConfig serverConfig;
	private int port;
	private String contextPath;
	private NettyServer realServer;
	
	//************************** 构造 ***************************************************
	public WebScoketServer(WebSocketHandler webSocketHandler, HandshakeInterceptor... interceptors) {
		this.serverConfig = new ServerConfig(webSocketHandler, interceptors);
		this.port = serverConfig.getPort();
		this.contextPath = serverConfig.getContextPath();
		createServer(serverConfig);
	}

	public WebScoketServer(int port, String contextPath, WebSocketHandler webSocketHandler, HandshakeInterceptor... interceptors) {
		this.serverConfig = new ServerConfig(port, contextPath, webSocketHandler, interceptors);
		this.port = port;
		this.contextPath = contextPath;
		createServer(serverConfig);
	}

	private void createServer(ServerConfig serverConfig) {
		this.realServer = ServerFactory.builder(serverConfig).createWebSocketServer();
	}

	//**************************** method *************************************************
	public void start() {
		// 新建线程异步启动服务，原因：开启服务最后会进行线程阻塞
		Thread thread = new Thread(realServer::start);
		thread.setName("server-thread");
		thread.start();
	}

	//**************************** get/set *************************************************
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

}
