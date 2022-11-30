package online.nonamekill.android.module.server.function;

public class MessageData {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_IP = 1;

    private int type = 0;
    private String date = null;
    private String message = null;

    public MessageData(String msg) {
        message = msg;
    }

    public MessageData(String message, int type) {
        this.message = message;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
