package com.chacha.igexperiments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    private static final String VERSION_URL = "https://raw.githubusercontent.com/ReSo7200/IGExperimentsHooksUpdates/refs/heads/main/version.json";
    private static final String CURRENT_VERSION = "4.0";

    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Executor for background tasks

    // Views
    private TextView moduleStatus, moduleSubtext, instagramStatusText;
    private MaterialButton restartInstagramButton;
    private LinearLayout contributorsContainer;

    private static Boolean isRoot = Shell.SU.available();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        moduleStatus = findViewById(R.id.module_status);
        moduleSubtext = findViewById(R.id.module_subtext);
        instagramStatusText = findViewById(R.id.instagram_status_text);
        restartInstagramButton = findViewById(R.id.restart_instagram_button);
        contributorsContainer = findViewById(R.id.contributors_container);

        // Check for Updates
        checkForUpdate();

        // Check Module and Instagram Status
        checkModuleStatus();
        checkInstagramStatus();

        // Restart Instagram Button Logic
        restartInstagramButton.setOnClickListener(v -> {
            if (isRoot) {
                restartInstagramWithRoot();
            } else {
                restartInstagramNonRoot();
            }
        });

        // Setup Contributors and Special Thanks
        setupContributorsAndSpecialThanks();
    }

    // Function to check for the latest version
    private void checkForUpdate() {
        executor.submit(() -> {
            String versionData = fetchVersionData();

            if (versionData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(versionData);
                    String latestVersion = jsonObject.getString("latestVersion");
                    String updateUrl = jsonObject.getString("updateUrl");

                    if (isNewVersionAvailable(latestVersion)) {
                        runOnUiThread(() -> showUpdateDialog(updateUrl, latestVersion));
                    }
                } catch (Exception e) {
                    Log.e("VersionCheck", "Error parsing JSON", e);
                }
            }
        });
    }

    private String fetchVersionData() {
        try {
            URL url = new URL(VERSION_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            return result.toString();
        } catch (Exception e) {
            Log.e("VersionCheck", "Error fetching version data", e);
            return null;
        }
    }

    private boolean isNewVersionAvailable(String latestVersion) {
        return CURRENT_VERSION.compareTo(latestVersion) < 0;
    }

    private void showUpdateDialog(String updateUrl, String newVersion) {
        new AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("A new version is available: " + newVersion + "\nDo you want to update?")
                .setPositiveButton("Update", (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))))
                .setNegativeButton("Later", null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void checkModuleStatus() {
        boolean isModuleEnabled = isModuleActive();

        if (!isModuleEnabled) {
            moduleStatus.setText(R.string.module_status_disabled);
            moduleStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            moduleSubtext.setText(R.string.request_enable_module);
            restartInstagramButton.setEnabled(false);
        } else if (!isRoot) {
            moduleStatus.setText(R.string.module_status_enabled_no_root);
            moduleStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            moduleSubtext.setText(R.string.request_enable_root);
            restartInstagramButton.setEnabled(true);
        } else {
            moduleStatus.setText(R.string.module_status_enabled);
            moduleStatus.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            moduleSubtext.setText(R.string.module_active);
            restartInstagramButton.setEnabled(true);
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkInstagramStatus() {
        String instagramPackage = Utils.IG_PACKAGE_NAME; // IG package name
        PackageManager pm = this.getPackageManager(); // Get PackageManager

        try {
            PackageInfo packageInfo = pm.getPackageInfo(instagramPackage, 0);
            String versionName = packageInfo.versionName;

            instagramStatusText.setText(getString(R.string.installed_instagram_version) + versionName);
            instagramStatusText.setTextColor(getResources().getColor(R.color.green));
        } catch (PackageManager.NameNotFoundException e) {
            instagramStatusText.setText(getString(R.string.not_installed_instagram));
            instagramStatusText.setTextColor(getResources().getColor(R.color.red));
        } catch (Exception e) {
            instagramStatusText.setText(getString(R.string.error_instagram));
            instagramStatusText.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void restartInstagramWithRoot() {
        try {
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes("am force-stop com.instagram.android\n");
            os.flush();
            os.writeBytes("am start -n com.instagram.android/com.instagram.mainactivity.InstagramMainActivity\n");
            os.flush();
        } catch (Exception e) {
            Toast.makeText(this, R.string.failed_restart_insta_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void restartInstagramNonRoot() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:com.instagram.android"));
        startActivity(intent);
        Toast.makeText(this, R.string.non_root_restart_insta_toast, Toast.LENGTH_SHORT).show();
    }

    private void setupContributorsAndSpecialThanks() {
        List<Contributor> contributors = Arrays.asList(
                new Contributor("ReSo7200", "https://github.com/ReSo7200", "https://linkedin.com/in/abdalhaleem-altamimi", null),
                new Contributor("Xhookman", "https://github.com/xhookman", null, null)
        );

        for (Contributor contributor : contributors) {
            View view = LayoutInflater.from(this).inflate(R.layout.contributor_card, contributorsContainer, false);
            setupContributorCard(view, contributor);
            contributorsContainer.addView(view);
        }
    }

    private void setupContributorCard(View view, Contributor contributor) {
        TextView nameTextView = view.findViewById(R.id.contributor_name);
        nameTextView.setText(contributor.getName());

        ImageButton githubButton = view.findViewById(R.id.github_button);
        if (contributor.getGithubUrl() != null) {
            githubButton.setVisibility(View.VISIBLE);
            githubButton.setOnClickListener(v -> openLink(contributor.getGithubUrl()));
        } else {
            githubButton.setVisibility(View.GONE);
        }

        ImageButton linkedinButton = view.findViewById(R.id.linkedin_button);
        if (contributor.getLinkedinUrl() != null) {
            linkedinButton.setVisibility(View.VISIBLE);
            linkedinButton.setOnClickListener(v -> openLink(contributor.getLinkedinUrl()));
        } else {
            linkedinButton.setVisibility(View.GONE);
        }

        ImageButton telegramButton = view.findViewById(R.id.telegram_button);
        if (contributor.getTelegramUrl() != null) {
            telegramButton.setVisibility(View.VISIBLE);
            telegramButton.setOnClickListener(v -> openLink(contributor.getTelegramUrl()));
        } else {
            telegramButton.setVisibility(View.GONE);
        }
    }

    private void openLink(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static boolean isModuleActive() {
        return false;
    }
}
