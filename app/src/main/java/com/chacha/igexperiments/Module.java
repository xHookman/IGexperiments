package com.chacha.igexperiments;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @noinspection ALL
 */
public class Module implements IXposedHookLoadPackage {


    // Define static sets to store found classes across multiple invocations
    static Set<String> requiredSet = new HashSet<>(Arrays.asList("com.instagram.mainactivity.InstagramMainActivity"));
    private String className, methodName, secondClassName;

    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {


        if (lpparam.packageName.equals(Utils.MY_PACKAGE_NAME)) {
            findAndHookMethod(Utils.MY_PACKAGE_NAME + ".MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        if (lpparam.packageName.equals(Utils.IG_PACKAGE_NAME)) {
            try {
                ClassLoader classLoader = lpparam.classLoader;
                handleAutoMode(lpparam);
            } catch (Exception e) {
                XposedBridge.log("(DevOptionsEnable) Error handling Dev Options: " + e.getMessage());
            }

        }

    }

    private void handleAutoMode(XC_LoadPackage.LoadPackageParam lpparam) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        try {
            int num_of_hooks = 0;
            //  Perform dynamic search
            for (char first : characters.toCharArray()) {
                for (char second : characters.toCharArray()) {
                    for (char third : characters.toCharArray()) {
                        String classToHook = "X." + first + second + third;

                        try {
                            Class<?> targetClass = XposedHelpers.findClass(classToHook, lpparam.classLoader);
                            Method[] methods = targetClass.getDeclaredMethods();
                            Field[] fields = targetClass.getDeclaredFields();

                            try {
                                for (Method method : targetClass.getDeclaredMethods()) {
                                    if (methods.length == 1 && methods[0].getName().equals("A00") &&
                                            methods[0].getReturnType() == Boolean.TYPE &&
                                            methods[0].getParameterCount() == 1 && method.getParameterTypes()[0].getName().contains("UserSession") &&
                                            fields.length == 0 && Modifier.isFinal(method.getModifiers())) {

                                        num_of_hooks += 1;
                                        Class<?> UserSessionClass = XposedHelpers.findClass(Utils.USER_SESSION_CLASS, lpparam.classLoader);
                                        hookDevOptions(targetClass, "A00", UserSessionClass);


                                    }
                                }
                            } catch (NoClassDefFoundError | XposedHelpers.ClassNotFoundError e) {
                                //XposedBridge.log("(DevOptionsEnable) Skipping method due to missing dependency: " + e.getMessage());
                            } catch (Exception e) {
                                //XposedBridge.log("(DevOptionsEnable) General exception while inspecting method: " + e.getMessage());
                            }

                        } catch (NoClassDefFoundError | XposedHelpers.ClassNotFoundError e) {
                            //XposedBridge.log("(DevOptionsEnable) Skipping class " + classToHook + " due to missing dependency: " + e.getMessage());
                        } catch (Exception e) {
                            //XposedBridge.log("(DevOptionsEnable) General exception while inspecting class: " + e.getMessage());
                        }
                    }
                }
            }

            if (num_of_hooks <= 0) {
                // No suitable classes found
                XposedBridge.log("(DevOptionsEnable) No suitable classes found during dynamic search.");
            }

        } catch (Exception e) {
            XposedBridge.log("(DevOptionsEnable) Error in Dev-Options: " + e.getMessage());
        }
    }


    private void hookDevOptions(Class<?> targetClass, String methodToHook, Class<?> secondTargetClass) {
        try {
            XposedHelpers.findAndHookMethod(
                    targetClass,
                    methodToHook,
                    secondTargetClass, // Second class parameter (UserSessionClass)
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            //XposedBridge.log("(DevOptionsEnable) Successfully Hooked into method: " + methodToHook + " in class: " + targetClass.getName());
                            return true; // Ensure the method always returns true
                        }
                    }
            );
            //XposedBridge.log("(DevOptionsEnable) Successfully hooked method: " + methodToHook + " in class: " + targetClass.getName());
        } catch (NoSuchMethodError e) {
            //XposedBridge.log("(DevOptionsEnable) No such method: " + methodToHook + " in class: " + targetClass.getName() + " - " + e.getMessage());
        } catch (NoClassDefFoundError e) {
            //XposedBridge.log("(DevOptionsEnable) No such class definition found for: " + targetClass.getName() + " or parameter class: " + secondTargetClass.getName() + " - " + e.getMessage());
        } catch (XposedHelpers.ClassNotFoundError e) {
            //XposedBridge.log("(DevOptionsEnable) XposedHelpers couldn't find class: " + targetClass.getName() + " or parameter class: " + secondTargetClass.getName() + " - " + e.getMessage());
        } catch (Exception e) {
            //XposedBridge.log("(DevOptionsEnable) General exception while hooking method: " + methodToHook + " in class: " + targetClass.getName() + " - " + e.getMessage());
        }
    }

    private void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(AndroidAppHelper.currentApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show());
    }

    public String getTime() {
        // Format for displaying the current date and time
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Get current date and time
        String currentTime = sdf.format(new Date());

        // Log the current time
        return "Time: " + currentTime + " - ";
    }

}
