package com.konifar.facebooksdk4sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.Arrays;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static final List<String> READ_PERMISSIONS =
      Arrays.asList("email", "user_birthday", "user_friends");

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String PICTURE = "picture";
  private static final String EMAIL = "email";
  private static final String BIRTHDAY = "birthday";
  private static final String GENDER = "gender";
  private static final String REQUEST_FIELDS =
      TextUtils.join(",", new String[] { ID, NAME, PICTURE, EMAIL, BIRTHDAY, GENDER });
  private static final String FIELDS = "fields";

  @InjectView(R.id.img_profile) ImageView imgProfile;
  @InjectView(R.id.txt_id) TextView txtId;
  @InjectView(R.id.txt_name) TextView txtName;
  @InjectView(R.id.txt_birthday) TextView txtBirthday;
  @InjectView(R.id.txt_email) TextView txtEmail;
  @InjectView(R.id.txt_gender) TextView txtGender;

  private CallbackManager callbackManager;
  private AccessTokenTracker accessTokenTracker;
  private ProfileTracker profileTracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    Log.e(TAG, "onCreate");

    initFacebook();
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
            JSONObject json = response.getJSONObject();
            renderView(json);
          }
        });
    Bundle parameters = new Bundle();
    parameters.putString(FIELDS, REQUEST_FIELDS);
    request.setParameters(parameters);
    request.executeAsync();
  }

  private void renderView(JSONObject json) {
    try {
      final long id = json.getLong(ID);
      final String name = json.getString(NAME);
      final String email = json.getString(EMAIL);
      final String picture = json.getJSONObject("picture").getJSONObject("data").getString("url");
      final String birthday = json.getString(BIRTHDAY);
      final String gender = json.getString(GENDER);

      txtId.setText(String.valueOf(id));
      txtName.setText(name);
      txtBirthday.setText(birthday);
      txtGender.setText(gender);
      txtEmail.setText(email);
      Log.e(TAG, "picture: " + picture);
      ImageLoader.getInstance().displayImage(picture, imgProfile);
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  private void setProfile(Profile profile) {
    if (profile != null) {
      txtId.setText(String.valueOf(profile.getId()));
      txtName.setText(profile.getName());
      ImageLoader.getInstance()
          .displayImage(profile.getProfilePictureUri(100, 100).toString(), imgProfile);
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

  @OnClick(R.id.btn_share) void onClickBtnShare() {
    ShareActivity.start(this);
  }
}
