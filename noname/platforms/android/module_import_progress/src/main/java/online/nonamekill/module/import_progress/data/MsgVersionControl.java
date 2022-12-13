package online.nonamekill.module.import_progress.data;

import android.net.Uri;

public class MsgVersionControl {
    public static final String MSG_TYPE_EXTRA_CURRENT = "覆盖当前版本";
    public static final String MSG_TYPE_EXTRA_INTERNAL = "新增版本,私有目录,速度极快";
    public static final String MSG_TYPE_EXTRA_EXTERNAL_DOCUMENT = "新增版本,Android同级目录,速度极慢,数据会一直保留(推荐安卓13使用)";
    public static final String MSG_TYPE_EXTRA_EXTENSION = "作为扩展导入";

    private int msgType = 0;
    private Uri uri;

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
