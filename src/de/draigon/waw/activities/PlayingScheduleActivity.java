package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.layouts.MatchDayLayout;
import de.draigon.waw.layouts.MatchLayout;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;
import java.util.List;

import static de.draigon.waw.Constants.*;


public class PlayingScheduleActivity extends Activity implements View.OnClickListener {
    private static final String TAG = PlayingScheduleActivity.class.getCanonicalName();

    private MatchDayLayout matchDayLayout;
    private SharedPreferences prefs;
    private ScrollView scrollView;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.scrollView = new ScrollView(this);
        final ScrollView.LayoutParams scrollViewLayoutParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        this.scrollView.setLayoutParams(scrollViewLayoutParams);
        this.setContentView(this.scrollView);
        if (savedInstanceState == null) {
            refresh();
        } else {
            Log.d(TAG, "recreating playing schedule from savedInstanceState");
            //noinspection unchecked
            this.matchDayLayout = new MatchDayLayout(this, this, (List<MatchDay>) savedInstanceState.getSerializable(MATCH_DAYS));
            this.scrollView.removeAllViews();
            this.scrollView.addView(this.matchDayLayout);
        }


    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        if (this.matchDayLayout.getMatchDays() != null) {
            savedInstanceState.putSerializable(MATCH_DAYS, this.matchDayLayout.getMatchDays());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");


    }


    public void onClick(final View view) {
        final Match match = ((MatchLayout) view).getMatch();
        final Intent intent;
        if (match.isBettable()) {
            intent = new Intent(this, BetMatchActivity.class);
        } else {
            intent = new Intent(this, AllMatchBetsActivity.class);
        }
        intent.putExtra(MATCH, match);
        startActivityForResult(intent, REQUEST_MATCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_MATCH) {
            if (resultCode == RESULT_OK) {
                Match match = (Match) intent.getSerializableExtra(MATCH);
                Log.d(TAG, "recieved result, switching match with id: " + match.getId());
                matchDayLayout.getMatchDays().updateMatch(match);
                matchDayLayout.refresh();
                Log.v(TAG, "finished refreshing of matchDayLayout");
                this.scrollView.removeAllViews();
                this.scrollView.addView(this.matchDayLayout);
            }

        } else {
            Log.wtf(TAG, "unknown request code returned to onActivityResult, " +
                    "expected: REQUEST_MATCH, recieved: " + requestCode);
        }

    }


    private class PlayingScheduleDownloader extends AsyncTask<URI, Integer, List<MatchDay>> {

        @Override
        protected List<MatchDay> doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getPlayingSchedule(uris[0], PlayingScheduleActivity.this.prefs.getString(USERNAME, ""), PlayingScheduleActivity.this.prefs.getString(PASSWORD, ""));
            } catch (ConnectException e) {
                return null;
            }

        }


        @Override
        protected void onPostExecute(final List<MatchDay> matchDays) {
            if (matchDays == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                PlayingScheduleActivity.this.finish();
                return;
            }
            PlayingScheduleActivity.this.matchDayLayout = new MatchDayLayout(PlayingScheduleActivity.this, PlayingScheduleActivity.this, matchDays);
            PlayingScheduleActivity.this.scrollView.removeAllViews();
            PlayingScheduleActivity.this.scrollView.addView(PlayingScheduleActivity.this.matchDayLayout);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.refresh_data, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                refresh();
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }

    private void refresh() {
        new PlayingScheduleDownloader().execute(URI.create(this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)));

    }
}