package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.*;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.util.interfaces.IHttpCallbackListener;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	protected static final int LEVEL_PROVINCE = 1;
	protected static final int LEVEL_CITY = 2;
	protected static final int LEVEL_COUNTY = 3;

	private static final int Msg_WebResponse_Error = 1;

	private int currentLevel;

	private ListView lv_child;
	private TextView title;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> childs = new ArrayList<String>();
	private ArrayList<Province> provinceList;
	private ArrayList<City> cityList;
	private ArrayList<County> countyList;

	private Province selectedProvince;
	private City selectedCity;
	CoolWeatherDB db ;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Msg_WebResponse_Error:
				Toast.makeText(ChooseAreaActivity.this,
						"Get data from server error", 0).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		title = (TextView) findViewById(R.id.title);
		lv_child = (ListView) findViewById(R.id.lv_Child);
		db= CoolWeatherDB.getInstance(this);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, childs);
		lv_child.setAdapter(adapter);
		lv_child.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					showWeather();
				}
			}

		});
		queryProvinces();
	}

	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = (ArrayList<Province>) db.loadProvinces();
		if (provinceList != null) {
			title.setText("全国");
			childs.clear();
			for (Province province : provinceList) {
				childs.add(province.getProvinceName());
			}
			currentLevel = LEVEL_PROVINCE;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.setNotifyOnChange(true);
					lv_child.setSelection(0);
					ChooseAreaActivity.this.title.setText("China");
				}
			});
		} else {
			queryFromServer("", LEVEL_PROVINCE);
		}
	}

	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = (ArrayList<City>) db.loadCities(selectedProvince.getId());
		if (cityList != null) {
			childs.clear();
			for (City city : cityList) {
				childs.add(city.getCityName());
			}
			currentLevel = LEVEL_CITY;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.setNotifyOnChange(true);
					lv_child.setSelection(0);
					title.setText(selectedProvince.getProvinceName());
				}
			});
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), LEVEL_CITY);
		}
	}

	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = (ArrayList<County>) db.loadCounties(selectedCity.getId());
		if (countyList != null) {
			childs.clear();
			for (County county : countyList) {
				childs.add(county.getCountyName());
			}
			currentLevel = LEVEL_COUNTY;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					adapter.setNotifyOnChange(true);
					lv_child.setSelection(0);
					title.setText(selectedCity.getCityName());
				}
			});
		} else {
			queryFromServer(selectedCity.getCityCode(), LEVEL_COUNTY);
		}
	}

	private void queryFromServer(String code, final int level) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + code
				+ ".xml";

		HttpUtil.SendHttpRequest(address, new IHttpCallbackListener() {

			@Override
			public void OnResponseSuccess(String response) {
				// TODO Auto-generated method stub
				CoolWeatherDB db = CoolWeatherDB
						.getInstance(ChooseAreaActivity.this);
				if (!TextUtils.isEmpty(response)) {
					boolean isSucess = false;
					switch (level) {
					case LEVEL_PROVINCE:
						Utility.handleProvincesResponse(db, response);
						break;
					case LEVEL_CITY:
						Utility.handleCitiesResponse(db, response,
								selectedProvince.getId());
						break;
					case LEVEL_COUNTY:
						Utility.handleCountiesResponse(db, response,
								selectedCity.getId());
						break;
					default:
						break;
					}
					if (isSucess) {
						switch (level) {
						case LEVEL_PROVINCE:
							queryProvinces();
							break;
						case LEVEL_CITY:
							queryCities();
							break;
						case LEVEL_COUNTY:
							queryCounties();
							break;
						default:
							break;
						}
					}
				}
			}

			@Override
			public void OnResponseFailed(Exception e) {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = Msg_WebResponse_Error;
				handler.sendMessage(msg);
			}
		});
	}

	private void showWeather() {
		// TODO Auto-generated method stub

	}
}
