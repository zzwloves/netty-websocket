package com.zzwloves.netty.websocket.client;

import com.zzwloves.netty.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzwloves.netty.websocket.handler.WebSocketHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.AttributeKey;

/**
 * WebSocket客户端处理类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class WebSocketClientMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private static Logger logger = LoggerFactory.getLogger(WebSocketClientMessageHandler.class);
	
	private WebSocketHandler webSocketHandler;

	public WebSocketClientMessageHandler(WebSocketHandler webSocketHandler) {
		super();
		this.webSocketHandler = webSocketHandler;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
			throws Exception {
        Channel channel = ctx.channel();
        final WebSocketFrame frame = msg;
        if (frame instanceof TextWebSocketFrame) {
            String text = ((TextWebSocketFrame) frame).text();
            WebSocketMessage<String> message = new TextMessage(text);
	        webSocketHandler.handleMessage((WebSocketSession)ctx.channel().attr(AttributeKey.valueOf("webSocketSession")).get(),
			        message);
        } else if (frame instanceof BinaryWebSocketFrame) {
	        BinaryMessage message = new BinaryMessage(((BinaryWebSocketFrame) frame).content().array());
	        webSocketHandler.handleMessage((WebSocketSession)ctx.channel().attr(AttributeKey.valueOf("webSocketSession")).get(),
			        message);
        } else if (frame instanceof PongWebSocketFrame) {
	        PongMessage message = new PongMessage(((PongWebSocketFrame) frame).content());
	        webSocketHandler.handleMessage((WebSocketSession)ctx.channel().attr(AttributeKey.valueOf("webSocketSession")).get(),
			        message);
        } else if (frame instanceof CloseWebSocketFrame) {
	        channel.close();
        }

		
	}
	
	 /** 
	  * channel 通道 Inactive 不活跃的
	  */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		webSocketHandler.afterConnectionClosed((WebSocketSession)ctx.channel().attr(AttributeKey.valueOf("webSocketSession")).get(),
				new CloseStatus());
	}

	/**
	 * 发生异常
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
		webSocketHandler.handleTransportError((WebSocketSession)ctx.channel().attr(AttributeKey.valueOf("webSocketSession")).get(),
				cause);
	}
	
}
