package com.zzwloves.netty.websocket;

import java.util.Objects;

/**
 * 抽象WebSocket消息类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class AbstractWebScoketMessage<T> implements WebSocketMessage<T> {

	private T payload;
	
	public AbstractWebScoketMessage() {
		super();
	}

	public AbstractWebScoketMessage(T payload) {
		super();
		if (Objects.isNull(payload)) {
			throw new IllegalArgumentException("payload must not be null");
		}
		this.payload = payload;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}
	
}
