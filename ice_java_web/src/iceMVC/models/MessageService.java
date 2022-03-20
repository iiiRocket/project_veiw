package iceMVC.models;

import java.util.ArrayList;
import java.util.HashMap;
import iceMVC.Utility;

public class MessageService {
    static public void save(ArrayList<Message> list) {
        StringBuilder all = new StringBuilder();

        for (Message m:list) {
            StringBuilder s = new StringBuilder();
            s.append(m.author);
            s.append("\n");
            s.append(m.message);
            s.append("\n");

            all.append(s);
        }

        Utility.log("save all <%s>", all);
//        String filename = "Message.txt";
        String filename = Message.class.getSimpleName() + ".txt";
        Utility.save(filename, all.toString());;
    }

    static public ArrayList<Message> load() {
        String filename = Message.class.getSimpleName() + ".txt";
        String data = Utility.load(filename);
        String[] lines = data.split("\n");

        ArrayList<Message> all = new ArrayList<>();

        for (int i = 0; i < lines.length; i = i + 2) {
            // i = 0, i + 1 = 1;
            // i = 2, i + 1 = 3;
            // i = 4, i + 1 = 5;
            String author = lines[i];
            String message = lines[i + 1];

            Message m = new Message();
            m.author = author;
            m.message = message;

            all.add(m);
        }
        return all;
    }

    static public void add(HashMap<String, String> form) {
        String author = form.get("author");
        String message = form.get("message");
        Message m = new Message();
        m.author = author;
        m.message = message;

        ArrayList<Message> messageList = load();
        messageList.add(m);
        MessageService.save(messageList);
    }
}
