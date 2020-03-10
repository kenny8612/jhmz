package com.kenny.jhmz.ui.outstorage;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.data.StorageRecode;

import java.util.Objects;

import io.realm.Realm;

public class OutStorageFragment extends Fragment {
    private EditText ed_barcode;
    private EditText ed_number;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.out_storage_fragment, container, false);
        ed_barcode = root.findViewById(R.id.ed_barcode);
        ed_number = root.findViewById(R.id.ed_number);

        Button bt_out_storage = root.findViewById(R.id.bt_out_storage);
        bt_out_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ed_barcode.getText().toString().isEmpty() && !ed_number.getText().toString().isEmpty()){
                    String barcode = ed_barcode.getText().toString();
                    int number = Integer.parseInt(ed_number.getText().toString());
                    Realm realm = Realm.getDefaultInstance();
                    Product product = realm.where(Product.class).equalTo("barcode", barcode).findFirst();
                    if(product != null){
                        int storage_number = product.getNumber();
                        if(storage_number < number){
                            Toast toast = Toast.makeText(getContext(), getString(R.string.err_out_storage_number), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        showRemindDialog(product, number);
                    }else {
                        Toast toast = Toast.makeText(getContext(), getString(R.string.err_out_storage_no_product), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.error_out_storage_data), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        return root;
    }

    private void showRemindDialog(final Product product, final int number) {
        final int remain_number = product.getNumber() - number;
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext())).setIcon(R.mipmap.ic_launcher).setTitle("出库确认？")
                .setMessage("\n编号（" + product.getBarcode() + "）\n名称（"+ product.getName()+ "）\n原库存量（" + product.getNumber() + "）"+"\n当前出库数量（"+number+"）"+"\n剩余库存量（"+remain_number+")")
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        product.setNumber(remain_number);
                        product.setTime(System.currentTimeMillis());

                        StorageRecode ph = realm.createObject(StorageRecode.class);
                        ph.setBarcode(product.getBarcode());
                        ph.setName(product.getName());
                        ph.setTotal_storage_number(product.getNumber());
                        ph.setOut_storage_number(number);
                        ph.setTime(product.getTime());
                        ph.setOperator_type(StorageRecode.OPERATOR_TYPE_OUT);
                        realm.copyToRealm(ph);
                        realm.commitTransaction();

                        Toast toast = Toast.makeText(getContext(), getString(R.string.out_storage_success), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}
