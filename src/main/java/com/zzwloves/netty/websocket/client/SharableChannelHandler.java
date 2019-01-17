package com.zzwloves.netty.websocket.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: zhuzhengwei
 * @date: 2019/1/17 16:12
 */
@ChannelHandler.Sharable
public class SharableChannelHandler<T> extends SimpleChannelInboundHandler<T> {

	/**
	 * <strong>Please keep in mind that this method will be renamed to
	 * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
	 * <p>
	 * Is called for each message of type {@link I}.
	 *
	 * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
	 *            belongs to
	 * @param msg the message to handle
	 * @throws Exception is thrown if an error occurred
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {

	}
}