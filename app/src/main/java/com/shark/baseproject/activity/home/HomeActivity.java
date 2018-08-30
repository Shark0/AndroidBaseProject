package com.shark.baseproject.activity.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.shark.baseproject.R;
import com.shark.baseproject.activity.product.ProductListActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bindViews();
    }

    private void bindViews() {
        bindProductListDemoButton();
    }

    private void bindProductListDemoButton() {
        findViewById(R.id.activityHome_productListDemoButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startProductListActivity();
    }

    private void startProductListActivity() {
        startActivity(new Intent(this, ProductListActivity.class));
    }
}
