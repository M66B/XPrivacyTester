package biz.bokhorst.xprivacytester;

import java.lang.reflect.Method;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.provider.CalendarContract.Calendars;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Account manager methods
		try {
			AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
			((TextView) findViewById(R.id.getAccounts)).setText(Integer
					.toString(accountManager.getAccounts().length));
			((TextView) findViewById(R.id.getCurrentSyncs)).setText(Integer
					.toString(ContentResolver.getCurrentSyncs().size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Package manager methods
		try {
			PackageManager packageManager = getPackageManager();
			((TextView) findViewById(R.id.getInstalledApplications))
					.setText(Integer.toString(packageManager
							.getInstalledApplications(0).size()));
			((TextView) findViewById(R.id.getInstalledPackages))
					.setText(Integer.toString(packageManager
							.getInstalledPackages(0).size()));
			Intent view = new Intent(Intent.ACTION_VIEW);
			view.setData(Uri.parse("http://www.faircode.eu/"));
			((TextView) findViewById(R.id.queryIntentActivities))
					.setText(Integer.toString(packageManager
							.queryIntentActivities(view, 0).size()));
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		Cursor cursor;
		ContentResolver cr = getContentResolver();

		// Browser provider
		try {
			String[] proj = new String[] { Browser.BookmarkColumns.TITLE,
					Browser.BookmarkColumns.URL };
			String sel = Browser.BookmarkColumns.BOOKMARK + " = 0";
			// 0 = history, 1 = bookmark
			cursor = getContentResolver().query(Browser.BOOKMARKS_URI, proj,
					sel, null, null);
			((TextView) findViewById(R.id.BrowserProvider2))
					.setText(cursor == null ? "null" : Integer.toString(cursor
							.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Calendar provider
		cursor = cr.query(Calendars.CONTENT_URI,
				new String[] { Calendars._ID }, null, null, null);
		((TextView) findViewById(R.id.CalendarProvider2))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// Callog provider
		try {
			cursor = this.getContentResolver().query(
					android.provider.CallLog.Calls.CONTENT_URI,
					new String[] { CallLog.Calls._ID }, null, null, null);
			((TextView) findViewById(R.id.CallLogProvider))
					.setText(cursor == null ? "null" : Integer.toString(cursor
							.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Contacts provider
		try {
			cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
					new String[] { ContactsContract.Contacts._ID }, null, null,
					null);
			((TextView) findViewById(R.id.ContactsProvider2))
					.setText(cursor == null ? "null" : Integer.toString(cursor
							.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// SMS provider
		try {
			cursor = cr.query(Uri.parse("content://sms/"), null, null, null,
					null);
			((TextView) findViewById(R.id.SmsProvider))
					.setText(cursor == null ? "null" : Integer.toString(cursor
							.getCount()));
			if (cursor != null)
				cursor.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Read SMSes
		try {
			SmsManager smsManager = SmsManager.getDefault();
			Method getMessages = smsManager.getClass().getMethod(
					"getAllMessagesFromIcc");
			@SuppressWarnings("unchecked")
			List<SmsMessage> msgs = (List<SmsMessage>) getMessages
					.invoke(smsManager);
			((TextView) findViewById(R.id.getAllMessagesFromIcc))
					.setText(Integer.toString(msgs.size()));
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Line 1 number
		try {
			TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			String phoneNumber = telManager.getLine1Number();
			((TextView) findViewById(R.id.getLine1Number))
					.setText(phoneNumber == null ? "null" : phoneNumber);
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}

		// Read clipboard
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
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
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
						getPackageName());
				startActivity(intent);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		case R.id.menu_restore_sms:
			try {
				intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
				intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
						"com.google.android.talk");
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
				smsManager.sendTextMessage(phoneNumber, null, "XPrivacy", null,
						null);
			} catch (Throwable ex) {
				ex.printStackTrace();
				Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
