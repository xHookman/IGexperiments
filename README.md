<img src="https://socialify.git.ci/xHookman/IGexperiments/image?description=1&font=Source%20Code%20Pro&language=1&name=1&pattern=Solid&pulls=1&stargazers=1&theme=Light" alt="IGexperiments" width="640" height="320" />

# IGExperiments

🔓 **Unlock Instagram's Developer Options!** 🚀✨
IGExperiments is a module that unlocks Instagram's hidden developer options, giving you access to advanced features like **White Hat settings** (enables SSL unpinning), **Test User mode**, and much more.

## Features

- Enable Developer Options in Instagram (rooted or non-rooted devices).
- Multiple modes: Normal, Hecker (manual), and Auto (automatic detection).
- Compatible with both rooted and non-rooted devices (via LSPatch).

### Root vs Non-Root Functionality

| Feature                          | Rooted Devices                      | Non-Rooted Devices (LSPatch)     |
|-----------------------------------|-------------------------------------|----------------------------------|
| **Modes Available**               | Normal, Hecker, Auto                | Normal (Manual updates)          |
| **Auto Mode (Auto-detect hooks)** | ✅ Yes                              | ❌ No                             |
| **Hecker Mode (Manual classes)**  | ✅ Yes                              | ❌ No                             |
| **Normal Mode (Standard hooks)**  | ✅ Yes                              | ✅ Yes                            |
| **Kill Button (Force stop/start)**| ✅ Yes (Force stop Instagram)       | ❌ No                             |
| **Manual Updates**                | ✅ Yes (Easier and often automatic) | ✅ Yes (Requires manual patching) |

> **Note**:  
> - **Rooted Devices** have full access to all modes, and manual updates are easier or automatic in some cases.  
> - **Non-Rooted Devices** are limited to **Normal Mode**, which requires manual updates and patching using LSPatch.


## Installation

### For Rooted Devices

1. Install the module and activate it using **Magisk**, **EdXposed**, **LSPosed**, etc.
2. Select the desired mode in the module (Normal, Hecker, or Auto).
3. Kill the Instagram app to apply the changes.
4. Done! Access developer options by long-pressing the Instagram home button.

> **Note:**  
- **Auto Mode** works with Instagram versions 334 or higher.  
- **Hecker Mode** allows you to manually specify classes and methods for older or custom versions.

### For Non-Rooted Devices (via LSPatch)

1. Install the module.
2. Check the list of compatible Instagram versions and install the desired version.
3. Patch the Instagram app using **LSPatch**.
4. Force stop Instagram and restart it.
5. Done! Access developer options by long-pressing the Instagram home button.

> **Note:**  
- The module requires Instagram to be patched in **Local Patch Mode** via **LSPatch** in order to apply updates.

---

## How to Update Instagram

#### Rooted Devices

**Option 1: Auto Mode**

1. Uninstall Instagram.
2. Install the latest version from the **Google Play Store** or **APKMirror**.
3. Use **Auto Mode** in the module to re-enable developer options.

**Option 2: Legacy Method (_Enable Employee Options_)**

1. Go to **Developer Options** > **MetaConfig Settings & Overrides**.
2. Search for **Employee**.
3. Enable:
   - **is employee**
   - **employee options**
   - **is employee or test user**
4. Disable the module from **Magisk**, **EdXposed**, **LSPosed**, etc.
5. Install a newer version of Instagram from **APKMirror**.

#### Non-Rooted Devices (via LSPatch)

1. Go to **Developer Options** > **MetaConfig Settings & Overrides**.
2. Search for **Employee**.
3. Enable:
   - **is employee**
   - **employee options**
   - **is employee or test user**
4. Download a newer version of Instagram from **APKMirror**.
5. Patch the APK using **LSPatch** and choose **Local Patch Mode**.

---

## FAQ

### The module doesn't start.

1. Ensure you have root access if required.
2. Open a ticket and provide your device name, OS version, root status, and the logs from LSPosed (if rooted).

### Developer options are not showing up.

1. Ensure you've installed a compatible Instagram version.
2. Ensure the module is enabled in **LSPosed**, **EdXposed**, or your preferred manager.
3. Deactivate and reactivate the module if necessary.
4. If none of the above works, check the logs in **LSPosed** and open a ticket with the log details.

### Where is the home button?

The **Instagram home button** is the one at the bottom of the app, not the home button on your phone.

---

## Contributing

Contributions are always welcome! You can help keep the module up-to-date or extend its functionality.

### Supporting New Instagram Versions

#### Option 1: IGExperimentsPatcher

You can use my [**IGExperimentsPatcher**](https://github.com/xHookman/IGExperimentsPatcher) tool to patch the Instagram APK directly. It can provide the necessary class, method, and argument types to use in the module without waiting for an update.

#### Option 2: Manual Class Hooks

If you're an experienced developer, you can help by contributing to the [**IGHookClasses repository**](https://github.com/ReSo7200/IGExperimentsHooksUpdates) to update the class hooks for new Instagram versions.

---

## Authors

- [@xHookman](https://github.com/xHookman) - Main developer and creator of the IGExperiments module.
- [@ReSo7200](https://github.com/ReSo7200) - Contributor and maintainer of IGHookClasses.
