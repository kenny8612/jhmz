package com.kenny.jhmz.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.StorageRecode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmResults;

public class RecodeAdapter extends BaseAdapter {
    private final Context mContext;
    private RealmResults<StorageRecode> storageRecodes;
    private final DateFormat dateFormat;

    public RecodeAdapter(Context mContext) {
        this.mContext = mContext;
        dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    }

    @Override
    public int getCount() {
        if(storageRecodes != null)
            return storageRecodes.size();
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return storageRecodes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView;
        if (view == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recode_item, viewGroup, false);
        else
            convertView = view;

        TextView tv_barcode = convertView.findViewById(R.id.tv_barcode);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_in_number = convertView.findViewById(R.id.tv_in_number);
        TextView tv_out_number = convertView.findViewById(R.id.tv_out_number);
        TextView tv_total_number = convertView.findViewById(R.id.tv_total_number);
        TextView tv_time = convertView.findViewById(R.id.tv_time);

        StorageRecode product = storageRecodes.get(i);
        if (product != null) {
            tv_barcode.setText(String.format("条码:%s", product.getBarcode()));
            tv_name.setText(String.format("名称:%s", product.getName()));
            tv_in_number.setText(String.format(Locale.US,"入库:%d", product.getIn_storage_number()));
            tv_out_number.setText(String.format(Locale.US,"出库:%d", product.getOut_storage_number()));
            String time = dateFormat.format(new Date(product.getTime()));
            if (product.getOperator_type() == StorageRecode.OPERATOR_TYPE_IN) {
                convertView.setBackgroundColor(Color.parseColor("#FFFACD"));
                tv_time.setText(String.format("入库时间:%s", time));
                tv_total_number.setText(String.format(Locale.US,"总库存:%d", product.getTotal_storage_number()));
                tv_out_number.setVisibility(View.GONE);
                tv_in_number.setVisibility(View.VISIBLE);
            } else if (product.getOperator_type() == StorageRecode.OPERATOR_TYPE_OUT) {
                convertView.setBackgroundColor(Color.parseColor("#FFF0F5"));
                tv_time.setText(String.format("出库时间:%s", time));
                tv_total_number.setText(String.format(Locale.US,"剩余库存:%d", product.getTotal_storage_number()));
                tv_in_number.setVisibility(View.GONE);
                tv_out_number.setVisibility(View.VISIBLE);
            }else if (product.getOperator_type() == StorageRecode.OPERATOR_TYPE_DELETE) {
                convertView.setBackgroundColor(Color.parseColor("#A8A8A8"));
                tv_total_number.setText(String.format(Locale.US,"总库存:%d", product.getTotal_storage_number()));
                tv_in_number.setVisibility(View.GONE);
                tv_out_number.setVisibility(View.GONE);
                tv_time.setText(String.format("删除时间:%s", time));
            }
        }
        return convertView;
    }

    public RealmResults<StorageRecode> getStorageRecodes() {
        return storageRecodes;
    }

    public void setStorageRecodes(RealmResults<StorageRecode> storageRecodes) {
        this.storageRecodes = storageRecodes;
    }
}
