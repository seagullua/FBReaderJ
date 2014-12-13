package org.ads;
import com.google.android.gms.ads.*;
import android.app.Activity;

public class Ads
{
	static private InterstitialAd interstitial;
	static private InterstitialAd previous;
	static private String MY_AD_UNIT_ID = "ca-app-pub-1612697960946304/9433789877";
	static private Activity activity = null;
	static private long last_shown = 0;
	static private long time_between = 110;
	static private long grow_each = 10;
	
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
			long current_time = System.currentTimeMillis()/1000;
			if(current_time - last_shown > time_between)
			{
				previous = interstitial;
				previous.show();
				interstitial = null;
				
				last_shown = current_time;
				time_between += grow_each;
				prepare();
			}
		}
	}
}
