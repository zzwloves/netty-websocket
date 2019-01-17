package com.zzwloves.netty.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * netty 客户端
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 * @param <T>
 */
abstract class AbstractNettyClient<T> implements NettyClient<T> {

	private static Logger logger = LoggerFactory.getLogger(AbstractNettyClient.class);

	private Bootstrap bootstrap;
	private NioEventLoopGroup group;
	private ChannelFuture channelFuture;
	private ClientConfig clientConfig;
	private ClientType clientType;

	public AbstractNettyClient(ClientConfig clientConfig, ClientType clientType) {
		this.clientConfig = clientConfig;
		this.clientType = clientType;
		prepare();
	}

	protected void prepare() {
		group = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("loghingHandler", new LoggingHandler(LogLevel.ERROR));
						pipeline.addLast("httpClientCodec", new HttpClientCodec());
						pipeline.addLast("httpObjectAggregator", new HttpObjectAggregator(1024 * 1024 * 10));
						for (ChannelHandler channelHandler : clientConfig.getChannelHandlers()) {
							String simpleName = channelHandler.getClass().getSimpleName();
							String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
							pipeline.addLast(name, channelHandler);
						}
					}
				});
	}

	protected void connect() throws InterruptedException {
		if (channelFuture == null || !channelFuture.channel().isActive()) {
			synchronized (this.getClass()) {
				if (channelFuture == null || !channelFuture.channel().isActive()) {
					URI uri = clientConfig.getUri();
					int port = uri.getPort() == -1 ? 80 : uri.getPort();
					channelFuture = bootstrap.connect(uri.getHost(), port).sync().addListener(future -> logger.info("连接上服务器"));
				}
			}
		}
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public ClientConfig getClientConfig() {
		return clientConfig;
	}

	public ClientType getClientType() {
		return clientType;
	}


	public Bootstrap getBootstrap() {
		return bootstrap;
	}

	public NioEventLoopGroup getGroup() {
		return group;
	}
}
