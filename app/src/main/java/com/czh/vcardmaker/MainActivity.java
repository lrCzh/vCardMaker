package com.czh.vcardmaker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

//    private static final String TAG = "MainActivity";

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    private EditText et_name;
    private EditText et_company;
    private EditText et_address;
    private EditText et_phone;
    private EditText et_email;
    private EditText et_website;

    private String name;
    private String company;
    private String address;
    private String phone;
    private String email;
    private String website;

    private StringBuilder sb = new StringBuilder();
    private AlertDialog writeDataDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_name = findViewById(R.id.et_name);
        et_company = findViewById(R.id.et_company);
        et_address = findViewById(R.id.et_address);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_website = findViewById(R.id.et_website);
        Button btn = findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = et_name.getText().toString().trim();
                company = et_company.getText().toString().trim();
                address = et_address.getText().toString().trim();
                phone = et_phone.getText().toString().trim();
                email = et_email.getText().toString().trim();
                website = et_website.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    dataLack();
                    return;
                }

                if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
                    hintOpenNfc();
                    return;
                }

                sb.setLength(0);
                sb.append("BEGIN:VCARD\n")
                        .append("VERSION:3.0\n")
                        .append("FN:").append(name).append("\n")
                        .append("TEL;TYPE=CELL:").append(phone).append("\n")
                        .append("EMAIL;TYPE=HOME,INTERNET:").append(email).append("\n")
                        .append("ADR;TYPE=HOME:;;").append(address).append(";;;;\n")
                        .append("ORG:").append(company).append("\n")
                        .append("URL:").append(website).append("\n")
                        .append("END:VCARD");

                writeData();
            }
        });

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            nonSupport();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            hintOpenNfc();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            if (TextUtils.isEmpty(sb.toString())) {
                Toast.makeText(this, "请先完善信息点击确定按钮再贴NFC标签", Toast.LENGTH_SHORT).show();
                return;
            }
            NdefMessage ndefMessage = new NdefMessage(NdefRecord.createMime("text/vcard", sb.toString().getBytes()));
            writeTag(ndefMessage, tag);
            if (writeDataDialog != null) {
                writeDataDialog.dismiss();
            }
        }
    }


    /**
     * 弹对话框提示设备不支持NFC
     */
    private void nonSupport() {
        AlertDialog nonSupportDialog = new AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage("很遗憾，您的设备不支持NFC！")
                .setCancelable(false)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();

        nonSupportDialog.show();
    }


    /**
     * 用于引导用户开启NFC
     */
    private void hintOpenNfc() {
        AlertDialog hintOpenNfcDialog = new AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage("使用本程序需要开启NFC，是否前往设置页面开启？")
                .setCancelable(false)
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                        startActivity(setNfc);
                    }
                }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();

        hintOpenNfcDialog.show();
    }


    /**
     * 弹对话框，开始准备写数据
     */
    private void writeData() {
        writeDataDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Tip")
                .setMessage("请将NFC标签或者贴纸靠近手机背面！")
                .setCancelable(false)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        writeDataDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //恢复默认状态
                if (mNfcAdapter != null)
                    mNfcAdapter.disableForegroundDispatch(MainActivity.this);
            }
        });

        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(MainActivity.this, mPendingIntent, null, null);

        writeDataDialog.show();
    }


    /**
     * 写入标签
     */
    public void writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "失败：标签无法擦写", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, "失败：标签容量不足", Toast.LENGTH_SHORT).show();
                    return;
                }
                ndef.writeNdefMessage(message);
                Toast.makeText(MainActivity.this, "数据写入成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 弹对话框提示信息不全
     */
    private void dataLack() {
        AlertDialog dataLackDialog = new AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage("请至少完善姓名和号码信息！")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();

        dataLackDialog.show();
    }
}
