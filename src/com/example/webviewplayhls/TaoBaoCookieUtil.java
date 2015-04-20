package com.example.webviewplayhls;

import java.util.Date;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class TaoBaoCookieUtil
{
	private static final String TAG = "TaoBaoCookieUtil";
	private static TaoBaoCookieUtil mTaoBaoCookieUtil;
	private String cnaCookie = "";

	public static TaoBaoCookieUtil getInstance()
	{
		if (mTaoBaoCookieUtil == null)
			mTaoBaoCookieUtil = new TaoBaoCookieUtil();
		return mTaoBaoCookieUtil;
	}

	public void addCookie(Context paramContext, String paramString1, String paramString2)
	{
		CookieSyncManager.createInstance(paramContext);
		CookieManager localCookieManager = CookieManager.getInstance();
		localCookieManager.setAcceptCookie(true);
		localCookieManager.removeExpiredCookie();
		Date localDate = new Date();
		localDate.setTime(864000000L + localDate.getTime());
		localCookieManager.setCookie(paramString1, paramString2 + ";path=/;domain=.taobao.com;expires=" + localDate.toGMTString());
		CookieSyncManager.getInstance().sync();
	}

	public String getCnaCookie()
	{
		return this.cnaCookie;
	}

	public String getCookie(Context paramContext, String paramString)
	{
		CookieSyncManager.createInstance(paramContext);
		return CookieManager.getInstance().getCookie(paramString);
	}

	public String getCookieCNA(Context paramContext, String paramString)
	{
		String[] arrayOfString = null;
		int i = 0;
		String str1 = getCookie(paramContext, paramString);
		Object localObject = "";
		if ((str1 != null) && (str1.contains("cna=")))
		{
			arrayOfString = str1.split("; ");
			i = arrayOfString.length;
		}
		for (int j = 0;; ++j)
		{
			if (j >= i)
				;
			while (true)
			{

				String str2 = arrayOfString[j];
				if (!(str2.contains("cna=")))
					break;
				this.cnaCookie = str2;
				localObject = str2;
				return (String) localObject;
			}
		}
	}
}
