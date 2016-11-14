package com.coolweather.app.util.interfaces;

public interface IHttpCallbackListener {

	public void OnResponseSuccess(String response);
	
	public  void OnResponseFailed(Exception e);
	
}
