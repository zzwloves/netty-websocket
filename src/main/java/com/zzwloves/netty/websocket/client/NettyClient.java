package com.zzwloves.netty.websocket.client;

import java.util.concurrent.Future;

/**
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 * @param <T>
 */
public interface NettyClient<T> {

	Future<T> start() throws Exception;
}
