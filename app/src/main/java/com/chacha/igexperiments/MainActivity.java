package com.chacha.igexperiments;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.coniy.fileprefs.FileSharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {
    private EditText customClassName;
    private TextView textHookedClass, textViewDownload;
    private CheckBox checkBoxUseCustomClass;
    private Button btnHook;
    private Spinner igVersionsSpinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<InfoIGVersion> igVersions;

    private void initPreferences(){
        sharedPreferences = Preferences.loadPreferences(this);
        editor = Preferences.getEditor();

        boolean useGithub = sharedPreferences.getBoolean("useGithub", true);
        checkBoxUseCustomClass.setChecked(!useGithub);
        customClassName.setEnabled(!useGithub);
        btnHook.setEnabled(!useGithub);
        igVersionsSpinner.setEnabled(useGithub);

        textHookedClass.setText(sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK));
    }

    private void initViews(){
        customClassName = findViewById(R.id.editTextClassName);
        textHookedClass = findViewById(R.id.textView3);
        textViewDownload = findViewById(R.id.textViewDownload);
        checkBoxUseCustomClass = findViewById(R.id.useCustomClass);
        btnHook = findViewById(R.id.btnHook);
        igVersionsSpinner = findViewById(R.id.igVersionsSpinner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initPreferences();

        igVersions = new ArrayList<>();
        try {
            igVersions = getIGVersions();
        } catch (JSONException e) {
            Log.e("IGEXPERIMENTS", "Error while parsing JSON");
            e.printStackTrace();
        }

        ArrayAdapter<InfoIGVersion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, igVersions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.sort(Comparator.comparing(InfoIGVersion::getVersion));
        igVersionsSpinner.setAdapter(adapter);
        setIGItemPosition();

        customClassName.setText(sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK));

        checkBoxUseCustomClass.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean("useGithub", !b).commit();
            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class), sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK)));
            customClassName.setEnabled(b);
            btnHook.setEnabled(b);
            igVersionsSpinner.setEnabled(!b);
        });

        btnHook.setOnClickListener(view -> {
            editor.putString("className", customClassName.getText().toString()).commit();
            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class), customClassName.getText().toString()));
        });

        igVersionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("className", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getClassToHook()).commit();
                textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class), ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getClassToHook()));
                textViewDownload.setText(String.format(getResources().getString(R.string.download), ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getUrl()));
                FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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

    private String getJSONContent(){
        try {
            Log.println(Log.INFO, "IGexperiments", "Reading raw content from github file");
            URL url = new URL("https://raw.githubusercontent.com/xHookman/IGexperiments/custom_class/app/src/main/assets/class_to_hook.json");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Scanner s = new Scanner(url.openStream());
            StringBuilder content = new StringBuilder();
            while (s.hasNextLine()) {
                content.append(s.nextLine());
            }
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private ArrayList<InfoIGVersion> getIGVersions() throws JSONException {
        ArrayList<InfoIGVersion> versions = new ArrayList<>();
        Log.e("IGEXPERIMENTS", getJSONContent());
        JSONObject jsonObject = new JSONObject(getJSONContent());
        JSONArray jsonArray = jsonObject.getJSONArray("ig_versions");
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject infoVersions = jsonArray.getJSONObject(i);
            versions.add(new InfoIGVersion(infoVersions.getString("version"),
                    infoVersions.getString("class_to_hook"),
                    infoVersions.getString("download")));
        }
        return versions;
    }

    private void setIGItemPosition(){
        for (int i = 0; i < igVersions.size(); i++) {
            if (igVersions.get(i).getClassToHook().equals(sharedPreferences.getString("className", ""))){
                igVersionsSpinner.setSelection(i);
            }
        }
    }
}