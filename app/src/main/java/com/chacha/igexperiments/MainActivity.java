package com.chacha.igexperiments;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coniy.fileprefs.FileSharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {
    private LinearLayout layoutHeckerMode, layoutSwitch;
    private EditText customClassName, customMethodName, customSecondClassName;
    private TextView textHookedClass, textViewError, infoHooktext, howtotext;
    private ImageButton btnDonate, btnGithub;
    private Button switchModeBtn;
    private Button btnHook, btnDownload, btnKill;
    private Spinner igVersionsSpinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<InfoIGVersion> iGVersionsInfos;

    /**
     * Init views preferences
     */
    private void initViewsPreferences() {
        sharedPreferences = Preferences.loadPreferences(this);
        editor = Preferences.getEditor();

        // Get the stored mode from preferences, default is "Normal"
        String useMode = sharedPreferences.getString("Mode", "Normal");

        // Set the button text and UI based on the mode
        if (useMode.equals("Hecker")) {
            layoutHeckerMode.setVisibility(View.VISIBLE);  // Show Hecker-specific UI
            textHookedClass.setVisibility(View.VISIBLE);   // Show Hooked Class
            switchModeBtn.setText(getResources().getString(R.string.hecker_mode));  // Set button text to Hecker mode
        } else if (useMode.equals("Normal")) {
            layoutHeckerMode.setVisibility(View.GONE);     // Hide Hecker-specific UI
            textHookedClass.setVisibility(View.VISIBLE);   // Show Hooked Class
            switchModeBtn.setText(getResources().getString(R.string.normal_mode));      // Set button text to Normal mode
        } else if (useMode.equals("Auto")) {
            layoutHeckerMode.setVisibility(View.GONE);     // Hide Hecker-specific UI
            textHookedClass.setVisibility(View.GONE);      // Hide Hooked Class
            switchModeBtn.setText(getResources().getString(R.string.auto_mode));        // Set button text to Auto mode
        }

        // Update the displayed hooked class and method names
        textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class),
                sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK),
                sharedPreferences.getString("methodName", Utils.DEFAULT_METHOD_TO_HOOK)));
    }

    /**
     * Init the views
     */
    private void initViews() {
        customClassName = findViewById(R.id.editTextClassName);
        customMethodName = findViewById(R.id.editTextMethodName);
        customSecondClassName = findViewById(R.id.editTextSecondClassName);
        textHookedClass = findViewById(R.id.textView3);
        switchModeBtn = findViewById(R.id.modeToggleButton);
        btnHook = findViewById(R.id.btnHook);
        igVersionsSpinner = findViewById(R.id.igVersionsSpinner);
        layoutHeckerMode = findViewById(R.id.layoutHeckerMode);
        btnDownload = findViewById(R.id.btnDownload);
        btnKill = findViewById(R.id.btnKill);
        textViewError = findViewById(R.id.textViewError);
        btnDonate = findViewById(R.id.btnDonate);
        btnGithub = findViewById(R.id.btnGithub);
        infoHooktext = findViewById(R.id.textView2);
        howtotext = findViewById(R.id.howtotext);
        layoutSwitch = findViewById(R.id.linearLayout2);
    }

    /**
     * @return true if an error is detected
     */
    private boolean isErrorDetected() {
        return iGVersionsInfos.size() == 0;
    }

    /**
     * Init the spinner with the different IG versions
     */
    private void initIGVersionsSpinner() {
        ArrayAdapter<InfoIGVersion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, iGVersionsInfos);

        if (isErrorDetected())
            textViewError.setVisibility(View.VISIBLE);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        igVersionsSpinner.setAdapter(adapter);
        setIGItemPosition();
    }

    /**
     * Init the differents views functions and listeners
     */
    @SuppressLint("SetTextI18n")
    private void initViewsFunctions() {
        customClassName.setText(sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK));
        customMethodName.setText(sharedPreferences.getString("methodName", Utils.DEFAULT_METHOD_TO_HOOK));
        customSecondClassName.setText(sharedPreferences.getString("secondClassName", Utils.DEFAULT_SECOND_CLASS_TO_HOOK));

        // Define modes
        String[] modes = {"Normal", "Hecker", "Auto"};
        int[] modeTextResources = {R.string.normal_mode, R.string.hecker_mode, R.string.auto_mode};

        // Load the current mode from preferences (default is Normal)
        String currentMode = sharedPreferences.getString("Mode", "Normal");
        final int[] currentModeIndex = {Arrays.asList(modes).indexOf(currentMode)}; // Get index of the current mode
        if (currentModeIndex[0] == -1)
            currentModeIndex[0] = 0; // Default to "Normal" if mode not found

        // Set the initial text based on the current mode
        switchModeBtn.setText(getResources().getString(modeTextResources[currentModeIndex[0]]));

        // Add click listener to cycle through modes
        switchModeBtn.setOnClickListener(v -> {


            // Rooted: Cycle through all three modes: Normal, Hecker, and Auto
            currentModeIndex[0] = (currentModeIndex[0] + 1) % modes.length; // Cycle through 0, 1, 2

            String selectedMode = modes[currentModeIndex[0]];

            // Update the button text to reflect the current mode
            switchModeBtn.setText(getResources().getString(modeTextResources[currentModeIndex[0]]));

            // Save the selected mode to SharedPreferences
            editor.putString("Mode", selectedMode).commit();

            // Update className, methodName, and secondClassName based on the selected mode
            switch (selectedMode) {
                case "Hecker":
                    editor.putString("className", customClassName.getText().toString()).commit();
                    editor.putString("methodName", customMethodName.getText().toString()).commit();
                    editor.putString("secondClassName", customSecondClassName.getText().toString()).commit();
                    layoutHeckerMode.setVisibility(View.VISIBLE); // Show Hecker-specific UI

                    // Show hooked class
                    textHookedClass.setVisibility(View.VISIBLE);
                    break;

                case "Normal":
                    editor.putString("className", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getClassToHook()).commit();
                    editor.putString("methodName", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getMethodToHook()).commit();
                    editor.putString("secondClassName", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getSecondClassToHook()).commit();
                    layoutHeckerMode.setVisibility(View.GONE); // Hide Hecker-specific UI

                    // Show hooked class
                    textHookedClass.setVisibility(View.VISIBLE);
                    break;

                case "Auto":
                    editor.putString("className", "Auto").commit(); // For Auto, store a specific value
                    editor.putString("methodName", "Auto").commit();
                    editor.putString("secondClassName", "Auto").commit();
                    layoutHeckerMode.setVisibility(View.GONE); // Hide Hecker-specific UI

                    // Hide hooked class in Auto mode
                    textHookedClass.setVisibility(View.GONE);
                    break;
            }

            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);

            // Update the text showing the hooked class, method, etc. (Only update if NOT in Auto mode)
            if (!selectedMode.equals("Auto")) {
                textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class),
                        sharedPreferences.getString("className", Utils.DEFAULT_CLASS_TO_HOOK),
                        sharedPreferences.getString("methodName", Utils.DEFAULT_METHOD_TO_HOOK)));
            }
        });

        btnHook.setOnClickListener(view -> {
            editor.putString("className", customClassName.getText().toString()).commit();
            editor.putString("methodName", customMethodName.getText().toString()).commit();
            editor.putString("secondClassName", customSecondClassName.getText().toString()).commit();
            FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class),
                    customClassName.getText().toString(),
                    customMethodName.getText().toString()));
        });

        igVersionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putString("className", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getClassToHook()).commit();
                editor.putString("methodName", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getMethodToHook()).commit();
                editor.putString("secondClassName", ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getSecondClassToHook()).commit();

                if (!sharedPreferences.getString("Mode", "Normal").equals("Auto"))
                    textHookedClass.setText(String.format(getResources().getString(R.string.hooked_class),
                            ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getClassToHook(),
                            ((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getMethodToHook()));

                FileSharedPreferences.makeWorldReadable(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnDownload.setOnClickListener(view -> {
            if (isErrorDetected()) {
                textViewError.setTextSize(textViewError.getTextSize() + 0.5f);
                return;
            }

            openUrl(((InfoIGVersion) igVersionsSpinner.getSelectedItem()).getUrl());
        });

        if (isRoot()) {
            btnKill.setOnClickListener(view -> {
                killAction();
            });
        } else {
            btnKill.setText("Use LSPatch to enable the module on Instagram");
            btnKill.setOnClickListener(view -> {
                Toast.makeText(this, "1. Enable the module on Instagram Using LSPatch", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "2. Stop the app manually!", Toast.LENGTH_SHORT).show();
            });
        }

        btnDonate.setOnClickListener(view -> Donation.openDonationLink(this));
        btnGithub.setOnClickListener(view -> openUrl("https://github.com/xHookman/IGexperiments/"));
    }

    /**
     * Init array of IG versions infos
     */
    private void initIGVersionsInfos() {
        iGVersionsInfos = getIGVersionsInfos();
    }

    public static Boolean isRoot() {
        return Shell.SU.available();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initViewsPreferences();
        initIGVersionsInfos();
        initIGVersionsSpinner();
        initViewsFunctions();

        if (isRoot() && !isModuleActive()) {
            disableView();
            textViewError.setText("Module DISABLED!");
            textViewError.setVisibility(View.VISIBLE);
            infoHooktext.setText("The module isn't enabled, Please enable it!");

        } else if (!isRoot()) {
            disableView();
            howtotext.setText("1. Download a compatible Instagram version\n" +
                    "2. Install it\n" +
                    "3. Patch Instagram using LSPatch (Local Patch Mode)\n" +
                    "4. Add our mod to Instagram Module scope using LSPatch\n" +
                    "5. Force stop Instagram and Start it!!!");
            textViewError.setVisibility(View.GONE);
            infoHooktext.setText("Use supported versions\nChoose one of the supported versions and click 'Download APK'\nSee Github page for more information.");
            layoutHeckerMode.setVisibility(View.GONE);
            layoutSwitch.setVisibility(View.GONE);
        }
    }

    // Disable when module not enabled
    private void disableView() {
        customClassName.setEnabled(false);
        customMethodName.setEnabled(false);
        textHookedClass.setEnabled(false);
        switchModeBtn.setEnabled(false);
        btnHook.setEnabled(false);
        layoutHeckerMode.setEnabled(false);
        btnKill.setEnabled(false);
    }

    private static boolean isModuleActive() {
        return false;
    }

    /**
     * Open url in browser
     *
     * @param url url to open
     */
    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /**
     * Kill and start Instagram app
     */
    @SuppressLint("SuspiciousIndentation")
    private void killAction() {
        if (isRoot()) {
            try {
                Process su = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(su.getOutputStream());
                os.writeBytes("adb shell" + "\n");
                os.flush();
                os.writeBytes("am force-stop " + Utils.IG_PACKAGE_NAME + "\n");
                os.flush();
                os.writeBytes("am start -n " + Utils.IG_PACKAGE_NAME + "/com.instagram.mainactivity.InstagramMainActivity" + "\n");
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Root not granted !", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Stop the app manually!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @return Return the json of supported IG versions
     */
    private static String getJSONContent() {
        try {
            Log.println(Log.INFO, "IGexperiments", "Reading raw content from github file");
            URL url = new URL("https://raw.githubusercontent.com/ReSo7200/IGExperimentsUpdates/master/hooks.json");
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

    /**
     * @return Return an ArrayList of differents supported IG versions
     */
    public static ArrayList<InfoIGVersion> getIGVersionsInfos() {
        ArrayList<InfoIGVersion> versions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(getJSONContent());
            JSONArray jsonArray = jsonObject.getJSONArray("ig_versions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject infoVersions = jsonArray.getJSONObject(i);
                versions.add(new InfoIGVersion(infoVersions.getString("version"),
                        infoVersions.getString("class_to_hook"),
                        infoVersions.getString("method_to_hook"),
                        infoVersions.getString("second_class_to_hook"),
                        infoVersions.getString("download")));
            }
        } catch (JSONException e) {
            Log.e("IGEXPERIMENTS", "Error while parsing JSON");
            e.printStackTrace();
        }
        return versions;
    }

    /**
     * Set the correct position of the spinner depending on the saved version
     */
    private void setIGItemPosition() {
        for (int i = 0; i < iGVersionsInfos.size(); i++) {
            if (iGVersionsInfos.get(i).getClassToHook().equals(sharedPreferences.getString("className", ""))) {
                igVersionsSpinner.setSelection(i);
            }
        }
    }
}
