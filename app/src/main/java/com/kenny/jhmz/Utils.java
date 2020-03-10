package com.kenny.jhmz;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.kenny.jhmz.data.Product;
import com.kenny.jhmz.data.StorageRecode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;

public class Utils {
    public interface OnPositiveButtonClickListener{
        void onPositiveButton();
    }

    public interface OnPasswordPassClickListener{
        void onPasswordPass();
    }

    public static void LongClickProductItem(final Context context, final Product product, final OnPositiveButtonClickListener listener){
        String[] items = {"复制条码", "修改名称", "删除"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher)
                .setTitle("选择操作")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText(null, product.getBarcode());
                            Objects.requireNonNull(clipboard).setPrimaryClip(clipData);
                            Toast.makeText(context, context.getString(R.string.copy_done), Toast.LENGTH_SHORT).show();
                        } else if (i == 1) {
                            showModifyDialog(context, product, listener);
                        } else if (i == 2) {
                            showDeleteProductDialog(context, product, listener);
                        }
                    }
                });
        builder.create().show();
    }

    public static void LongClickRecodeItem(final Context context, final StorageRecode recode, final OnPositiveButtonClickListener listener){
        String[] items = {"复制条码", "删除"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher)
                .setTitle("选择操作")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText(null, recode.getBarcode());
                            Objects.requireNonNull(clipboard).setPrimaryClip(clipData);
                            Toast.makeText(context, context.getString(R.string.copy_done), Toast.LENGTH_SHORT).show();
                        } else if (i == 1) {
                            showDeleteRecodeDialog(context, recode, listener);
                        }
                    }
                });
        builder.create().show();
    }

    private static void showDeleteRecodeDialog(Context context, final StorageRecode recode, final OnPositiveButtonClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle("确定删除该条库存记录？")
                .setMessage("\n编号（" + recode.getBarcode() + "）\n名称（" + recode.getName() + "）")
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Realm _realm = Realm.getDefaultInstance();
                        _realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                recode.deleteFromRealm();
                                listener.onPositiveButton();
                            }
                        });
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    private static void showModifyDialog(final Context context, final Product product, final OnPositiveButtonClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_storage_modify, null);
        final EditText etName = view.findViewById(R.id.ed_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view).setTitle("修改产品名称")
                .setMessage("编号（"+ product.getBarcode() + "）\n旧名称（" + product.getName() + "）?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(context.getString(R.string.modify), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        product.setName(etName.getText().toString());
                        product.setTime(System.currentTimeMillis());
                        realm.commitTransaction();
                        listener.onPositiveButton();
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.create().show();
    }

    private static void showDeleteProductDialog(final Context context, final Product product, final OnPositiveButtonClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher).setTitle("确定删除库存？")
                .setMessage("\n编号（" + product.getBarcode() + "）\n名称（" + product.getName() + "）\n库存量（" + product.getNumber() + "）")
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Realm _realm = Realm.getDefaultInstance();
                        _realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                StorageRecode ph = new StorageRecode();
                                ph.setBarcode(product.getBarcode());
                                ph.setName(product.getName());
                                ph.setTime(System.currentTimeMillis());
                                ph.setTotal_storage_number(product.getNumber());
                                ph.setOperator_type(StorageRecode.OPERATOR_TYPE_DELETE);
                                realm.copyToRealm(ph);
                                product.deleteFromRealm();
                                listener.onPositiveButton();
                            }
                        });
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void showPasswordDialog(final Context context, final OnPasswordPassClickListener listener) {
        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("请输入密码")
                .setView(editText)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = editText.getText().toString();
                        if (!password.isEmpty() && password.equals("12345678")) {
                            listener.onPasswordPass();
                        }
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        outBuff.flush();
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    public static String getVersionName(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getSupportBeginDayOfMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return firstDate;
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:29:38
     * @param year
     * @param monthOfYear
     * @return
     */
    public static Date getSupportEndDayOfMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return lastDate;
    }
}
