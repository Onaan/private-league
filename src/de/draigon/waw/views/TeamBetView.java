package de.draigon.waw.views;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.draigon.waw.R;
import de.draigon.waw.TeamBetData;
import de.draigon.waw.utils.PrefConstants;
import de.draigon.waw.utils.WAWHttpClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 07.06.12
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */

public class TeamBetView extends Activity implements AdapterView.OnItemClickListener {

    private ListView lv;
    private SharedPreferences prefs;
    private ArrayAdapter<CharSequence> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teambet);
        lv = (ListView) findViewById(R.id.lv_teambet);
        prefs = getSharedPreferences(PrefConstants.PREFS_NAME, MODE_PRIVATE);
        lv.setOnItemClickListener(this);

    }

    public void onResume() {
        super.onResume();
        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);
        try {
            new getTeambet().execute(new URI(prefs.getString(PrefConstants.GET_SERVER, getResources().getString(R.string.default_get_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    private class getTeambet extends AsyncTask<URI, Integer, TeamBetData> {

        @Override
        protected TeamBetData doInBackground(URI... uris) {
            return new WAWHttpClient().getTeamBetData(uris[0], prefs.getString(PrefConstants.USERNAME, ""), prefs.getString(PrefConstants.PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(TeamBetData teams) {
            adapter.clear();

            for (CharSequence s : teams.getChoices()) {
                adapter.add(s);
                if (s.equals(teams.getSelected())) {
                    lv.setSelection(adapter.getCount() - 1);
                }
            }


        }
    }
}