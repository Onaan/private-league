package de.draigon.waw.activities;

import android.app.Activity;
import android.content.Intent;
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

import static de.draigon.waw.Constants.*;

/**
 * Allows the betting of a given Match.
 */

public class BetMatchActivity extends Activity {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = BetMatchActivity.class.getName();
    private boolean autofocus = true;
    private EditText guestScoreBet;
    private EditText homeScoreBet;
    private Match match;
    private SharedPreferences prefs;
    private TextView guest;
    private TextView home;
// ------------------- LIFECYCLE/CALLBACK METHODS -------------------


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
        this.home.setFocusable(true);
        final EditText.OnClickListener ocl = new EditText.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //TODO: does not work consistently on ICS, looking for better solution
                Log.v(TAG, "selecting all text");
                ((EditText) view).selectAll();
            }
        };
        final EditText.OnFocusChangeListener ofcl = new EditText.OnFocusChangeListener() {


            @Override
            public void onFocusChange(final View view, final boolean hasFocus) {
                if (hasFocus && !BetMatchActivity.this.autofocus) {
                    view.performClick();
                }
                BetMatchActivity.this.autofocus = false;
            }
        };
        this.homeScoreBet.setOnClickListener(ocl);
        this.homeScoreBet.setOnFocusChangeListener(ofcl);
        this.guestScoreBet.setOnClickListener(ocl);
        this.guestScoreBet.setOnFocusChangeListener(ofcl);
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
// -------------------------- OTHER METHODS --------------------------

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
// -------------------------- INNER CLASSES --------------------------

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
            final int result;
            switch (betState) {
                case OK:
                    message = getResources().getString(R.string.upload_bet_success);
                    result = RESULT_OK;
                    break;
                case LATE:
                    message = getResources().getString(R.string.upload_bet_late);
                    result = RESULT_CANCELED;
                    break;
                case UNKNOWN_USER:
                    message = getResources().getString(R.string.upload_bet_unknown_user);
                    result = RESULT_CANCELED;
                    break;
                case ILLEGAL_PARAMETER:
                    message = getResources().getString(R.string.upload_bet_illegal_parameter);
                    result = RESULT_CANCELED;
                    break;
                default:
                    throw new IllegalStateException("Illegal BetState " + betState + "recieved");
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            final Intent data = new Intent();
            data.putExtra(MATCH, BetMatchActivity.this.match);
            setResult(result, data);
            BetMatchActivity.this.finish();
        }
    }
}