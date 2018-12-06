package com.zzwloves.netty.websocket;

/**
 * @author: zhuzhengwei
 * @date: 2018/11/7 13:12
 */
public class BinaryMessage extends AbstractWebScoketMessage<byte[]> {
	public BinaryMessage(byte[] payload) {
		super(payload);
	}

}
