package com.chacha.igexperiments;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import eu.chainfire.libsuperuser.Shell;

public class Module implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private String className, methodName, secondClassName;

    /**
     * Init the preferences
     */
    private void initPreferences(){
        XposedPreferences.loadPreferences();
        XposedPreferences.reloadPrefs();

        if(XposedPreferences.getPrefs().getBoolean("useHeckerMode", false))
            XposedBridge.log("(IGExperiments) Using class name from preferences");
        else
            XposedBridge.log("(IGExperiments) Using class name from Github");


    }

    /**
     * Initialize the class and method to hook
     */
    private void initElemToHook(){
        className = XposedPreferences.getPrefs().getString("className", "");
        methodName = XposedPreferences.getPrefs().getString("methodName", "");
        secondClassName = XposedPreferences.getPrefs().getString("secondClassName","");


        if(className.equals("")){
            XposedBridge.log("(IGExperiments) No class name found, using default");
            className = Utils.DEFAULT_CLASS_TO_HOOK; // Change this if you want to update the app
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

            if (!Shell.SU.available()){
                try {
                    Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
                    Object parser = parserCls.newInstance();
                    File apkPath = new File(lpparam.appInfo.sourceDir);
                    Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
                    String versionName = (String)XposedHelpers.getObjectField(pkg, "mVersionName");

                    //showToast(versionName);
                    if (versionName.equals("301.0.0.0.103")){
                        Class<?> targetClass = XposedHelpers.findClass("X.1B3", lpparam.classLoader);
                        Class<?> c08470cRClass = XposedHelpers.findClass("X.0cR", lpparam.classLoader);

                        // Hook into the A00 method and make it always return true
                        XposedHelpers.findAndHookMethod(targetClass, "A00", c08470cRClass,
                                new XC_MethodReplacement() {
                                    @Override
                                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                        // Always return true
                                        return true;
                                    }
                                });
                    } else if (versionName.equals("301.0.0.0.82")) {
                        Class<?> targetClass = XposedHelpers.findClass("X.1B0", lpparam.classLoader);
                        Class<?> C08420cPClass = XposedHelpers.findClass("X.0cP", lpparam.classLoader);

                        // Hook into the A00 method and make it always return true
                        XposedHelpers.findAndHookMethod(targetClass, "A00", C08420cPClass,
                                new XC_MethodReplacement() {
                                    @Override
                                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                        // Always return true
                                        return true;
                                    }
                                });

                    }
                    else if (versionName.equals("302.0.0.0.0")) {
                        Class<?> targetClass = XposedHelpers.findClass("X.1B2", lpparam.classLoader);
                        Class<?> C08420cPClass = XposedHelpers.findClass("X.0cR", lpparam.classLoader);

                        // Hook into the A00 method and make it always return true
                        XposedHelpers.findAndHookMethod(targetClass, "A00", C08420cPClass,
                                new XC_MethodReplacement() {
                                    @Override
                                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                        // Always return true
                                        return true;
                                    }
                                });

                    }
                    else if (versionName.equals("301.0.0.0.110")) {
                        Class<?> targetClass = XposedHelpers.findClass("X.1B2", lpparam.classLoader);
                        Class<?> C08420cPClass = XposedHelpers.findClass("X.0cR", lpparam.classLoader);

                        // Hook into the A00 method and make it always return true
                        XposedHelpers.findAndHookMethod(targetClass, "A00", C08420cPClass,
                                new XC_MethodReplacement() {
                                    @Override
                                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                        // Always return true
                                        return true;
                                    }
                                });

                    }
                    else if (versionName.equals("302.0.0.0.21")) {
                        Class<?> targetClass = XposedHelpers.findClass("X.1Ay", lpparam.classLoader);
                        Class<?> C08420cPClass = XposedHelpers.findClass("X.0cQ", lpparam.classLoader);

                        // Hook into the A00 method and make it always return true
                        XposedHelpers.findAndHookMethod(targetClass, "A00", C08420cPClass,
                                new XC_MethodReplacement() {
                                    @Override
                                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                                        // Always return true
                                        return true;
                                    }
                                });

                    }
                    else {
                        showToast("Not supported Instagram version!\nCheck Github page!");
                    }
                } catch (Throwable ignored) {
                }
            }

            else{
                initPreferences();
                initElemToHook();
                try {
                    Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
                    Object parser = parserCls.newInstance();
                    File apkPath = new File(lpparam.appInfo.sourceDir);
                    Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
                    String versionName = (String)XposedHelpers.getObjectField(pkg, "mVersionName");

                    int versionNameClass = getFirstNumberOfVersion(versionName);

                    XposedBridge.log("(IGExperiments) Hooking class: " + className);
                    XposedBridge.log("(IGExperiments) Hooking method: " + methodName);
                    XposedBridge.log("(IGExperiments) Hooking Second class: " + secondClassName);

                    if (versionNameClass <= 299){
                        XposedHelpers.findAndHookMethod(className, lpparam.classLoader, methodName,
                                XposedHelpers.findClass("com.instagram.service.session.UserSession", lpparam.classLoader),
                                XC_MethodReplacement.returnConstant(true));
                    }
                    else {
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


            }catch (Throwable ignored){

                }
            }

        }

        if(lpparam.packageName.equals(Utils.MY_PACKAGE_NAME)) {
            findAndHookMethod(Utils.MY_PACKAGE_NAME + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
    }

    private void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static int getFirstNumberOfVersion(String input) {
        String[] parts = input.split("\\.");
        if (parts.length > 0) {
            try {
                return Integer.parseInt(parts[0]);
            } catch (NumberFormatException e) {
                // Handle the case where the first part is not a valid integer
                e.printStackTrace();
            }
        }
        // Return a default value if parsing fails
        return 0;
    }

}
