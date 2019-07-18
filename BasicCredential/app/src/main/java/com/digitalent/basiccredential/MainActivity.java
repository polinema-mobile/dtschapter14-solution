package com.digitalent.basiccredential;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int LOCK_REQUEST_CODE = 221;
    public static final int SECURITY_SETTING_REQUEST_CODE = 223;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv_label);
        authenticateApp();
    }

    // Method untuk autentikasi
    private void authenticateApp() {
        // Isiasi obyek KeyguardManager
        // KeyguardManager digunakan untuk mengontrol buka (unlock) dan tutup (lock)
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //Cek apakah device Android lebih atau sama dengan versi Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Membuat intent untuk membuka tampilan autentikasi
            Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
            try {
                //Jalankan intent jika autentikasi telah dilakukan, kirim request code
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (Exception e) {

                //Exception dijalankan jika device Android belum mengimplementasikan PIN/password/pattern
                //User dipaksa untuk membuat pengaturan security untuk membuatan PIN/password/pattern
                Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    //Jalankan intent ke setting
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (Exception ex) {
                    //Jika setting tidak ditemukan, infokan ke user jika user harus membuat PIN/passowrd/pattern
                    // secara manual
                    textView.setText(getResources().getString(R.string.setting_label));
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    // Jika autentikasi tidak sukses, update TextView pada main_activity.xml
                    textView.setText(getResources().getString(R.string.unlock_failed));
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                // Cek apakah user sudah menyalakan pengaturan security baik PIN/password/pattern
                if (isDeviceSecure()) {
                    // Jika ditemukan pengaturan security
                    Toast.makeText(this, getResources().getString(R.string.device_is_secure),
                            Toast.LENGTH_SHORT).show();
                    // Lakukan autentikasi ulang
                    authenticateApp();
                } else {
                    // Jika tidak ditemukan pengaturan security, update TextView di main_activity.xml
                    textView.setText(getResources().getString(R.string.security_device_cancelled));
                }
                break;
        }
    }

    // Method untuk cek security setting
    private boolean isDeviceSecure(){
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //Cek pengaturan security pada SDK versi 16 (Jelly Beans) ke atas
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager.isKeyguardSecure();

        // Kita dapat menggunakan keyguardManager.isDeviceSecure();
        // akan tetapi minimal SDK yang harus digunakan adalah versi 23 atau Marsmellow
        // Contoh :
        // return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && keyguardManager.isDeviceSecure();
    }

    // Method untuk autentikasi ulang. Method ini dipanggil langsung dari parameter onClick()
    // pada view button di main_activity.xml
    public void reauthenticate(View view){
        authenticateApp();
    }


}
