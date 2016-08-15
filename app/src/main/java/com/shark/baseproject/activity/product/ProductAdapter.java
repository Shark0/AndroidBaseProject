package com.shark.baseproject.activity.product;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.shark.baseproject.R;
import com.shark.baseproject.webservice.task.product.entitiy.ProductItemEntity;
import java.util.List;

/**
 * Created by Shark0 on 2015/12/31.
 */
public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING_ITEM = 0;
    private static final int VIEW_TYPE_RETRY_ITEM = 1;
    private static final int VIEW_TYPE_PRODUCT_ITEM = 2;

    private ProductAdapterListener listener;
    private List<ProductItemEntity> productItemEntityList;
    private boolean hasNextPage;
    private boolean isNextWorkError;

    public ProductAdapter(ProductAdapterListener listener, List<ProductItemEntity> productItemEntityList) {
        this.listener = listener;
        this.productItemEntityList = productItemEntityList;
    }

    @Override
    public int getItemViewType(int position) {
        if(isNextWorkError && position == getItemCount() -1) {
            return VIEW_TYPE_RETRY_ITEM;
        }
        if(hasNextPage && position == getItemCount() -1) {
            return VIEW_TYPE_LOADING_ITEM;
        }
        return VIEW_TYPE_PRODUCT_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOADING_ITEM:
                return new FooterLoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loading, parent, false));
            case VIEW_TYPE_RETRY_ITEM:
                return new FooterRetryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_retry, parent, false), listener);
            default:
                return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false), listener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProductViewHolder) {
            bindProductViewHolder((ProductViewHolder) holder, position);
        }
    }

    private void bindProductViewHolder(ProductViewHolder holder, int position) {
        ProductItemEntity productItemEntity = productItemEntityList.get(position);

        String title = productItemEntity.getTitle();
        holder.titleTextView.setText(title);
    }

    @Override
    public int getItemCount() {
        return productItemEntityList.size() + ((hasNextPage || isNextWorkError) ? 1 : 0);
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isNextWorkError() {
        return isNextWorkError;
    }

    public void setIsNextWorkError(boolean isNextWorkError) {
        this.isNextWorkError = isNextWorkError;
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ProductAdapterListener listener;
        View contentContainer;
        TextView titleTextView;

        public ProductViewHolder(View itemView, ProductAdapterListener listener) {
            super(itemView);
            this.listener = listener;
            contentContainer = itemView.findViewById(R.id.adapterProduct_contentView);
            contentContainer.setOnClickListener(this);
            titleTextView = (TextView) itemView.findViewById(R.id.adapterProduct_titleTextView);
        }

        @Override
        public void onClick(View view) {
            int index = getLayoutPosition();
            if(index < 0) {
                return;
            }
            switch (view.getId()) {
                case R.id.adapterProduct_contentView:
                    listener.onProductItemClick(index);
                    break;
            }
        }
    }

    private class FooterLoadingViewHolder extends RecyclerView.ViewHolder {
        public FooterLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class FooterRetryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProductAdapterListener listener;
        public FooterRetryViewHolder(View itemView, ProductAdapterListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.findViewById(R.id.adapterRetry_retryButton).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRetryItemClick();
        }
    }

    public static interface ProductAdapterListener {
        public void onProductItemClick(int position);
        public void onRetryItemClick();
    }
}
