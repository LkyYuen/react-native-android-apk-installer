package com.lkyyuen.androidapkinstaller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.File;

public class AndroidApkInstallerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private static final int REQUEST_CODE = 1;
    private static final String E_INSTALL_CANCELLED = "E_INSTALL_CANCELLED";

    private Promise mPickerPromise;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if (requestCode == REQUEST_CODE) {
                if (mPickerPromise != null) {
                    // Log.d("SUPER-PORTAL-APP", String.valueOf(resultCode));
                    // Log.d("SUPER-PORTAL-APP", String.valueOf(Activity.RESULT_CANCELED));
                    if (resultCode == Activity.RESULT_CANCELED) {
                        mPickerPromise.resolve("401");
                    } else if (resultCode == Activity.RESULT_OK) {
                        mPickerPromise.resolve("200");
                    } else {
                        mPickerPromise.reject("400");
                    }

                    mPickerPromise = null;
                }
            }
        }
    };

    public AndroidApkInstallerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(mActivityEventListener); //Register this native module as Activity result listener
    }

    @Override
    public String getName() {
        return "AndroidApkInstaller";
    }

    @ReactMethod
    public void install(String filePath, Promise promise) {

        Bundle bundle= new Bundle();

        Intent intent = new Intent();
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        // intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        File apkFile = new File(filePath);
        Uri apkUri;

        // Store the promise to resolve/reject when picker returns data
        mPickerPromise = promise;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String authority = reactContext.getPackageName() + ".fileprovider";
                apkUri = FileProvider.getUriForFile(reactContext, authority, apkFile);
            } else {
                apkUri = Uri.fromFile(apkFile);
            }
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            reactContext.startActivityForResult(intent, REQUEST_CODE, bundle);

        } catch (Exception e) {
            mPickerPromise.reject(e);
            mPickerPromise = null;
        }
    }
}
