package com.kenny.jhmz.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.ui.adapter.ProductAdapter;
import com.kenny.jhmz.ui.control.ProductListView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchStorageFragment extends Fragment {
    private ProductAdapter productAdapter;
    private RealmResults<Product> products;
    private EditText et_min_storage_number;
    private EditText et_max_storage_number;
    private EditText et_storage_search;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.search_storage_fragment, container, false);
        ProductListView listView = root.findViewById(R.id.lv_search);
        productAdapter = new ProductAdapter(getContext());
        listView.setAdapter(productAdapter);

        et_storage_search = root.findViewById(R.id.ed_storage_search);
        et_storage_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    search();
                }
                return true;
            }
        });
        et_min_storage_number = root.findViewById(R.id.et_min_storage_number);
        et_max_storage_number = root.findViewById(R.id.et_max_storage_number);
        et_max_storage_number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    search();
                }
                return true;
            }
        });

        ImageView bt_search = root.findViewById(R.id.bt_storage_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        return root;
    }

    private void search() {
        boolean search_by_number = false;
        int min_number = 0;
        int max_number = 0;

        String search_content = et_storage_search.getText().toString();
        if (!et_min_storage_number.getText().toString().isEmpty())
            min_number = Integer.parseInt(et_min_storage_number.getText().toString());
        if (!et_max_storage_number.getText().toString().isEmpty())
            max_number = Integer.parseInt(et_max_storage_number.getText().toString());

        if (min_number >= 0 && max_number >= 0 && max_number >= min_number)
            search_by_number = true;

        products = null;
        Realm realm = Realm.getDefaultInstance();
        if (!search_content.isEmpty()) {
            products = realm.where(Product.class).equalTo("barcode", search_content).findAll();
            if (products != null && products.size() > 0) {
                update_list();
                return;
            }
            RealmQuery<Product> realmQuery = realm.where(Product.class);
            if (search_by_number)
                realmQuery = realmQuery.between("number", min_number, max_number);

            products = realmQuery.contains("name", search_content).findAll();
        } else {
            if (search_by_number) {
                RealmQuery<Product> realmQuery = realm.where(Product.class).between("number", min_number, max_number);
                products = realmQuery.findAll();
            }
        }
        update_list();
    }

    private void update_list() {
        productAdapter.setProducts(products);
        productAdapter.notifyDataSetChanged();
    }
}
