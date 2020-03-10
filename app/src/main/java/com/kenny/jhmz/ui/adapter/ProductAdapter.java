package com.kenny.jhmz.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import io.realm.RealmResults;

public class ProductAdapter extends BaseAdapter {
    private final Context mContext;
    private RealmResults<Product> products;

    public ProductAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        if(products != null)
            return products.size();
       return 0;
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root_view;
        if (view == null)
            root_view = LayoutInflater.from(mContext).inflate(R.layout.product_item, viewGroup, false);
        else
            root_view = view;

        TextView barcode = root_view.findViewById(R.id.tv_barcode);
        TextView name = root_view.findViewById(R.id.tv_name);
        TextView number = root_view.findViewById(R.id.tv_number);

        Product product = products.get(i);
        if(product != null){
            barcode.setText(product.getBarcode());
            name.setText(product.getName());

            int num = product.getNumber();
            number.setText(String.valueOf(num));
            if(num < 10)
                number.setTextColor(Color.RED);
            else
                number.setTextColor(Color.BLACK);
        }
        return root_view;
    }

    public void setProducts(RealmResults<Product> productList) {
        products = productList;
    }

    public RealmResults<Product> getProducts(){
        return products;
    }
}
