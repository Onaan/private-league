package de.draigon.waw.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import de.draigon.waw.R;
import de.draigon.waw.utils.HttpUtil;

import java.net.ConnectException;
import java.net.URI;

import static de.draigon.waw.Constants.*;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 20.06.12
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class GroupChecker extends AsyncTask<URI, Integer, CharSequence[]> {

    private final Context context;
    private final SharedPreferences prefs;
    private static final String TAG = GroupChecker.class.getName();

    public GroupChecker(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected CharSequence[] doInBackground(URI... uris) {
        if (!prefs.getString(USERNAME, "").equals("") && !prefs.getString(PASSWORD, "").equals("")) {
            try {
                return new HttpUtil().getGroups(uris[0], prefs.getString(USERNAME, ""), prefs.getString(PASSWORD, ""));
            } catch (ConnectException e) {
                Log.d(TAG, "could not determine groups, internet available?", e);

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(CharSequence[] groups) {
        if (groups == null) {
            Toast.makeText(context.getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }
        String groupString = "";
        for (CharSequence group : groups) {
            groupString += group;
            groupString += ",";
        }
        prefs.edit().putString(GROUPS, groupString).commit();
        Log.d(TAG, "groups found: " + groupString);
        Toast.makeText(context.getApplicationContext(), R.string.groups_refreshed, Toast.LENGTH_SHORT).show();
    }
}
