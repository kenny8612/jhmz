package com.kenny.jhmz.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.data.StorageRecode;
import com.kenny.jhmz.Utils;
import com.kenny.jhmz.ui.adapter.ProductAdapter;
import com.kenny.jhmz.ui.control.ProductListView;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AllStorageFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ProductListView listView = root.findViewById(R.id.lv_all_storage);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Product> productList = realm.where(Product.class).findAll().sort("time", Sort.DESCENDING);
        ProductAdapter productAdapter = new ProductAdapter(getContext());
        productAdapter.setProducts(productList);
        listView.setAdapter(productAdapter);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_recode:
                Realm realm = Realm.getDefaultInstance();
                try {
                    if (realm.where(Product.class).findAll().size() > 0 || realm.where(StorageRecode.class).findAll().size() > 0) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.US);
                        String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/jhmz/";
                        File dir = new File(path);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        String time = dateFormat.format(new Date(System.currentTimeMillis()));
                        String fileName = "database-" + time + ".realm";
                        Utils.copyFile(new File(realm.getPath()), new File(path + fileName));
                        Toast.makeText(getContext(), "导出成功\n"+path + fileName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "当前没有库存记录，导出失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "导出失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.import_recode:
                new LFilePicker()
                        .withActivity(getActivity())
                        .withRequestCode(1000)
                        .withChooseMode(true)
                        .withMutilyMode(false)
                        .withFileFilter(new String[]{".realm"})
                        .withStartPath("/storage/emulated/0/jhmz")
                        .start();
                break;
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("版本名:"+Utils.getVersionName(Objects.requireNonNull(getContext()))+"\n版本号:"+Utils.getVersionCode(getContext()));
                builder.create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
