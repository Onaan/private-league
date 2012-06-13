package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.draigon.waw.Constants;
import de.draigon.waw.R;
import de.draigon.waw.data.TeamBet;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;


public class TeamBetActivity extends Activity implements AdapterView.OnItemClickListener {
// ------------------------------ FIELDS ------------------------------

    private ArrayAdapter<CharSequence> adapter;
    private ListView lv;
    private SharedPreferences prefs;
// ------------------------ INTERFACE METHODS ------------------------
// --------------------- Interface OnItemClickListener ---------------------

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, final long l) {
    }
// ------------------- LIFECYCLE/CALLBACK METHODS -------------------

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teambet);
        this.lv = (ListView) findViewById(R.id.lv_teambet);
        this.prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        this.lv.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1);
        this.lv.setAdapter(this.adapter);
        new getTeambet().execute(URI.create(this.prefs.getString(Constants.GET_SERVER, getResources().getString(R.string.default_get_server))));
    }
// -------------------------- INNER CLASSES --------------------------

    private class getTeambet extends AsyncTask<URI, Integer, TeamBet> {
        @Override
        protected TeamBet doInBackground(final URI... uris) {
            try {
                return new HttpUtil().getTeamBetData(uris[0], TeamBetActivity.this.prefs.getString(Constants.USERNAME, ""), TeamBetActivity.this.prefs.getString(Constants.PASSWORD, ""));  //To change body of implemented methods use File | Settings | File Templates.
            } catch (ConnectException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final TeamBet teams) {
            if (teams == null) {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                TeamBetActivity.this.finish();
                return;
            }
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