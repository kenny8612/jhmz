package com.kenny.jhmz;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private final String[] PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_in_storage, R.id.navigation_out_storage, R.id.navigation_storage_search, R.id.navigation_storage_recode)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permissions),
                    2000,
                    PERMS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!Realm.getDefaultInstance().isClosed())
            Realm.getDefaultInstance().close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == android.app.Activity.RESULT_OK) {
            if (requestCode == 1000) {
                if (data != null) {
                    List<String> list = data.getStringArrayListExtra("paths");
                    if (list != null) {
                        String path = list.get(0);
                        File file = new File(path);
                        if (file.exists())
                            showPopupWindow(path);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPopupWindow(final String path) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View mPopView = inflater.inflate(R.layout.import_warn_dialog, null);
        final PopupWindow popupWindow = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(mPopView, Gravity.CENTER_VERTICAL, 0, 0);

        Button bt_ok = mPopView.findViewById(R.id.bt_ok);
        Button bt_cancel = mPopView.findViewById(R.id.bt_cancel);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                Utils.showPasswordDialog(MainActivity.this, new Utils.OnPasswordPassClickListener() {
                    @Override
                    public void onPasswordPass() {
                        Realm realm = Realm.getDefaultInstance();
                        try {
                            Utils.copyFile(new File(path), new File(realm.getPath()));
                            realm.close();
                            Toast.makeText(getApplicationContext(), "导入成功，应用将自动重启生效", Toast.LENGTH_LONG).show();
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
