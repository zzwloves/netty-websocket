package com.zzwloves.netty.websocket;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;

/**
 * @author: zhuzhengwei
 * @date: 2019/1/29 15:29
 */
public class ServerHttpRequest {

	private URI uri;
	private HttpHeaders headers;
	private HttpVersion version;
	private HttpMethod method;

	public ServerHttpRequest(URI uri, HttpHeaders headers, HttpVersion version, HttpMethod method) {
		this.uri = uri;
		this.headers = headers;
		this.version = version;
		this.method = method;
	}

	public URI getUri() {
		return uri;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public HttpVersion getVersion() {
		return version;
	}

	public HttpMethod getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return "ServerHttpRequest{" +
				"uri=" + uri +
				", headers=" + headers +
				", version=" + version +
				", method=" + method +
				'}';
	}
}