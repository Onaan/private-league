package de.draigon.waw.utils;

import android.graphics.Bitmap;
import android.view.View;

public class Screenshot {
// -------------------------- STATIC METHODS --------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    public static Bitmap takeScreenShot(final View view) {
// image naming and path  to include sd card  appending name you choose for file
        final Bitmap bitmap;
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
