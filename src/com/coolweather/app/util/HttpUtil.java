package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.coolweather.app.util.interfaces.IHttpCallbackListener;

public class HttpUtil {

	public static void SendHttpRequest(final String address,
			final IHttpCallbackListener listener) {
		new Thread( 
			new Runnable() {
				@Override
				public void run() {
					HttpURLConnection connection = null;
					try {
						String response = null;						
						URL url = new URL(address);
						connection = (HttpURLConnection) url.openConnection();
						connection.setReadTimeout(8000);
						connection.setRequestMethod("GET");
						connection.setConnectTimeout(8000);
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(inputStream));

						String line;
						do {
							line = reader.readLine();
							response += line;
						} while (line != null);
						if(listener!=null)
						listener.OnResponseSuccess(response);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						if(listener!=null)
						listener.OnResponseFailed(e);
						e.printStackTrace();
					}
					finally
					{
						if(connection!=null)
							connection.disconnect();
					}
				};
			}

		).start();

	}
}
