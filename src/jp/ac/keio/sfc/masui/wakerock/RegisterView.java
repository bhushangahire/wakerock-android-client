package jp.ac.keio.sfc.masui.wakerock;

import java.net.URL;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class RegisterView extends Activity {
	private int mHour;
	private int mMinute;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int rid;
    private String uid;
    private String pass;
    private TextView mDateDisplay;
    private Button mPickDate;
    private TextView mTimeDisplay;
    private Button mPickTime;
    private Button mRegister;
	static final int TIME_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID = 1;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                updateDisplay();
            }
        };
    private DatePickerDialog.OnDateSetListener mDateSetListener =
    	new DatePickerDialog.OnDateSetListener() {
        	public void onDateSet(DatePicker view, int year, 
        			int monthOfYear, int dayOfMonth) {
        		mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerview);
        // capture our View elements
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);
        mPickTime = (Button) findViewById(R.id.pickTime);
        mRegister = (Button) findViewById(R.id.submitRegistration);

        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        uid = getIntent().getExtras().getString("USER_ID");
        pass = getIntent().getExtras().getString("PASSWORD");
        if(getIntent().hasExtra("ALARM_YEAR")){
        	Bundle extras = getIntent().getExtras();
        	setExtras(extras);
        }
        // set listener
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        mRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	api_access_register();
            	Intent intent = new Intent(RegisterView.this, AlarmView.class);
            	intent.putExtra(Consts.EXTRA_KEY_USER_ID, uid);
            	intent.putExtra(Consts.EXTRA_KEY_REQUEST_ID, rid);
            	intent.putExtra(Consts.EXTRA_KEY_PASSWORD, pass);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_YEAR, mYear);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_MONTH, mMonth);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_DAY, mDay);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_HOUR, mHour);
            	intent.putExtra(Consts.EXTRA_KEY_ALARM_MINUTE, mMinute);
                startActivity(intent);
                finish();
            }
        });
        updateDisplay();
    }
    private void setExtras(Bundle extras){
        this.mYear = extras.getInt(Consts.EXTRA_KEY_ALARM_YEAR);
        this.mMonth = extras.getInt(Consts.EXTRA_KEY_ALARM_MONTH);
        this.mDay = extras.getInt(Consts.EXTRA_KEY_ALARM_DAY);
        this.mHour = extras.getInt(Consts.EXTRA_KEY_ALARM_HOUR);
        this.mMinute = extras.getInt(Consts.EXTRA_KEY_ALARM_MINUTE);    
    }
    private void updateDisplay() {
        mTimeDisplay.setText(
            new StringBuilder()
                    .append(pad(mHour)).append(":")
                    .append(pad(mMinute)));
        mDateDisplay.setText(
                new StringBuilder()
                        .append(mYear).append(" - ")
                        .append(mMonth + 1).append(" - ")
                        .append(mDay).append(" "));
    }
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
			return new TimePickerDialog(this,
                    mTimeSetListener, mHour, mMinute, false);
        case DATE_DIALOG_ID:
    		return new DatePickerDialog(this,
                    mDateSetListener,
                    mYear, mMonth, mDay);
        }
        return null;
    }
    /*
    public void call_phone(){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:09068396460"));
        startActivity(intent);    	
    }
    */
    public void api_access_register(){
    	try{
    		String ret = Api_Access.http_connect(new URL(Consts.WAKEROCK_URL_REGISTER
        			+ "p=" + this.pass + "&"
        			+ "u=" + this.uid + "&"
        			+ "y=" + this.mYear + "&"
        			+ "m=" + this.mMonth + "&"
        			+ "d=" + this.mDay + "&"
        			+ "h=" + this.mHour + "&"
        			+ "mm=" + this.mMinute));
        	this.rid = Integer.valueOf(ret);
    	}catch(Exception e){
    	}
    }
}