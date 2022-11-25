package online.nonamekill.common.data;

public class DataKey<T> {

    private static final int INVALID = -1;

    public static final DataKey<Integer> KEY_SETTING_BUTTON_LEFT = new DataKey<>("key_setting_button_lef", 100);
    public static final DataKey<Integer> KEY_SETTING_BUTTON_TOP = new DataKey<>("key_setting_button_top", 100);

    private final String key;
    private final T defaultValue;
    private final Class<?> clazz;

    private DataKey(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.clazz = defaultValue.getClass();
    }

    public String getKey() {
        return key;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public T getDefaultValue() {
        return (T) defaultValue;
    }
}
