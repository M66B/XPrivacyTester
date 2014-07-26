package biz.bokhorst.xprivacytester;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
		((TextView) findViewById(R.id.getAccounts)).setText(Integer
				.toString(accountManager.getAccounts().length));
		((TextView) findViewById(R.id.getCurrentSyncs)).setText(Integer
				.toString(ContentResolver.getCurrentSyncs().size()));
	}
}
