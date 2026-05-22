package duress.keyboard;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class EmergencyModeActivity extends Activity {

    private AlertDialog adminErrorDialog;
    private static int isPendingAdmin = 0;

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
            if (dpm.isAdminActive(admin)) {
                ShowLogDialog(t.toString());
            } else {                
                if (isPendingAdmin == 0) {
                    isPendingAdmin = 2;
                    ShowEmergencyDialog();
                } else if (isPendingAdmin == 1) { 
                    isPendingAdmin = 2;
                    ShowAdminErrorDialog();
                }
            }
        }
    }

    private void ShowLogDialog(String error) {
        final boolean isRu = "ru".equalsIgnoreCase(Locale.getDefault().getLanguage());
        TextView tv = new TextView(this);
        tv.setText(error);
        tv.setTextIsSelectable(true);
        tv.setPadding(40, 40, 40, 40);

        adminErrorDialog = new AlertDialog.Builder(this)
                .setTitle(isRu ? "Ошибка" : "Error")
                .setView(tv)
                .setCancelable(false)
                .setPositiveButton("OK", (d, i) -> finish())
                .create();
        adminErrorDialog.show();
    }

    private void ShowEmergencyDialog() {
        final boolean isRu = "ru".equalsIgnoreCase(Locale.getDefault().getLanguage());
        TextView tv = new TextView(this);
        tv.setText(isRu ? "Привет. Это экстренный режим. Он заблокирует экран и попросит систему стереть данные при вводе любого неверного пароля на экране блокировки. Достаточно, чтобы вы ввели больше 4 символов и допустили хотя бы 1 ошибку. Предоставьте права администратора для работы этой функции." 
                        : "Hello. This is the emergency mode. It will lock the screen and ask the system to wipe data upon entering any incorrect password on the lock screen. It is enough to enter more than 4 characters and make at least 1 mistake. Please grant Device Admin rights to enable this feature.");
        
        
        adminErrorDialog = new AlertDialog.Builder(this)
                .setTitle(isRu ? "Экстренный режим" : "Emergency Mode")
                .setView(tv)
                .setCancelable(false)
                .setPositiveButton("OK", (d, i) -> {
                    isPendingAdmin = 1;
                    AllowAdmin();                
                })
                .create();
        adminErrorDialog.show();
    }

    private void ShowAdminErrorDialog() {
    final boolean isRussian = "ru".equalsIgnoreCase(Locale.getDefault().getLanguage());

    final LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    );
    lp.bottomMargin = dpToPx(12);

    TextView t1 = new TextView(this);
    if (isRussian) {
        t1.setText("Вы, либо система, отменили активацию прав администратора. Если это были вы, например вы случайно нажали \"отмена\", попробуйте снова.");
    } else {
        t1.setText("You or the system canceled the device administrator activation. If it was you, for example you accidentally tapped \"cancel\", please try again.");
    }
    root.addView(t1, lp);
    
    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
    String title = isRussian ? "Ошибка активации" : "Activation Error";
    
    builder.setTitle(title)
           .setView(root)
           .setCancelable(false);
           
    adminErrorDialog = builder.create();

    Button b1 = new Button(this);
    b1.setText(isRussian ? "Попробовать снова" : "Try again");
    root.addView(b1, lp);
    b1.setOnClickListener(new View.OnClickListener() {
        @Override 
        public void onClick(View v) {            
			isPendingAdmin = 1;
			adminErrorDialog.dismiss();
            AllowAdmin();
        }
    });

    TextView t2 = new TextView(this);
    if (isRussian) {
        t2.setText("Если это была система, перейдите в настройки приложения, нажмите 3 точки в правом верхнем углу, затем \"разрешить ограниченные настройки\". После чего вернитесь сюда и попробуйте снова.");
    } else {
        t2.setText("If it was the system, go to the app settings, tap the 3 dots in the upper right corner, then \"allow restricted settings\". Then return here and try again.");
    }
    root.addView(t2, lp);

    Button b2 = new Button(this);
    b2.setText(isRussian ? "Перейти в настройки приложения" : "Go to app settings");
    root.addView(b2, lp);
    b2.setOnClickListener(new View.OnClickListener() {
        @Override 
        public void onClick(View v) {
            Detalis();
        }
    });

    TextView t3 = new TextView(this);
    if (isRussian) {
        t3.setText("Если 3 точек нет, значит окно активации прав администратора не является ограниченной настройкой. Тогда вернитесь наверх и попробуйте снова.");
    } else {
        t3.setText("If there are no 3 dots, it means the admin activation window is not a restricted setting. Then return to the top and try again.");
    }
    root.addView(t3, lp);

    adminErrorDialog.show();

    android.view.Window window = adminErrorDialog.getWindow();
    if (window != null) {
        android.view.WindowManager.LayoutParams lp2 = window.getAttributes();
        lp2.gravity = android.view.Gravity.CENTER;
        lp2.x = 0;
        lp2.y = 0;
        window.setAttributes(lp2);
    }
  }

	private void Detalis() {
    startActivity(
	new Intent(
		android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            android.net.Uri.fromParts(
						"package",
						getApplicationContext().getPackageName(),
						null
                        )
					)
			);
	}    

    private void AllowAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, MyDeviceAdminReceiver.class));
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adminErrorDialog != null && adminErrorDialog.isShowing()) adminErrorDialog.dismiss();
    }
}
