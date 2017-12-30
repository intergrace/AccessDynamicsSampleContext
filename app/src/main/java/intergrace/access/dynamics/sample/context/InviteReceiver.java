package intergrace.access.dynamics.sample.context;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InviteReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(InviteReceiver.class.getSimpleName(), "received");
		context.startService(new Intent(context, SampleContextService.class));
	}
}
