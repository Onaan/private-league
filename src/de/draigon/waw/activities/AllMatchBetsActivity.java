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


/**
 * Shows the score (temporary or final) of the match given as Intent-extra with the key
 * {@link de.draigon.waw.utils.PrefConstants}.MATCH.  Additionally the bets of all participants are shown.
 */
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


    /*
    Set up the view and refresh data
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, " onCreate called");
        setContentView(R.layout.all_match_bets);
        if (savedInstanceState == null) {
            Log.d(TAG, "setting match to instance sent with intent");
            this.match = (Match) getIntent().getSerializableExtra(MATCH);
        } else {
            Log.d(TAG, "recreating match instance from savedInstanceState");
            this.match = (Match) savedInstanceState.getSerializable(MATCH);
        }
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.allBets = (ListView) findViewById(R.id.lv_all_match_bets);
        this.home = (TextView) findViewById(R.id.t_all_match_bets_home);
        this.guest = (TextView) findViewById(R.id.t_all_match_bets_guest);
        this.homeScore = (TextView) findViewById(R.id.t_all_match_bets_home_score);
        this.guestScore = (TextView) findViewById(R.id.t_all_match_bets_guest_score);
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        for (final CharSequence s : this.match.getBets()) {
            this.adapter.add(s);
            Log.d(TAG, "bet " + s + " added");
        }
        this.allBets.setAdapter(this.adapter);
        refresh();

    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        Log.d(TAG, "putting match instance into savedInstanceState");
        savedInstanceState.putSerializable(MATCH, this.match);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, " onResume called");
    }


    /*
   As the only menu option is refresh, it's only accessible during a running match
    */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        Log.v(TAG, " onCreateOptionsMenu called");
        if (this.match.isRunning()) {
            final MenuInflater blowUp = getMenuInflater();
            blowUp.inflate(R.menu.refresh_data, menu);
            Log.v(TAG, " match is running, showing menu");
            return true;
        }
        Log.v(TAG, " match is not running, not showing menu");
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Log.v(TAG, " menu option with id " + item.getItemId() + "selected");
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                new SingleMatchDownloader().execute(this.match.getId());
                break;
            default:
                throw new IllegalArgumentException("Unknown menu option selected: " +
                        item.getItemId() + " expected R.id.R.id.menu_refresh_data: " +
                        R.id.menu_refresh_data);
        }
        return true;
    }

    /*
    Async task, getting a single match. Only called when refreshing the view.
     */
    private class SingleMatchDownloader extends AsyncTask<String, Integer, Match> {
        private final String TAG = SingleMatchDownloader.class.getName();

        /**
         * Fetches a single match asynchronously from {@link de.draigon.waw.utils.PrefConstants}.GET_SERVER.
         *
         * @param matchId id of the match to fetch. Only the first argument is used
         * @return the match to fetch, or null if the match is not found, the parameter is null, or a ConnectException occurs while connecting to the server.
         * @throws NullPointerException if the first argument is null
         */
        @Override
        protected Match doInBackground(final String... matchId) {
            if (matchId == null) {
                Log.e(this.TAG, "parameter matchId is null, this should not happen. Returning null");
                return null;
            }
            Log.v(this.TAG, "trying to update Match with Id: " + matchId[0]);
            try {
                final Match result = new HttpUtil().getSingleMatch(
                        URI.create(AllMatchBetsActivity.this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)),
                        matchId[0],
                        AllMatchBetsActivity.this.prefs.getString(USERNAME, ""),
                        AllMatchBetsActivity.this.prefs.getString(PASSWORD, ""));
                if (result != null) {
                    Log.v(this.TAG, "returning Match with Id: " + result.getId());
                } else {
                    Log.w(this.TAG, "returning null Match, requested matchId: " + matchId[0]);
                }
                return result;
            } catch (ConnectException e) {
                return null;
            }
        }

        /**
         * Updates the ui-thread to show the given match. Displays a {@link R.string}.no_internet_connection
         * toast message if the parameter is null.
         *
         * @param match the Match to display
         */
        @Override
        protected void onPostExecute(final Match match) {
            if (match == null) {
                Log.w(this.TAG, "Match to display is null, most likely there is no internet connection or the server " +
                        "is down. Displaying R.string.no_internet_connection message");
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            AllMatchBetsActivity.this.match = match;
            refresh();
        }
    }

    /**
     * Updates the ui-thread to show the current match.
     */
    private void refresh() {
        Log.v(TAG, "refreshing UI, match is running: " + this.match.isRunning());
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