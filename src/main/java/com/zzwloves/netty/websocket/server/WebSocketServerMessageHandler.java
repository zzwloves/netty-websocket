package com.zzwloves.netty.websocket.server;

import com.zzwloves.netty.websocket.*;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;

/**
 * WebSocket 服务消息处理类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class WebSocketServerMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
		
	private ServerConfig serverConfig;
	private WebSocketHandler webSocketHandler;

	/**
	 * 构造
	 * @author zhengwei.zhu
	 * @param serverConfig
	 */
	public WebSocketServerMessageHandler(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.webSocketHandler = serverConfig.getWebSocketHandler();
	}

	/**
	 * channel 通道 Inactive 不活跃的
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端关闭了通信通道并且不可以传输数据
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		// 移除
		System.out.println("客户端与服务端连接关闭："
				+ ctx.channel().remoteAddress().toString());
		Channel channel = ctx.channel();
		webSocketHandler.afterConnectionClosed((WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get(), 
				new CloseStatus());
	}

	/**
	 * channel 通道 Read 读取 Complete 完成 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	/**
	 * exception 异常 Caught 抓住 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		Channel channel = ctx.channel();
		WebSocketSession session = (WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get();
		this.webSocketHandler.handleTransportError(session, cause);
		ctx.close();
	}

	/**
	 * 接收客户端发送的消息 channel 通道 Read 读
	 * 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
			throws Exception {
		final WebSocketFrame frame = msg;
		Channel channel = ctx.channel();
		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			WebSocketServerHandshaker handShake = (WebSocketServerHandshaker) channel.attr(AttributeKey.valueOf("handShakeHandler")).get();
			handShake.close(channel, (CloseWebSocketFrame) frame.retain());
			WebSocketSession session = (WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get();
			webSocketHandler.afterConnectionClosed(session, new CloseStatus());
		}
		// 判断是否ping消息
		else if (frame instanceof PingWebSocketFrame) {
			channel.write(new PongWebSocketFrame(frame.content().retain()));
		}
		// 文本消息
		else if (frame instanceof TextWebSocketFrame) {
			String text = ((TextWebSocketFrame) frame).text();
			WebSocketMessage<String> message = new TextMessage(text);

			WebSocketSession session = (WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get();
			webSocketHandler.handleMessage(session, message);
		}
		// 二进制消息
		else if (frame instanceof BinaryWebSocketFrame) {
			ByteBuf content = ((BinaryWebSocketFrame) frame).content();
			byte[] bytes = content.array();
			WebSocketSession session = (WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get();
			webSocketHandler.handleMessage(session, new BinaryMessage(bytes));

		}
	}

}
