package jp.ac.keio.sfc.masui.wakerock;

import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmView extends Activity {
	private Button mCancel;
	private Button mModify;
	private TextView mState;
	private TextView mAlarmTime;
	private int alarm_year;
	private int alarm_month;
	private int alarm_day;
	private int alarm_hour;
	private int alarm_minute;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private String uid;
	private String pass;
	private int rid;
	private boolean matched = false;
	private boolean waited = false;
	private String target_phone_number = null;
	private ScheduledExecutorService service;
	private Handler handler = new Handler();
	private int count;

	@Override
    public void onDestroy(){
		super.onDestroy();
		service.shutdown();
		api_access_cancel();
	}
/*	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("*********** requestCode ***********", Integer.toString(requestCode));
		Log.v("*********** resultCode **************", Integer.toString(resultCode));
		startActivity(new Intent(this, LoginView.class));
	}
*/	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.alarmview);
        setViews();
        Bundle extras = getIntent().getExtras();
        setExtras(extras);
        setTime();
        this.mAlarmTime.setText(
        	"Alarm: "	
        	+ Integer.toString(this.alarm_year)
        	+ "-"
        	+ Integer.toString(this.alarm_month + 1)
        	+ "-"
        	+ Integer.toString(this.alarm_day)
        	+ " "
        	+ Integer.toString(this.alarm_hour)
        	+ ":"
        	+ Integer.toString(this.alarm_minute)
        	);
        service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						count++;
						if(count == Consts.TIME_TO_CHECK_MATCHING) {
							api_access_matched();
							//AlarmView.this.matched = true;
							//AlarmView.this.waited = false;
							//AlarmView.this.target_phone_number = "12345";
							count = 0;
						}
						setTime();
						if(AlarmView.this.matched && AlarmView.this.waited == false
								&& AlarmView.this.alarm_year == AlarmView.this.mYear
								&& AlarmView.this.alarm_month == AlarmView.this.mMonth
								&& AlarmView.this.alarm_day == AlarmView.this.mDay
								&& AlarmView.this.alarm_hour == AlarmView.this.mHour
								&& AlarmView.this.alarm_minute == AlarmView.this.mMinute){
							callPhone();
							//SystemClock.sleep(1000);
							service.shutdown();
							//finish();
						}
					}
				});
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	        
        mModify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(AlarmView.this, RegisterView.class);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_YEAR, AlarmView.this.alarm_year);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_MONTH, AlarmView.this.alarm_month);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_DAY, AlarmView.this.alarm_day);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_HOUR, AlarmView.this.alarm_hour);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_MINUTE, AlarmView.this.alarm_minute);
            	intent.putExtra(Consts.EXTRA_KEY_USER_ID, AlarmView.this.uid);
            	intent.putExtra(Consts.EXTRA_KEY_PASSWORD, AlarmView.this.pass);
                startActivity(intent);
                finish();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	api_access_cancel();
                finish();
            }
        });
		api_access_matched();
	} 
	private void api_access_matched(){
		try{
			String ret = Api_Access.http_connect(new URL(
							Consts.WAKEROCK_URL_MATCHED
							+ "p=" + this.pass + "&"
							+ "u=" + this.uid + "&"
							+ "rid=" + this.rid));
			if(ret.equals(Consts.STATUS_MATCH_FAILED)){
				this.matched = false;
			}else if(ret.equals(Consts.STATUS_MATCH_WAIT)){
				this.matched = true;
				this.waited = true;
			}else{
				this.matched = true;
				this.target_phone_number = ret;
			}
			setStateToText();
		}catch(Exception e){}		
	}
	private void setStateToText(){
		if(this.matched == false){
			this.mState.setText("NOT MATCHED");
		}
		else if(this.matched && this.waited){
			this.mState.setText("MATCHED (RECEIVER)");
		}
		else if(this.matched && this.waited == false){
			this.mState.setText("MATCHED (SENDER)");
		}
	}
	private boolean api_access_cancel(){
		try{
			String ret = Api_Access.http_connect(new URL(
							Consts.WAKEROCK_URL_CANCEL
							+ "p=" + this.pass + "&"
							+ "u=" + this.uid + "&"
							+ "rid=" + this.rid));
			if(ret.equals(Consts.STATUS_CANCEL_OK)){
				return true;
			}else if(ret.equals(Consts.STATUS_CANCEL_FAILED)){
				return false;
			}
		}catch(Exception e){}
		return false;
	}
	private void setExtras(Bundle extras){
		this.uid = extras.getString(Consts.EXTRA_KEY_USER_ID);
		this.pass = extras.getString(Consts.EXTRA_KEY_PASSWORD);
		this.rid = extras.getInt(Consts.EXTRA_KEY_REQUEST_ID);
        this.alarm_year = extras.getInt(Consts.EXTRA_KEY_ALARM_YEAR);
        this.alarm_month = extras.getInt(Consts.EXTRA_KEY_ALARM_MONTH);
        this.alarm_day = extras.getInt(Consts.EXTRA_KEY_ALARM_DAY);
        this.alarm_hour = extras.getInt(Consts.EXTRA_KEY_ALARM_HOUR);
        this.alarm_minute = extras.getInt(Consts.EXTRA_KEY_ALARM_MINUTE);    
    }
    private void setViews(){
        mState = (TextView) findViewById(R.id.text_state);
        mAlarmTime = (TextView) findViewById(R.id.text_alarmtime);
        mCancel = (Button) findViewById(R.id.button_cancel);
        mModify = (Button) findViewById(R.id.button_modify);
    }
    private void setTime(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }
/*
    private String calcRest(){
    	int diff_year = this.alarm_year - this.mYear;
    	int diff_month = this.alarm_month - this.mMonth;
    	int diff_day = this.alarm_day - this.mDay;
    	int diff_hour = this.alarm_hour - this.mHour;
    	int diff_minute = this.alarm_minute - this.mMinute;
    	if(diff_hour < 0){ diff_day -= 1; diff_hour += 24; }
    	if(diff_minute < 0){ diff_hour -= 1; diff_minute += 60; }
    	String rest = "";
    	if(diff_year != 0) { rest += Integer.toString(diff_year) + " year"; }
    	if(diff_month != 0) { rest += Integer.toString(diff_month) + " month"; }
    	if(diff_day != 0) { rest += Integer.toString(diff_day) + " day"; }
    	rest += Integer.toString(diff_hour) + "h";
    	rest += Integer.toString(diff_minute) + "min"; 
    	return rest;
    }
*/
 
    private void callPhone(){
        Ringtone ringtone;
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + this.target_phone_number));
        startActivity(intent);
        //startActivityForResult(intent, Consts.REQUEST_CODE_CALL_PHONE);    	
        finish();
    }
}
