package iceMVC.routes;

import iceMVC.Request;
import iceMVC.Utility;
import iceMVC.models.Message;
import iceMVC.models.MessageService;
import static iceMVC.Utility.log;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Route {
    public static byte[] routeIndex() {
        String body = Utility.html("index.html");
        String response = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes();
    }
    public static String messageListHtml(ArrayList<Message> list) {
        StringBuilder sb = new StringBuilder();
        for (Message m:list) {
            String s = String.format("%s: %s", m.author, m.message);
            sb.append(s);
            sb.append("<br>");
        }
        return sb.toString();
    }

    public static byte[] routeMessage(Request request) {
        Utility.log("routeMessage in");
        HashMap<String, String> data;
        if (request.method.equals("POST")) {
            Utility.log("POST");
            data = request.form;
        } else if (request.method.equals("GET")) {
            Utility.log("GET");
            data = request.query;
        } else {
            String m = String.format("Unknown method <%s>", request.method);
            throw new RuntimeException(m);
        }

        Utility.log("request data: <%s>", data);

        if (data != null) {
            MessageService.add(data);
        }

        String header = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n";
        String body = Utility.html("html_basic.html");

        ArrayList<Message> messageList = MessageService.load();
        body = body.replace("{messages}", messageListHtml(messageList));
        String response = header + body;
        return response.getBytes();
    }

    public static byte[] routeMessage1() {
        String header = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n";
        String body = Utility.html("html_basic.html");
        String response = header + body;
        return response.getBytes();
    }

    public static byte[] routeLogin() {
        String body = Utility.html("login.html");
        String response = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes();
    }

    public static byte[] routeImage(String filepath) {
        String dir = "static";
        String path = dir + "/" + filepath;
        // body
        String header = "HTTP/1.1 200 very OK\r\nContent-Type: image/gif;\r\n\r\n";
        byte[] body = new byte[1];
        try (FileInputStream is = new FileInputStream(path)) {
            body = is.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream response = new ByteArrayOutputStream();
        try {
            response.write(header.getBytes());
            response.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toByteArray();
    }

    public static byte[] route404() {
        String body = "<html><body><h1>404</h1><br><img src='/doge2.gif'></body></html>";
        String response = "HTTP/1.1 404 NOT OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes();
    }

    public static byte[] routeDemo1() {
        String body = "demo1";
        String header = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n";
        String response = header + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] routeDemo2() {
        String body = "demo2";
        String header = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n";
        String response = header + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] routeDemo3(Request request) {
        String m = request.method;
        String body = String.format("this is %s request", m);
        String header = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n";
        String response = header + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] routeDemo4(Request request) {
        String body = Utility.html("demo4.html");
        String response = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] routeDemo4Get(Request request) {
        log("query : <%S>", request.query);
        String v = request.query.get("demo_input1");
        log("v : <%S>", v);
        String body = Utility.html("demo4.html") + v;
        String response = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] routeDemo4Post(Request request) {
//        log("in routeDemo4Post");
        String v = request.form.get("demo_input2");
//        log("v : <%S>", v);
        String body = Utility.html("demo4.html") + v;
        String response = "HTTP/1.1 200 very OK\r\nContent-Type: text/html;\r\n\r\n" + body;
        return response.getBytes(StandardCharsets.UTF_8);
    }
}
