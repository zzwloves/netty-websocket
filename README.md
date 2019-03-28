# netty-websocket
该框架使用netty框架完成对websocket的支持，可以创建websocket服务端，也可以创建websocket客户端
使用方法非常简单，使用创建对象的方式，然后调用start()就可以启动，可以使用main方法进行使用，也可以在web应用中进行使用
例如：
1、创建WebSocket服务端并开启：
    new WebScoketServer(port, path, WebSocketHandler).start()
2、创建WebSocket客户端并开启：
    new WebScoketClient(url, header, WebSocketHandler).start()
