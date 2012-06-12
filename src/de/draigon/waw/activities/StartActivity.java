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
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.utils.PrefConstants.*;


public class StartActivity extends Activity {
    private static final String TAG = StartActivity.class.getName();
    private SharedPreferences prefs;
    private Button playingScheduleButton;
    private Button rankingButton;
    private Button teamBetButton;

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

    private class UpdateChecker extends AsyncTask<URI, Integer, String> {


        @Override
        protected String doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getServerAppVersion(uris[0]);
            } catch (ConnectException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(final String serverVersion) {
            String app_ver = "";
            try {
                app_ver = StartActivity.this.getPackageManager().getPackageInfo(StartActivity.this.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            Log.d(TAG, "Installed version :" + app_ver);
            Log.d(TAG, "Version on server: " + serverVersion);
            final boolean newVersion = app_ver.compareTo(serverVersion) < 0;
            if (newVersion) {
                new UpdateAvailableDialog(StartActivity.this).show();
            }
        }


    }

    @SuppressWarnings("unused")
    public void goToPlayingSchedule(final View view) {
        final Intent intent = new Intent(this, PlayingScheduleActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void goToSetLoginData(final View view) {
        final Intent intent = new Intent(this, SetLoginDataActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void goToRanking(final View view) {
        final Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void goToTeamBet(final View view) {
        final Intent intent = new Intent(this, TeamBetActivity.class);
        startActivity(intent);
    }
    /*   @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.refresh_data, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.server_location_menu_edit_server_data:
                final Intent intent = new Intent(this, SetServerDataMenu.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }*/


}