 package com.zzwloves.netty.websocket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

 /**
 * netty 服务类
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
class NettyServer {
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	private ServerBootstrap serverBootstrap;
	private ServerConfig serverConfig;
	private ChannelFuture channelFuture;
	private ServerType serverType;
	
	public NettyServer() {}

	public NettyServer(ServerConfig serverConfig, ServerType serverType) {
		this.serverConfig = serverConfig;
		this.serverType = serverType;
	}
	
	/**
	 * 服务创建并启动
	 * @author zhengwei.zhu
	 */
	public void start() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup);
			serverBootstrap.channel(NioServerSocketChannel.class);
			if (serverType.equals(ServerType.WEBSOCKET_SERVER)) {				
				serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						ChannelPipeline channelPipeline = ch.pipeline();
						// 设置30秒没有读到数据，则触发一个READER_IDLE事件。
						// pipeline.addLast(new IdleStateHandler(30, 0, 0));
						// HttpServerCodec：将请求和应答消息解码为HTTP消息
						channelPipeline.addLast("httpServercodec",new HttpServerCodec());
						// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
						channelPipeline.addLast("aggregator",new HttpObjectAggregator(1024*1024*10));
						// ChunkedWriteHandler：向客户端发送HTML5文件
						channelPipeline.addLast("httpChunked",new ChunkedWriteHandler());
						// 在管道中添加我们自己的接收数据实现方法
						channelPipeline.addLast("handShakeHandler",new WebSocketServerHandShakeHandler(serverConfig));
						channelPipeline.addLast("messageHandler",new WebSocketServerMessageHandler(serverConfig));
						
					}
				});
			}
			
			/**
	         * 你可以设置这里指定的通道实现的配置参数。 我们正在写一个TCP/IP的服务端，
	         * 因此我们被允许设置socket的参数选项比如tcpNoDelay和keepAlive。
	         * 请参考ChannelOption和详细的ChannelConfig实现的接口文档以此可以对ChannelOptions的有一个大概的认识。
	         */
			serverBootstrap = serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
	        /**
	         * option()是提供给NioServerSocketChannel用来接收进来的连接。
	         * childOption()是提供给由父管道ServerChannel接收到的连接，
	         * 在这个例子中也是NioServerSocketChannel。
	         */
			serverBootstrap = serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
	       
	        // 绑定端口
	        channelFuture = serverBootstrap.bind(this.serverConfig.getPort());
			// 开启服务
			channelFuture.sync();

			logger.info("WebSocket Server started on port: {} with context path {}", serverConfig.getPort(), serverConfig.getContextPath());
			// 这里会一直等待，直到socket被关闭
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}

	public ServerBootstrap getServerBootstrap() {
		return serverBootstrap;
	}

	public void setServerBootstrap(ServerBootstrap serverBootstrap) {
		this.serverBootstrap = serverBootstrap;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

	public void setChannelFuture(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

}
