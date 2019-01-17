package com.zzwloves.netty.websocket.client;

import java.net.URI;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.AttributeKey;

import com.zzwloves.netty.websocket.WebSocketSession;
import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				new WebSocketClientMessageHandler(webSocketHandler)),
				ClientType.WEBSOCKET_CLIENT);
	}
	
	private WebSocketClient(ClientConfig clientConfig, ClientType clientType) {
		super(clientConfig, clientType);
	}
	
	@Override
	public void start() throws Exception {
		connect();
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

		handShakeHandler.webSocketSesssionFuture().sync();

		// 这里会一直等待，直到socket被关闭
		channel.closeFuture().sync().addListener(future -> {
			logger.error("连接已断开");
		});
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

	public WebSocketSession getWebSocketSession() {
		ChannelFuture channelFuture = getChannelFuture();
		if (channelFuture == null) {
			throw new RuntimeException("未先调用start方法后");
		}
		return (WebSocketSession) getChannelFuture().channel().attr(AttributeKey.valueOf("webSocketSession")).get();
	}

}
