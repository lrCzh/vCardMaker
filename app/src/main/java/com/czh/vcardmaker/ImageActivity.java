package com.czh.vcardmaker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {


    private ImageView imageView;

    public static final int TYPE_WEIXIN = 1;
    public static final int TYPE_ALIPAY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        imageView = findViewById(R.id.iv);
        int type = getIntent().getIntExtra("type", 0);
        if (type == TYPE_WEIXIN) {
            Glide.with(this).load(R.drawable.weixin).into(imageView);
        } else
            Glide.with(this).load(R.drawable.alipay).into(imageView);
    }

    public static void actionStart(Context context, int type) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
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
