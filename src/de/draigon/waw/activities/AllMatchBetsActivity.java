package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.utils.PrefConstants.*;


public class AllMatchBetsActivity extends Activity {

    private ListView allBets;

    private ArrayAdapter<CharSequence> adapter;
    private Match match;
    private TextView home;
    private TextView homeScore;
    private TextView guest;
    private TextView guestScore;
    private SharedPreferences prefs;
    private static final String TAG = AllMatchBetsActivity.class.getCanonicalName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_match_bets);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.allBets = (ListView) findViewById(R.id.lv_all_match_bets);
        this.match = (Match) getIntent().getSerializableExtra(MATCH);
        this.home = (TextView) findViewById(R.id.t_all_match_bets_home);
        this.guest = (TextView) findViewById(R.id.t_all_match_bets_guest);
        this.homeScore = (TextView) findViewById(R.id.t_all_match_bets_home_score);
        this.guestScore = (TextView) findViewById(R.id.t_all_match_bets_guest_score);
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        for (final CharSequence s : this.match.getBets()) {
            this.adapter.add(s);
            Log.d(TAG, s + "");
        }
        this.allBets.setAdapter(this.adapter);
        refresh();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (this.match.isRunning()) {
            final MenuInflater blowUp = getMenuInflater();
            blowUp.inflate(R.menu.refresh_data, menu);
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                new SingleMatchDownloader().execute(this.match.getId());
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }

    private class SingleMatchDownloader extends AsyncTask<String, Integer, Match> {


        @Override
        protected Match doInBackground(final String... matchId) {
            try {
                return new HttpUtil().getSingleMatch(URI.create(AllMatchBetsActivity.this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)), matchId[0], AllMatchBetsActivity.this.prefs.getString(USERNAME, ""), AllMatchBetsActivity.this.prefs.getString(PASSWORD, ""));
            } catch (ConnectException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Match match) {
            if (match == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            AllMatchBetsActivity.this.match = match;
            refresh();
        }
    }

    private void refresh() {
        this.home.setText(this.match.getHome());
        this.guest.setText(this.match.getGuest());
        if (!this.match.isRunning()) {
            this.homeScore.setText(this.match.getHomeScore());
            this.guestScore.setText(this.match.getGuestScore());
        } else {
            this.homeScore.setText(this.match.getHomeTempScore());
            this.homeScore.setTextColor(Color.YELLOW);
            this.guestScore.setText(this.match.getGuestTempScore());
            this.guestScore.setTextColor(Color.YELLOW);
        }
    }


}