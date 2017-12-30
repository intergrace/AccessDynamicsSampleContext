package intergrace.access.dynamics.sample.context;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AccessDynamicsSampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    Log.d(AccessDynamicsSampleActivity.class.getSimpleName(), "Activity started");
    }
}
