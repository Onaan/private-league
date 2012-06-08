package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.draigon.waw.data.Match;
import de.draigon.waw.R;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;

import static de.draigon.waw.utils.PrefConstants.*;


/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 05.06.12
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class MatchDetailsActivity extends Activity {
    private Match match;
    private TextView home;
    private TextView guest;
    private EditText homeTip;
    private EditText guestTip;
    private SharedPreferences prefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        match = (Match) getIntent().getSerializableExtra("match");
        setContentView(R.layout.matchdetails);
        home = (TextView) findViewById(R.id.t_matchdetails_home);
        guest = (TextView) findViewById(R.id.t_matchdetails_guest);
        homeTip = (EditText) findViewById(R.id.et_matchdetails_bet_home);
        guestTip = (EditText) findViewById(R.id.et_matchdetails_bet_guest);
        home.setText(match.getHomeTeam());
        guest.setText(match.getGuestTeam());
        homeTip.setText(match.getHomeScoreTip());
        guestTip.setText(match.getGuestScoreTip());
    }

    public void onResume() {
        super.onResume();

    }

    public void uploadBet(View view) {
        try {
            new BetUploader().execute(new URI(prefs.getString(POST_SERVER, getResources().getString(R.string.default_post_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.


        }

    }


    private class BetUploader extends AsyncTask<URI, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(URI... uris) {
            match.setHomeScoreTip(homeTip.getText().toString());
            match.setGuestScoreTip(guestTip.getText().toString());

            return new HttpUtil().uploadBet(uris[0], prefs.getString(USERNAME, ""), prefs.getString(PASSWORD, ""), match);

        }


        @Override
        protected void onPostExecute(Boolean success) {
            MatchDetailsActivity.this.finish();

        }
    }

}