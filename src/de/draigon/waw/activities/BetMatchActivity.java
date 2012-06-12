package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.utils.BetState;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.utils.PrefConstants.*;

/**
 * Allows the betting of a given Match.
 */

public class BetMatchActivity extends Activity {
    private Match match;
    private TextView home;
    private TextView guest;
    private EditText homeScoreBet;
    private EditText guestScoreBet;
    private SharedPreferences prefs;

    public static final String TAG = BetMatchActivity.class.getName();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.bet_match);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.home = (TextView) findViewById(R.id.t_matchdetails_home);
        this.guest = (TextView) findViewById(R.id.t_matchdetails_guest);
        this.homeScoreBet = (EditText) findViewById(R.id.et_matchdetails_bet_home);
        this.guestScoreBet = (EditText) findViewById(R.id.et_matchdetails_bet_guest);
        if (savedInstanceState == null) {
            Log.d(TAG, "setting match to instance sent with intent");
            this.match = (Match) getIntent().getSerializableExtra(MATCH);
            this.homeScoreBet.setText(this.match.getHomeScoreBet());
            this.guestScoreBet.setText(this.match.getGuestScoreBet());
        } else {
            Log.d(TAG, "recreating match instance and temporary bet from savedInstanceState");
            this.match = (Match) savedInstanceState.getSerializable(MATCH);
            this.homeScoreBet.setText(savedInstanceState.getString(HOME_SCORE_BET));
            this.guestScoreBet.setText(savedInstanceState.getString(GUEST_SCORE_BET));
        }
        this.home.setText(this.match.getHome());
        this.guest.setText(this.match.getGuest());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");

    }


    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        Log.d(TAG, "putting match instance and temporary bet into savedInstanceState");
        savedInstanceState.putSerializable(MATCH, this.match);
        savedInstanceState.putString(HOME_SCORE_BET, this.homeScoreBet.getText().toString());
        savedInstanceState.putSerializable(GUEST_SCORE_BET, this.guestScoreBet.getText().toString());
    }

    @SuppressWarnings("unused")
    public void uploadBet(final View view) {
        if ("-".equals(this.homeScoreBet.getText().toString()) || "-".equals(this.guestScoreBet.getText().toString())) {
            Log.w(TAG, "illegal parameter, " + this.homeScoreBet.getText().toString() + ":" +
                    this.guestScoreBet.getText().toString() + " is not a valid result");
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_bet_illegal_parameter),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "trying to upload bet");
        new BetUploader().execute(URI.create(this.prefs.getString(POST_SERVER, DEFAULT_POST_SERVER)));
    }


    private class BetUploader extends AsyncTask<URI, Integer, BetState> {

        @Override
        protected BetState doInBackground(final URI... uris) {
            BetMatchActivity.this.match.setHomeScoreBet(BetMatchActivity.this.homeScoreBet.getText().toString());
            BetMatchActivity.this.match.setGuestScoreBet(BetMatchActivity.this.guestScoreBet.getText().toString());
            try {
                return new HttpUtil().uploadBet(uris[0], BetMatchActivity.this.prefs.getString(USERNAME, ""), BetMatchActivity.this.prefs.getString(PASSWORD, ""), BetMatchActivity.this.match);

            } catch (ConnectException e) {
                return null;
            }

        }


        @Override
        protected void onPostExecute(final BetState betState) {
            if (betState == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                BetMatchActivity.this.finish();
                return;
            }
            final String message;
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
                case ILLEGAL_PARAMETER:
                    message = getResources().getString(R.string.upload_bet_illegal_parameter);
                    break;
                default:
                    throw new IllegalStateException("Illegal BetState " + betState + "recieved");
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            BetMatchActivity.this.finish();

        }
    }

}