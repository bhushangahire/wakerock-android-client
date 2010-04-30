package jp.ac.keio.sfc.masui.wakerock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;

public class Api_Access {

	public static String http_connect(URL url){
		try{
			String ret = "";
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			InputStream in = http.getInputStream();
			ret = InputStreamToString.asString(in);
			Log.v("HTTP_RESULT", ret);
			http.disconnect();
	        return ret;
	        }catch(Exception ex){
	        	Log.v("HTTP_RESULT", "ERROR");
	            return "failed";
	        }
	}
	public static String inputStreemToString(InputStream in) throws IOException{	    
	    BufferedReader reader = 
	        new BufferedReader(new InputStreamReader(in, "UTF-8"));
	    StringBuffer buf = new StringBuffer();
	    String str;
	    while ((str = reader.readLine()) != null) {
	            buf.append(str);
	            buf.append("\n");
	    }
	    return buf.toString();
	}
}

