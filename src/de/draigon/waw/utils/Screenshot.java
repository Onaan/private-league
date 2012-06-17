package de.draigon.waw.utils;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 14.06.12
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public class Screenshot {
    public static Bitmap takeScreenShot(View view) {
// image naming and path  to include sd card  appending name you choose for file
        Bitmap bitmap;
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
