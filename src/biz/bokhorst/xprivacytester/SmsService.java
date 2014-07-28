package biz.bokhorst.xprivacytester;

import android.app.IntentService;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SmsService extends IntentService {
	public SmsService(String name) {
		super("XPrivacyTester");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent != null
				&& TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(intent
						.getAction())) {
			Toast.makeText(this, "SMS received: " + intent.getDataString(),
					Toast.LENGTH_LONG).show();
		}
	}
}