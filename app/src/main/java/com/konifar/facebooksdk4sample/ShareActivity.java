package com.konifar.facebooksdk4sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import java.util.Arrays;
import java.util.List;

/**
 * https://developers.facebook.com/docs/sharing/android
 */
public class ShareActivity extends FragmentActivity {

  private static final String TAG = ShareActivity.class.getSimpleName();
  private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
  private static final String SAMPLE_SHARE_URL = "http://konifar.hatenablog.com";
  private static final String SAMPLE_SHARE_TITLE = "Sample title";
  private static final String SAMPLE_SHARE_DESCRIPTION = "Sample description";
  private static final String SAMPLE_IMAGE_URL =
      "https://pbs.twimg.com/profile_images/2852902509/7426cb61a7c884a05b2b0daf0b583ec8.jpeg";

  @InjectView(R.id.txt_share_auto_result) TextView txtShareAutoResult;

  private CallbackManager callbackManager;

  static void start(Context context) {
    Intent intent = new Intent(context, ShareActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_share);
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
            shareToFacebookFeed();
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
    toolbar.setTitle(R.string.share);
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @OnClick(R.id.btn_share_dialog) void onClickBtnShareDialog() {
    if (ShareDialog.canShow(ShareLinkContent.class)) {
      ShareLinkContent content =
          new ShareLinkContent.Builder().setContentUrl(Uri.parse(SAMPLE_SHARE_URL))
              .setContentTitle(SAMPLE_SHARE_TITLE)
              .setContentDescription(SAMPLE_SHARE_DESCRIPTION)
              .build();
      ShareDialog.show(this, content);
    }
  }

  @OnClick(R.id.btn_share_auto) void onClickBtnShareAuto() {
    LoginManager manager = LoginManager.getInstance();
    manager.logInWithPublishPermissions(this, PUBLISH_PERMISSIONS);
  }

  @OnClick(R.id.btn_share_photo_auto) void onClickBtnSharePhotoAuto() {
    LoginManager manager = LoginManager.getInstance();
    manager.logInWithPublishPermissions(this, PUBLISH_PERMISSIONS);
  }

  private void shareToFacebookFeed() {
    ShareLinkContent content =
        new ShareLinkContent.Builder().setContentUrl(Uri.parse(SAMPLE_SHARE_URL))
            .setContentTitle(SAMPLE_SHARE_TITLE)
            .setContentDescription(SAMPLE_SHARE_DESCRIPTION)
            .setImageUrl(Uri.parse(SAMPLE_IMAGE_URL))
            .build();
    ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
      @Override public void onSuccess(Sharer.Result result) {
        Log.e(TAG, "onSuccess");
        txtShareAutoResult.setText(R.string.share_auto_completed);
      }

      @Override public void onCancel() {
        Log.e(TAG, "onCancel");
        txtShareAutoResult.setText(R.string.share_auto_failed);
      }

      @Override public void onError(FacebookException e) {
        Log.e(TAG, "onError");
        txtShareAutoResult.setText(R.string.share_auto_failed);
      }
    });
  }

  private void sharePhotoToFacebook() {
    SharePhoto photo = new SharePhoto.Builder().setImageUrl(Uri.parse(SAMPLE_IMAGE_URL)).build();
    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo)
        .setContentUrl(Uri.parse(SAMPLE_SHARE_URL))
        .setRef(SAMPLE_SHARE_TITLE)
        .build();

    ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
      @Override public void onSuccess(Sharer.Result result) {
        Log.e(TAG, "onSuccess");
        txtShareAutoResult.setText(R.string.share_auto_completed);
      }

      @Override public void onCancel() {
        Log.e(TAG, "onCancel");
        txtShareAutoResult.setText(R.string.share_auto_failed);
      }

      @Override public void onError(FacebookException e) {
        Log.e(TAG, "onError");
        txtShareAutoResult.setText(R.string.share_auto_failed);
      }
    });
  }
}
