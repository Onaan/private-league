package de.draigon.waw.views;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.draigon.waw.MatchDayView;
import de.draigon.waw.R;
import de.draigon.waw.utils.WAWHttpClient;

import java.net.URI;
import java.net.URISyntaxException;

import static de.draigon.waw.utils.PrefConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 05.06.12
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class Ranking extends Activity {
    ListView lv;
    ArrayAdapter<CharSequence> adapter;
    SharedPreferences prefs;


    private MatchDayView v;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        lv = (ListView) findViewById(R.id.lv_ranking);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    public void onResume() {
        super.onResume();
        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);
        try {
            new getRankings().execute(new URI(prefs.getString(GET_SERVER, getResources().getString(R.string.default_get_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private class getRankings extends AsyncTask<URI, Integer, CharSequence[]> {

        @Override
        protected CharSequence[] doInBackground(URI... uris) {
            return new WAWHttpClient().getRankings(uris[0], prefs.getString(USERNAME, ""), prefs.getString(PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(CharSequence[] rankings) {
            adapter.clear();
            for (CharSequence s : rankings) {
                adapter.add(s);
            }

        }
    }
}