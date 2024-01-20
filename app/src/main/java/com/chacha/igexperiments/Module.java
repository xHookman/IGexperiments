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
    private String className, methodName, secondClassName;

    /**
     * Init the preferences
     */
    private String initPreferences() {
        XposedPreferences.loadPreferences();
        XposedPreferences.reloadPrefs();

        if (XposedPreferences.getPrefs().getBoolean("useHeckerMode", false)) {
            XposedBridge.log("(IGExperiments) Using class name from preferences");
            return "Hecker";
        }
        XposedBridge.log("(IGExperiments) Using class name from Github");

        return "Normal";


    }

    /**
     * Initialize the class and method to hook
     */
    private void initElemToHook() {
        className = XposedPreferences.getPrefs().getString("className", "");
        methodName = XposedPreferences.getPrefs().getString("methodName", "");
        secondClassName = XposedPreferences.getPrefs().getString("secondClassName", "");


        if (className.equals("")) {
            XposedBridge.log("(IGExperiments) No class name found, using default");
            className = Utils.DEFAULT_CLASS_TO_HOOK;
            methodName = Utils.DEFAULT_METHOD_TO_HOOK;
            secondClassName = Utils.DEFAULT_SECOND_CLASS_TO_HOOK;
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        XposedPreferences.loadPreferences();
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {

        if (lpparam.packageName.equals(Utils.IG_PACKAGE_NAME)) {

            boolean success = false;

            try {
                String type = initPreferences();
                initElemToHook();
                // if not normal means Hecker mode is being used!
                if (!type.equals("Normal")) {
                    success = true;
                    // DEV PURPOSES
                    //showToast("HECKER MODE");
                    //showToast("(IGExperiments) Hooking class: " + classToHook);
                    //showToast("(IGExperiments) Hooking method: " + methodToHook);
                    //showToast("(IGExperiments) Hooking Second class: " + secondClassToHook);

                    Class<?> targetClass = XposedHelpers.findClass(className, lpparam.classLoader);
                    Class<?> secondTargetClass = XposedHelpers.findClass(secondClassName, lpparam.classLoader);
                    XposedHelpers.findAndHookMethod(targetClass, methodName, secondTargetClass,
                            new XC_MethodReplacement() {
                                @Override
                                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                    // Always return true
                                    return true;
                                }
                            });
                }
            } catch (Exception e) {

            }
            // if not success it means the hecker mode wasn't used "Root wasn't granted at the first place"
            if (!success) {
                try {
                    Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
                    Object parser = parserCls.newInstance();
                    File apkPath = new File(lpparam.appInfo.sourceDir);
                    Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
                    String versionName = (String) XposedHelpers.getObjectField(pkg, "mVersionName");

                    InfoIGVersion infoForTargetVersion = getInfoByVersion(versionName);

                    String classToHook = infoForTargetVersion.getClassToHook();
                    String methodToHook = infoForTargetVersion.getMethodToHook();
                    String secondClassToHook = infoForTargetVersion.getSecondClassToHook();

                    // DEV PURPOSES
//                        showToast(versionName);
//                        showToast("(IGExperiments) Hooking class: " + classToHook);
//                        showToast("(IGExperiments) Hooking method: " + methodToHook);
//                        showToast("(IGExperiments) Hooking Second class: " + secondClassToHook);
//                        XposedBridge.log(getTime() + versionName);
                    XposedBridge.log(getTime() + "(IGExperiments) Hooking class: " + classToHook);
                    XposedBridge.log(getTime() + "(IGExperiments) Hooking method: " + methodToHook);
                    XposedBridge.log(getTime() + "(IGExperiments) Hooking Second class: " + secondClassToHook);


                    Class<?> targetClass = XposedHelpers.findClass(classToHook, lpparam.classLoader);
                    Class<?> secondTargetClass = XposedHelpers.findClass(secondClassToHook, lpparam.classLoader);
                    XposedHelpers.findAndHookMethod(targetClass, methodToHook, secondTargetClass,
                            new XC_MethodReplacement() {
                                @Override
                                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                    // Always return true
                                    XposedBridge.log("(IGExperiments) Successfully Hooked into method");
                                    return true;
                                }
                            });


                } catch (InstantiationException | IllegalAccessException e) {
                    XposedBridge.log(getTime() + "Reflection error: " + e.getMessage());
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    XposedBridge.log(getTime() + "Illegal argument in method call: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) { // Catch other exceptions that might not be predicted
                    XposedBridge.log(getTime() + "Unhandled exception: " + e.getMessage());
                    e.printStackTrace();
                }
                XposedBridge.log(getTime() + "End!");
            }


            //}

        }

        if (lpparam.packageName.equals(Utils.MY_PACKAGE_NAME)) {
            findAndHookMethod(Utils.MY_PACKAGE_NAME + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
    }


    private static String getJSONContent() {
        try {
            Log.println(Log.INFO, "IGexperiments", "Reading raw content from github file");
            URL url = new URL("https://raw.githubusercontent.com/ReSo7200/IGexperiments/master/classes_to_hook.json");
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
            Log.e("IGEXPERIMENTS", "Error while parsing JSON");
            e.printStackTrace();
        }
    }

    // Retrieve InfoIGVersion by version
    public InfoIGVersion getInfoByVersion(String version) {
        loadIGVersions();
        for (InfoIGVersion info : versions) {
            if (info.getVersion().contains(version)) {
                return info;
            }

        }
        // if the installed Instagram version wasn't supported:
        showToast("Version is not supported, Use Hecker mode or use a supported version!");

        return null; // Version not found
    }

    private void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getTime() {
        // Format for displaying the current date and time
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Get current date and time
        String currentTime = sdf.format(new Date());

        // Log the current time
        String time = "Time: " + currentTime + " - ";

        return time;
    }

}
