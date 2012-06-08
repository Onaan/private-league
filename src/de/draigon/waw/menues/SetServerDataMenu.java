package de.draigon.waw.menues;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import de.draigon.waw.R;
import de.draigon.waw.utils.PrefConstants;

public class SetServerDataMenu extends Activity {

    private SharedPreferences prefs;
    private EditText getServer;
    private EditText postServer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_server_data);
        this.prefs = getSharedPreferences(PrefConstants.PREFS_NAME, MODE_PRIVATE);
        this.getServer = (EditText) findViewById(R.id.et_set_server_data_get_server);
        this.postServer = (EditText) findViewById(R.id.et_set_server_data_post_server);
        this.getServer.setText(this.prefs.getString(PrefConstants.GET_SERVER, getResources().getString(R.string.default_get_server)));
        this.postServer.setText(this.prefs.getString(PrefConstants.POST_SERVER, getResources().getString(R.string.default_post_server)));

    }


    public void saveServerData(final View view) {
        final SharedPreferences.Editor e = this.prefs.edit();
        e.putString(PrefConstants.GET_SERVER, this.getServer.getText().toString());
        e.putString(PrefConstants.POST_SERVER, this.postServer.getText().toString());
        this.finish();

    }
}
