package com.zzwloves.netty.websocket.client;

import java.net.URI;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;

/**
 * WebSocket 客户端
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class WebSocketClient extends AbstractNettyClient<WebSocketSession> {
	
	private WebSocketSession webSocketSession;

	public WebSocketClient(String url, HttpHeaders headers, WebSocketHandler webSocketHandler) {
		this(new ClientConfig(url, headers, webSocketHandler,
				new HttpClientCodec(),
				new HttpObjectAggregator(1024*1024*10),
				new WebSocketClientHandShakeHandler(webSocketHandler),
				new WebSocketClientMessageHandler(webSocketHandler)),
				ClientType.WEBSOCKET_CLIENT);
	}
	
	private WebSocketClient(ClientConfig clientConfig, ClientType clientType) {
		super(clientConfig, clientType);
	}
	
	@Override
	public WebSocketSession start() throws Exception {
        URI uri = new URI(clientConfig.getUrl());
        int port = uri.getPort() == -1 ? 80 : uri.getPort();
        channelFuture = bootstrap.connect(uri.getHost(), port).sync();
        final Channel channel = channelFuture.channel();
        HttpHeaders httpHeaders = clientConfig.getHeaders() != null ? clientConfig.getHeaders() : new DefaultHttpHeaders();
        // 进行握手
		WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
				uri, WebSocketVersion.V13, (String)null, true, httpHeaders);
		WebSocketClientHandShakeHandler handShakeHandler = channel.pipeline().get(WebSocketClientHandShakeHandler.class);
		handShakeHandler.setHandshaker(handshaker);
		handshaker.handshake(channel);
		//阻塞等待是否握手成功
		handShakeHandler.handshakeFuture().sync();
		
		handShakeHandler.webSocketSesssionFuture().sync();
		this.webSocketSession = (WebSocketSession) channelFuture.channel().attr(AttributeKey.valueOf("webSocketSession")).get();
			
//		// 这里会一直等待，直到socket被关闭
//		channelFuture.channel().closeFuture().sync();
		return this.webSocketSession;
	}

	public String getUrl() {
		return clientConfig.getUrl();
	}

	public void setUrl(String url) {
		clientConfig.setUrl(url);
	}

	public HttpHeaders getHeaders() {
		return clientConfig.getHeaders();
	}

	public void setHeaders(HttpHeaders headers) {
		clientConfig.setHeaders(headers);
	}

	public WebSocketHandler getWebSocketHandler() {
		return (WebSocketHandler) clientConfig.getNettyHandler();
	}

	public void setWebSocketHandler(WebSocketHandler webSocketHandler) {
		clientConfig.setNettyHandler(webSocketHandler);
	}
	
}
