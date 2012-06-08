package de.draigon.waw.menues;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import de.draigon.waw.R;
import de.draigon.waw.utils.PrefConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 06.06.12
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public class SetServerDataMenu extends Activity {

    private SharedPreferences prefs;
    private EditText getServer;
    private EditText postServer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_server_data);
        prefs = getSharedPreferences(PrefConstants.PREFS_NAME, MODE_PRIVATE);
        getServer = (EditText) findViewById(R.id.et_set_server_data_get_server);
        postServer = (EditText) findViewById(R.id.et_set_server_data_post_server);

        getServer.setText(prefs.getString(PrefConstants.GET_SERVER, getResources().getString(R.string.default_get_server)));
        postServer.setText(prefs.getString(PrefConstants.POST_SERVER, getResources().getString(R.string.default_post_server)));

    }


    public void saveServerData(View view) {

        SharedPreferences.Editor e = prefs.edit();
        e.putString(PrefConstants.GET_SERVER, getServer.getText().toString());
        e.putString(PrefConstants.POST_SERVER, postServer.getText().toString());
        this.finish();

    }
}
