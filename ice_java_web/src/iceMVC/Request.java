package iceMVC;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Request {
    public String rawData;
    public String path;
    public String method;
    public String body;

    public HashMap<String, String> query;
    public HashMap<String, String> form;
    
    public HashMap<String, String> cookies;

    public Request(String rawRequest) {
        this.rawData = rawRequest;
        String[] parts = rawRequest.split("\r\n\r\n", 2);
        this.body = parts[1];

        String headers = parts[0];
        String[] lines = headers.split("\r\n");
        String requestLine = lines[0];
        String[] requestLineData = requestLine.split(" ");
        this.method = requestLineData[0];

//        log("method: %s", this.method);
        this.parsePath(requestLineData[1]);

        log("query: %s", this.query);
        
        this.parseForm(this.body);

    }
    
    public HashMap<String, String> cookiesFromHeader(HashMap<String, String> headers) {
        HashMap<String, String> cookies = new HashMap<>();
        String cookies_str = headers.get("Cookie");
        String[] cookies_parts = cookies_str.split(";");
        for (int i = 0; i < cookies_parts.length; i++) {
            String part = cookies_parts[i].strip();
//            log("part:<%S>", part);
            String[] kv = part.split("=", 2);
            String k = kv[0];
            String v = kv[1];
//            log("k:<%S>", k);
//            log("v:<%S>", v);
            cookies.put(k, v);
        }
        return cookies;
    }
    
    public HashMap<String, String> headerFromRequest() {
        HashMap<String, String> result = new HashMap<>();

        String[] parts = this.rawData.split("\r\n\r\n", 2);
        String headers = parts[0];

        String[] lines = headers.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] kv = line.split(": ", 2);
            String k = kv[0];
            String v = kv[1];
            result.put(k, v);
        }
//        log("result: %s", result);
        return result;
    }
    public void parsePath(String path) {
        Integer index = path.indexOf("?");
        if (index.equals(-1)) {
            this.path = path;
            this.query = null;
        } else {
            this.path = path.substring(0, index);
            String queryString = path.substring(index + 1);
//            log("queryString: <%s>", queryString);
            String[] args = queryString.split("&");

            this.query = new HashMap<>();
            for (String e:args) {
//                log("e <%s>", e);
                String[] kv = e.split("=", 2);
                String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String v = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                this.query.put(k, v);
            }
        }
    }

    private void parseForm(String body) {
        if (body.strip().length() > 0 ) {
            // URL 反转义
            body = URLDecoder.decode(body, StandardCharsets.UTF_8);
            // 也可以用字符串指定编码
            // body = URLDecoder.decode(body, "UTF-8");
            String[] args = body.split("&");
            this.form = new HashMap<>();
            for (String arg: args) {
                String[] kv = arg.split("=", 2);
                this.form.put(kv[0], kv[1]);
            }
        } else {
            this.form = null;
        }

        log("parsedForm <%s>", this.form);
    }

    private static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

}
