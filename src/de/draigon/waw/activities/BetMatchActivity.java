package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.utils.BetState;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;

import static de.draigon.waw.utils.PrefConstants.*;

public class BetMatchActivity extends Activity {
    private Match match;
    private TextView home;
    private TextView guest;
    private EditText homeTip;
    private EditText guestTip;
    private SharedPreferences prefs;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.match = (Match) getIntent().getSerializableExtra("match");
        this.setContentView(R.layout.bet_match);
        this.home = (TextView) findViewById(R.id.t_matchdetails_home);
        this.guest = (TextView) findViewById(R.id.t_matchdetails_guest);
        this.homeTip = (EditText) findViewById(R.id.et_matchdetails_bet_home);
        this.guestTip = (EditText) findViewById(R.id.et_matchdetails_bet_guest);
        this.home.setText(this.match.getHome());
        this.guest.setText(this.match.getGuest());
        this.homeTip.setText(this.match.getHomeScoreBet());
        this.guestTip.setText(this.match.getGuestScoreBet());
    }

    @Override
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


    private class BetUploader extends AsyncTask<URI, Integer, BetState> {

        @Override
        protected BetState doInBackground(final URI... uris) {
            BetMatchActivity.this.match.setHomeScoreBet(BetMatchActivity.this.homeTip.getText().toString());
            BetMatchActivity.this.match.setGuestScoreBet(BetMatchActivity.this.guestTip.getText().toString());
            return new HttpUtil().uploadBet(uris[0], BetMatchActivity.this.prefs.getString(USERNAME, ""), BetMatchActivity.this.prefs.getString(PASSWORD, ""), BetMatchActivity.this.match);

        }


        @Override
        protected void onPostExecute(final BetState betState) {
            String message;
            switch (betState) {
                case OK:
                    message = getResources().getString(R.string.upload_bet_success);
                    break;
                case LATE:
                    message = getResources().getString(R.string.upload_bet_late);
                    break;
                case UNKNOWN_USER:
                    message = getResources().getString(R.string.upload_bet_unknown_user);
                    break;
                default:
                    throw new IllegalStateException("Illegal BetState " + betState + "recieved");
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            BetMatchActivity.this.finish();

        }
    }

}