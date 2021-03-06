package de.draigon.waw.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import de.draigon.waw.data.User;
import de.draigon.waw.layouts.MatchDayLayout;
import de.draigon.waw.layouts.MatchLayout;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;
import java.util.List;

import static de.draigon.waw.Constants.*;


@SuppressWarnings({"UnusedDeclaration"})
public class FixtureActivity extends Activity implements View.OnClickListener {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = FixtureActivity.class.getCanonicalName();

    private MatchDayLayout matchDayLayout;
    private ScrollView scrollView;
    private SharedPreferences prefs;
    private ProgressDialog dialog;
    private User user;
// ------------------------ INTERFACE METHODS ------------------------
// --------------------- Interface OnClickListener ---------------------

    @Override
    public void onClick(final View view) {
        final Match match = ((MatchLayout) view).getMatch();
        final Intent intent;
        if (match.isBettable()) {
            intent = new Intent(this, BetMatchActivity.class);
        } else {
            intent = new Intent(this, AllMatchBetsActivity.class);
        }
        intent.putExtra(MATCH, match);
        intent.putExtra(USER, this.user);
        startActivityForResult(intent, REQUEST_MATCH);
    }
// ------------------- LIFECYCLE/CALLBACK METHODS -------------------

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
            this.user = (User) getIntent().getSerializableExtra(USER);
            refresh();
        } else {
            Log.d(TAG, "recreating playing schedule from savedInstanceState");
            //noinspection unchecked
            this.user = (User) savedInstanceState.getSerializable(USER);
            this.matchDayLayout = new MatchDayLayout(this, this, (List<MatchDay>) savedInstanceState.getSerializable(MATCH_DAYS));
            if (this.matchDayLayout.getMatchDays() != null) { //TODO: herausfinden warum das hier manchmal null ist, kann in der theorie nicht vorkommen... Das Problem tritt auf, wenn man nach dem starten das tel schnell dreht.
                this.scrollView.removeAllViews();
                this.scrollView.addView(this.matchDayLayout);
                refreshTitle();
            } else {
                refresh();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");
    }

    @Override
    public void onPause() {
        if (this.dialog != null) {
            this.dialog.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        if (this.matchDayLayout != null && this.matchDayLayout.getMatchDays() != null) {
            savedInstanceState.putSerializable(MATCH_DAYS, this.matchDayLayout.getMatchDays());
        }
        savedInstanceState.putSerializable(USER, this.user);
    }

    @Override
    public void onActivityResult
            (
                    final int requestCode,
                    final int resultCode,
                    final Intent intent) {
        if (requestCode == REQUEST_MATCH) {
            if (resultCode == RESULT_OK) {
                final Match match = (Match) intent.getSerializableExtra(MATCH);
                this.user = (User) intent.getSerializableExtra(USER);
                Log.d(TAG, "recieved result, switching match with id: " + match.getId());
                this.matchDayLayout.getMatchDays().updateMatch(match);
                this.matchDayLayout.refresh();
                Log.v(TAG, "finished refreshing of matchDayLayout");
                this.scrollView.removeAllViews();
                this.scrollView.addView(this.matchDayLayout);
            }
        } else {
            Log.wtf(TAG, "unknown request code returned to onActivityResult, " +
                    "expected: REQUEST_MATCH, recieved: " + requestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.refresh_data, menu);
        if (this.user.getNumberOfGroups() < 2) {
            menu.removeItem(R.id.menu_switch_group_multi);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_switch_group_multi:
                final String newGroup = this.user.switchGroup();
                Toast.makeText(this, getResources().getString(R.string.group_switched_to) + " " + newGroup, Toast.LENGTH_SHORT).show();
            case R.id.menu_refresh_data:
                refresh();
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }
// -------------------------- OTHER METHODS --------------------------

    private void refresh() {
        this.dialog = ProgressDialog.show(this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_please_wait));
        new PlayingScheduleDownloader().execute(URI.create(this.prefs.getString(GET_SERVER, DEFAULT_GET_SERVER)));
    }

    private void refreshTitle() {
        setTitle(this.user.getActiveGroup());
    }

    private void renameActivity() {
        setTitle(this.user.getActiveGroup());
    }
// -------------------------- INNER CLASSES --------------------------

    private class PlayingScheduleDownloader extends AsyncTask<URI, Integer, List<MatchDay>> {
        @Override
        protected List<MatchDay> doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getPlayingSchedule(uris[0], FixtureActivity.this.user);
            } catch (ConnectException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final List<MatchDay> matchDays) {
            if (matchDays == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                FixtureActivity.this.finish();
                return;
            }
            FixtureActivity.this.matchDayLayout = new MatchDayLayout(FixtureActivity.this, FixtureActivity.this, matchDays);
            FixtureActivity.this.scrollView.removeAllViews();
            FixtureActivity.this.scrollView.addView(FixtureActivity.this.matchDayLayout);
            renameActivity();
            synchronized (FixtureActivity.this) {
                FixtureActivity.this.dialog.dismiss();
            }
        }
    }
}