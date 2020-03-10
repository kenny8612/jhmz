package com.kenny.jhmz.ui.instorage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.data.StorageRecode;

import java.util.Objects;

import io.realm.Realm;

public class InStorageFragment extends Fragment {
    private EditText ed_barcode;
    private EditText ed_name;
    private EditText ed_number;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.in_storage_fragment, container, false);
        ed_barcode = root.findViewById(R.id.ed_barcode);
        ed_name = root.findViewById(R.id.ed_name);
        ed_number = root.findViewById(R.id.ed_number);
        final Button bt_in_storage = root.findViewById(R.id.bt_in_storage);
        bt_in_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inStorage();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ed_barcode.setFocusable(true);
        //ed_barcode.setFocusableInTouchMode(true);
        //ed_barcode.requestFocus();
        //InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //inputManager.showSoftInput(ed_barcode, 0);
    }

    private void inStorage() {
        final String barcode = ed_barcode.getText().toString();
        String number = ed_number.getText().toString();
        if (!barcode.isEmpty() && !number.isEmpty()) {
            final String name = ed_name.getText().toString();
            final int num = Integer.parseInt(ed_number.getText().toString());
            final Realm _realm = Realm.getDefaultInstance();
            final Product product = _realm.where(Product.class).equalTo("barcode", barcode).findFirst();
            if (product != null) {
                showUpdateDialog(product, num, name);
            } else {
                _realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        final Product obj = _realm.createObject(Product.class, barcode);
                        obj.setName(name);
                        obj.setNumber(num);
                        obj.setTime(System.currentTimeMillis());
                        realm.copyToRealm(obj);

                        StorageRecode ph = realm.createObject(StorageRecode.class);
                        ph.setBarcode(obj.getBarcode());
                        ph.setName(obj.getName());
                        ph.setIn_storage_number(obj.getNumber());
                        ph.setTotal_storage_number(obj.getNumber());
                        ph.setTime(obj.getTime());
                        ph.setOperator_type(StorageRecode.OPERATOR_TYPE_IN);
                        realm.copyToRealm(ph);

                        Toast toast = Toast.makeText(getContext(), getString(R.string.in_storage_success), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                    }
                });
            }
        }else {
            Toast toast = Toast.makeText(getContext(), getString(R.string.error_in_storage_data), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void showUpdateDialog(final Product product, final int new_number, final String new_name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext())).setIcon(R.mipmap.ic_launcher).setTitle("入库确认？")
                .setMessage("库存编号（" + product.getBarcode() + "）\n名称（" + product.getName() + "） \n原库存量（" + product.getNumber() + "）\n当前入库数量（" + new_number
                        + "）\n已经存在，是否执行更新库存操作？")
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateStorage(product, new_number, new_name);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private void updateStorage(final Product product, final int new_number, final String new_name) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if (!new_name.isEmpty() && product.getName().isEmpty())
            product.setName(new_name);
        else if (!new_name.isEmpty() && !product.getName().isEmpty() && !new_name.equals(product.getName()))
            product.setName(new_name);

        product.setNumber(product.getNumber() + new_number);
        product.setTime(System.currentTimeMillis());

        StorageRecode ph = realm.createObject(StorageRecode.class);
        ph.setBarcode(product.getBarcode());
        ph.setName(product.getName());
        ph.setIn_storage_number(new_number);
        ph.setTotal_storage_number(product.getNumber());
        ph.setTime(product.getTime());
        ph.setOperator_type(StorageRecode.OPERATOR_TYPE_IN);
        realm.copyToRealm(ph);
        realm.commitTransaction();
        Toast toast = Toast.makeText(getContext(), getString(R.string.in_storage_success), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
