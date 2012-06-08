package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.draigon.waw.R;
import de.draigon.waw.utils.HttpUtil;

import java.net.URI;
import java.net.URISyntaxException;

import static de.draigon.waw.utils.PrefConstants.*;

public class RankingActivity extends Activity {
    private ListView rankingList;
    private ArrayAdapter<CharSequence> adapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        this.rankingList = (ListView) findViewById(R.id.lv_ranking);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        this.rankingList.setAdapter(this.adapter);
        try {
            new getRankings().execute(new URI(this.prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server))));
        } catch (URISyntaxException e) {
            throw new IllegalStateException(this.prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server)) + "is not  a valid URI", e);
        }
    }

    private class getRankings extends AsyncTask<URI, Integer, CharSequence[]> {

        @Override
        protected CharSequence[] doInBackground(final URI... uris) {
            return new HttpUtil().getRankings(uris[0], RankingActivity.this.prefs.getString(USERNAME, ""), RankingActivity.this.prefs.getString(PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(final CharSequence[] rankings) {
            RankingActivity.this.adapter.clear();
            for (final CharSequence s : rankings) {
                RankingActivity.this.adapter.add(s);
            }

        }
    }
}