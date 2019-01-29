package com.zzwloves.netty.websocket.server.handshake;

import com.zzwloves.netty.websocket.ServerHttpRequest;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;

import java.util.Map;

/**
 * 握手拦截器
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public interface HandshakeInterceptor {

	/**
	 * 握手前
	 * @author zhengwei.zhu
	 * @param serverHttpRequest
	 * @param wsHandler
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	boolean beforeHandshake(ServerHttpRequest serverHttpRequest, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception;

	/**
	 * 握手后
	 * @author zhengwei.zhu
	 * @param wsHandler
	 * @param exception
	 */
	void afterHandshake(WebSocketHandler wsHandler, Exception exception);
}
