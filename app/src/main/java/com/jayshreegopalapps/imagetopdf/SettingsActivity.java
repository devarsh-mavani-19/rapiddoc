package com.jayshreegopalapps.imagetopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.services.drive.DriveScopes;

public class SettingsActivity extends AppCompatActivity {
    String lang;
    Button color;
    Button logout;

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
    void refresh() {
        LoadSettings.load(SettingsActivity.this);
        //todo set language
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        lang = preferences.getString("language", "english");
        logout = findViewById(R.id.button_logout_settings);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestScopes(new Scope(DriveScopes.DRIVE_FILE)).build();
                GoogleSignInClient client = GoogleSignIn.getClient(SettingsActivity.this, signInOptions);

                client.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingsActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SettingsActivity.this, "Sign Out Failed", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
            }
        });

        color = findViewById(R.id.color_settings);
        final int[] r = {preferences.getInt("red", 0)};
        final int[] g = {preferences.getInt("green", 39)};
        final int[] b = {preferences.getInt("blue", 38)};
        color.setBackgroundColor(Color.rgb(r[0], g[0], b[0]));
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(SettingsActivity.this)
                        .showAlphaSlider(false)
                        .setTitle("Choose color")
                        .initialColor(Color.argb(255, r[0], g[0], b[0]))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {

                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                String colorHex = Integer.toHexString(selectedColor);
                                r[0] = Integer.valueOf( colorHex.substring( 2, 4 ), 16 );
                                g[0] = Integer.valueOf( colorHex.substring( 4, 6 ), 16 );
                                b[0] = Integer.valueOf( colorHex.substring( 6, 8 ), 16 );
                                color.setBackgroundColor(Color.rgb(r[0], g[0], b[0]));
                                preferences.edit().putInt("red", r[0]).commit();
                                preferences.edit().putInt("green", g[0]).commit();
                                preferences.edit().putInt("blue", b[0]).commit();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
            }
        });
    }
}
