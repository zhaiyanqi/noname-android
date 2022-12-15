package online.nonamekill.common.data;

public class DataKey<T> {

    private static final int INVALID = -1;
    /** setting球距离左边边距 */
    public static final DataKey<Integer> KEY_SETTING_BUTTON_LEFT = new DataKey<>("key_setting_button_lef", 100);
    /** setting球距离顶部边距 */
    public static final DataKey<Integer> KEY_SETTING_BUTTON_TOP = new DataKey<>("key_setting_button_top", 100);
    /** 游戏目录 */
    public static final DataKey<String> KEY_GAME_PATH = new DataKey<>("key_game_path", "");

    // x5内核开关
    public static final DataKey<Boolean> KEY_IS_X5_CORE = new DataKey<>("key_is_x5_core", false);

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
