package com.shark.baseproject.activity.purchase;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shark.base.activity.iab.entity.IabSkuItemEntity;
import com.shark.baseproject.R;
import com.shark.baseproject.webservice.task.purchase.entity.PurchaseItemEntity;

import java.util.HashMap;
import java.util.List;


/**
 * Created by shark on 2015/3/30.
 */
public class PurchaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private PurchaseAdapterListener listener;
    private FragmentManager fragmentManager;
    private List<IabSkuItemEntity> skuDetailsList;
    private List<PurchaseItemEntity> purchaseItemEntityList;

    private HashMap<String, TextView> discountTimeTextViewHashMap = new HashMap<>();

    public PurchaseAdapter(PurchaseAdapterListener listener, FragmentManager fragmentManager,
                           List<IabSkuItemEntity> skuDetailsList, List<PurchaseItemEntity> purchaseItemEntityList) {
        this.listener = listener;
        this.fragmentManager = fragmentManager;
        this.skuDetailsList = skuDetailsList;
        this.purchaseItemEntityList = purchaseItemEntityList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PurchaseItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_purchase_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindPurchaseItem((PurchaseItemViewHolder) holder, skuDetailsList.get(position));

    }


    private void bindPurchaseItem(PurchaseItemViewHolder holder, IabSkuItemEntity skuItem) {
        holder.titleTextView.setText(skuItem.getTitle() + ", " + skuItem.getPrice());
        PurchaseItemEntity item = findPurchaseItemBySku(purchaseItemEntityList, skuItem.getSku());
        if(item != null) {
            holder.descriptionView.setText(item.getDescription());
        }
    }

    private class PurchaseItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        PurchaseAdapterListener listener;
        private TextView titleTextView;
        private TextView descriptionView;
        public PurchaseItemViewHolder(View itemView, PurchaseAdapterListener listener) {
            super(itemView);
            this.listener = listener;
            titleTextView = (TextView) itemView.findViewById(R.id.adapterPurchaseItem_titleTextView);
            itemView.setOnClickListener(this);
            descriptionView = (TextView) itemView.findViewById(R.id.adapterPurchaseItem_descriptionTextView);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition() - 1;
            if(index >= 0) {
                listener.onPurchaseItemClick(index);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 1 + skuDetailsList.size();
    }

    public HashMap<String, TextView> getDiscountTimeTextViewHashMap() {
        return discountTimeTextViewHashMap;
    }

    private PurchaseItemEntity findPurchaseItemBySku(List<PurchaseItemEntity> purchaseItemEntityList, String sku) {
        for(PurchaseItemEntity purchaseItemEntity: purchaseItemEntityList) {
            if(purchaseItemEntity.getSku().equalsIgnoreCase(sku)) {
                return purchaseItemEntity;
            }
        }
        return null;
    }

    public static interface PurchaseAdapterListener {
        public void onPurchaseItemClick(int index);
    }
}
