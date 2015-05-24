package de.KIS.networkinfo;


import android.support.v7.app.ActionBarActivity;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.telephony.SignalStrength;
import android.text.format.DateFormat;
import android.util.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity
{
	TelephonyManager tel; 
	final Handler handler = new Handler ();	
	PhoneStateListener pslistener;  
	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
	int SignalStrength = 0;
	int code, cid, lac, mcc, mnc, networkType, width, height;
	String result, line, cids, lacs, mccs, mncs, nType, roam, network, networkOperator, sim_country_code, time, operatorName, imei, SIMserial, imsi, ip, brand, model, version, widths, heights;
	boolean roaming;
	boolean mainisopen;
	long back_pressed;
	InputStream is = null;
	private HistoryDataSource datasource;
	List<Entry> CellHistoryList = new ArrayList<Entry>();
	public static final String IP_ADRESS = "http://192.168.2.100/insert.php";

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		mainisopen = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler.postDelayed(runnable, 500);
		datasource = new HistoryDataSource(this);
	}

	final Runnable runnable = new Runnable()
	{
		@Override
		public void run() 
		{
			tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			phoneStateSetup();
		}
	};

	public void phoneStateSetup()
	{
		pslistener = new PhoneStateListener()
		{
			public void onCellLocationChanged(CellLocation location)
			{
				GsmCellLocation cellLocation = (GsmCellLocation) tel.getCellLocation();

				cid = cellLocation.getCid();
				lac = cellLocation.getLac(); 
				networkType = tel.getNetworkType();
				networkOperator = tel.getNetworkOperator();
				nType = null;
				roam = null;
				mcc = Integer.parseInt(networkOperator.substring(0, 3));
				mnc = Integer.parseInt(networkOperator.substring(3)); 
				network = mcc + " 0" + mnc;
				time = format.format(System.currentTimeMillis());
				roaming = tel.isNetworkRoaming();
				sim_country_code = tel.getSimCountryIso();
				operatorName = tel.getNetworkOperatorName();

				imei = tel.getDeviceId();
				SIMserial = tel.getSimSerialNumber();
				imsi = tel.getSubscriberId();
				ip = getLocalIpAddress();
				brand = Build.BRAND;
				model = Build.MODEL; 
				version = Build.VERSION.RELEASE;

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				width = size.x;
				height = size.y;

				if (roaming == false)
				{
					roam = "inaktiv";
				}
				else
				{
					roam = "aktiv";
				}

				switch (networkType)
				{
				case 0:
					nType = "Unknown";
					cid = -1;
					lac = -1;
					break;
				case 1:
					nType = "GPRS";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;   
				case 2:
					nType = "EDGE";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;
				case 3:
					nType = "UMTS";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break;
				case 4:
					nType = "CDMA";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break;  
				case 5:
					nType = "EVDO rev. 0";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;  
				case 6:
					nType = "EVDO rev. A";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;  
				case 7:
					nType = "1xRTT";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;  
				case 8:
					nType = "HSDPA";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break;  
				case 9:
					nType = "HSUPA";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break;  
				case 10:
					nType = "HSPA";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break; 
				case 11:
					nType = "iDen";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;
				case 12:
					nType = "EVDO rev. B";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;  
				case 13:
					nType = "LTE";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;   
				case 14:
					nType = "eHRPD";
					cid = cellLocation.getCid();
					lac = cellLocation.getLac();
					break;             
				case 15:
					nType = "HSPA+";
					cid = cellLocation.getCid() & 0xffff;
					lac = cellLocation.getLac() & 0xffff;
					break;                         
				}

				final TextView t_sdf = (TextView)findViewById(R.id.textView32);
				final TextView t_cid = (TextView)findViewById(R.id.textView9);
				final TextView t_lac = (TextView)findViewById(R.id.textView30);
				final TextView t_nType = (TextView)findViewById(R.id.textView15);
				final TextView t_mcc = (TextView)findViewById(R.id.textView10);
				final TextView t_mnc = (TextView)findViewById(R.id.textView11);
				final TextView t_roam = (TextView)findViewById(R.id.textView14);
				final TextView t_operatorName = (TextView)findViewById(R.id.textView12);
				final TextView t_sim_country_code = (TextView)findViewById(R.id.textView13);

				final TextView t_imsi = (TextView)findViewById(R.id.textView20);
				final TextView t_imei = (TextView)findViewById(R.id.textView21);
				final TextView t_brand = (TextView)findViewById(R.id.textView26);
				final TextView t_model = (TextView)findViewById(R.id.textView36);
				final TextView t_android_version = (TextView)findViewById(R.id.textView27);
				final TextView t_ip = (TextView)findViewById(R.id.textView28);
				final TextView t_SIMserial = (TextView)findViewById(R.id.textView24);	
				final TextView t_width = (TextView)findViewById(R.id.textView39);
				final TextView t_height = (TextView)findViewById(R.id.textView40);

				Button button1 = (Button) findViewById(R.id.button1);
				RadioButton r0 = (RadioButton)findViewById(R.id.radio0);
				RadioButton r1 = (RadioButton)findViewById(R.id.radio1);
				RadioButton r2 = (RadioButton)findViewById(R.id.radio2);

				t_sdf.setText("" + DateFormat.format("dd.MM.yyyy hh:mm:ss", System.currentTimeMillis()));
				t_cid.setText(String.valueOf(cid));
				t_lac.setText(String.valueOf(lac));
				t_nType.setText(nType);
				t_mcc.setText(String.valueOf(mcc));
				t_mnc.setText ("0" + String.valueOf(mnc));
				t_roam.setText(String.valueOf(roam));	
				t_operatorName.setText(operatorName);
				t_sim_country_code.setText(sim_country_code);

				t_imsi.setText(imsi);
				t_imei.setText(imei);		
				t_brand.setText(brand);
				t_model.setText(model);
				t_android_version.setText(version);
				t_ip.setText(ip);
				t_SIMserial.setText(SIMserial);
				t_width.setText(String.valueOf(width) + " pixels");
				t_height.setText(String.valueOf(height) + " pixels");

				try 
				{
					datasource.open();
					datasource.createEntry(cid, lac, nType, network, time);
					datasource.close();
				}
				catch (Exception ex) 
				{
					Toast.makeText(null, ex.toString(), Toast.LENGTH_LONG);
				}


				if (r0.isChecked() == true)
				{
					button1.setOnClickListener(new View.OnClickListener()
					{

						public void onClick(View v)
						{	
							cids = Integer.toString(cid);
							lacs = Integer.toString(lac);
							mccs = Integer.toString(mcc);
							mncs = Integer.toString(mnc);
							operatorName = t_operatorName.getText().toString();
							sim_country_code = t_sim_country_code.getText().toString();
							roam = t_roam.getText().toString();
							nType = t_nType.getText().toString();
							imsi = t_imsi.getText().toString();
							imei = t_imei.getText().toString();
							ip = t_ip.getText().toString();
							SIMserial = t_SIMserial.getText().toString();
							brand = t_brand.getText().toString();
							model = t_model.getText().toString();
							version = t_android_version.getText().toString();
							widths = Integer.toString(width);
							heights = Integer.toString(height);

							send0();
						}

						public void send0()
						{
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

							nameValuePairs.add(new BasicNameValuePair("cid", cids));
							nameValuePairs.add(new BasicNameValuePair("lac", lacs));
							nameValuePairs.add(new BasicNameValuePair("mcc", mccs));
							nameValuePairs.add(new BasicNameValuePair("mnc", mncs));
							nameValuePairs.add(new BasicNameValuePair("operatorName", operatorName));
							nameValuePairs.add(new BasicNameValuePair("sim_country_code", sim_country_code));
							nameValuePairs.add(new BasicNameValuePair("roaming", roam));
							nameValuePairs.add(new BasicNameValuePair("networkType", nType));
							nameValuePairs.add(new BasicNameValuePair("imsi", imsi));
							nameValuePairs.add(new BasicNameValuePair("imei", imei));
							nameValuePairs.add(new BasicNameValuePair("ip", ip));
							nameValuePairs.add(new BasicNameValuePair("SIMserial", SIMserial));
							nameValuePairs.add(new BasicNameValuePair("brand", brand));
							nameValuePairs.add(new BasicNameValuePair("model", model));
							nameValuePairs.add(new BasicNameValuePair("AndroidVersion", version));
							nameValuePairs.add(new BasicNameValuePair("width", widths));
							nameValuePairs.add(new BasicNameValuePair("height", heights));

							try
							{
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(IP_ADRESS);
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								HttpResponse response = httpclient.execute(httppost); 
								HttpEntity entity = response.getEntity();
								is = entity.getContent();
								Log.e("pass 1", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 1", e.toString());
								Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
							}     

							try
							{
								BufferedReader reader = new BufferedReader (new InputStreamReader(is, "iso-8859-1"), 8);
								StringBuilder sb = new StringBuilder();
								while ((line = reader.readLine()) != null)
								{
									sb.append(line + "\n");
								}
								is.close();
								result = sb.toString();
								Log.e("pass 2", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 2", e.toString());
							}     

							try
							{
								JSONObject json_data = new JSONObject(result);
								code = (json_data.getInt("code"));

								if (code == 1)
								{
									Toast.makeText(getBaseContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(getBaseContext(), "Sorry, Try Again", Toast.LENGTH_SHORT).show();
								}
							}
							catch (Exception e)
							{
								Log.e("Fail 3", e.toString());
							}
						}						
					}); 
				}
				else if (r1.isChecked() == true)
				{
					button1.setOnClickListener(new View.OnClickListener() 
					{
						public void onClick(View v) 
						{
							cids = Integer.toString(cid);
							lacs = Integer.toString(lac);
							mccs = Integer.toString(mcc);
							mncs = Integer.toString(mnc);
							operatorName = t_operatorName.getText().toString();
							sim_country_code = t_sim_country_code.getText().toString();
							roam = t_roam.getText().toString();
							nType = t_nType.getText().toString();		

							send1();
						}

						public void send1() 
						{
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

							nameValuePairs.add(new BasicNameValuePair("cid", cids));
							nameValuePairs.add(new BasicNameValuePair("lac", lacs));
							nameValuePairs.add(new BasicNameValuePair("mcc", mccs));
							nameValuePairs.add(new BasicNameValuePair("mnc", mncs));
							nameValuePairs.add(new BasicNameValuePair("operatorName", operatorName));
							nameValuePairs.add(new BasicNameValuePair("sim_country_code", sim_country_code));
							nameValuePairs.add(new BasicNameValuePair("roaming", roam));
							nameValuePairs.add(new BasicNameValuePair("networkType", nType));

							try
							{
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(IP_ADRESS);
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								HttpResponse response = httpclient.execute(httppost); 
								HttpEntity entity = response.getEntity();
								is = entity.getContent();
								Log.e("pass 1", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 1", e.toString());
								Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
							}     

							try
							{
								BufferedReader reader = new BufferedReader (new InputStreamReader(is,"iso-8859-1"), 8);
								StringBuilder sb = new StringBuilder();
								while ((line = reader.readLine()) != null)
								{
									sb.append(line + "\n");
								}
								is.close();
								result = sb.toString();
								Log.e("pass 2", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 2", e.toString());
							}     

							try
							{
								JSONObject json_data = new JSONObject(result);
								code=(json_data.getInt("code"));

								if (code == 1)
								{
									Toast.makeText(getBaseContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(getBaseContext(), "Sorry, Try Again", Toast.LENGTH_SHORT).show();
								}
							}
							catch (Exception e)
							{
								Log.e("Fail 3", e.toString());
							}
						}
					}); 
				}
				else if (r2.isChecked() == true)
				{
					button1.setOnClickListener(new View.OnClickListener() 
					{

						public void onClick(View v) 
						{
							imsi = t_imsi.getText().toString();
							imei = t_imei.getText().toString();
							ip = t_ip.getText().toString();
							SIMserial = t_SIMserial.getText().toString();
							brand = t_brand.getText().toString();
							model = t_model.getText().toString();
							version = t_android_version.getText().toString();
							widths = Integer.toString(width);
							heights = Integer.toString(height);

							send2();
						}

						public void send2() 
						{
							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

							nameValuePairs.add(new BasicNameValuePair("imsi", imsi));
							nameValuePairs.add(new BasicNameValuePair("imei", imei));
							nameValuePairs.add(new BasicNameValuePair("ip", ip));
							nameValuePairs.add(new BasicNameValuePair("SIMserial", SIMserial));
							nameValuePairs.add(new BasicNameValuePair("brand", brand));
							nameValuePairs.add(new BasicNameValuePair("model", model));
							nameValuePairs.add(new BasicNameValuePair("AndroidVersion", version));
							nameValuePairs.add(new BasicNameValuePair("width", widths));
							nameValuePairs.add(new BasicNameValuePair("height", heights));

							try
							{
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(IP_ADRESS);
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								HttpResponse response = httpclient.execute(httppost); 
								HttpEntity entity = response.getEntity();
								is = entity.getContent();
								Log.e("pass 1", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 1", e.toString());
								Toast.makeText(getApplicationContext(), "Invalid IP Address", Toast.LENGTH_SHORT).show();
							}     

							try
							{
								BufferedReader reader = new BufferedReader (new InputStreamReader(is,"iso-8859-1"), 8);
								StringBuilder sb = new StringBuilder();
								while ((line = reader.readLine()) != null)
								{
									sb.append(line + "\n");
								}
								is.close();
								result = sb.toString();
								Log.e("pass 2", "connection success ");
							}
							catch (Exception e)
							{
								Log.e("Fail 2", e.toString());
							}     

							try
							{
								JSONObject json_data = new JSONObject(result);
								code=(json_data.getInt("code"));

								if (code == 1)
								{
									Toast.makeText(getBaseContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Toast.makeText(getBaseContext(), "Sorry, Try Again", Toast.LENGTH_SHORT).show();
								}
							}
							catch (Exception e)
							{
								Log.e("Fail 3", e.toString());
							}
						}
					}); 
				}
			}

			public void onSignalStrengthsChanged(SignalStrength signalStrength)
			{
				int dBm = -113 + 2 * signalStrength.getGsmSignalStrength();
				int asu = signalStrength.getGsmSignalStrength();

				TextView signalStrengthdB = (TextView)findViewById(R.id.textView33);	
				TextView signalStrengthasu = (TextView)findViewById(R.id.textView34);
				signalStrengthdB.setText(String.valueOf(dBm) + " dBm");
				signalStrengthasu.setText(String.valueOf(asu) + " asu");
			}
		};


		try
		{
			tel.listen(pslistener, PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		catch (Exception e)
		{

		}
		try
		{
			if (pslistener != null)
			{
				tel.listen(pslistener, PhoneStateListener.LISTEN_NONE);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getLocalIpAddress()
	{
		try 
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) 
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} 
		catch (Exception ex) 
		{
			Log.e("IP Address", ex.toString());
		}
		return null; 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.toString().equals("Refresh/Home"))
		{
			mainisopen = true;
			setContentView(R.layout.activity_main);
			MainActivity.this.recreate();
		}
		if (item.toString().equals("History"))
		{
			mainisopen = false;
			setContentView(R.layout.history);
			CellHistoryList.clear();
			try {
				datasource.open();
				CellHistoryList = datasource.getAllEntries();
				datasource.close();
			}
			catch (Exception ex) {
				Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
			}


			ArrayAdapter<Entry> adapterHistory = new ArrayAdapter<Entry>(MainActivity.this, android.R.layout.simple_list_item_1, CellHistoryList);
			ListView lHistory = (ListView) findViewById(R.id.listView1);
			lHistory.setAdapter(adapterHistory);
		}
		if (item.toString().equals("Legal Information"))
		{
			mainisopen = false;
			setContentView(R.layout.info);		
		}
		if (item.toString().equals("Exit"))
		{
			showDialog(1);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Dialog onCreateDialog(int id)
	{
		switch (id)
		{ 
		case 1: 
			Builder builder1 = new AlertDialog.Builder(this); 
			builder1.setMessage("Close Application?"); 				//Textanzeige bei Drücken des Tasters
			builder1.setCancelable(true); 							//Zweiter Button zum Auswählen

			builder1.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{ 
				public void onClick(DialogInterface dialog, int which)
				{ 
					MainActivity.this.finish(); 
				} 
			}); 

			builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{ 
				public void onClick(DialogInterface dialog, int which)
				{ 
					Toast.makeText(getApplicationContext(), "Proceed Application", Toast.LENGTH_SHORT).show(); 
				} 
			}); 

			AlertDialog dialog1 = builder1.create(); 				//Alert-Dialog erstellen und mit builder verknüpfen, dann mit create aufrufen
			dialog1.show();											//dialog zeigen mit .show
		}
		return super.onCreateDialog(id);							//super nötig, da aus public Methode verfügbar, dann zurückgeben
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && mainisopen == false)
		{
			setContentView(R.layout.activity_main);
			MainActivity.this.recreate();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed()
	{
		if (back_pressed + 2000 > System.currentTimeMillis() && mainisopen == true)
		{ 
			super.onBackPressed();
			return;
		}
		else 
			Toast.makeText(getApplicationContext(), "Press again to exit the application!", Toast.LENGTH_SHORT).show();

		back_pressed = System.currentTimeMillis();
	}
}