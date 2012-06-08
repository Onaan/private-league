package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.layouts.MatchDayLayout;
import de.draigon.waw.layouts.MatchLayout;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;
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
        try {
            new PlayingScheduleDownloader().execute(new URI(this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    public void onClick(final View view) {
        Match match = ((MatchLayout) view).getMatch();
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
            return new HttpUtil().getPlayingSchedule(uris[0], PlayingScheduleActivity.this.prefs.getString(USERNAME, ""), PlayingScheduleActivity.this.prefs.getString(PASSWORD, ""));

        }


        @Override
        protected void onPostExecute(final List<MatchDay> matchDays) {
            PlayingScheduleActivity.this.matchDayLayout = new MatchDayLayout(PlayingScheduleActivity.this, PlayingScheduleActivity.this, matchDays);
            PlayingScheduleActivity.this.scrollView.removeAllViews();
            PlayingScheduleActivity.this.scrollView.addView(PlayingScheduleActivity.this.matchDayLayout);
        }
    }
}