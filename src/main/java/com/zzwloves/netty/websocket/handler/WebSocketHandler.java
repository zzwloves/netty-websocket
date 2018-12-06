package com.zzwloves.netty.websocket.handler;

import com.zzwloves.netty.websocket.CloseStatus;
import com.zzwloves.netty.websocket.WebSocketMessage;
import com.zzwloves.netty.websocket.WebSocketSession;

/**
 * WebSocket处理类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public interface WebSocketHandler extends NettyHandler {
	
	void afterConnectionEstablished(WebSocketSession session) throws Exception;

	void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception;

	void handleTransportError(WebSocketSession session, Throwable exception) throws Exception;

	void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception;

}
