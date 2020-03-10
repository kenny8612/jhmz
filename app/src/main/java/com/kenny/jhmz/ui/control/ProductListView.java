package com.kenny.jhmz.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.Utils;
import com.kenny.jhmz.ui.adapter.ProductAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class ProductListView extends ListView {
    ProductAdapter productAdapter;
    private boolean number_click_flag;

    public ProductListView(Context context) {
        super(context);
        initView(context);
    }

    public ProductListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProductListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_listview_header, null);
        addHeaderView(view);

        TextView tv_number = view.findViewById(R.id.tv_number);
        tv_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortListView();
            }
        });

        setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product) adapterView.getItemAtPosition(i);
                Utils.LongClickProductItem(getContext(), product, new Utils.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButton() {
                        productAdapter.notifyDataSetChanged();
                    }
                });
                return true;
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if(adapter instanceof ProductAdapter)
            productAdapter = (ProductAdapter)adapter;
        super.setAdapter(adapter);
    }

    private void sortListView(){
        if(productAdapter != null && productAdapter.getProducts() != null) {
            Sort sort_flag = number_click_flag ? Sort.DESCENDING : Sort.ASCENDING;
            number_click_flag = !number_click_flag;

            RealmResults<Product> products = productAdapter.getProducts();
            products = products.sort("number", sort_flag);

            productAdapter.setProducts(products);
            productAdapter.notifyDataSetChanged();
        }
    }
}
