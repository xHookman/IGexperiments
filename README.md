# IGExperiments

Enable developer options in Instagram!


## Installation

* Rooted Devices

  1. Install the module and enable it using (magisk, Edxposed, LSposed etc...)
  2. Run the module and select the desired Instagram version.
  3. Kill instagram
  4. Done! you can check the developer options by holding on the home button.
     
  Extra: You can use Hecker mode to test newer versions with its Method and Classes

* Non-Rooted Devices (Using LSPatch)

  1. Install the module
  2. Check the list of compatible versions and install the desired one
  3. Patch the Instagram app using LSPatch (Local Patch Mode)
  4. Add the module to the Instagram Module Scope using LSPatch
  5. Force stop Instagram and start it!
  6. Done! you can check the developer options by holding on the home button.


## How to update Instagram
### Enable Employee options!
* Rooted Devices
  
1. Go to Developer Options Page
2. MetaConfig Settings & Overrides
3. Search for "Employee"
4. Enable "is employee", "employee options", "is employee or test user"
5. Disable the Modulde from (magisk, Edxposed, LSposed etc...)
5. Download and Install a newer version from Apkmirror

* Non-Rooted Devices (Using LSPatch)

1. Go to Developer Options Page
2. MetaConfig Settings & Overrides
3. Search for "Employee"
4. Enable "is employee", "employee options", "is employee or test user"
5. Download a newer version from Apkmirror
6. Use LSPatch to patch the APK "Select apk(s) from storage"
7. Select "Local" as a Patch mode, Patch and Install!



## FAQ


#### The module doesn't start up.


Open a ticket including your device name, OS, root status, and the logs from LSPosed if rooted.


Otherwise, we won't be able to help you!


#### The module starts, but the developer options are not showing up.

* Make sure you have installed one of the combitaple versions of Instagram.
* Make sure you have enabled the module from LSPosed/EdXposed/etc.
* If none worked, check LSPosed logs and send them to us by opening a ticket.
* Please note that if the logs don't include anything wrong, we won't be able to help you because most likely
   it's something with your device.

#### Where is the home button?


* The home button is the Instagram home button (not the phone home button, apparently!).

#### If you think you are smart and can solve a problem,


* Just fork it and contrubiute to the repository; don't come up to us insulting our team just because you don't know how to run it.





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
r2.A0i("is_employee", Boolean.valueOf(C1AX.A00(c12990lb)));
```
<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/1.png?raw=true">

Double click on the method name, A03:

<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/2.png?raw=true">

Now go at top, you will see a line like this: 
```
/* renamed from: X.1AX reason: invalid class name */
```
<img src="https://github.com/xHookman/IGexperiments/blob/master/readme/3.png?raw=true">


You now know the class to hook: X.1AX

Method to hook: A00

Second class to hook: X.0lb (the last three characters of 'c12990lb' for example)

You can now try if it works by enabling HECKER mode and completing the class name and method name field, click on hook and kill Instagram - Root devices ONLY!

## Authors

- [@xHookman](https://github.com/xHookman)
- [@ReSo7200](https://github.com/ReSo7200)
- [@Vasilis](https://github.com/down-bad)
- [@rmnscnce](https://github.com/rmnscnce)

