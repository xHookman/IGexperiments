package com.chacha.igexperiments;

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
            XposedHelpers.findAndHookMethod("X.8PW", lpparam.classLoader, "A03",
                    XposedHelpers.findClass("com.instagram.service.session.UserSession", lpparam.classLoader),
                    XC_MethodReplacement.returnConstant(true));

           /* Object obj = XposedHelpers.newInstance(XposedHelpers.findClass("com.instagram.debug.quickexperiment.storage.QuickExperimentBisectStoreModel", lpparam.classLoader));

            Integer ouin = (Integer) XposedHelpers.callMethod(obj,"getUniverseIndex", "is_employee");
            XposedBridge.log("Universe index: " + ouin);*/


            /*final Object[] userSession = new Object[1];
            final Object[] fragmentActivity = new Object[1];

            XposedHelpers.findAndHookConstructor("com.instagram.service.session.UserSession", lpparam.classLoader, XposedHelpers.findClass("com.instagram.user.model.User", lpparam.classLoader), XposedHelpers.findClass("X.08u", lpparam.classLoader),
            XposedHelpers.findClass("X.0YI", lpparam.classLoader), boolean.class, XposedHelpers.findClass("X.0Y5", lpparam.classLoader), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Toast.makeText(AndroidAppHelper.currentApplication(), "got userSession: ", Toast.LENGTH_SHORT).show();
                    userSession[0] = param.thisObject;
                }
            });

            XposedHelpers.findAndHookMethod("com.instagram.mainactivity.MainActivity", lpparam.classLoader, "onStart", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    fragmentActivity[0] = (Activity) param.thisObject;
                    Toast.makeText(AndroidAppHelper.currentApplication(), "got fragmentActivity: " + fragmentActivity[0], Toast.LENGTH_SHORT).show();
                }
            });

            XposedHelpers.findAndHookConstructor("com.instagram.actionbar.ActionBarTitleViewSwitcher", lpparam.classLoader, android.content.Context.class, android.util.AttributeSet.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                  
                   // Toast.makeText(AndroidAppHelper.currentApplication(), "ActionBarTitleViewSwitcher: "+ param.thisObject.getClass(), Toast.LENGTH_LONG).show();
                    XposedHelpers.callMethod(XposedHelpers.findClass("com.instagram.debug.devoptions.api.DeveloperOptionsLauncher", lpparam.classLoader), "launchDeveloperOptionModalActivity",
                            AndroidAppHelper.currentApplication(),
                            activity,
                            userSession[0],
                            "salope");
                }
            });*/
        }

        if(lpparam.packageName.equals("com.chacha.igexperiments")) {
            findAndHookMethod("com.chacha.igexperiments" + ".MainActivity", lpparam.classLoader,
                    "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
    }
}
