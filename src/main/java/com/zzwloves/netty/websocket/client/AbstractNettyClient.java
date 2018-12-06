package com.zzwloves.netty.websocket.client;

import com.zzwloves.netty.websocket.handler.WebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * netty 客户端
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 * @param <T>
 */
abstract class AbstractNettyClient<T> implements NettyClient<T> {
	
	protected Bootstrap bootstrap;
	protected ChannelFuture channelFuture;
	protected ClientConfig clientConfig;
	private ClientType clientType;
	
	public AbstractNettyClient(ClientConfig clientConfig, ClientType clientType) {
		this.clientConfig = clientConfig;
		this.clientType = clientType;
		createClient();
	}

	protected void createClient() {
		EventLoopGroup group=new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_BACKLOG, 1024*1024*10)
				.group(group)
				.handler(new LoggingHandler(LogLevel.INFO))
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						socketChannel.pipeline().addLast(clientConfig.getChannelHandlers());
					}
				});
	}


	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public void setBootstrap(Bootstrap bootstrap) {
		this.bootstrap = bootstrap;
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public void setChannelFuture(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public void setClientConfig(ClientConfig clientConfig) {
		this.clientConfig = clientConfig;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

}
