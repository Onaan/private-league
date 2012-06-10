package de.draigon.waw.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.utils.PrefConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 10.06.12
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class UpdateAvailableDialog {
    private final AlertDialog dialog;
    private final Activity caller;

    public UpdateAvailableDialog(final Activity caller) {
        this.caller = caller;
        final Resources res = caller.getResources();
        final AlertDialog.Builder builder = new AlertDialog.Builder(caller);
        builder.setMessage(res.getString(R.string.update_dialog_version_available))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        update(PrefConstants.APPLICATION_DOWNLOAD_LINK);
                    }
                })
                .setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        this.dialog = builder.create();

    }

    public void show() {
        this.dialog.show();
    }

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
            file.mkdirs();
            final File outputFile = new File(file, fileName);
            final FileOutputStream fos = new FileOutputStream(outputFile);
            final InputStream is = c.getInputStream();
            final byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();//till here, it works fine - .apk is download to my sdcard in download file
            final Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + fileName)), "application/vnd.android.package-archive");
            this.caller.startActivity(intent);//installation is not working

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.caller.getApplicationContext(), this.caller.getResources().getText(R.string.update_dialog_update_failed), Toast.LENGTH_LONG).show();
        }
    }


}