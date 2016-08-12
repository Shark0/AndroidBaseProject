package com.shark.baseproject.activity.product;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.shark.base.webservice.WebServiceErrorType;
import com.shark.baseproject.R;
import com.shark.baseproject.activity.BaseDemoActivity;
import com.shark.baseproject.webservice.task.product.ProductListTask;
import com.shark.baseproject.webservice.task.product.entitiy.ProductItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shark0 on 2016/8/12.
 */
public class ProductListActivity extends BaseDemoActivity implements ProductListTask.ProductListTaskListener, ProductAdapter.ProductAdapterListener {

    private boolean isNetworkError = false;
    private boolean hasNextPage = false;
    private String nextPage = "";

    private RecyclerView.OnScrollListener onScrollListener;

    private LinearLayoutManager layoutManager;
    private List<ProductItemEntity> productItemEntityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        setContentContainerId(R.id.activityProductList_productListRecyclerView);
        initProductListRecyclerView();
        showLoadingView();
        requestProductListWebService(nextPage);
    }

    private void initProductListRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activityProductList_productListRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void bindProductListRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activityProductList_productListRecyclerView);
        ProductAdapter adapter;
        if(recyclerView.getAdapter() == null) {
            adapter = new ProductAdapter(this, productItemEntityList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter = (ProductAdapter) recyclerView.getAdapter();
        }
        adapter.setHasNextPage(hasNextPage);
        adapter.setIsNextWorkError(isNetworkError);
        adapter.notifyDataSetChanged();

        if (hasNextPage) {
            if (onScrollListener == null) {
                onScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (isLastItemVisibility(layoutManager)) {
                            recyclerView.setOnScrollListener(null);
                            requestProductListWebService(nextPage);
                        }
                    }
                };
            }
            recyclerView.setOnScrollListener(onScrollListener);
        } else {
            recyclerView.setOnScrollListener(null);
        }
    };

    private void requestProductListWebService(String nextPage) {
        ProductListTask task = new ProductListTask(this, nextPage);
        startWebServiceTask(task);
    }

    @Override
    public void onProductListTaskFirstPageSuccess(List<ProductItemEntity> productItemEntityList, boolean hasNextPage, String nextPage) {
        showContentContainer();
        this.isNetworkError = false;
        this.hasNextPage = hasNextPage;
        this.nextPage = nextPage;
        this.productItemEntityList.clear();
        this.productItemEntityList.addAll(productItemEntityList);
        bindProductListRecyclerView();
    }

    @Override
    public void onProductListTaskFirstPageEmpty() {
        showEmptyView("There have no product items");
    }

    @Override
    public void onProductListTaskSuccess(List<ProductItemEntity> productItemEntities, boolean hasNextPage, String nextPage) {
        this.isNetworkError = false;
        this.hasNextPage = hasNextPage;
        this.nextPage = nextPage;
        this.productItemEntityList.addAll(productItemEntityList);
        bindProductListRecyclerView();
    }

    @Override
    public void onProductListTaskEmpty() {
        this.isNetworkError = false;
        this.hasNextPage = false;
        bindProductListRecyclerView();
    }

    @Override
    public void onProductListTaskError(int resultCode, String description) {
        showMessageView(description);
    }

    @Override
    public void onProductListTaskNetworkError(WebServiceErrorType errorType, boolean isFirstPage) {
        if(isFirstPage) {
            showNetworkErrorView(generateNetworkErrorDescription(errorType));
            return;
        }
        isNetworkError = false;
        bindProductListRecyclerView();
    }

    @Override
    protected void onNetworkErrorRetryButtonClick() {
        showLoadingView();
        requestProductListWebService(nextPage);
    }

    @Override
    public void onProductItemClick(int position) {
        Toast.makeText(this, "Click Produt " + productItemEntityList.get(position).getTitle(), Toast.LENGTH_LONG).show();
        return;
    }

    @Override
    public void onRetryItemClick() {
        isNetworkError = false;
        bindProductListRecyclerView();

    }

    private boolean isLastItemVisibility(LinearLayoutManager layoutManager) {
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        return (visibleItemCount + firstVisibleItemPosition + 5 >= totalItemCount);
    }

}
