package iceMVC.models;

public class Message {
    public String author;
    public String message;

    @Override
    public String toString() {
//        Utility.log("message to string");
        String s = String.format(
                "(author: %s, message: %s)",
                this.author,
                this.message
        );
        return s;
    }
}
