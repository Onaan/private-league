package de.devtecture.waw.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import de.devtecture.waw.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = Updater.class.getName();
    private final Context context;
// --------------------------- CONSTRUCTORS ---------------------------

    public Updater(final Context context) {
        this.context = context;
    }
// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public void update(final String apkurl) {
        try {
            final URL url = new URL(apkurl);
            final HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            final String PATH = Environment.getExternalStorageDirectory() + "/download/";
            final String[] apkUrlComponents = apkurl.split("/");
            final String fileName = apkUrlComponents[apkUrlComponents.length - 1];
            final File file = new File(PATH);
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
            final File outputFile = new File(file, fileName);
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final InputStream is = c.getInputStream();
            final byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + fileName)), "application/vnd.android.package-archive");
            this.context.startActivity(intent);
        } catch (IOException e) {
            Log.e(TAG, "Error getting data", e);
            Toast.makeText(this.context.getApplicationContext(), this.context.getResources().getText(R.string.update_dialog_update_failed), Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }
    }
}