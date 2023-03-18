package com.chacha.igexperiments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.util.Objects;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {
    String IG_PACKAGE_NAME = "com.instagram.android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                os.writeBytes("am force-stop " + IG_PACKAGE_NAME + "\n");
                os.flush();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }

                //launch instagram
                Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(IG_PACKAGE_NAME);
                context.startActivity(Objects.requireNonNull(LaunchIntent));
            }
        }catch (Exception exception){
            Toast.makeText(context, "Root not granted !", Toast.LENGTH_SHORT).show();
    }}

    public void killIG(View view) {
        killAction();
    }
}