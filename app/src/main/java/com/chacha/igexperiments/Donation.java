package com.chacha.igexperiments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class Donation {

    /**
     * Open the donation link
     * @param context context
     */
    public static void openDonationLink(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A3YW496LXQZ5A&source=url"));
        context.startActivity(browserIntent);
        Toast.makeText(context, context.getResources().getString(R.string.donation_thanks), Toast.LENGTH_LONG).show();
    }

    /**
     * Remind the user to give a donation by an alert dialog
     * @param context context
     */
    public static void remindDonation(Context context) {
        int nb = Preferences.getPrefs().getInt("donationReminder", 0);
        if (Preferences.getPrefs().getBoolean("donationRemindBool", true)) {
            if (nb % 4 == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, 4);
                builder.setMessage("Hey, do not forget to give a donation for these free works if you can, I'm a student :) It will be very nice if you do it")
                        .setNegativeButton("Pleeeeeeeaaaaase stop", (dialog, id) -> {
                            Preferences.getEditor().putBoolean("donationRemindBool", false);
                            Preferences.getEditor().apply();
                        })
                        .setNeutralButton("Later", (dialog, id) -> {})
                        .setPositiveButton("Do it now", (dialog, id) -> {
                            Preferences.getEditor().putBoolean("donationRemindBool", false);
                            openDonationLink(context);
                            Preferences.getEditor().apply();
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            Preferences.getEditor().putInt("donationReminder", nb + 1);
            Preferences.getEditor().apply();
        }
    }
}
