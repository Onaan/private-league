package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import de.draigon.waw.R;

import static de.draigon.waw.utils.PrefConstants.*;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 05.06.12
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class SetLoginDataActivity extends Activity {
    private EditText username;
    private EditText password;
    private CheckBox showPassword;
    private SharedPreferences prefs;
    private RadioGroup rg;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_login_data);
        this.username = (EditText) findViewById(R.id.et_set_login_data_name);
        this.password = (EditText) findViewById(R.id.et_set_login_data_password);
        this.showPassword = (CheckBox) findViewById(R.id.cb_set_login_data_show);
        this.rg=(RadioGroup) findViewById(R.id.rg_set_login_data_server_select);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    public void onResume() {
        super.onResume();
        username.setText(prefs.getString(USERNAME, ""));
        password.setText(prefs.getString(PASSWORD, ""));
        showPassword.setChecked(prefs.getBoolean("showPassword", false));
        togglePasswordVisibility(showPassword);
        if(prefs.getString(GET_SERVER,"").equals(DP_GET_SERVER)){
            rg.check(R.id.rb_set_login_data_server_select_2);
        }


    }

    public void saveLoginData(final View view) {
        SharedPreferences.Editor e = prefs.edit();
        String getServer = null;
        String postServer = null;
        if (R.id.rb_set_login_data_server_select_1==rg.getCheckedRadioButtonId()){
            getServer = IT_NRW_GET_SERVER;
            postServer = IT_NRW_POST_SERVER;
        }
        if (R.id.rb_set_login_data_server_select_2==rg.getCheckedRadioButtonId()){
            getServer = DP_GET_SERVER;
            postServer = DP_POST_SERVER;
        }

        e.putString(POST_SERVER,postServer);
        e.putString(GET_SERVER,getServer);
        e.putString(USERNAME, username.getText().toString());
        e.putString(PASSWORD, password.getText().toString());
        e.putBoolean("showPassword", showPassword.isChecked());

        e.commit();
        this.finish();
    }

    public void togglePasswordVisibility(final View view) {
        if (!showPassword.isChecked()) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            password.setTransformationMethod(null);
        }
    }
}