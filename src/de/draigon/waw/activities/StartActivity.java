package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import de.draigon.waw.R;
import de.draigon.waw.menues.SetServerDataMenu;

import static de.draigon.waw.utils.PrefConstants.*;


/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 04.06.12
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class StartActivity extends Activity {

    private static final String TAG = StartActivity.class.getName();
    private SharedPreferences loginPrefs;
    private Button spielplanButton;
    private Button rankingButton;
    private Button teamBetButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, TAG + ".onCreate() called");
        setContentView(R.layout.start_activity);
        loginPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        spielplanButton = (Button) findViewById(R.id.b_startActivity_playing_schedule);
        rankingButton = (Button) findViewById(R.id.b_startActivity_ranking);
        teamBetButton = (Button) findViewById(R.id.b_startActivity_special_bet);
    }

    public void onResume() {
        super.onResume();
        if (loginPrefs.getString(USERNAME, "").length() > 0 && loginPrefs.getString(PASSWORD, "").length() > 0) {
            rankingButton.setEnabled(true);
            spielplanButton.setEnabled(true);
            //TODO: funktionsfaehig machen
            teamBetButton.setEnabled(false);
        } else {
            rankingButton.setEnabled(false);
            spielplanButton.setEnabled(false);
            teamBetButton.setEnabled(false);
        }

    }


    public void goToPlayingSchedule(final View view) {
        final Intent intent = new Intent(this, PlayingScheduleActivity.class);
        startActivity(intent);
    }

    public void goToSetLoginData(final View view) {
        final Intent intent = new Intent(this, SetLoginDataActivity.class);
        startActivity(intent);
    }

    public void goToRanking(final View view) {
        final Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    public void goToTeamBet(final View v) {
        final Intent intent = new Intent(this, TeamBetActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.server_location, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }


}