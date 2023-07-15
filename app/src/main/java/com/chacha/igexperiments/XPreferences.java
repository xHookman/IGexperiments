package com.chacha.igexperiments;

import android.app.Application;
import android.os.Environment;

import java.io.File;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class XPreferences {
    private XSharedPreferences pref;

    private XSharedPreferences getPref() {
        XSharedPreferences pref = new XSharedPreferences(Utils.MY_PACKAGE_NAME, Utils.PREFS_NAME);
        return pref.getFile().canRead() ? pref : null;
    }

    private XSharedPreferences getLegacyPrefs() {
        File f = new File(Environment.getDataDirectory(), "data/" + Utils.MY_PACKAGE_NAME + "/shared_prefs/" + Utils.PREFS_NAME + ".xml");
        return new XSharedPreferences(f);
    }

    public XSharedPreferences loadPreferences() {
        if (XposedBridge.getXposedVersion() < 93) {
            pref = getLegacyPrefs();
        } else {
            pref = getPref();
        }

        if (pref != null) {
            pref.reload();
        } else {
            XposedBridge.log("Can't load preference in the module");
        }

        return pref;
    }
}
