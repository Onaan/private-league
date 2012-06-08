package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import de.draigon.waw.R;
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.layouts.MatchDayLayout;
import de.draigon.waw.layouts.MatchLayout;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static de.draigon.waw.utils.PrefConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 05.06.12
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class PlayingScheduleActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "de.draigon.waw.activities.PlayingScheduleActivity";

    private MatchDayLayout v;
    private SharedPreferences prefs;
    private ScrollView sv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        sv = new ScrollView(this);
        ScrollView.LayoutParams lp = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        sv.setLayoutParams(lp);
        this.setContentView(sv);


    }

    public void onResume() {
        super.onResume();
        try {
            new PlayingScheduleDownloader().execute(new URI(prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    public void onClick(View view) {
        final Intent intent = new Intent(this, MatchDetailsActivity.class);
        intent.putExtra("match", ((MatchLayout) view).getMatch());
        startActivity(intent);
    }


    private class PlayingScheduleDownloader extends AsyncTask<URI, Integer, List<MatchDay>> {

        @Override
        protected List<MatchDay> doInBackground(URI... uris) {
            return new HttpUtil().getPlayingSchedule(uris[0], prefs.getString(USERNAME, ""), prefs.getString(PASSWORD, ""));

        }


        @Override
        protected void onPostExecute(List<MatchDay> spieltage) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            v = new MatchDayLayout(PlayingScheduleActivity.this, PlayingScheduleActivity.this, spieltage);
            sv.removeAllViews();
            sv.addView(v);
        }
    }
}