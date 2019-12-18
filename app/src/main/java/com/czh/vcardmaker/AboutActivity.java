package com.czh.vcardmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private LinearLayout ll_weixin;
    private LinearLayout ll_alipay;
    private TextView tv_privacy_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ll_weixin = findViewById(R.id.ll_weixin);
        ll_alipay = findViewById(R.id.ll_alipay);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);

        ll_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageActivity.actionStart(AboutActivity.this, ImageActivity.TYPE_WEIXIN);
            }
        });

        ll_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageActivity.actionStart(AboutActivity.this, ImageActivity.TYPE_ALIPAY);
            }
        });

        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this,PrivacyPolicyActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }else
            return super.onOptionsItemSelected(item);
    }
}
