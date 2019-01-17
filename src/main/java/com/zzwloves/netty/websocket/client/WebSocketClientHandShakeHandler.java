package com.zzwloves.netty.websocket.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.adapter.StandardWebSocketSession;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

/**
 * WebSocket握手处理类
 *
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class WebSocketClientHandShakeHandler extends SharableChannelHandler<FullHttpResponse> {

	private static Logger logger = LoggerFactory.getLogger(WebSocketClientHandShakeHandler.class);

	private WebSocketHandler webSocketHandler;

	private WebSocketClientHandshaker handshaker;

	private ChannelPromise handshakeFuture;

	private ChannelPromise webSocketSesssionFuture;

	public WebSocketClientHandShakeHandler(WebSocketHandler webSocketHandler) {
		super();
		this.webSocketHandler = webSocketHandler;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		this.handshakeFuture = ctx.newPromise();
		this.webSocketSesssionFuture = ctx.newPromise();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg)
			throws Exception {
		System.err.println(msg);
		Channel channel = ctx.channel();
		final FullHttpResponse response = msg;
		if (!this.handshaker.isHandshakeComplete()) {
			try {
				//握手协议返回，设置结束握手
				this.handshaker.finishHandshake(channel, response);
				//设置成功
				this.handshakeFuture.setSuccess();
				if (logger.isDebugEnabled()) {
					logger.debug("WebSocket Client connected! response headers[sec-websocket-extensions]:{}" + response.headers());
				}

				InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
				URI uri = new URI("ws://" + remoteAddress.toString() + "");
				ctx.channel().attr(AttributeKey.valueOf("URI")).set(uri);

				// 握手成功回调
				afterHandShake(ctx);
			} catch (WebSocketHandshakeException var7) {
				FullHttpResponse res = (FullHttpResponse) msg;
				String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
				this.handshakeFuture.setFailure(new Exception(errorMsg));
			}
		} else {
			throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + msg.status() + ", content=" + msg.content().toString(CharsetUtil.UTF_8) + ')');
		}

	}

	/**
	 * 握手成功后回调方法
	 */
	private void afterHandShake(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		WebSocketSession session = (WebSocketSession) channel.attr(AttributeKey.valueOf("webSocketSession")).get();
		if (Objects.isNull(session)) {
			session = new StandardWebSocketSession(ctx, (InetSocketAddress) channel.localAddress(), (InetSocketAddress) channel.remoteAddress());
			channel.attr(AttributeKey.valueOf("webSocketSession")).set(session);
		}
		this.webSocketSesssionFuture.setSuccess();
		webSocketHandler.afterConnectionEstablished(session);

	}

	public WebSocketClientHandshaker getHandshaker() {
		return handshaker;
	}

	public void setHandshaker(WebSocketClientHandshaker handshaker) {
		this.handshaker = handshaker;
	}

	public ChannelFuture handshakeFuture() {
		return handshakeFuture;
	}

	public ChannelFuture webSocketSesssionFuture() {
		return webSocketSesssionFuture;
	}

}
