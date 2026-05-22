package duress.keyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.Locale;

public class EmergencyModeActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(this, MyDeviceAdminReceiver.class);

        try {
            dpm.setMaximumFailedPasswordsForWipe(admin, 1);
            dpm.lockNow();
            finish();
        } catch (Throwable t) {
            final boolean isRu = "ru".equalsIgnoreCase(Locale.getDefault().getLanguage());
            boolean isAdmin = dpm.isAdminActive(admin);
            String title;
            String message;

            if (isAdmin) {
                title = isRu ? "Ошибка" : "Error";
                message = t.toString();
            } else {
                title = isRu ? "Экстренный режим" : "Emergency Mode";
                if (isRu) {
                    message = "Привет. Это экстренный режим. Он заблокирует экран и попросит систему стереть данные при вводе любого неверного пароля на экране блокировки. Достаточно, чтобы вы ввели больше 4 символов и допустили хотя бы 1 ошибку. Предоставьте права администратора для работы этой функции.";
                } else {
                    message = "Hello. This is emergency mode. It will lock the screen and ask the system to wipe data upon entering an incorrect password. Just enter more than 4 characters and make at least 1 mistake. Please grant Device Admin rights to enable this feature.";
                }
            }

            showDialog(title, message, isAdmin);
        }
    }

    private void showDialog(final String title, final String message, final boolean isAdmin) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextIsSelectable(true);
        tv.setPadding(40, 40, 40, 40);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(tv)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    if (!isAdmin) {
                        try {
                            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, MyDeviceAdminReceiver.class));
                            startActivity(intent);
                        } catch (Exception e) {}
                    }
                    finish();
                })
                .create();

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
    }
}
