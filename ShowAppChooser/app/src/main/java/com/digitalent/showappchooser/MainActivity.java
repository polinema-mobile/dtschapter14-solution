package com.digitalent.showappchooser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendEmail(View view){
        Intent i = new Intent(Intent.ACTION_SEND);

        // Isi intent send email
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"dts@polinema.ac.id"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Test Email");
        i.putExtra(Intent.EXTRA_TEXT, "Welcome to dts 2019");

        // Buat daftar client email yang ada di hardware android
        List<ResolveInfo> possibleApps = getPackageManager()
                .queryIntentActivities(i, PackageManager.MATCH_ALL);

        if(possibleApps.size() > 1){
            // Jika client email lebih dari satu, tampilkan app chooser
            String title = "Pilih Client Email";
            Intent chooser = Intent.createChooser(i, title);
            startActivity(chooser);
        } else if(i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }
}
