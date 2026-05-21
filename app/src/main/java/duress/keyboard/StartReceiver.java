package duress.keyboard;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

public class StartReceiver extends BroadcastReceiver {

    private static Context appContext;	
	
	private final static ServiceConnection connection = new ServiceConnection() {
        @Override
        public final void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public final void onServiceDisconnected(ComponentName name) {
		BindHelper();	
        }
    };

    private final static void BindHelper() {
    if (appContext==null) return;
	Intent serviceIntent = new Intent(appContext, HelperService.class);
    appContext.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);    
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
     
        final PendingResult pendingResult = goAsync();

        new Thread(() -> {
            try {
                appContext = context.getApplicationContext();
                BindHelper();
                Thread.sleep(45_000);
                Start.RunService(appContext);
            } catch (Exception e) {
               
            } finally {
                pendingResult.finish();
            }
        }).start();
    }
}
