package com.reactlibrary;


import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

import java.util.concurrent.Executor;

public class BiometricsModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private final String RESULT_VALUE_KEY = "result";
    private final String RESULT_ERROR_CODE_KEY = "error_code";


    BiometricsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Biometrics";
    }


    @ReactMethod
    public void getCurrentBiometricType(Callback callback) {
        if (BiometricManager.from(reactContext).canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            callback.invoke("","feature_none");
            return;
        }
        if (reactContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FACE)) {
            callback.invoke("","feature_face");
            return;
        }
        if (reactContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            callback.invoke("","feature_fingerprint");
            return;
        }
        callback.invoke("","feature_none");
    }

    @ReactMethod
    public void openBiometricDialog(final Callback onSuccess, final Callback onFail) {
        BiometricPrompt.AuthenticationCallback authCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                WritableMap map = Arguments.createMap();
                map.putBoolean(RESULT_VALUE_KEY, false);
                map.putInt(RESULT_ERROR_CODE_KEY, errorCode);


                onFail.invoke("",map);
                biometricPrompt.cancelAuthentication();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                WritableMap map = Arguments.createMap();
                map.putBoolean(RESULT_VALUE_KEY, true);

                onSuccess.invoke("",map);
                biometricPrompt.cancelAuthentication();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        };


        Executor executor = ContextCompat.getMainExecutor(reactContext);
        FragmentActivity currentActivity = (FragmentActivity) getCurrentActivity();

        assert currentActivity != null;
        biometricPrompt = new BiometricPrompt(currentActivity, executor,
                authCallback
        );

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(currentActivity.getString(R.string.biometricsTitle))
                .setSubtitle(currentActivity.getString(R.string.biometricsDescription))
                .setNegativeButtonText(currentActivity.getString(R.string.biometricsCancel))
                .build();

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                biometricPrompt.authenticate(promptInfo);
            }
        });


    }
}
