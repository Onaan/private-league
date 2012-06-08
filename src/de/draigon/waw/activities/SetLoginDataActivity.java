package de.draigon.waw.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import de.draigon.waw.R;

import static de.draigon.waw.utils.PrefConstants.*;


public class SetLoginDataActivity extends Activity {
    private EditText username;
    private EditText password;
    private CheckBox showPassword;
    private SharedPreferences prefs;
    private RadioGroup rg;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_login_data);
        this.username = (EditText) findViewById(R.id.et_set_login_data_name);
        this.password = (EditText) findViewById(R.id.et_set_login_data_password);
        this.showPassword = (CheckBox) findViewById(R.id.cb_set_login_data_show);
        this.rg = (RadioGroup) findViewById(R.id.rg_set_login_data_server_select);
        this.prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    @Override
    public void onResume() {
        super.onResume();
        this.username.setText(this.prefs.getString(USERNAME, ""));
        this.password.setText(this.prefs.getString(PASSWORD, ""));
        this.showPassword.setChecked(this.prefs.getBoolean("showPassword", false));
        togglePasswordVisibility(this.showPassword);
        if (this.prefs.getString(GET_SERVER, "").equals(DP_GET_SERVER)) {
            this.rg.check(R.id.rb_set_login_data_server_select_2);
        }


    }

    @SuppressWarnings("unused")
    public void saveLoginData(final View view) {
        final SharedPreferences.Editor e = this.prefs.edit();
        String getServer = null;
        String postServer = null;
        if (R.id.rb_set_login_data_server_select_1 == this.rg.getCheckedRadioButtonId()) {
            getServer = IT_NRW_GET_SERVER;
            postServer = IT_NRW_POST_SERVER;
        }
        if (R.id.rb_set_login_data_server_select_2 == this.rg.getCheckedRadioButtonId()) {
            getServer = DP_GET_SERVER;
            postServer = DP_POST_SERVER;
        }
        e.putString(POST_SERVER, postServer);
        e.putString(GET_SERVER, getServer);
        e.putString(USERNAME, this.username.getText().toString().trim());
        e.putString(PASSWORD, this.password.getText().toString().trim());
        e.putBoolean("showPassword", this.showPassword.isChecked());
        e.commit();
        Toast.makeText(getApplicationContext(), getResources().getText(R.string.set_login_data_save_message), Toast.LENGTH_SHORT).show();
        this.finish();
    }

    private void togglePasswordVisibility(final View view) {
        if (!this.showPassword.isChecked()) {
            this.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            this.password.setTransformationMethod(null);
        }
    }


}