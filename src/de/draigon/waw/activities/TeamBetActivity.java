package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.draigon.waw.R;
import de.draigon.waw.data.TeamBet;
import de.draigon.waw.utils.HttpUtil;
import de.draigon.waw.utils.PrefConstants;

import java.net.URI;
import java.net.URISyntaxException;


public class TeamBetActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView lv;
    private SharedPreferences prefs;
    private ArrayAdapter<CharSequence> adapter;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teambet);
        this.lv = (ListView) findViewById(R.id.lv_teambet);
        this.prefs = getSharedPreferences(PrefConstants.PREFS_NAME, MODE_PRIVATE);
        this.lv.setOnItemClickListener(this);

    }

    public void onResume() {
        super.onResume();
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        this.lv.setAdapter(this.adapter);
        try {
            new getTeambet().execute(new URI(this.prefs.getString(PrefConstants.GET_SERVER, getResources().getString(R.string.default_get_server))));
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
    }

    private class getTeambet extends AsyncTask<URI, Integer, TeamBet> {

        @Override
        protected TeamBet doInBackground(final URI... uris) {
            return new HttpUtil().getTeamBetData(uris[0], TeamBetActivity.this.prefs.getString(PrefConstants.USERNAME, ""), TeamBetActivity.this.prefs.getString(PrefConstants.PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected void onPostExecute(final TeamBet teams) {
            TeamBetActivity.this.adapter.clear();
            for (final CharSequence s : teams.getChoices()) {
                TeamBetActivity.this.adapter.add(s);
                if (s.equals(teams.getSelected())) {
                    TeamBetActivity.this.lv.setSelection(TeamBetActivity.this.adapter.getCount() - 1);
                }
            }


        }
    }
}