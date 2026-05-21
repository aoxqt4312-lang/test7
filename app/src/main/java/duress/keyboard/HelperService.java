package duress.keyboard;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

public class HelperService extends Service {
    private boolean isRunning = false;

	private static Context appContext;	
	
	private final static ServiceConnection connection = new ServiceConnection() {        
        @Override
        public final void onServiceDisconnected(ComponentName name) {
		BindHelper();	
        }
    };

    private final static void BindHelper() {
    if (appContext==null) return;
	Intent serviceIntent = new Intent(appContext, RiderService.class);
    appContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);    
    }


	private void initBindAndStart() {
	   if (!isRunning) {
        isRunning = true;
		appContext=getApplicationContext();   
        forceBindAndStart();
		Start.RunService(this);
        }
	}

	private void forceBindAndStart() {
    BindHelper();
	Intent intent = new Intent(this, RiderService.class);	
	try {startService(intent);} 
    catch (Throwable t) {}
    }
	    

    @Override
    public IBinder onBind(Intent intent) {
        initBindAndStart();
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	initBindAndStart();
    return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
		Start.RunService(this);
        super.onDestroy();
    }
}
