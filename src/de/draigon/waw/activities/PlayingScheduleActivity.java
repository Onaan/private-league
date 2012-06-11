package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import static de.draigon.waw.utils.PrefConstants.*;


public class PlayingScheduleActivity extends Activity implements View.OnClickListener {
    private static final String TAG = PlayingScheduleActivity.class.getCanonicalName();

    private MatchDayLayout matchDayLayout;
    private SharedPreferences prefs;
    private ScrollView scrollView;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.scrollView = new ScrollView(this);
        final ScrollView.LayoutParams lp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        this.scrollView.setLayoutParams(lp);
        this.setContentView(this.scrollView);


    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

    }


    public void onClick(final View view) {
        final Match match = ((MatchLayout) view).getMatch();
        if (match.isBettable()) {
            final Intent intent = new Intent(this, BetMatchActivity.class);
            intent.putExtra(MATCH, match);
            startActivity(intent);
        } else {
            final Intent intent = new Intent(this, AllMatchBetsActivity.class);
            intent.putExtra(MATCH, match);
            startActivity(intent);
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