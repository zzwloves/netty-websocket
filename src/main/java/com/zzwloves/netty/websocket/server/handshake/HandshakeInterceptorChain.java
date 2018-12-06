package com.zzwloves.netty.websocket.server.handshake;

/**
 * 握手连接器链
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public class HandshakeInterceptorChain {

	private HandshakeInterceptor[] interceptors;
	
	public HandshakeInterceptorChain() {
		super();
	}

	public HandshakeInterceptorChain(HandshakeInterceptor[] interceptors) {
		super();
		this.interceptors = interceptors;
	}

	public HandshakeInterceptor[] getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(HandshakeInterceptor[] interceptors) {
		this.interceptors = interceptors;
	}
	
}
