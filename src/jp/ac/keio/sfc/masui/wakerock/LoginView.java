package jp.ac.keio.sfc.masui.wakerock;

import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginView extends Activity {
	private Button mLogin;
	private Button mSignup;
    private EditText et_uid;
    private EditText et_pass;
	private String uid;
	private String pass;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  
        mLogin = (Button)findViewById(R.id.button_login);
        mSignup = (Button)findViewById(R.id.button_signup);
        et_uid = (EditText)findViewById(R.id.user_id);
        et_pass = (EditText)findViewById(R.id.password);
        //api_access_register();
        mLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
        		uid = et_uid.getText().toString();
        		pass = et_pass.getText().toString();
            	if(api_access_login()){
            		Intent intent = new Intent(LoginView.this, RegisterView.class);
            		intent.putExtra(Consts.EXTRA_KEY_USER_ID, uid);
            		intent.putExtra(Consts.EXTRA_KEY_PASSWORD, pass);
            		startActivity(intent);
            		finish();
            	}
            }
        });
        mSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Consts.WAKEROCK_URL_SIGNUP));  
            	startActivity(intent);
            	finish();
            }
        });
	}
	public boolean api_access_login(){
		try{
			String ret = Api_Access.http_connect(new URL(
							Consts.WAKEROCK_URL_LOGIN
							+ "p=" + this.pass + "&"
							+ "u=" + this.uid));
			if(ret.equals(Consts.STATUS_LOGIN_OK)){
				return true;
			}else if(ret.equals(Consts.STATUS_LOGIN_FAILED)){
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    		alertDialog.setTitle("Login Status");
	    		alertDialog.setMessage("Login Failed");
	    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int which) {
	    		        return;
	    		 } }); 
	    		alertDialog.show();
				return false;
			}
		}catch(Exception e){}
		return false;
	}
	/*
    public void api_access_register(){
    	try{
    		URL u = new URL(Consts.WAKEROCK_URL_REGISTER
        			+ "p=" + "ihalab" + "&"
        			+ "u=" + "ihara" + "&"
        			+ "y=2010&"
        			+ "m=10&"
        			+ "d=10&"
        			+ "h=10&"
        			+ "mm=10");
    		String ret = Api_Access.http_connect(u);
    		Log.v("URL", u.toString());
    		Log.v("test", ret);
        	//this.rid = Integer.valueOf(ret);
    	}catch(Exception e){
    	}
    }
    */
}
