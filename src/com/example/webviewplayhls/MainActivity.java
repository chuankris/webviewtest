package com.example.webviewplayhls;

import java.lang.reflect.InvocationTargetException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity
{
	/*private String url = "http://122.225.101.114:8069/iPad/jwplayer.html ";*/
	//private String url="http://v.youku.com/v_show/id_XNjkxMDM4ODg4_ev_5.html";
	//private String url = "http://detail.m.tmall.com/item.htm?id=41198959064&fm=detail";
	//private String url = "http://ah2.zhangyue.com/zybook/app/app.php?ca=Channel.Index&usr=i473103044&rgt=7&p1=150204163200951807&p2=108601&p3=770003&p4=501603&p5=16&p6=IJIGAAAEAGBDJFCFAHID&p7=IGADAIACGEADCFJ&p9=0&p16=MI+2&p21=10203&pk=5B101&launch=inpage&inav=1&key=5B4";
	private String url = "http://wap.cmread.com/rbc/p/zonghesy.jsp?vt=3";
	private View myView = null;
	private WebView mWebView;
	private Context mContext;
	private WebViewClient mWebViewClient = new WebViewClient()
	{
		String cnaCookie = null;

		// 处理页面导航
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (url.startsWith("http://h5.m.taobao.com/awp/core/detail.htm"))
			{
				this.cnaCookie = TaoBaoCookieUtil.getInstance().getCookieCNA(mContext, url);
				if ((this.cnaCookie == null) && ((TaoBaoCookieUtil.getInstance().getCnaCookie()) != null))
				{
					TaoBaoCookieUtil.getInstance().addCookie(mContext, url, TaoBaoCookieUtil.getInstance().getCnaCookie());
					mWebView.loadUrl(url);
				}
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			Log.e("qxc", "onPageFinished");
			super.onPageFinished(view, url);
			view.loadUrl("javascript:window.local_obj.showSource('<head>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			Log.e("qxc", "onpagestarted");
			super.onPageStarted(view, url, favicon);

		}
	};

	final class InJavaScriptLocalObj
	{
		public void showSource(String html)
		{
			Log.e("qxc", "html = " + html);

			Toast.makeText(mContext, html, Toast.LENGTH_LONG).show();
		}
	}

	// 浏览网页历史记录
	// goBack()和goForward()
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			mChromeClient.onHideCustomView();
		}
		return super.onKeyDown(keyCode, event);
	}

	private WebChromeClient mChromeClient = new WebChromeClient()
	{

		private CustomViewCallback myCallback = null;

		// 配置权限 （在WebChromeClinet中实现）
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
		{
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		// 扩充数据库的容量（在WebChromeClinet中实现）
		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota,
		        WebStorage.QuotaUpdater quotaUpdater)
		{

			quotaUpdater.updateQuota(estimatedSize * 2);
		}

		// 扩充缓存的容量
		@Override
		public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
		{

			quotaUpdater.updateQuota(spaceNeeded * 2);
		}

		// Android 使WebView支持HTML5 Video（全屏）播放的方法
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback)
		{
			if (myCallback != null)
			{
				myCallback.onCustomViewHidden();
				myCallback = null;
				return;
			}

			ViewGroup parent = (ViewGroup) mWebView.getParent();
			parent.removeView(mWebView);
			parent.addView(view);
			myView = view;
			myCallback = callback;
			mChromeClient = this;
		}

		@Override
		public void onHideCustomView()
		{
			if (myView != null)
			{
				if (myCallback != null)
				{
					myCallback.onCustomViewHidden();
					myCallback = null;
				}

				ViewGroup parent = (ViewGroup) myView.getParent();
				parent.removeView(myView);
				parent.addView(mWebView);
				myView = null;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		mWebView = (WebView) findViewById(R.id.test);
		mWebView.setWebChromeClient(mChromeClient);
		mWebView.setWebViewClient(mWebViewClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		//	String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		//Log.i("TAG", "appCacheDir==" + appCacheDir);
		//mWebView.getSettings().setAppCachePath(appCacheDir);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 1);// 设置缓冲大小，我设的是10M
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setPluginsEnabled(true);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

		mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mWebView.getSettings().setBuiltInZoomControls(true);
		//            mWebView.getSettings().setDisplayZoomControls(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		String cnaCookie = null;
		try
		{
			mWebView.loadUrl(url);
			//mWebView.loadUrl(url);
		}
		catch (Exception e)
		{

			finish();
		}

	}

	class iface
	{
		public void print(String data)
		{
			data = "" + data + "";
			System.out.println(data);
			//DO the stuff
		}
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		try
		{
			mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		mChromeClient.onHideCustomView();
		mWebView.onPause();
		mWebView.pauseTimers();

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		try
		{
			mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		mWebView.onResume();
		mWebView.resumeTimers();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mWebView.stopLoading();
		mWebView.destroy();
	}
}
