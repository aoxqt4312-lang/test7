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


private void AllowAdmin() {
	ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);				
    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
	String explanation;
	if ("ru".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
	explanation = "Дайте разрешение Администратора. Необходимо для работы функции стирания данных. Стирает данные когда вы введете код сброса на экране блокировки используя клавитуру этого приложения и нажмёте стрелку Enter (⏎). Также опционально вы можете включить сброс данных при других событиях. Также опционально может блокировать экран.";
	} else {
	explanation = "Grant Administrator permission. This is required for the data wipe feature to work. Data will be wiped when you enter the reset code on the lock screen using the app's keyboard and press the Enter arrow (⏎). You can also optionally enable data reset on other events. Also optionally can lock the screen.";
	}
	intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation);
	startActivity(intent);
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
            isPengingAdmin=1;
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
