package biz.bokhorst.xprivacytester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony.Sms.Intents;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	public SmsReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = (intent == null ? null : intent.getAction());
		if (Intents.SMS_DELIVER_ACTION.equals(action) || Intents.SMS_RECEIVED_ACTION.equals(action)) {
			Bundle bundle = intent.getExtras();
			if (bundle == null)
				Toast.makeText(context, action, Toast.LENGTH_LONG).show();
			else {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (Object currentObj : pdusObj) {
					SmsMessage message = SmsMessage.createFromPdu((byte[]) currentObj);
					String origin = message.getDisplayOriginatingAddress();
					Toast.makeText(context, action + ": " + origin, Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}