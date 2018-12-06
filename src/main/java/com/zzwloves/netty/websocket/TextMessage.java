package com.zzwloves.netty.websocket;

/**
 * 文本消息
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class TextMessage extends AbstractWebScoketMessage<String> {

	public TextMessage(String payload) {
		super(payload);
	}

	@Override
	public String toString() {
		return "TextMessage {getPayload()=" + getPayload() + "}";
	}

}
