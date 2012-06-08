package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;

import static de.draigon.waw.utils.PrefConstants.*;

public class MatchDetailsActivity extends Activity {
    private Match match;
    private TextView home;
    private TextView guest;
    private EditText homeTip;
    private EditText guestTip;
    private SharedPreferences prefs;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.match = (Match) getIntent().getSerializableExtra("match");
        setContentView(R.layout.matchdetails);
        this.home = (TextView) findViewById(R.id.t_matchdetails_home);
        this.guest = (TextView) findViewById(R.id.t_matchdetails_guest);
        this.homeTip = (EditText) findViewById(R.id.et_matchdetails_bet_home);
        this.guestTip = (EditText) findViewById(R.id.et_matchdetails_bet_guest);
        this.home.setText(this.match.getHomeTeam());
        this.guest.setText(this.match.getGuestTeam());
        this.homeTip.setText(this.match.getHomeScoreTip());
        this.guestTip.setText(this.match.getGuestScoreTip());
    }

    public void onResume() {
        super.onResume();

    }

    @SuppressWarnings("unused")
    public void uploadBet(final View view) {
        try {
            new BetUploader().execute(new URI(this.prefs.getString(POST_SERVER, getResources().getString(R.string.default_post_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.


        }

    }


    private class BetUploader extends AsyncTask<URI, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(final URI... uris) {
            MatchDetailsActivity.this.match.setHomeScoreTip(MatchDetailsActivity.this.homeTip.getText().toString());
            MatchDetailsActivity.this.match.setGuestScoreTip(MatchDetailsActivity.this.guestTip.getText().toString());
            return new HttpUtil().uploadBet(uris[0], MatchDetailsActivity.this.prefs.getString(USERNAME, ""), MatchDetailsActivity.this.prefs.getString(PASSWORD, ""), MatchDetailsActivity.this.match);

        }


        @Override
        protected void onPostExecute(final Boolean success) {
            MatchDetailsActivity.this.finish();

        }
    }

}