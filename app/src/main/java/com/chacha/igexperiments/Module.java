package com.chacha.igexperiments;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Module implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private String className;
    private String methodName;
    private String secondClassName;

    private static final String MODE_NORMAL = "Normal";
    private static final String MODE_AUTO = "Auto";
    private static final String MODE_HECKER = "Hecker";

    private static final String PACKAGE_PARSER_CLASS = "android.content.pm.PackageParser";
    private static final String IG_PACKAGE_NAME = Utils.IG_PACKAGE_NAME;
    private static final String MY_PACKAGE_NAME = Utils.MY_PACKAGE_NAME;

    @Override
    public void initZygote(StartupParam startupParam) {
        XposedPreferences.loadPreferences();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {

        if (lpparam.packageName.equals(MY_PACKAGE_NAME)) {
            findAndHookMethod(MY_PACKAGE_NAME + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        if (lpparam.packageName.equals(IG_PACKAGE_NAME)) {
            processIGPackage(lpparam);
        }
    }

    private void processIGPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        boolean success = false;
        try {
            String mode = initPreferences();
            try {
                initElementsToHook(); // Attempt to hook elements
            } catch (Exception ignored) {
            }

            switch (mode) {
                case MODE_NORMAL:
                    success = handleNormalMode(lpparam);
                    break;
                case MODE_HECKER:
                    success = handleHeckerMode(lpparam);
                    break;
                case MODE_AUTO:
                    success = handleAutoMode(lpparam);
                    break;
                default:
                    XposedBridge.log("Unsupported mode.");
                    break;
            }

            if (!success) {
                XposedBridge.log("Selected method didn't work!");
            }
        } catch (Exception e) {
            XposedBridge.log("Unhandled exception: " + e.getMessage());
        }
    }

    private String initPreferences() {
        // this fixes returning normal mode for non-rooted devices
        try {
            XposedPreferences.loadPreferences();
            XposedPreferences.reloadPrefs();
            return XposedPreferences.getPrefs().getString("Mode", MODE_NORMAL);
        } catch (Exception e) {
            return MODE_NORMAL; // fallback to a default mode
        }
    }

    private void initElementsToHook() {
        className = XposedPreferences.getPrefs().getString("className", Utils.DEFAULT_CLASS_TO_HOOK);
        methodName = XposedPreferences.getPrefs().getString("methodName", Utils.DEFAULT_METHOD_TO_HOOK);
        secondClassName = XposedPreferences.getPrefs().getString("secondClassName", Utils.DEFAULT_SECOND_CLASS_TO_HOOK);
    }

    private boolean handleNormalMode(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            String versionName = getAppVersionName(lpparam);
            InfoIGVersion infoForTargetVersion = getInfoByVersion(versionName);

            if (infoForTargetVersion != null) {
                logHookingDetails(infoForTargetVersion);
                hookMethod(lpparam, infoForTargetVersion);
                return true;
            }

        } catch (Exception e) {
            XposedBridge.log("Error in normal mode: " + e.getMessage());
        }
        return false;
    }

    private boolean handleHeckerMode(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            logHookingDetails(className, methodName, secondClassName);
            hookMethod(lpparam, className, methodName, secondClassName);
            return true;
        } catch (Exception e) {
            XposedBridge.log("Error in Hecker mode: " + e.getMessage());
        }
        return false;
    }

    private boolean handleAutoMode(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            String versionName = getAppVersionName(lpparam);
            if (isVersionCompatibleForAutoMode(versionName)) {
                return tryDynamicClassSearch(lpparam);
            } else {
                showToast("Versions older than 334.x aren't compatible with auto mode!");
            }
        } catch (Exception e) {
            XposedBridge.log("Error in auto mode: " + e.getMessage());
        }
        return false;
    }

    private String getAppVersionName(XC_LoadPackage.LoadPackageParam lpparam) throws Exception {
        Class<?> parserClass = XposedHelpers.findClass(PACKAGE_PARSER_CLASS, lpparam.classLoader);
        Object parser = parserClass.newInstance();
        File apkPath = new File(lpparam.appInfo.sourceDir);
        Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
        return (String) XposedHelpers.getObjectField(pkg, "mVersionName");
    }

    private void hookMethod(XC_LoadPackage.LoadPackageParam lpparam, InfoIGVersion info) throws ClassNotFoundException {
        hookMethod(lpparam, info.getClassToHook(), info.getMethodToHook(), info.getSecondClassToHook());
    }

    private void hookMethod(XC_LoadPackage.LoadPackageParam lpparam, String classToHook, String methodToHook, String secondClassToHook) throws ClassNotFoundException {
        Class<?> targetClass = XposedHelpers.findClass(classToHook, lpparam.classLoader);
        Class<?> secondTargetClass = XposedHelpers.findClass(secondClassToHook, lpparam.classLoader);

        XposedHelpers.findAndHookMethod(targetClass, methodToHook, secondTargetClass, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                XposedBridge.log("Successfully Hooked into method: " + methodToHook);
                return true;
            }
        });
    }

    private boolean tryDynamicClassSearch(XC_LoadPackage.LoadPackageParam lpparam) {
        String methodToHook = "A00";
        String secondClassToHook = "com.instagram.common.session.UserSession";
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (char first : characters.toCharArray()) {
            for (char second : characters.toCharArray()) {
                for (char third : characters.toCharArray()) {
                    String classToHook = "X." + first + second + third;
                    if (attemptClassHook(lpparam, classToHook, methodToHook, secondClassToHook)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean attemptClassHook(XC_LoadPackage.LoadPackageParam lpparam, String classToHook, String methodToHook, String secondClassToHook) {
        try {
            Class<?> targetClass = XposedHelpers.findClass(classToHook, lpparam.classLoader);
            Class<?> secondTargetClass = XposedHelpers.findClass(secondClassToHook, lpparam.classLoader);

            if (hasBooleanReturningMethod(targetClass)) {
                hookMethod(lpparam, classToHook, methodToHook, secondClassToHook);
                return true;
            }
        } catch (Exception e) {
            XposedBridge.log("Error attempting to hook class: " + classToHook + ", Error: " + e.getMessage());
        }
        return false;
    }

    private boolean hasBooleanReturningMethod(Class<?> targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.getReturnType() == Boolean.TYPE) {
                return true;
            }
        }
        return false;
    }

    private boolean isVersionCompatibleForAutoMode(String versionName) {
        int majorVersion = Integer.parseInt(versionName.split("\\.")[0]);
        return majorVersion >= 334;
    }

    private void logHookingDetails(InfoIGVersion info) {
        logHookingDetails(info.getClassToHook(), info.getMethodToHook(), info.getSecondClassToHook());
    }

    private void logHookingDetails(String classToHook, String methodToHook, String secondClassToHook) {
        XposedBridge.log(getTime() + "Hooking class: " + classToHook);
        XposedBridge.log(getTime() + "Hooking method: " + methodToHook);
        XposedBridge.log(getTime() + "Hooking second class: " + secondClassToHook);
    }

    private void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show());
    }

    public String getTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "Time: " + sdf.format(new Date()) + " - ";
    }

    private static String getJSONContent() {
        try {
            Log.i("IGexperiments", "Reading raw content from github file");
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
            Log.e("IGexperiments", "Error fetching JSON content", e);
        }
        return "";
    }

    private static ArrayList<InfoIGVersion> versions;

    public static void loadIGVersions() {
        versions = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(getJSONContent());
            JSONArray jsonArray = jsonObject.getJSONArray("ig_versions");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject infoVersions = jsonArray.getJSONObject(i);
                InfoIGVersion versionInfo = new InfoIGVersion(
                        infoVersions.getString("version"),
                        infoVersions.getString("class_to_hook"),
                        infoVersions.getString("method_to_hook"),
                        infoVersions.getString("second_class_to_hook"),
                        infoVersions.getString("download")
                );
                versions.add(versionInfo);
            }
        } catch (JSONException e) {
            Log.e("IGEXPERIMENTS", "Error while parsing JSON", e);
        }
    }

    public InfoIGVersion getInfoByVersion(String version) {
        loadIGVersions();
        for (InfoIGVersion info : versions) {
            if (info.getVersion().contains(version)) {
                return info;
            }
        }
        showToast("Version not supported, Use Hecker mode or try a supported version!");
        return null;
    }
}
