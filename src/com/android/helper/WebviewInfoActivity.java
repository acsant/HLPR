package com.android.helper;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewInfoActivity extends FragmentActivity {
	
	private WebView webView;
	ProgressDialog mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_info);
		
		Intent webInfoIntent = getIntent();
		webView = (WebView) findViewById(R.id.webViewInfo);
		mProgress = new ProgressDialog(this);
		mProgress.setTitle("Please Wait");
		mProgress.setMessage("Loading Webpage...");
		mProgress.setCanceledOnTouchOutside(false);
		mProgress.show();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.setWebViewClient(new CustomWebViewClient());
		webView.loadUrl(webInfoIntent.getStringExtra(InformationDisplay.InformationList.WEB_INFO_EXTRA));
		setupActionBar();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			super.onBackPressed();
		}
	}
	
	private class CustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!mProgress.isShowing()) {
				mProgress.show();
			}
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public void onPageFinished (WebView view, String url) {
			if (mProgress.isShowing()) {
				mProgress.dismiss();
			}
		}

	}
}
