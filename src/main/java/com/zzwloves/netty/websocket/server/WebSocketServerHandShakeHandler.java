package com.zzwloves.netty.websocket.server;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.adapter.StandardWebSocketSession;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptor;
import com.zzwloves.netty.websocket.server.handshake.HandshakeInterceptorChain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

/**
 * WebSocket 服务握手处理类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class WebSocketServerHandShakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
		
	private ServerConfig serverConfig;
	private WebSocketHandler webSocketHandler;

	/**
	 * 构造
	 * @author zhengwei.zhu
	 * @param serverConfig
	 */
	public WebSocketServerHandShakeHandler(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.webSocketHandler = serverConfig.getWebSocketHandler();
	}

	/**
	 * 接收客户端发送的消息 channel 通道 Read 读
	 * 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
			throws Exception {
		final FullHttpRequest req = msg;
		HttpHeaders headers = req.headers();
		if (!req.decoderResult().isSuccess()
				|| (!"websocket".equals(headers.get("Upgrade")))) {
			sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		// 获取url后置参数
		Channel channel = ctx.channel();
		WebSocketSession session = new StandardWebSocketSession(ctx, 
				(InetSocketAddress)channel .localAddress(), (InetSocketAddress)channel.remoteAddress());
		channel.attr(AttributeKey.valueOf("webSocketSession")).set(session);
		Map<String, Object> attributes = session.getAttributes();
		String uriStr = req.uri();
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uriStr);
		Map<String, List<String>> parameters = queryStringDecoder.parameters();
		for (Entry<String, List<String>> parameterEntry : parameters.entrySet()) {
			attributes.put(parameterEntry.getKey(), parameterEntry.getValue().get(0));
		}
		String webSocketURL = "ws://" + headers.get(HttpHeaderNames.HOST) + uriStr;
		URI uri = new URI(webSocketURL);
		ctx.channel().attr(AttributeKey.valueOf("URI")).set(uri);
		
		// 构造握手工厂
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				webSocketURL, null, false);
		
		// 握手拦截
		HandshakeInterceptorChain interceptorChain = this.serverConfig.getInterceptorChain();
		HandshakeInterceptor[] interceptors = interceptorChain.getInterceptors();
		for (HandshakeInterceptor interceptor : interceptors) {
			boolean beforeHandshake = interceptor.beforeHandshake(webSocketHandler, attributes);
			if (!beforeHandshake) {
				throw new RuntimeException("没通过握手拦截！！");
			}
		}
		
		// 解决部分苹果浏览器版本过低，WebSocket协议过旧造成无法连接问题、
		String secWebSocketExtensions = headers.get("Sec-WebSocket-Extensions");
		if (secWebSocketExtensions == null || !secWebSocketExtensions.contains("permessage-deflate")) {			
			headers.set("Sec-WebSocket-Extensions","permessage-deflate");
		}
		
		// 创建握手
		WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			// 进行握手
			ChannelPromise promise = (ChannelPromise) handshaker.handshake(ctx.channel(), req);
			if (promise.isSuccess()) {
				for (int i = interceptors.length - 1; 0 <= i; i--) {
					interceptors[i].afterHandshake(webSocketHandler, null);
				}
				ctx.channel().attr(AttributeKey.valueOf("handShakeHandler")).set(handshaker);
				// 握手结束，进行连接成功回调方法
				webSocketHandler.afterConnectionEstablished(session);				
			} else {
				throw new UnsupportedOperationException(String.format("%s 握手失败", promise.cause()));
			}
		}
	}
	
	private void sendHttpResponse(ChannelHandlerContext ctx,
			FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
					CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		// 如果是非Keep-Alive，关闭连接
		ChannelFuture cf = ctx.channel().writeAndFlush(res);
		if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
			cf.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
