package online.nonamekill.common.data;

import android.content.Context;

import com.tencent.mmkv.MMKV;

public class DataManager {

    private DataManager() {
    }

    private static final class Inner {
        private static final DataManager HOLDER = new DataManager();
    }

    public static DataManager getInstance() {
        return Inner.HOLDER;
    }

    public void initialize(Context context) {
        MMKV.initialize(context);
    }

    public <T> T getValue(DataKey<T> key) {
        Class<?> clazz = key.getClazz();

        if (Integer.class.equals(clazz)) {
            return (T) (Integer) MMKV.defaultMMKV().getInt(key.getKey(), (int) key.getDefaultValue());
        } else if (Boolean.class.equals(clazz)) {
            return (T) (Boolean) MMKV.defaultMMKV().getBoolean(key.getKey(), (boolean) key.getDefaultValue());
        } else if (String.class.equals(clazz)) {
            return (T) (String) MMKV.defaultMMKV().getString(key.getKey(), (String) key.getDefaultValue());
        } else if (Float.class.equals(clazz)) {
            return (T) (Float) MMKV.defaultMMKV().getFloat(key.getKey(), (float) key.getDefaultValue());
        }

        return null;
    }

    public <T> void setValue(DataKey<T> key, T value) {
        Class<?> clazz = key.getClazz();

        if (Integer.class.equals(clazz)) {
            MMKV.defaultMMKV().putInt(key.getKey(), (int) value);
        } else if (Boolean.class.equals(clazz)) {
            MMKV.defaultMMKV().putBoolean(key.getKey(), (boolean) value);
        } else if (String.class.equals(clazz)) {
            MMKV.defaultMMKV().putString(key.getKey(), (String) value);
        } else if (Float.class.equals(clazz)) {
            MMKV.defaultMMKV().putFloat(key.getKey(), (float) value);
        }
    }
}
