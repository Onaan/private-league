package de.devtecture.waw.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import de.devtecture.waw.Constants;
import de.devtecture.waw.R;
import de.devtecture.waw.utils.Updater;


public class UpdateAvailableDialog {
// ------------------------------ FIELDS ------------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private static final String TAG = UpdateAvailableDialog.class.getName();
    @SuppressWarnings({"UnusedDeclaration"})
    private final Activity caller;
    private final AlertDialog dialog;
    private final Updater updater;
// --------------------------- CONSTRUCTORS ---------------------------

    public UpdateAvailableDialog(final Activity caller) {
        this.caller = caller;
        this.updater = new Updater(caller);
        final Resources res = caller.getResources();
        final AlertDialog.Builder builder = new AlertDialog.Builder(caller);
        builder.setMessage(res.getString(R.string.update_dialog_version_available))
                .setCancelable(false)
                .setPositiveButton(res.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        UpdateAvailableDialog.this.updater.update(Constants.APPLICATION_DOWNLOAD_LINK);
                    }
                })
                .setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        this.dialog = builder.create();
    }
// -------------------------- OTHER METHODS --------------------------

    public void show() {
        this.dialog.show();
    }
}