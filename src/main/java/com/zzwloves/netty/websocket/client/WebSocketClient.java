package com.zzwloves.netty.websocket.client;

import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.*;

/**
 * WebSocket 客户端
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class WebSocketClient extends AbstractNettyClient<WebSocketSession> {

	private static Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

	public WebSocketClient(String url, HttpHeaders headers, WebSocketHandler webSocketHandler) {
		this(new ClientConfig(url, headers, webSocketHandler,
				new WebSocketClientHandShakeHandler(webSocketHandler),
				new WebSocketClientMessageHandler(webSocketHandler)));
	}
	
	private WebSocketClient(ClientConfig clientConfig) {
		super(clientConfig);
	}

	@Override
	public void handShake() throws InterruptedException {
		ChannelFuture channelFuture = getChannelFuture();
		Channel channel = channelFuture.channel();
		ClientConfig clientConfig = getClientConfig();
		HttpHeaders httpHeaders = clientConfig.getHeaders() != null ? clientConfig.getHeaders() : new DefaultHttpHeaders();
		// 进行握手
		WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
				clientConfig.getUri(), WebSocketVersion.V13, (String)null, true, httpHeaders);
		WebSocketClientHandShakeHandler handShakeHandler = channel.pipeline().get(WebSocketClientHandShakeHandler.class);
		handShakeHandler.setHandshaker(handshaker);
		handshaker.handshake(channel);
		//阻塞等待是否握手成功
		handShakeHandler.handshakeFuture().sync().addListener(future -> logger.info("握手成功"));

		handShakeHandler.webSocketSesssionFuture().sync().addListener(future -> latch.countDown());

		// 这里会一直等待，直到socket被关闭
		channel.closeFuture().sync().addListener(future -> logger.error("连接已断开"));
	}

	@Override
	public WebSocketSession future() {
		return (WebSocketSession) getChannelFuture().channel().attr(AttributeKey.valueOf("webSocketSession")).get();
	}

	public URI getURI() {
		return getClientConfig().getUri();
	}

	public HttpHeaders getHeaders() {
		return getClientConfig().getHeaders();
	}


	public WebSocketHandler getWebSocketHandler() {
		return (WebSocketHandler) getClientConfig().getNettyHandler();
	}

	public void stop() throws InterruptedException {
		ChannelFuture channelFuture = getChannelFuture();
		if (channelFuture == null) {
			if (!latch.await(30, TimeUnit.SECONDS)) {
				throw new RuntimeException("客户端未运行或正在开启中，不允许进行关闭操作");
			}
		}
		if (channelFuture != null) {
			getChannelFuture().channel().close().sync();
		}
	}

}
