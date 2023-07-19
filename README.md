# IGExperiments

Allow you to enable developer options in Instagram!

When the module is enabled, kill Instagram and long press home button. You will be able go to developer page and sometimes other stuff.

It might not work for all versions because classes name and methods name often change from update to another :/

You will need Lsposed/Xposed framework to use it.(Root devices)

LSPatch(Non-Root devices)

## FAQ

#### How to support more Instagram versions ?

First you will need to use Jadx to decompile an Instagram apk.
Jadx: 
Instagram apks: 

- Open Jadx and select your apk.
- Click on the text search button at top, wait for decompiling (it can takes several times)
- Search for "is_employee" and find a line similar to this:

```
r2.A0i("is_employee", Boolean.valueOf(C8WQ.A03(userSession)));
```

You now know the class to hook: C8WQ so you will use X.C8WQ!
