import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class EmergencyModeActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, MyDeviceAdminReceiver.class);            
            dpm.setMaximumFailedPasswordsForWipe(admin, 1);
            dpm.lockNow();
        } catch (Throwable t) {}        
        finish();
    }
}
