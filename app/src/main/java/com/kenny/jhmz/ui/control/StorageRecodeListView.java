package com.kenny.jhmz.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kenny.jhmz.data.StorageRecode;
import com.kenny.jhmz.Utils;
import com.kenny.jhmz.ui.adapter.RecodeAdapter;

public class StorageRecodeListView extends ListView {
    public StorageRecodeListView(Context context) {
        super(context);
        initView(context);
    }

    public StorageRecodeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StorageRecodeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                StorageRecode recode = (StorageRecode)adapterView.getItemAtPosition(i);
                Utils.LongClickRecodeItem(context, recode, new Utils.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButton() {
                        RecodeAdapter adapter = (RecodeAdapter)getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });
    }
}
