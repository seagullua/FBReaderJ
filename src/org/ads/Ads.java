package org.ads;
import com.google.android.gms.ads.*;
import android.app.Activity;

public class Ads
{
	static private InterstitialAd interstitial;
	static private InterstitialAd previous;
	static private String MY_AD_UNIT_ID = "ca-app-pub-1612697960946304/9433789877";
	static private Activity activity = null;
	
	static public void init(Activity act)
	{
		activity = act;
	}
	
	static public void prepare()
	{
		if(interstitial == null)
		{
			interstitial = new InterstitialAd(activity);
			interstitial.setAdUnitId(MY_AD_UNIT_ID);

			// Create ad request.
			AdRequest adRequest = new AdRequest.Builder().build();

			// Begin loading your interstitial.
			interstitial.loadAd(adRequest);
		}
	}
	
	static public void showHere()
	{	
		prepare();
		if (interstitial.isLoaded()) {
			previous = interstitial;
			previous.show();
			interstitial = null;
			prepare();
		}
	}
}
