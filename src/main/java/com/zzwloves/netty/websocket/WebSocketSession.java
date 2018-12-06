package com.zzwloves.netty.websocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

/**
 * WebSocket session 接口
 * @author zhengwei.zhu
 * @version <b>1.0.0</b>
 */
public interface WebSocketSession {

	/**
	 * Return a unique session identifier.
	 */
	String getId();

	/**
	 * Return the URI used to open the WebSocket connection.
	 */
	URI getUri();

	/**
	 * Return the map with attributes associated with the WebSocket session.
	 * methods.
	 */
	Map<String, Object> getAttributes();


	/**
	 * Return the address on which the request was received.
	 */
	InetSocketAddress getLocalAddress();

	/**
	 * Return the address of the remote client.
	 */
	InetSocketAddress getRemoteAddress();

	/**
	 * Return whether the connection is still open.
	 */
	boolean isOpen();

	/**
	 * Send a WebSocket message either {@link TextMessage} or
	 * {@link BinaryMessage}.
	 * @throws Exception 
	 */
	void sendMessage(WebSocketMessage<?> message) throws Exception;

	/**
	 * Close the WebSocket connection with status 1000, i.e. equivalent to:
	 * <pre class="code">
	 * session.close(CloseStatus.NORMAL);
	 * </pre>
	 */
	void close() throws IOException;

	/**
	 * Close the WebSocket connection with the given close status.
	 */
	void close(CloseStatus status) throws IOException;
}
