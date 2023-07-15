package com.chacha.igexperiments;

// imports
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Module implements IXposedHookLoadPackage {
    String IG_PACKAGE_NAME = "com.instagram.android";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if(lpparam.packageName.equals(IG_PACKAGE_NAME)) {
            // hooking the requited method
            XposedHelpers.findAndHookMethod("X.8W4", lpparam.classLoader, "A03",
                    XposedHelpers.findClass("com.instagram.service.session.UserSession", lpparam.classLoader),
                    XC_MethodReplacement.returnConstant(true));
        }
            // checking if the module is active or not
        if(lpparam.packageName.equals("com.chacha.igexperiments")) {
            findAndHookMethod("com.chacha.igexperiments" + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
    }
}
