package com.zzwloves.netty.websocket.client;

/**
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 * @param <T>
 */
public interface NettyClient<T> {

	T start() throws Exception;
}
