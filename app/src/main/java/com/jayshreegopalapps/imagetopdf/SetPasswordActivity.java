package com.jayshreegopalapps.imagetopdf;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SetPasswordActivity extends AppCompatActivity {
    Button changePassword;
    Button setPassword;
    Button removePassword;
    TextInputEditText password, retypePassword;
    TextView status;
    private String mPassword;
    SharedPreferences preferences;
    private static final String MODE_ASK_PASSWORD = "askpassword";
    private static final String MODE_NEW_PASSWORD = "newpassword";
    private static final String MODE_DEFULT_MODE = "defaultmode";


    public String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        getSupportActionBar().hide();
        changePassword = findViewById(R.id.change_password_set_password);
        password = findViewById(R.id.password_set_password);
        retypePassword = findViewById(R.id.password_retype_set_password);
        status = findViewById(R.id.text_view_set_password);
        setPassword = findViewById(R.id.button_password_next_set_password);
        removePassword= findViewById(R.id.remove_password_set_password);
        preferences = getSharedPreferences("com.jayshreegopalapps.ImageToPdf", MODE_PRIVATE);

        boolean isPasswordSet = preferences.getBoolean("isPasswordSet", false);
        if(isPasswordSet) {
            mPassword = preferences.getString("password", "");
            if(mPassword.equals("")) {
                setToNewPasswordMode();
            } else {
                setToAskPasswordMode();
            }
        } else {
            setToNewPasswordMode();
        }

        setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mode) {
                    case MODE_NEW_PASSWORD:
                        if(retypePassword.getText().toString().equals(password.getText().toString())) {
                            preferences.edit().putString("password" ,(password.getText().toString())).commit();
                            preferences.edit().putBoolean("isPasswordSet", true).commit();
                            Toast.makeText(SetPasswordActivity.this, getString(R.string.password_set_successfuly), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SetPasswordActivity.this, getString(R.string.password_do_not_match), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MODE_ASK_PASSWORD:
                        if(mPassword.equals(password.getText().toString())) {
                            setToDefaultMode();
                        } else {
                            Toast.makeText(SetPasswordActivity.this, getString(R.string.password_do_not_match), Toast.LENGTH_SHORT).show();
                        }


                        break;

                    default:
                        break;
                }
            }
        });

        removePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(SetPasswordActivity.this).setTitle(getString(R.string.remove_password)).setMessage(getString(R.string.confirm_remove_password)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putString("password", "").commit();
                        preferences.edit().putBoolean("isPasswordSet", false).commit();
                        dialog.dismiss();
                        finish();
                        Toast.makeText(SetPasswordActivity.this, getString(R.string.password_removed), Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToNewPasswordMode();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadSettings.load(SetPasswordActivity.this);
        RelativeLayout rl= findViewById(R.id.rl_set_password_parent);
        LoadSettings.setViewTheme(rl, SetPasswordActivity.this);
    }

    private void setToDefaultMode() {
        mode = MODE_DEFULT_MODE;
        retypePassword.setText("");
        password.setText("");
        retypePassword.setVisibility(View.INVISIBLE);
        status.setVisibility(View.INVISIBLE);
        changePassword.setVisibility(View.VISIBLE);
        removePassword.setVisibility(View.VISIBLE);
        setPassword.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(password.getApplicationWindowToken(), 0);
        }
        if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(retypePassword.getApplicationWindowToken(), 0);
        }
    }

    private void setToAskPasswordMode() {
        mode = MODE_ASK_PASSWORD;
        status.setVisibility(View.VISIBLE);
        retypePassword.setVisibility(View.INVISIBLE);
        status.setText(getString(R.string.enter_password));
        changePassword.setVisibility(View.INVISIBLE);
        removePassword.setVisibility(View.INVISIBLE);
        setPassword.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
    }

    private void setToNewPasswordMode() {
        mode = MODE_NEW_PASSWORD;
        retypePassword.setVisibility(View.VISIBLE);
        changePassword.setVisibility(View.INVISIBLE);
        status.setVisibility(View.VISIBLE);
        status.setText(getString(R.string.enter_password));
        removePassword.setVisibility(View.INVISIBLE);
        setPassword.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
    }


}
