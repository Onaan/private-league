package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private SharedPreferences loginPrefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_login_data);
        this.username = (EditText) findViewById(R.id.et_set_login_data_name);
        this.password = (EditText) findViewById(R.id.et_set_login_data_password);
        this.showPassword = (CheckBox) findViewById(R.id.cb_set_login_data_show);
        loginPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    public void onResume() {
        super.onResume();
        username.setText(loginPrefs.getString(USERNAME, ""));
        password.setText(loginPrefs.getString(PASSWORD, ""));
        showPassword.setChecked(loginPrefs.getBoolean("showPassword", false));
        togglePasswordVisibility(showPassword);


    }

    public void saveLoginData(final View view) {
        SharedPreferences.Editor e = loginPrefs.edit();
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