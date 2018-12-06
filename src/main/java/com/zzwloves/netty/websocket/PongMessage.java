package com.zzwloves.netty.websocket;

import io.netty.buffer.ByteBuf;

/**
 * Pong 消息
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class PongMessage extends AbstractWebScoketMessage<ByteBuf> {

	public PongMessage() {
		super();
	}

	public PongMessage(ByteBuf payload) {
		super(payload);
	}

}
