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
		String action = (intent == null ? null : intent.getAction());
		if (TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(action))
			Toast.makeText(this, action + ": " + intent.getDataString(),
					Toast.LENGTH_LONG).show();
	}
}