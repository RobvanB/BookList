package com.vanbran.booklist;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class about extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		WebView webView = new WebView(this);
		setContentView(webView);
		
		String url = "file:///android_asset/about.html";
		webView.loadUrl(url) ;
	}	
}	

