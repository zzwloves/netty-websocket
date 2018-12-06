package com.zzwloves.netty.websocket.client;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpHeaders;

import com.zzwloves.netty.websocket.handler.NettyHandler;

/**
 * 客户端配置类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class ClientConfig {
	
	private String url;
	private HttpHeaders headers;
	private NettyHandler nettyHandler;
	private ChannelHandler[] channelHandlers;
	
	public ClientConfig(String url, HttpHeaders headers, NettyHandler nettyHandler, ChannelHandler... channelHandlers) {
		super();
		this.url = url;
		this.headers = headers;
		this.nettyHandler = nettyHandler;
		this.channelHandlers = channelHandlers;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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

	public void setChannelHandlers(ChannelHandler[] channelHandlers) {
		this.channelHandlers = channelHandlers;
	}
}
