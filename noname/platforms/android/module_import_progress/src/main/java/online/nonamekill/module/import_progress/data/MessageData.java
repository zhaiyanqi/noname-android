package online.nonamekill.module.import_progress.data;

import android.text.TextUtils;

public class MessageData {

    public static final int TYPE_NORMAL = 0;
    public static final String TYPE_IP = "#24ED2D";
    public static final String TYPE_ERROR = "#ff2626";
    public static final String TYPE_WARING = "FFFFF426";

    private static final String INFO = "[INFO]";
    private static final String WARING = "[WARING]";
    private static final String ERROR = "[ERROR]";


    private String type = null;
    private String threadDate = null;
    private String message;

    public MessageData(String msg) {
        message = msg;
    }

    public MessageData(String message, String type) {
        this.message = message;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThreadDate() {
        return threadDate;
    }

    public void setThreadDate(String threadDate) {
        this.threadDate = threadDate;
    }

    public String getLogLevelType(){
        if(TextUtils.isEmpty(type)) return "INFO";
        switch (type) {
            case TYPE_ERROR:
                return ERROR;
            case TYPE_WARING:
                return WARING;
            default:
                return INFO;
        }
    }
}
