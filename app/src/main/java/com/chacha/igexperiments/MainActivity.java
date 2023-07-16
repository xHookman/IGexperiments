package com.chacha.igexperiments;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.coniy.fileprefs.FileSharedPreferences;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {
    private EditText customClassName;
    private TextView textHookedClass;
    private CheckBox checkBoxUseCustomClass;
    private Button btnHook;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private void initPreferences(){
        sharedPreferences = Preferences.loadPreferences(this);
        editor = Preferences.getEditor();

        boolean useGithub = sharedPreferences.getBoolean("useGithub", true);
        checkBoxUseCustomClass.setChecked(!useGithub);
        customClassName.setEnabled(!useGithub);
        btnHook.setEnabled(!useGithub);

        if(useGithub){
            textHookedClass.setText("Hooked class : " + getClassNameFromGithub());
        } else {
            textHookedClass.setText("Hooked class : " + customClassName.getText().toString());
        }
    }

    private void initViews(){
        customClassName = findViewById(R.id.editTextClassName);
        textHookedClass = findViewById(R.id.textView3);
        checkBoxUseCustomClass = findViewById(R.id.useCustomClass);
        btnHook = findViewById(R.id.btnHook);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initPreferences();

        customClassName.setText(sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK));

        checkBoxUseCustomClass.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean("useGithub", !b).commit();
            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            if(!b){
                textHookedClass.setText("Hooked class : " + getClassNameFromGithub());
            } else {
                textHookedClass.setText("Hooked class : " + customClassName.getText().toString());
            }
            customClassName.setEnabled(b);
            btnHook.setEnabled(b);
        });

        btnHook.setOnClickListener(view -> {
            editor.putString("className", customClassName.getText().toString()).commit();
            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            textHookedClass.setText("Hooked class : " + customClassName.getText().toString());
        });

        if(!isModuleActive()){
            Toast.makeText(this, "Module DISABLED !", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Module enabled !", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isModuleActive(){
        return false;
    }

    public void killAction() {
        Context context = this;
        try {
            if (Shell.SU.available()) {
                Process su = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(su.getOutputStream());
                os.writeBytes("adb shell" + "\n");
                os.flush();
                os.writeBytes("am force-stop " + Utils.IG_PACKAGE_NAME + "\n");
                os.flush();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }

                //launch instagram
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(Utils.IG_PACKAGE_NAME);
                context.startActivity(Objects.requireNonNull(LaunchIntent));
            }
        }catch (Exception exception){
            Toast.makeText(context, "Root not granted !", Toast.LENGTH_SHORT).show();
    }}

    public void killIG(View view) {
        killAction();
    }

    public static String getClassNameFromGithub(){
        try {
            Log.println(Log.INFO, "IGexperiments", "Reading raw content from github file");
            URL url = new URL("https://raw.githubusercontent.com/xHookman/IGexperiments/custom_class/app/src/main/assets/class_to_hook");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Scanner s = new Scanner(url.openStream());
            return s.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}