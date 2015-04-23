package com.konifar.facebooksdk4sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
  private static final List<String> READ_PERMISSIONS =
      Arrays.asList("email", "user_birthday", "user_friends");

  private CallbackManager callbackManager;
  private AccessTokenTracker accessTokenTracker;
  private ProfileTracker profileTracker;

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
            requestUserInfo(loginResult.getAccessToken());
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

    accessTokenTracker = new AccessTokenTracker() {
      @Override
      protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
          AccessToken currentAccessToken) {
        // On AccessToken changes fetch the new profile which fires the event on
        // the ProfileTracker if the profile is different
        Profile.fetchProfileForCurrentAccessToken();
      }
    };

    profileTracker = new ProfileTracker() {
      @Override protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
        setProfile(currentProfile);
      }
    };

    // Ensure that our profile is up to date
    Profile.fetchProfileForCurrentAccessToken();
    setProfile(Profile.getCurrentProfile());
  }

  private void requestUserInfo(AccessToken accessToken) {
    GraphRequest request =
        GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
          @Override
          public void onCompleted(JSONObject object, GraphResponse response) {
            Log.e(TAG, "response: " + response.toString());
          }
        });
    Bundle parameters = new Bundle();
    parameters.putString("fields", "id,name,email,gender,birthday");
    request.setParameters(parameters);
    request.executeAsync();
  }

  private void setProfile(Profile profile) {
    if (profile != null) {
      Log.e(TAG, "Name: " + profile.getName() + ", Id: " + profile.getId());
    } else {
      Log.e(TAG, "profile is null.");
    }
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

  @Override
  public void onDestroy() {
    super.onDestroy();
    accessTokenTracker.stopTracking();
    profileTracker.stopTracking();
  }

  @OnClick(R.id.btn_login) void onClickBtnLogin() {
    Log.e(TAG, "onClickBtnLogin");
    LoginManager manager = LoginManager.getInstance();
    manager.logInWithReadPermissions(this, READ_PERMISSIONS);
  }
}
