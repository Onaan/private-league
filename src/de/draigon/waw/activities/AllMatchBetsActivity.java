package de.draigon.waw.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;

import static de.draigon.waw.utils.PrefConstants.MATCH;


public class AllMatchBetsActivity extends Activity {

    private ListView allBets;

    private ArrayAdapter<CharSequence> adapter;
    private Match match;
    private TextView home;
    private TextView homeScore;
    private TextView guest;
    private TextView guestScore;
    private static final String TAG = AllMatchBetsActivity.class.getCanonicalName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_match_bets);
        this.allBets = (ListView) findViewById(R.id.lv_all_match_bets);
        this.match = (Match) getIntent().getSerializableExtra(MATCH);
        this.home = (TextView) findViewById(R.id.t_all_match_bets_home);
        this.guest = (TextView) findViewById(R.id.t_all_match_bets_guest);
        this.homeScore = (TextView) findViewById(R.id.t_all_match_bets_home_score);
        this.guestScore = (TextView) findViewById(R.id.t_all_match_bets_guest_score);
        this.home.setText(this.match.getHome());
        this.guest.setText(this.match.getGuest());
        if (!"-".equals(this.match.getHomeScore())) {
            this.homeScore.setText(this.match.getHomeScore());
            this.guestScore.setText(this.match.getGuestScore());
        } else {
            this.homeScore.setText(this.match.getHomeTempScore());
            this.homeScore.setTextColor(Color.YELLOW);
            this.guestScore.setText(this.match.getGuestTempScore());
            this.guestScore.setTextColor(Color.YELLOW);
        }
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        for (final CharSequence s : this.match.getBets()) {
            this.adapter.add(s);
            Log.d(TAG, s + "");
        }
        this.allBets.setAdapter(this.adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
    }


}