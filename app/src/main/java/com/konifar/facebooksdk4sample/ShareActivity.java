package com.konifar.facebooksdk4sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import java.util.Arrays;
import java.util.List;

public class ShareActivity extends FragmentActivity {

  private static final String TAG = ShareActivity.class.getSimpleName();
  private static final List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");

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

  @OnClick(R.id.btn_share) void onClickBtnShare() {
    // TODO
  }
}
