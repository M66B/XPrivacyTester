package biz.bokhorst.xprivacytester;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import com.google.android.gm.contentprovider.GmailContract;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.net.sip.SipManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.CalendarContract.Calendars;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.provider.UserDictionary;
import android.telephony.CellInfo;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.InputDevice;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private GoogleApiClient gClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Cursor cursor;
		final ContentResolver cr = getContentResolver();

		final AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);
		SensorManager sensitiveMan = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Account manager methods
		try {
			((TextView) findViewById(R.id.getAccounts)).setText(Integer.toString(accountManager.getAccounts().length));

			((TextView) findViewById(R.id.getAccountsByType)).setText(Integer.toString(accountManager
					.getAccountsByType("com.google").length));

			OnAccountsUpdateListener listener = new OnAccountsUpdateListener() {
				@Override
				public void onAccountsUpdated(Account[] accounts) {
					((TextView) MainActivity.this.findViewById(R.id.addOnAccountsUpdatedListener)).setText(Integer
							.toString(accounts.length));
					accountManager.removeOnAccountsUpdatedListener(this);
				}
			};
			accountManager.addOnAccountsUpdatedListener(listener, null, true);

			((TextView) findViewById(R.id.getCurrentSyncs)).setText(Integer.toString(ContentResolver.getCurrentSyncs()
					.size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Package manager methods
		try {
			PackageManager packageManager = getPackageManager();
			((TextView) findViewById(R.id.getInstalledApplications)).setText(Integer.toString(packageManager
					.getInstalledApplications(0).size()));
			((TextView) findViewById(R.id.getInstalledPackages)).setText(Integer.toString(packageManager
					.getInstalledPackages(0).size()));
			Intent view = new Intent(Intent.ACTION_VIEW);
			view.setData(Uri.parse("http://www.faircode.eu/"));
			((TextView) findViewById(R.id.queryIntentActivities)).setText(Integer.toString(packageManager
					.queryIntentActivities(view, 0).size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Browser provider
		try {
			String[] proj = new String[] { Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL };
			String sel = Browser.BookmarkColumns.BOOKMARK + " = 0";
			// 0 = history, 1 = bookmark
			cursor = getContentResolver().query(Browser.BOOKMARKS_URI, proj, sel, null, null);
			((TextView) findViewById(R.id.BrowserProvider2)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Calendar provider
		try {
			cursor = cr.query(Calendars.CONTENT_URI, new String[] { Calendars._ID }, null, null, null);
			((TextView) findViewById(R.id.CalendarProvider2)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.CalendarProvider2)).setText(ex.getClass().getName());
		}

		// Callog provider
		try {
			cursor = this.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
					new String[] { CallLog.Calls._ID }, null, null, null);
			((TextView) findViewById(R.id.CallLogProvider)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.CallLogProvider)).setText(ex.getClass().getName());
		}

		// Contacts provider
		try {
			cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] { ContactsContract.Contacts._ID },
					null, null, null);
			((TextView) findViewById(R.id.ContactsProvider2)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.ContactsProvider2)).setText(ex.getClass().getName());
		}

		// SMS provider
		try {
			cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null);
			((TextView) findViewById(R.id.SmsProvider)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.SmsProvider)).setText(ex.getClass().getName());
		}

		// Read SMSes
		try {
			SmsManager smsManager = SmsManager.getDefault();
			Method getMessages = smsManager.getClass().getMethod("getAllMessagesFromIcc");
			@SuppressWarnings("unchecked")
			List<SmsMessage> msgs = (List<SmsMessage>) getMessages.invoke(smsManager);
			((TextView) findViewById(R.id.getAllMessagesFromIcc)).setText(Integer.toString(msgs.size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.getAllMessagesFromIcc)).setText(ex.getClass().getName());
		}

		// Line 1 number
		try {
			String phoneNumber = telManager.getLine1Number();
			((TextView) findViewById(R.id.getLine1Number)).setText(phoneNumber == null ? "null" : phoneNumber);
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.getLine1Number)).setText(ex.getClass().getName());
		}

		// Android ID
		try {
			String value = android.provider.Settings.Secure.getString(cr, android.provider.Settings.Secure.ANDROID_ID);
			((TextView) findViewById(R.id.Settings_Secure_ANDROID_ID)).setText(value == null ? "null" : value);
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.Settings_Secure_ANDROID_ID)).setText(ex.getClass().getName());
		}

		// default_dns_server
		try {
			String value = android.provider.Settings.Global.getString(cr, "default_dns_server");
			((TextView) findViewById(R.id.default_dns_server)).setText(value == null ? "null" : value);
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.default_dns_server)).setText(ex.getClass().getName());
		}

		// wifi_country_code
		try {
			String value = android.provider.Settings.Global.getString(cr, "wifi_country_code");
			((TextView) findViewById(R.id.wifi_country_code)).setText(value == null ? "null" : value);
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.wifi_country_code)).setText(ex.getClass().getName());
		}

		// Input device
		try {
			int[] deviceId = InputDevice.getDeviceIds();
			if (deviceId == null || deviceId.length == 0)
				((TextView) findViewById(R.id.InputDevice)).setText("-");
			else {
				InputDevice inputDevice = InputDevice.getDevice(deviceId[0]);
				((TextView) findViewById(R.id.InputDevice)).setText(inputDevice.getName() + "/"
						+ inputDevice.getDescriptor());
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.InputDevice)).setText(ex.getClass().getName());
		}

		// Downloads provider
		try {
			cursor = cr.query(Uri.parse("content://downloads/my_downloads"), null, null, null, null);
			((TextView) findViewById(R.id.Downloads)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.Downloads)).setText(ex.getClass().getName());
		}

		// User dictionary
		try {
			cursor = cr.query(UserDictionary.Words.CONTENT_URI, null, null, null, null);
			((TextView) findViewById(R.id.UserDictionary)).setText(cursor == null ? "null" : Integer.toString(cursor
					.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.UserDictionary)).setText(ex.getClass().getName());
		}

		// GMailProvider
		try {
			accountManager.getAccountsByTypeAndFeatures("com.google", new String[] { "service_mail" },
					new AccountManagerCallback<Account[]>() {
						@Override
						public void run(AccountManagerFuture<Account[]> future) {
							Account[] accounts = null;
							try {
								accounts = future.getResult();
								if (accounts != null && accounts.length > 0) {
									// e-mail address
									String selectedAccount = accounts[0].name;
									Uri labels = GmailContract.Labels.getLabelsUri(selectedAccount);
									Cursor cursor = cr.query(labels, null, null, null, null);

									((TextView) findViewById(R.id.GMailProvider)).setText(cursor == null ? "null"
											: Integer.toString(cursor.getCount()));
									if (cursor != null)
										cursor.close();

								} else
									((TextView) findViewById(R.id.GMailProvider)).setText("No e-mail account");
							} catch (Throwable ex) {
								ex.printStackTrace();
								((TextView) findViewById(R.id.GMailProvider)).setText(ex.getClass().getName());
							}
						}
					}, null);
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.GMailProvider)).setText(ex.getClass().getName());
		}

		// GservicesProvider
		try {
			Uri gsf = Uri.parse("content://com.google.android.gsf.gservices");
			cursor = cr.query(gsf, null, null, new String[] { "android_id" }, null);
			String gsf_id = null;
			if (cursor.moveToFirst())
				gsf_id = Long.toHexString(Long.parseLong(cursor.getString(1)));
			((TextView) findViewById(R.id.GservicesProvider)).setText(gsf_id == null ? "null" : gsf_id);
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.GservicesProvider)).setText(ex.getClass().getName());
		}

		// SERIAL
		((TextView) findViewById(R.id.SERIAL)).setText(Build.SERIAL);

		// AdvertisingId
		new Thread() {
			@Override
			public void run() {
				try {
					final String adId = AdvertisingIdClient.getAdvertisingIdInfo(MainActivity.this).getId();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((TextView) findViewById(R.id.AdvertisingId)).setText(adId == null ? "null" : adId);
						}
					});
				} catch (final Throwable ex) {
					ex.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((TextView) findViewById(R.id.AdvertisingId)).setText(ex.getClass().getName());
						}
					});
				}
			}
		}.start();

		// IoBridge
		try {
			Class<?> cSystemProperties = Class.forName("android.os.SystemProperties");
			Method mGet = cSystemProperties.getMethod("get", String.class);
			String hostName = (String) mGet.invoke(null, "net.hostname");
			String serialNo = (String) mGet.invoke(null, "ro.serialno");
			((TextView) findViewById(R.id.net_hostname)).setText(hostName == null ? "null" : hostName);
			((TextView) findViewById(R.id.ro_serialno)).setText(serialNo == null ? "null" : serialNo);
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		try {
			new FileReader("/proc/stat").close();
			((TextView) findViewById(R.id.proc)).setText("readable");
		} catch (Throwable ex) {
			((TextView) findViewById(R.id.proc)).setText(ex.getClass().getName());
		}

		// USB device
		try {
			HashMap<String, UsbDevice> mapUsbDevice = usbManager.getDeviceList();
			if (mapUsbDevice.size() == 0)
				((TextView) findViewById(R.id.UsbDevice)).setText("no devices");
			else {
				UsbDevice usbDevice = mapUsbDevice.values().toArray(new UsbDevice[0])[0];
				((TextView) findViewById(R.id.UsbDevice)).setText(usbDevice.getDeviceId() + "/"
						+ usbDevice.getDeviceName());
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.UsbDevice)).setText(ex.getClass().getName());
		}

		// InetAddress
		new Thread() {
			@Override
			public void run() {
				try {
					final InetAddress addr1 = InetAddress.getByName("faircode.eu");
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((TextView) findViewById(R.id.InetAddress)).setText(addr1.toString());
						}
					});
				} catch (final Throwable ex) {
					ex.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							((TextView) findViewById(R.id.InetAddress)).setText(ex.getClass().getName());
						}
					});
				}
			}
		}.start();

		// NetworkInterface
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			if (netInterfaces == null)
				((TextView) findViewById(R.id.NetworkInterface)).setText("null");
			else {
				int count = 0;
				while (netInterfaces.hasMoreElements()) {
					count++;
					netInterfaces.nextElement();
				}
				((TextView) findViewById(R.id.NetworkInterface)).setText(Integer.toString(count));
			}
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.NetworkInterface)).setText(ex.getClass().getName());
		}

		// NetworkInfo
		try {
			NetworkInfo ni = conMan.getActiveNetworkInfo();
			DetailedState ds = ni.getDetailedState();
			((TextView) findViewById(R.id.NetworkInfo)).setText(ds == null ? "null" : ds.toString());
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.NetworkInfo)).setText(ex.getClass().getName());
		}

		// NetworkInfo
		try {
			NetworkInfo[] ani = conMan.getAllNetworkInfo();
			((TextView) findViewById(R.id.Connectivity)).setText(ani == null ? "null" : Integer.toString(ani.length));
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.Connectivity)).setText(ex.getClass().getName());
		}

		// WifiManager
		try {
			List<ScanResult> scans = wifiManager.getScanResults();
			((TextView) findViewById(R.id.WifiManager_getScanResults)).setText(scans == null ? "null" : Integer
					.toString(scans.size()));
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.WifiManager_getScanResults)).setText(ex.getClass().getName());
		}
		try {
			WifiInfo winfo = wifiManager.getConnectionInfo();
			((TextView) findViewById(R.id.WifiManager_getConnectionInfo)).setText(winfo == null ? "null" : winfo
					.toString());
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.WifiManager_getConnectionInfo)).setText(ex.getClass().getName());
		}
		try {
			DhcpInfo dhcp = wifiManager.getDhcpInfo();
			((TextView) findViewById(R.id.WifiManager_getDhcpInfo)).setText(dhcp == null ? "null" : dhcp.toString());
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.WifiManager_getDhcpInfo)).setText(ex.getClass().getName());
		}

		// BluetoothAdapter
		try {
			String btAddr = BluetoothAdapter.getDefaultAdapter().getAddress();
			((TextView) findViewById(R.id.BluetoothAdapter)).setText(btAddr == null ? "null" : btAddr);
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.BluetoothAdapter)).setText(ex.getClass().getName());
		}

		// Configuration
		try {
			int mcc = Resources.getSystem().getConfiguration().mcc;
			((TextView) findViewById(R.id.Configuration)).setText(Integer.toString(mcc));
		} catch (final Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.Configuration)).setText(ex.getClass().getName());
		}

		// Cell info
		try {
			List<CellInfo> listCellInfo = telManager.getAllCellInfo();
			((TextView) findViewById(R.id.getAllCellInfo)).setText(Integer.toString(listCellInfo.size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.getAllCellInfo)).setText(ex.getClass().getName());
		}

		// Cell info
		try {
			Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			((TextView) findViewById(R.id.getLastKnownLocation)).setText(lastLoc == null ? "null" : lastLoc.toString());
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.getLastKnownLocation)).setText(ex.getClass().getName());
		}

		// Sensor manager
		try {
			List<Sensor> listSensor = sensitiveMan.getSensorList(Sensor.TYPE_ALL);
			((TextView) findViewById(R.id.SensorManager)).setText(Integer.toString(listSensor.size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.SensorManager)).setText(ex.getClass().getName());
		}

		try {
			Process sh = Runtime.getRuntime().exec("getprop ro.serialno");
			BufferedReader br = new BufferedReader(new InputStreamReader(sh.getInputStream()));
			((TextView) findViewById(R.id.shell_ro_serialno)).setText(br.readLine());
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.shell_ro_serialno)).setText(ex.getClass().getName());
		}

		gClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addApi(ActivityRecognition.API)
				.addApi(AppIndex.APP_INDEX_API).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
				.build();
		gClient.connect();

		// TODO: EMailProvider
		// TODO: NFC
		// TODO: notifications
		// TODO: overlay
	}

	@Override
	public void onConnected(Bundle arg0) {
		android.util.Log.w("XPrivacyTester", "AppIndexApi=" + AppIndex.AppIndexApi.getClass());

		// FusedLocationApi
		try {
			Location loc = LocationServices.FusedLocationApi.getLastLocation(gClient);
			((TextView) findViewById(R.id.GMS5_getLastLocation)).setText(loc == null ? "null" : loc.toString());
		} catch (Throwable ex) {
			ex.printStackTrace();
			((TextView) findViewById(R.id.GMS5_getLastLocation)).setText(ex.getClass().getName());
		}

		LocationRequest locRec = new LocationRequest();
		locRec.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		LocationServices.FusedLocationApi.requestLocationUpdates(gClient, locRec, new LocationListener() {
			@Override
			public void onLocationChanged(Location loc) {
				try {
					((TextView) findViewById(R.id.GMS5_requestLocationUpdates)).setText(loc == null ? "null" : loc
							.toString());
				} catch (Throwable ex) {
					ex.printStackTrace();
					((TextView) findViewById(R.id.GMS5_requestLocationUpdates)).setText(ex.getClass().getName());
				}
			}
		});

		// ActivityRecognitionApi
		Intent activityIntent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getService(MainActivity.this, 0, activityIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(gClient, 0, pi);
		ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(gClient, pi);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_view:
			try {
				Intent view = new Intent(Intent.ACTION_VIEW);
				view.setData(Uri.parse("http://www.faircode.eu/"));
				startActivity(view);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_receive_sms:
			try {
				intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
				startActivity(intent);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_restore_sms:
			try {
				// Settings.Secure."sms_default_application"
				intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, "com.google.android.talk");
				startActivity(intent);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_send_sms:
			try {
				TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				String phoneNumber = telManager.getLine1Number();
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(phoneNumber, null, "XPrivacy", null, null);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_clipboard:
			try {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				ClipboardManager.OnPrimaryClipChangedListener clipListener = new ClipboardManager.OnPrimaryClipChangedListener() {
					@Override
					public void onPrimaryClipChanged() {
					}
				};
				clipboard.addPrimaryClipChangedListener(clipListener);
				clipboard.getPrimaryClip();
				clipboard.getPrimaryClipDescription();
				clipboard.getText();
				clipboard.hasPrimaryClip();
				clipboard.hasText();
				clipboard.removePrimaryClipChangedListener(clipListener);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_sip:
			try {
				SipManager sipManager = SipManager.newInstance(this);
				((TextView) findViewById(R.id.SIP)).setText(sipManager == null ? "null" : sipManager.getClass()
						.getName());
			} catch (Throwable ex) {
				((TextView) findViewById(R.id.SIP)).setText(ex.getClass().getName());
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
