package com.zzwloves.netty.websocket.server;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务工厂
 * @author zhengwei.zhu
 * @date 2018年6月20日 上午11:00:39
 * @version <b>1.0.0</b>
 */
public class ServerFactory {

	private static ReentrantLock lock = new ReentrantLock();
	
	private static ServerFactory instance = null;

	private ServerConfig serverConfig;

	private ServerFactory() {}
	
	public static ServerFactory builder(ServerConfig serverConfig) {
		if (instance == null) {
			lock.lock();			
			if (instance == null) {
				instance = new ServerFactory();
				instance.setServerConfig(serverConfig);
			}
			lock.unlock();
		}
		return instance;
	}

	/**
	 * 创建server
	 * @author zhengwei.zhu
	 * @return
	 */
	public NettyServer createWebSocketServer() {
		NettyServer nettyServer = new NettyServer(serverConfig,
				ServerType.WEBSOCKET_SERVER);
		return nettyServer;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	private void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
}
