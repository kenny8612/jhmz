package com.kenny.jhmz.ui.recode;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.kenny.jhmz.R;
import com.kenny.jhmz.data.StorageRecode;
import com.kenny.jhmz.Utils;
import com.kenny.jhmz.ui.adapter.RecodeAdapter;
import com.kenny.jhmz.ui.control.StorageRecodeListView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RecodeStorageFragment extends Fragment {
    private Sort sor_type = Sort.DESCENDING;
    private RecodeAdapter adapter;
    private String search_content;
    private int show_type = 2;
    private Date date_begin;
    private Date date_end;
    private EditText ed_storage_search;
    private boolean search_flag = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.recode_storage_fragment, container, false);
        StorageRecodeListView listView = root.findViewById(R.id.lv_recode);
        final TextView curr_date = root.findViewById(R.id.current_date);
        Calendar cd = Calendar.getInstance();
        int cur_year = cd.get(Calendar.YEAR);
        int cur_month = cd.get(Calendar.MONTH) + 1;
        curr_date.setText(String.format(Locale.CHINA, "%d年%d月", cur_year, cur_month));

        date_begin = Utils.getSupportBeginDayOfMonth(cur_year, cur_month);
        date_end = Utils.getSupportEndDayOfMonth(cur_year, cur_month);

        final Realm realm = Realm.getDefaultInstance();
        RealmResults<StorageRecode> realmResults = realm.where(StorageRecode.class)
                .between("time", date_begin.getTime(), date_end.getTime())
                .findAll().sort("time", sor_type);
        adapter = new RecodeAdapter(getContext());
        adapter.setStorageRecodes(realmResults);

        listView.setAdapter(adapter);

        ed_storage_search = root.findViewById(R.id.ed_storage_search);
        ed_storage_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (EditorInfo.IME_ACTION_SEARCH == actionId) {
                    search();
                }
                return true;
            }
        });
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                search_content = ed_storage_search.getText().toString();
                if (search_content.isEmpty() && search_flag) {
                    search_flag = false;

                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<StorageRecode> storageRecodes;
                    RealmQuery<StorageRecode> recodeRealmQuery = realm.where(StorageRecode.class)
                            .between("time", date_begin.getTime(), date_end.getTime()).sort("time", sor_type);
                    if (show_type == 0 || show_type == 1) {
                        storageRecodes = recodeRealmQuery.equalTo("operator_type", show_type).findAll();
                    } else
                        storageRecodes = recodeRealmQuery.findAll();

                    adapter.setStorageRecodes(storageRecodes);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        ed_storage_search.addTextChangedListener(afterTextChangedListener);

        ImageView bt_search = root.findViewById(R.id.bt_storage_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        ImageButton select_recode_date = root.findViewById(R.id.select_recode_date);
        select_recode_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dlg = new DatePickerDialog(new ContextThemeWrapper(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        date_begin = Utils.getSupportBeginDayOfMonth(year, monthOfYear + 1);
                        date_end = Utils.getSupportEndDayOfMonth(year, monthOfYear + 1);
                        Realm realm = Realm.getDefaultInstance();
                        RealmQuery<StorageRecode> realmQuery = realm.where(StorageRecode.class)
                                .between("time", date_begin.getTime(), date_end.getTime())
                                .sort("time", sor_type);
                        RealmResults<StorageRecode> storageRecodes;
                        if (show_type == 0 || show_type == 1) {
                            storageRecodes = realmQuery.equalTo("operator_type", show_type).findAll();
                        } else
                            storageRecodes = realmQuery.findAll();
                        adapter.setStorageRecodes(storageRecodes);
                        adapter.notifyDataSetChanged();
                        curr_date.setText(String.format(Locale.CHINA, "%d年%d月", year, monthOfYear + 1));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        LinearLayout mSpinners = findViewById(getContext().getResources().getIdentifier("android:id/pickers", null, null));
                        if (mSpinners != null) {
                            NumberPicker mMonthSpinner = findViewById(getContext().getResources().getIdentifier("android:id/month", null, null));
                            NumberPicker mYearSpinner = findViewById(getContext().getResources().getIdentifier("android:id/year", null, null));
                            mSpinners.removeAllViews();
                            if (mMonthSpinner != null) {
                                mSpinners.addView(mMonthSpinner);
                            }
                            if (mYearSpinner != null) {
                                mSpinners.addView(mYearSpinner);
                            }
                        }
                        View dayPickerView = findViewById(getContext().getResources().getIdentifier("android:id/day", null, null));
                        if (dayPickerView != null) {
                            dayPickerView.setVisibility(View.GONE);
                        }
                    }
                };
                dlg.setTitle("请选择库存记录日期");
                dlg.show();
            }
        });

        Spinner spinner_recode_type = root.findViewById(R.id.spinner_recode_type);
        spinner_recode_type.setSelection(2);
        spinner_recode_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RealmQuery<StorageRecode> realmQuery;
                switch (i){
                    case 0:
                        show_type = 0;
                        realmQuery = realm.where(StorageRecode.class)
                                .equalTo("operator_type", StorageRecode.OPERATOR_TYPE_IN)
                                .between("time", date_begin.getTime(), date_end.getTime())
                                .sort("time", sor_type);
                        if (search_content != null && !search_content.isEmpty())
                            realmQuery = realmQuery.equalTo("barcode", search_content);
                        adapter.setStorageRecodes(realmQuery.findAll());
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        show_type = 1;
                        realmQuery = realm.where(StorageRecode.class)
                                .equalTo("operator_type", StorageRecode.OPERATOR_TYPE_OUT)
                                .between("time", date_begin.getTime(), date_end.getTime())
                                .sort("time", sor_type);
                        if (search_content != null && !search_content.isEmpty())
                            realmQuery.equalTo("barcode", search_content);
                        adapter.setStorageRecodes(realmQuery.findAll());
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        show_type = 2;
                        realmQuery = realm.where(StorageRecode.class).between("time", date_begin.getTime(), date_end.getTime()).sort("time", sor_type);
                        if (search_content != null && !search_content.isEmpty())
                            realmQuery.equalTo("barcode", search_content);
                        adapter.setStorageRecodes(realmQuery.findAll());
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    private void search() {
        if (search_content != null && !search_content.isEmpty()) {
            search_flag = true;
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<StorageRecode> realmQuery;
            realmQuery = realm.where(StorageRecode.class)
                    .equalTo("barcode", search_content)
                    .sort("time", sor_type);
            if (show_type == 0 || show_type == 1)
                realmQuery = realmQuery.equalTo("operator_type", show_type);
            adapter.setStorageRecodes(realmQuery.findAll());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.recode_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all_recode) {
            if (adapter.getStorageRecodes() != null && adapter.getStorageRecodes().size() > 0) {
                showPopupWindow();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopupWindow() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View mPopView = inflater.inflate(R.layout.delete_recode_warn_dialog, null);
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
                Utils.showPasswordDialog(getActivity(), new Utils.OnPasswordPassClickListener() {
                    @Override
                    public void onPasswordPass() {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                RealmResults<StorageRecode> _realmQuery = adapter.getStorageRecodes();
                                if (_realmQuery != null && _realmQuery.size() > 0) {
                                    _realmQuery.deleteAllFromRealm();
                                    adapter.setStorageRecodes(null);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
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
}
