package com.konifar.facebooksdk4sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
  private static final List<String> READ_PERMISSIONS =
      Arrays.asList("email", "user_birthday", "user_friends");

  private CallbackManager callbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initFacebook();
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    Log.e(TAG, "onCreate");

    initToolbar();
  }

  @Override
  protected void onResume() {
    super.onResume();
    // Logs 'install' and 'app activate' App Events.
    AppEventsLogger.activateApp(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    // Logs 'app deactivate' App Event.
    AppEventsLogger.deactivateApp(this);
  }

  private void initFacebook() {
    FacebookSdk.sdkInitialize(getApplicationContext());

    callbackManager = CallbackManager.Factory.create();

    LoginManager.getInstance()
        .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
            Log.e(TAG, "onSuccess");
          }

          @Override
          public void onCancel() {
            Log.e(TAG, "onCancel");
          }

          @Override
          public void onError(FacebookException exception) {
            Log.e(TAG, "onError");
          }
        });
  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(R.string.app_name);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @OnClick(R.id.btn_login) void onClickBtnLogin() {
    Log.e(TAG, "onClickBtnLogin");
    LoginManager manager = LoginManager.getInstance();
    manager.logInWithReadPermissions(this, READ_PERMISSIONS);
  }
}
