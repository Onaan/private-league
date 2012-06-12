package de.draigon.waw.activities;

import android.app.Activity;
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
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.utils.PrefConstants.*;

public class RankingActivity extends Activity {
    private ListView rankingList;
    private ArrayAdapter<CharSequence> adapter;
    private SharedPreferences prefs;
    private CharSequence[] rankings;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        this.rankingList = (ListView) findViewById(R.id.lv_ranking);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        this.rankingList.setAdapter(this.adapter);
        if (savedInstanceState != null) {
            refresh((CharSequence[]) savedInstanceState.get(RANKING));
        } else {
            updateRankings();
        }

    }

    private void updateRankings() {
        new getRankings().execute(URI.create(this.prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server))));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater blowUp = getMenuInflater();
        blowUp.inflate(R.menu.refresh_data, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh_data:
                updateRankings();
                break;
            default:
                throw new IllegalArgumentException(item.getItemId() + "");
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putSerializable(RANKING, this.rankings);
    }

    private class getRankings extends AsyncTask<URI, Integer, CharSequence[]> {

        @Override
        protected CharSequence[] doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getRankings(uris[0], RankingActivity.this.prefs.getString(USERNAME, ""), RankingActivity.this.prefs.getString(PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
            } catch (ConnectException e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(final CharSequence[] rankings) {
            if (rankings == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
                RankingActivity.this.finish();
                return;
            }
            refresh(rankings);

        }


    }

    private void refresh(final CharSequence[] rankings) {
        this.adapter.clear();
        this.rankings = rankings;
        for (final CharSequence s : rankings) {
            this.adapter.add(s);
        }
    }
}