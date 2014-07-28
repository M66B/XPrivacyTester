package biz.bokhorst.xprivacytester;

import java.lang.reflect.Method;
import java.util.List;

import android.accounts.AccountManager;
import android.app.Activity;
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
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// View action
		Intent view = new Intent(Intent.ACTION_VIEW);
		view.setData(Uri.parse("http://www.faircode.eu/"));
		startActivity(view);

		// Account manager methods
		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		((TextView) findViewById(R.id.getAccounts)).setText(Integer
				.toString(accountManager.getAccounts().length));
		((TextView) findViewById(R.id.getCurrentSyncs)).setText(Integer
				.toString(ContentResolver.getCurrentSyncs().size()));

		// Package manager methods
		PackageManager packageManager = getPackageManager();
		((TextView) findViewById(R.id.getInstalledApplications))
				.setText(Integer.toString(packageManager
						.getInstalledApplications(0).size()));
		((TextView) findViewById(R.id.getInstalledPackages)).setText(Integer
				.toString(packageManager.getInstalledPackages(0).size()));
		((TextView) findViewById(R.id.queryIntentActivities))
				.setText(Integer.toString(packageManager.queryIntentActivities(
						view, 0).size()));

		Cursor cursor;
		ContentResolver cr = getContentResolver();

		// Browser provider
		String[] proj = new String[] { Browser.BookmarkColumns.TITLE,
				Browser.BookmarkColumns.URL };
		String sel = Browser.BookmarkColumns.BOOKMARK + " = 0";
		// 0 = history, 1 = bookmark
		cursor = getContentResolver().query(Browser.BOOKMARKS_URI, proj, sel,
				null, null);
		((TextView) findViewById(R.id.BrowserProvider2))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// Calendar provider
		cursor = cr.query(Calendars.CONTENT_URI,
				new String[] { Calendars._ID }, null, null, null);
		((TextView) findViewById(R.id.CalendarProvider2))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// Callog provider
		cursor = this.getContentResolver().query(
				android.provider.CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls._ID }, null, null, null);
		((TextView) findViewById(R.id.CallLogProvider))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// Contacts provider
		cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID }, null, null,
				null);
		((TextView) findViewById(R.id.ContactsProvider2))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// SMS provider
		cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null);
		((TextView) findViewById(R.id.SmsProvider))
				.setText(cursor == null ? "null" : Integer.toString(cursor
						.getCount()));
		if (cursor != null)
			cursor.close();

		// SMS
		SmsManager smsManager = SmsManager.getDefault();

		// Read SMSes
		try {
			Method getMessages = smsManager.getClass().getMethod(
					"getAllMessagesFromIcc");
			@SuppressWarnings("unchecked")
			List<SmsMessage> msgs = (List<SmsMessage>) getMessages
					.invoke(smsManager);
			((TextView) findViewById(R.id.getAllMessagesFromIcc))
					.setText(Integer.toString(msgs.size()));
		} catch (Exception ex) {
			((TextView) findViewById(R.id.getAllMessagesFromIcc)).setText(ex
					.toString());
			ex.printStackTrace();
		}

		// Send SMS
		smsManager.sendTextMessage("+123456789", null, "XPrivacy", null, null);
	}
}
