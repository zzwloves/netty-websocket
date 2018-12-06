package com.zzwloves.netty.websocket.adapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.zzwloves.netty.websocket.CloseStatus;
import com.zzwloves.netty.websocket.PingMessage;
import com.zzwloves.netty.websocket.PongMessage;
import com.zzwloves.netty.websocket.TextMessage;
import com.zzwloves.netty.websocket.WebSocketMessage;
import com.zzwloves.netty.websocket.WebSocketSession;

/**
 * 默认使用的WebSocket的session类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public final class StandardWebSocketSession implements WebSocketSession {

	private final ChannelHandlerContext ctx;

	private final InetSocketAddress localAddress;

	private final InetSocketAddress remoteAddress;

	private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	public StandardWebSocketSession(ChannelHandlerContext ctx,
			InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super();
		this.ctx = ctx;
		this.localAddress = localAddress;
		this.remoteAddress = remoteAddress;
	}

	/**
	 * 获取id值
	 * @author zhengwei.zhu
	 * @return session的id值
	 */
	@Override
	public String getId() {
		return (String) ctx.channel().attr(AttributeKey.valueOf("id")).get();
	}

	/**
	 * 获取uri
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#getUri()
	 * @return uri
	 */
	@Override
	public URI getUri() {
		
		return (URI) ctx.channel().attr(AttributeKey.valueOf("URI")).get();
	}

	/**
	 * 获取session关联的属性值
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#getAttributes()
	 * @return session关联的属性值
	 */
	@Override
	public Map<String, Object> getAttributes() {

		return attributes;
	}

	/**
	 * 本地地址
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#getLocalAddress()
	 * @return 本地地址
	 */
	@Override
	public InetSocketAddress getLocalAddress() {

		return this.localAddress;
	}

	/**
	 * 远程地址
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#getRemoteAddress()
	 * @return 远程地址
	 */
	@Override
	public InetSocketAddress getRemoteAddress() {

		return this.remoteAddress;
	}

	/**
	 * session是否打开
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#isOpen()
	 * @return true 打开，false 未打开
	 */
	@Override
	public boolean isOpen() {
		
		return ctx.channel().isOpen();
	}

	/**
	 * 发送消息
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#sendMessage(com.zzwloves.netty.websocket.WebSocketMessage)
	 * @param message 消息内容
	 * @throws Exception 异常
	 */
	@Override
	public void sendMessage(WebSocketMessage<?> message) throws Exception {
		if (!isOpen()) {
			throw new IllegalAccessException("不能在websocketSession关闭后发送消息！！！");
		}
		if (message instanceof PingMessage) {
			sendPingMessage((PingMessage) message);
		} else if (message instanceof PongMessage) {
			sendPongMessage((PongMessage) message);
		} else if (message instanceof TextMessage) {
			sendTextMessage((TextMessage) message);
		} else {
			throw new IllegalArgumentException("消息类型不支持!!!");
		}
	}
	
	private void sendPingMessage(PingMessage message) {
		ctx.writeAndFlush(new PingWebSocketFrame(message.getPayload()));
	}
	
	private void sendPongMessage(PongMessage message) {
		ctx.writeAndFlush(new PongWebSocketFrame(message.getPayload()));
	}
	
	private void sendTextMessage(TextMessage message) {
		ctx.writeAndFlush(new TextWebSocketFrame(message.getPayload()));
	}
	
	/**
	 * session 关闭
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#close()
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		ctx.close();
	}

	/**
	 * session 关闭，并设置关闭原因
	 * @author zhengwei.zhu
	 * @see com.zzwloves.netty.websocket.WebSocketSession#close(com.zzwloves.netty.websocket.CloseStatus)
	 * @param status 关闭原因
	 * @throws IOException
	 */
	@Override
	public void close(CloseStatus status) throws IOException {
		ctx.close();
	}

}
