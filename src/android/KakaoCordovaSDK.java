package com.raccoondev85.plugin.kakao;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.user.UserApiClient;



import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class KakaoCordovaSDK extends CordovaPlugin {

    private static final String LOG_TAG = "KakaoCordovaSDK";

    private static final int GALLERY_REQUEST_CODE = 9238;
    private String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private final String emptyString = "";
    private String hashKey;
    private JSONObject result;

    public void pluginInitialize() {
        Log.v(LOG_TAG, "kakao : initialize");
        super.pluginInitialize();
        result = new JSONObject();
        Application app = cordova.getActivity().getApplication();
        String packageName = app.getPackageName();
        int app_key = app.getResources().getIdentifier("kakao_app_key", "string", packageName);
        try {
            KakaoSdk.init(cordova.getActivity(), app.getString(app_key));

        } catch (Exception e) {

        }

    }

    public boolean execute(final String action, final JSONArray options, final CallbackContext callbackContext)
            throws JSONException {
        Log.v(LOG_TAG, "kakao : execute " + action);
        cordova.setActivityResultCallback(this);

        if (action.equals("login")) {
            this.login(callbackContext);
            return true;
        }
//        else if (action.equals("logout")) {
//            this.logout(callbackContext);
//            return true;
//        } else if (action.equals("unlinkApp")) {
//            this.unlinkApp(callbackContext);
//            return true;
//        }
        return false;
    }

     private void login(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Context context = cordova.getActivity();

                if(LoginClient.getInstance().isKakaoTalkLoginAvailable(cordova.getActivity())){
                    loginWithKakaoTalk(callbackContext);
                }else{
                    loginWithKakaoAccount(callbackContext);
                }

            }
        });
    }

    private void loginWithKakaoAccount(final CallbackContext callbackContext){
        LoginClient.getInstance().loginWithKakaoAccount(cordova.getActivity(), (token, error) ->{
            if(token != null){
                UserApiClient(callbackContext);
                try{
                    result.put("accessToken", token.getAccessToken());
                    result.put("expiresAt", token.getAccessTokenExpiresAt());
                    result.put("refreshToken", token.getRefreshToken());
                    Log.i("success", "success");

                }catch (Exception e){
                    callbackContext.error(e.getMessage());
                }

            }else{
                Log.e("error", "error");
                callbackContext.error(error.getMessage());
            }
            return null;
        });
    }

    private void loginWithKakaoTalk(final CallbackContext callbackContext){
        LoginClient.getInstance().loginWithKakaoTalk(cordova.getActivity(), (token, error) ->{
            if(token != null){
                UserApiClient(callbackContext);
                try{
                    result.put("accessToken", token.getAccessToken());
                    result.put("accessTokenExpiresAt", token.getAccessTokenExpiresAt());
                    result.put("refreshToken", token.getRefreshToken());
                    Log.i("success", "success");

                }catch (Exception e){
                    callbackContext.error(e.getMessage());
                }

            }else{
                callbackContext.error(error.getMessage());
            }
            return null;
        });
    }

    private void UserApiClient(final CallbackContext callbackContext){
        UserApiClient.getInstance().me((user, error) -> {
            if(user != null){
                try{
                    result.put("id", user.getId());
                }catch (Exception e){
                    callbackContext.error(e.getMessage());
                }
                callbackContext.success(result);
            }else{
                callbackContext.error(error.getMessage());
            }

            return null;
        });
    }

    //
//    private void requestMe(final CallbackContext callbackContext) {
//        cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Session.getCurrentSession().addCallback(new SessionCallback(callbackContext));
//                kakaoMeV2ResponseCallback = new KakaoMeV2ResponseCallback(callbackContext);
//                UserManagement.getInstance().me(kakaoMeV2ResponseCallback);
//            }
//        });
//
//    }
//
//    private void logout(final CallbackContext callbackContext) {
//        cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Session.getCurrentSession().addCallback(new SessionCallback(callbackContext));
//                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
//                    @Override
//                    public void onCompleteLogout() {
//                        Log.v(LOG_TAG, "kakao : onCompleteLogout");
//                        callbackContext.success("true");
//                    }
//                });
//            }
//        });
//    }
//
//    private void unlinkApp(final CallbackContext callbackContext) {
//        cordova.getThreadPool().execute(new Runnable() {
//            @Override
//            public void run() {
//                Session.getCurrentSession().addCallback(new SessionCallback(callbackContext));
//
//                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
//                    @Override
//                    public void onFailure(ErrorResult errorResult) {
//                        KakaoCordovaErrorHandler.errorHandler(callbackContext, errorResult);
//                    }
//
//                    @Override
//                    public void onSessionClosed(ErrorResult errorResult) {
//                        KakaoCordovaErrorHandler.errorHandler(callbackContext, errorResult);
//                        Session.getCurrentSession().checkAndImplicitOpen();
//                    }
//
//                    @Override
//                    public void onSuccess(Long userId) {
//                        callbackContext.success(Long.toString(userId));
//                    }
//
//                    @Override
//                    public void onNotSignedUp() {
//                        KakaoCordovaErrorHandler.errorHandler(callbackContext, "this user is not signed up");
//                    }
//                });
//            }
//        });
//    }

}
