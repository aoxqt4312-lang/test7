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
	
	private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public final void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public final void onServiceDisconnected(ComponentName name) {
		BindHelper();	
        }
    };
	
    private final void BindHelper() {
    try {	
	Intent serviceIntent = new Intent(appContext, RiderService.class);
    bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);    
    } catch (Throwable t) {} }

	private void initBindAndStart() {
	   if (!isRunning) {
        isRunning = true;		
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
