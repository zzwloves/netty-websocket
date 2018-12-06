package com.zzwloves.netty.websocket;

import io.netty.buffer.ByteBuf;

/**
 * Ping 消息
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class PingMessage extends AbstractWebScoketMessage<ByteBuf> {

	public PingMessage() {
		super();
	}

	public PingMessage(ByteBuf payload) {
		super(payload);
	}

}
