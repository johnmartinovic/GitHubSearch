package com.johnniem.githubsearch.common;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.johnniem.githubsearch.R;

/**
 * CGeneral helping tools
 */

public class Utils {

    public static String getNextLinkFromHeaderLink(String headerLink){
        if (headerLink == null) return null;

        int posNextBorder = headerLink.indexOf(">; rel=\"next\"");
        if ((posNextBorder < 0))
            return null;

        String nextLink = headerLink.substring(1, posNextBorder);

        return nextLink;
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a toast message.
     */
    public static void showSnackbar(View view,
                                 String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
