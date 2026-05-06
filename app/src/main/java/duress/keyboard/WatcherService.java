package duress.keyboard;

import android.app.*;
import android.os.storage.*;
import java.util.*;
import android.app.admin.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;

public class WatcherService extends DeviceAdminService {

	//required only if app have admin rights. 1 app can have as much as you like admin services. This will not cause any conflicts (or just disable this component).
	//нужно только если приложение имеет права админа. 1 приложение может иметь сколько угодно admin сервисов. Это не вызовет конфликтов (или просто отключите этот компонент).                		
	            
	private void BindHelper() {		
            try {
			new Thread(() -> {
			   try {
                   Context appContext = getApplicationContext();
                   Intent serviceIntent = new Intent(appContext, duress.keyboard.RiderService.class);

                   appContext.bindService(serviceIntent, new ServiceConnection() {
                       @Override
                       public void onServiceConnected(ComponentName name, IBinder service) {                       
                    
                       }

                       @Override
                       public void onServiceDisconnected(ComponentName name) {                        
                       BindHelper(); 
                       }
                   }, Context.BIND_AUTO_CREATE | Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
               } catch (Throwable BindError) {}
			}).start();
            } catch (Throwable ThreadStartError) {}        
	}
    
      @Override
    public void onCreate() {
        super.onCreate();		
		BindHelper();
		try {
        Class<?> serviceClass = Class.forName("duress.keyboard.RiderService");
        Intent serviceIntent = new Intent(this, serviceClass);
        startForegroundService(serviceIntent);
       } catch (Throwable t) {}	}

}
