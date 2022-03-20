package iceMVC;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utility {
    public static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    public static String html(String htmlName) {
        String dir = "templates";
        String path = dir + "/" + htmlName;

        byte[] body = new byte[1];
        try (FileInputStream is = new FileInputStream(path)) {
            body = is.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String r = new String(body, StandardCharsets.UTF_8);
        return r;
    }

    public static void save(String path, String data) {
        try (FileOutputStream os = new FileOutputStream(path)) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            String s = String.format("Save file <%s> error <%s>", path, e);
            throw new RuntimeException(s);
        }
    }

    public static String load(String path) {
        String content;
        try (FileInputStream is = new FileInputStream(path)) {
            content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            String s = String.format("Load file <%s> error <%s>", path, e);
            throw new RuntimeException(s);
        }
        return content;
    }

    public static void ensure(boolean condition, String message) {
        if (!condition) {
            log("%s", message);
        } else {
            log("测试成功");
        }
    }

    public static void main(String[] args) {
        String file = "a.txt";
//        String data = "test111222";
//        save(file, data);


        String content = load(file);
        Utility.log("content: <%s>", content);



    }
}
