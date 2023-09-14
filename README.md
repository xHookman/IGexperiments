# IGExperiments

Allow you to enable developer options in Instagram!

When the module is enabled, kill Instagram and long press home button. You will be able go to developer page and sometimes other stuff.

It might not work for all versions because classes name and methods name often change from update to another :/

You will need Lsposed/Xposed framework to use it.(Root devices)

LSPatch(Non-Root devices)

## FAQ

### How to support more Instagram versions ?

*You can now use my new tool IGExperimentsPatcher to directly patch Instagram apk without waiting me for update something, it will give the method and class to patch too if you prefer to use this Xposed module. Find it [here](https://github.com/xHookman/IGExperimentsPatcher), it's totally automatic.*

#### Otherwise if you are a hacker wearing a very dark hood  ROOT Devices ONLY:

First you will need to use [Jadx](https://github.com/skylot/jadx)
 to decompile an [Instagram apk](https://www.apkmirror.com/apk/instagram/).

- Open Jadx and select your apk.
- Click on the text search button at top, wait for decompiling (it can takes several times)
- Search for "```is_employee```" or "```"is_employee", Boolean.valueOf```" and find a line similar to:

```
r2.A0i("is_employee", Boolean.valueOf(C8WQ.A03(userSession)));
```
<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/1.png?raw=true">

Double click on the method name, A03:

<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/2.png?raw=true">

Now go at top, you will see a line like this: 
```
/* renamed from: X.8WQ  reason: invalid class name */
```
<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/3.png?raw=true">


You now know the class to hook: X.8WQ!

You can now try if it works by enabling HECKER mode and completing the class name and method name field, click on hook and kill Instagram :)

### How to use it with Non-Root Devices?

* Install and setup LSPatch
* Check and Install one of the compatible Instagram versions from SupportedVersions.txt
* Patch Instagram with our mod using LSPatch
* Patch our mod with itself using LSPatch
* Done! if the instagram version wasn't supported you will see a pop up within the instagram app

#### If you was able to access the dev menu, please enable the is_employee option so you can update the app without patching!
