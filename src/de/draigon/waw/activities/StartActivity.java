package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import de.draigon.waw.R;
import de.draigon.waw.dialogs.UpdateAvailableDialog;
import de.draigon.waw.tasks.GroupChecker;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.Constants.*;


@SuppressWarnings({"UnusedDeclaration"})
public class StartActivity extends Activity {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = StartActivity.class.getName();
    private Button playingScheduleButton;
    private Button rankingButton;
    private Button teamBetButton;
    private SharedPreferences prefs;
// ------------------- LIFECYCLE/CALLBACK METHODS -------------------

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, TAG + ".onCreate() called");
        setContentView(R.layout.start_activity);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.playingScheduleButton = (Button) findViewById(R.id.b_startActivity_playing_schedule);
        this.rankingButton = (Button) findViewById(R.id.b_startActivity_ranking);
        this.teamBetButton = (Button) findViewById(R.id.b_startActivity_special_bet);
        new UpdateChecker().execute(URI.create(this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)));
        new GroupChecker(this).execute(URI.create(this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.prefs.getString(USERNAME, "").length() > 0 && this.prefs.getString(PASSWORD, "").length() > 0) {
            this.rankingButton.setEnabled(true);
            this.playingScheduleButton.setEnabled(true);
            //TODO: funktionsfaehig machen
            this.teamBetButton.setEnabled(false);
        } else {
            this.rankingButton.setEnabled(false);
            this.playingScheduleButton.setEnabled(false);
            this.teamBetButton.setEnabled(false);
        }
    }
// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"unused", "UnusedParameters", "UnusedDeclaration"})
    public void goToPlayingSchedule(final View view) {
        final Intent intent = new Intent(this, FixtureActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings({"unused", "UnusedParameters", "UnusedDeclaration"})
    public void goToRanking(final View view) {
        final Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings({"unused", "UnusedParameters", "UnusedDeclaration"})
    public void goToSetLoginData(final View view) {
        final Intent intent = new Intent(this, SetLoginDataActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings({"unused", "UnusedParameters", "UnusedDeclaration"})
    public void goToTeamBet(final View view) {
        final Intent intent = new Intent(this, TeamBetActivity.class);
        startActivity(intent);
    }
// -------------------------- INNER CLASSES --------------------------

    private class UpdateChecker extends AsyncTask<URI, Integer, String> {
        @Override
        protected String doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getServerAppVersion(uris[0]);
            } catch (ConnectException e) {
                Log.e(TAG, "error determining server version", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String serverVersion) {
            String app_ver = "";
            try {
                app_ver = StartActivity.this.getPackageManager().getPackageInfo(StartActivity.this.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.wtf(TAG, "this can not happen", e);
            }
            Log.d(TAG, "Installed version :" + app_ver);
            Log.d(TAG, "Version on server: " + serverVersion);
            if (app_ver != null && app_ver.compareTo(serverVersion) < 0) {
                new UpdateAvailableDialog(StartActivity.this).show();
            }
        }
    }
}