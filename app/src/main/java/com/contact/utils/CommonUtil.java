package com.contact.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class CommonUtil {


    /*
     * Called to check permission(In Android M and above versions only)
     *
     * @param permission, which we need to pass
     * @return true, if permission is granted else false
     */
    public static boolean checkForPermission(final Context context, final String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        //If permission is granted then it returns 0 as result
        return result == PackageManager.PERMISSION_GRANTED;
    }

}
