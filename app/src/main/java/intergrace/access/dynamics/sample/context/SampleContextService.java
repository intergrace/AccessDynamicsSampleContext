package intergrace.access.dynamics.sample.context;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import intergrace.accdyns.remoting.AccessDynamics;
import intergrace.accdyns.remoting.ContextController;
import intergrace.accdyns.remoting.ContextSupport;
import intergrace.accdyns.remoting.ControllerRedirect;
import intergrace.accdyns.remoting.ControllerRequest;
import intergrace.accdyns.remoting.ControllerResource;
import intergrace.accdyns.remoting.ControllerResult;
import intergrace.accdyns.remoting.ResourceLoader;
import intergrace.accdyns.remoting.RouteType;

public class SampleContextService extends Service implements ServiceConnection {

	private boolean bound;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(SampleContextService.class.getSimpleName(), "onStartCommand()");
		if (bound) {
			unbindService(this);
			bound = false;
		}
		boolean result = bindService(new Intent("intergrace.access.dynamics.JOIN"), this, BIND_AUTO_CREATE);
		Log.d(SampleContextService.class.getSimpleName(), "bindService() returned: " + result);
		return START_STICKY;
	}

	@Override
	public void onServiceConnected(ComponentName componentName, IBinder binder) {
		final AccessDynamics registrar = AccessDynamics.Stub.asInterface(binder);
		bound = true;
		try {
			ContextSupport ctxSupp = registrar.registerContextPath("/sample", "Small Access Dynamics Webapp");
			if (ctxSupp == null) {
				Log.e(SampleContextService.class.getSimpleName(), "Couldn't register sample context");
				stopSelf();
				return;
			}
			Log.d(SampleContextService.class.getSimpleName(), "registering routes...");

			ctxSupp.registerResourceLoader(new ResourceLoader.Stub() {
				@Override
				public ControllerResource loadResource(String name) throws RemoteException {
					try {
						return new ControllerResource(getAssets().open(name));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});

			ctxSupp.registerRoute("/velo", RouteType.VELOCITY_TEMPLATE, ControllerRequest.METHOD_GET, new ContextController.Stub() {
				@Override
				public ControllerResult process(ControllerRequest request) throws RemoteException {
					Log.d(SampleContextService.class.getSimpleName(), "process() for: /velo");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("world", "Android Web World");
					return new ControllerResult("veloTest.vm", map);
				}
			}, "text/html");

			ctxSupp.registerRoute("/", RouteType.STATIC_RESOURCE, ControllerRequest.METHOD_GET, new ContextController.Stub() {
				@Override
				public ControllerResult process(ControllerRequest request) throws RemoteException {
					Log.d(SampleContextService.class.getSimpleName(), "process() for: /");
					return new ControllerRedirect("velo");
				}
			}, null);

		} catch (RemoteException e) {
			Log.e(SampleContextService.class.getSimpleName(), "Error registering context and routes", e);
			unbindService(this);
			bound = false;
			stopSelf();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName componentName) {
		Log.d(SampleContextService.class.getSimpleName(), "onServiceDisconnected()");
		bound = false;
		stopSelf();
	}

	@Override
	public void onDestroy() {
		Log.d(SampleContextService.class.getSimpleName(), "onDestroy");
		if (bound) {
			unbindService(this);
			bound = false;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Binding to this service is not supported");
	}
}
