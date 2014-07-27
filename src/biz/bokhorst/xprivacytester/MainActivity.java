package biz.bokhorst.xprivacytester;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.ContactsContract;
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

		// Acount manager methods
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

		// Calendar provider
		cursor = cr.query(Calendars.CONTENT_URI,
				new String[] { Calendars._ID }, null, null, null);
		((TextView) findViewById(R.id.CalendarProvider2))
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
	}
}
