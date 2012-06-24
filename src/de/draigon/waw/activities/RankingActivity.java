package de.draigon.waw.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.data.User;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.Constants.*;

@SuppressWarnings({"UnusedDeclaration"})
public class RankingActivity extends Activity {
// ------------------------------ FIELDS ------------------------------

    private ArrayAdapter<CharSequence> adapter;
    private CharSequence[] rankings;
    private ListView rankingList;
    private SharedPreferences prefs;
    private ProgressDialog dialog;
    private User user;
// ------------------- LIFECYCLE/CALLBACK METHODS -------------------

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        this.rankingList = (ListView) findViewById(R.id.lv_ranking);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        this.rankingList.setAdapter(this.adapter);
        if (savedInstanceState != null) {
            this.user = (User) savedInstanceState.getSerializable(USER);
            refresh((CharSequence[]) savedInstanceState.get(RANKING));
            refreshTitle();
        } else {
            this.user = (User) getIntent().getSerializableExtra(USER);
            updateRankings();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        savedInstanceState.putSerializable(USER, this.user);
        savedInstanceState.putSerializable(RANKING, this.rankings);
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
                updateRankings();
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }
// -------------------------- OTHER METHODS --------------------------

    private void refresh(final CharSequence[] rankings) {
        this.adapter.clear();
        this.rankings = rankings;
        for (final CharSequence s : rankings) {
            this.adapter.add(s);
        }
    }

    private void refreshTitle() {
        setTitle(this.user.getActiveGroup());
    }

    private void updateRankings() {
        this.dialog = ProgressDialog.show(this, getResources().getString(R.string.progress_title), getResources().getString(R.string.progress_please_wait));
        new getRankings().execute(URI.create(this.prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server))));
    }
// -------------------------- INNER CLASSES --------------------------

    private class getRankings extends AsyncTask<URI, Integer, CharSequence[]> {
        @Override
        protected CharSequence[] doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getRankings(uris[0], RankingActivity.this.user);
            } catch (ConnectException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final CharSequence[] rankings) {
            RankingActivity.this.dialog.dismiss();
            if (rankings == null) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT);
                RankingActivity.this.finish();
                return;
            }
            refresh(rankings);
            refreshTitle();
        }
    }
}