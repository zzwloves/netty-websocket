package com.zzwloves.netty.websocket.server.handshake;

import java.util.Map;

import com.zzwloves.netty.websocket.handler.WebSocketHandler;

/**
 * 握手拦截器
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public interface HandshakeInterceptor {

	/**
	 * 握手前
	 * @author zhengwei.zhu
	 * @param wsHandler
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	boolean beforeHandshake(WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception;

	/**
	 * 握手后
	 * @author zhengwei.zhu
	 * @param wsHandler
	 * @param exception
	 */
	void afterHandshake(WebSocketHandler wsHandler, Exception exception);
}
