package online.nonamekill.android.module.icon;

public class IconInfo {

    private int mIconId = 0;
    private String mTagName = "";

    public IconInfo(int icon, String tag) {
        mIconId = icon;
        mTagName = tag;
    }

    public int getIconId() {
        return mIconId;
    }

    public String getTagName() {
        return mTagName;
    }
}
