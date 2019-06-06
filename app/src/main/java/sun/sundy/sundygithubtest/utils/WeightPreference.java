package sun.sundy.sundygithubtest.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Sundy
 * create at 2018/7/13 15:59
 */
public class WeightPreference {

	private static WeightPreference instance;
	private SharedPreferences mSp;

	private WeightPreference(Context context) {
		mSp = context.getSharedPreferences("weight_preference", Context.MODE_PRIVATE);
	}

	public synchronized static WeightPreference getInstance(Context context) {
		if (instance == null) {
			instance = new WeightPreference(context.getApplicationContext());
		}
		return instance;
	}

	public boolean isFirstIn(){
		return mSp.getBoolean("isFirstIn",true);
	}

	public void setIsFirstIn(Boolean isFirstIn){
		mSp.edit().putBoolean("isFirstIn",isFirstIn).apply();
	}

}
