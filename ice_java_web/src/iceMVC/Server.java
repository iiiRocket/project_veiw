package iceMVC;

import iceMVC.routes.Route;
import static iceMVC.Utility.log;
import static homework.Homework.*;


import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) {
        run(9000);

//        testHeaderFromRequest();
//        testCookiesFromHeader();
    }

    private static void run(int port) {
        // 监听请求
        // 获取请求数据
        // 发送响应数据
        // 我们的服务器使用 9000 端口
        Utility.log("服务器启动, 访问 http://localhost:%s", port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                // accept 方法会一直停留在这里等待连接
                try (Socket socket = serverSocket.accept()) {
                    // 客户端连接成功
                    Utility.log("client 连接成功");
                    // 读取客户端请求数据
                    String request = SocketOperator.socketReadAll(socket);
                    byte[] response;
                    if (request.length() > 0) {
                        // 输出响应的数据
                        Utility.log("请求:\n%s", request);
                        // 解析 request 得到 path
                        Request r = new Request(request);

                        // 根据 path 来判断要返回什么数据
                        response = responseForPath(r);
                    } else {
                        response = new byte[1];
                        Utility.log("接受到了一个空请求");
                    }
                    SocketOperator.socketSendAll(socket, response);
                }
            }
        } catch (IOException ex) {
            System.out.println("exception: " + ex.getMessage());
        }
    }

    private static byte[] responseForPath(Request request) {
        String path = request.path;
        byte[] response;
        if (path.equals("/")) {
            response = Route.routeIndex();
        } else if (path.equals("/login")) {
            response = Route.routeLogin();
        } else if (path.equals("/message")) {
            response = Route.routeMessage(request);
        } else if (path.equals("/message1")) {
            response = Route.routeMessage1();
        } else if (path.equals("/doge.gif")) {
            response = Route.routeImage("doge.gif");
        } else if (path.equals("/doge2.gif")) {
            response = Route.routeImage("doge2.gif");
        } else if (path.equals("/demo1")) {
            response = Route.routeDemo1();
        } else if (path.equals("/demo2")) {
            response = Route.routeDemo2();
        } else if (path.equals("/demo3")) {
            response = Route.routeDemo3(request);
        } else if (path.equals("/demo4")) {
            response = Route.routeDemo4(request);
        } else if (path.equals("/demo4/get")) {
            response = Route.routeDemo4Get(request);
        } else if (path.equals("/demo4/post")) {
            response = Route.routeDemo4Post(request);
        } else {
            response = Route.route404();
        }

        return response;
    }
}
