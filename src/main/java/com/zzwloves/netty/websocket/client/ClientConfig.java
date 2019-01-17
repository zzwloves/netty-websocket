package com.zzwloves.netty.websocket.client;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaders;

import com.zzwloves.netty.websocket.handler.NettyHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 客户端配置类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class ClientConfig {
	
	private URI uri;
	private HttpHeaders headers;
	private NettyHandler nettyHandler;
	private SharableChannelHandler[] channelHandlers;
	
	public ClientConfig(String url, HttpHeaders headers, NettyHandler nettyHandler, SharableChannelHandler... channelHandlers) {
		super();
		try {
			this.uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.headers = headers;
		this.nettyHandler = nettyHandler;
		this.channelHandlers = channelHandlers;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public NettyHandler getNettyHandler() {
		return nettyHandler;
	}

	public void setNettyHandler(NettyHandler nettyHandler) {
		this.nettyHandler = nettyHandler;
	}

	public ChannelHandler[] getChannelHandlers() {
		return channelHandlers;
	}

	public void setChannelHandlers(SharableChannelHandler[] channelHandlers) {
		this.channelHandlers = channelHandlers;
	}
}
