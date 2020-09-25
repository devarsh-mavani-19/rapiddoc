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
                            Toast.makeText(SetPasswordActivity.this, "Password Set Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SetPasswordActivity.this, "Passwords Do not match!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MODE_ASK_PASSWORD:
                        if(mPassword.equals(password.getText().toString())) {
                            setToDefaultMode();
                        } else {
                            Toast.makeText(SetPasswordActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
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
                AlertDialog dialog = new AlertDialog.Builder(SetPasswordActivity.this).setTitle("Confirm Remove Password").setMessage("Are you sure you want to remove password?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putString("password", "").commit();
                        preferences.edit().putBoolean("isPasswordSet", false).commit();
                        dialog.dismiss();
                        Toast.makeText(SetPasswordActivity.this, "Password Removed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
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
        status.setText("Enter Password");
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
        status.setText("Enter Password");
        removePassword.setVisibility(View.INVISIBLE);
        setPassword.setVisibility(View.VISIBLE);
        password.setVisibility(View.VISIBLE);
    }


}
