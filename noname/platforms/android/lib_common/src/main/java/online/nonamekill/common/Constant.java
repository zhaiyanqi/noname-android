package online.nonamekill.common;

import android.animation.TimeInterpolator;
import android.view.animation.PathInterpolator;

public class Constant {

    public static final String GAME_FOLDER = "resources";
    public static final String GAME_FOLDER_NAME = "game";
    public static final String GAME_FILE = "game.js";

    public static final String ASSET_PACKAGE_NAME = "online.nonamekill.assets";

    public @interface Duration {
        int Alpha = 150;
        int Scale = 150;
    }

    public @interface Interpolator {
        PathInterpolator Alpha = new PathInterpolator(0.33f, 0, 0.67f, 1f);
        PathInterpolator Scale = new PathInterpolator(0.33f, 0, 0.67f, 1f);
        PathInterpolator Translate = new PathInterpolator(0.3f, 0, 0.1f, 1f);


    }
}
