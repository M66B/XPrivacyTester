package biz.bokhorst.xprivacytester;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent call = new Intent(Intent.ACTION_CALL);
		call.setData(Uri.parse("tel:911"));
		startActivity(call);

		Intent dial = new Intent(Intent.ACTION_DIAL);
		dial.setData(Uri.parse("tel:911"));
		startActivity(dial);

		Intent image = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivity(image);

		Intent simage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
		startActivity(simage);

		Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivity(video);

		Intent view = new Intent(Intent.ACTION_VIEW);
		view.setData(Uri.parse("http://www.faircode.eu/"));
		startActivity(view);

		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		((TextView) findViewById(R.id.getAccounts)).setText(Integer
				.toString(accountManager.getAccounts().length));
		((TextView) findViewById(R.id.getCurrentSyncs)).setText(Integer
				.toString(ContentResolver.getCurrentSyncs().size()));

		PackageManager packageManager = getPackageManager();
		((TextView) findViewById(R.id.getInstalledApplications))
				.setText(Integer.toString(packageManager
						.getInstalledApplications(0).size()));
		((TextView) findViewById(R.id.getInstalledPackages)).setText(Integer
				.toString(packageManager.getInstalledPackages(0).size()));
		((TextView) findViewById(R.id.queryIntentActivities))
				.setText(Integer.toString(packageManager.queryIntentActivities(
						view, 0).size()));
	}
}
