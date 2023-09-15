<img src="https://socialify.git.ci/xHookman/IGexperiments/image?description=1&font=Source%20Code%20Pro&language=1&name=1&pattern=Solid&pulls=1&stargazers=1&theme=Light" alt="IGexperiments" width="640" height="320" />
# IGExperiments


Enable developer options in Instagram!




## Installation

* Rooted Devices
```bash
  1. Install the module and enable it using (magisk, Edxposed, LSposed etc...)
  2. Run the module and select the desired Instagram version.
  3. Kill instagram
  4. Done! you can check the developer options by holding on the home button.
```
* Non-Rooted Devices (Using LSPatch)
```bash
  1. Install the module and one of the compatible Instagram versions
  2. Patch the Instagram app with the module
  3. Patch the module with itself
  4. Done! you can check the developer options by holding on the home button.
```

## How to update Instagram
### Enable Employee options!
* Go to Developer Options Page
* MetaConfig Settings & Overrides
* Search for "Employee"
* Enable "is employee", "employee options", "is employee or test user"
* Download a newer version from Apkmirror
* Use LSPatch to patch the APK "Select apk(s) from storage"
* Select "Local" as a Patch mode, Patch and Install! 
## Contributing

Contributions are always welcome!

To support newer versions:

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

## Authors

- [@xHookman](https://github.com/xHookman)
- [@ReSo7200](https://github.com/ReSo7200)
- [@Vasilis](https://github.com/down-bad)
- [@rmnscnce](https://github.com/rmnscnce)

