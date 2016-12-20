package com.shark.baseproject.activity.purchase;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.shark.base.activity.iab.BasePurchaseActivity;
import com.shark.base.activity.iab.entity.IabPurchaseEntity;
import com.shark.base.activity.iab.entity.IabSkuItemEntity;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.R;
import com.shark.baseproject.manager.LoginManager;
import com.shark.baseproject.webservice.task.purchase.PurchaseFinishTask;
import com.shark.baseproject.webservice.task.purchase.PurchaseItemListTask;
import com.shark.baseproject.webservice.task.purchase.entity.PurchaseItemEntity;
import com.shark.baseproject.webservice.worker.volley.VolleyWebServiceWorker;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shark0 on 2016/8/15.
 *  Step 1. Init Iab.
 *  Step 2. check iab purchased item, and consume purchase item.
 *  Step 3. request user purchasable sku list.
 *  Step 4. use user purchasable sku list to request iab purchase item list.
 *  Step 5. user click purchase item.
 *  Step 6. request iab purchase.
 *  Step 7. request your purchase api.
 *  Step 8. consume iab purchase.
 */
public class PurchaseActivity extends BasePurchaseActivity implements PurchaseItemListTask.PurchaseItemTaskListener, PurchaseAdapter.PurchaseAdapterListener, PurchaseFinishTask.PurchaseFinishTaskListener {

    private ArrayList<PurchaseItemEntity> purchaseItems = new ArrayList<>();
    private ArrayList<String> purchaseItemIds = new ArrayList<>();
    private List<IabPurchaseEntity> unConsumePurchaseList = new ArrayList<>();

    private List<IabSkuItemEntity> skuItemList = new ArrayList<>();
    private IabSkuItemEntity purchaseSkuItem;

    private PurchaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        setContentContainerId(R.id.activityPurchase_contentContainer);
        bindContentView();
        showLoadingView();
        //Step1 Init Iab Purchase - Shark.M.Lin
        initIabPurchaseService();
    }

    private void bindContentView() {
        bindNetworkStateViews();
    }

    protected void bindNetworkStateViews() {
        //When application use service API, there have 4 states application have to handle. - Shark.M.Lin
        //1. Loading - When activity start, application have to show loading view to let user know page is connecting.
        //2. Message - When user have no permission to see some page content, show some message to user.
        //3. Empty - If this page have no content, show some message and button to redirect user to other page.
        //4. Network Error - if network error, give user reconnect button.
        //5. init iab service error - if init iab error, give user reconnect button.
        //6. get google purchase item error - if get iab purchased item error, give user reconnect button.
        //BasePurchase only handle 6 states hide and show, developer have to bind 6 state views by self.
        setLoadingViewContainerId(R.id.viewLoading_container);
        setMessageViewContainerId(R.id.viewMessage_container);
        setEmptyViewContainerId(R.id.viewEmpty_container);
        setNetworkErrorViewContainerId(R.id.viewNetworkError_container);
        setInitIabErrorContainerId(R.id.viewInitIabServiceError_contentContainer);
        bindInitIabErrorView();
        setGetIabPurchaseItemErrorContainerId(R.id.viewGetIabPurchaseItemError_contentContainer);
        bindGetIabPurchaseItemErrorView();
    }

    private void bindInitIabErrorView() {
        findViewById(R.id.viewInitIabServiceError_retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initIabPurchaseService();
            }
        });
    }

    private void bindGetIabPurchaseItemErrorView() {
        findViewById(R.id.viewGetIabPurchaseItemError_retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestIabPurchasedItemWebService();
            }
        });
    }

    private void bindPurchaseItemRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activityPurchase_purchaseItemRecyclerView);
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (adapter == null) {
            adapter = new PurchaseAdapter(this, getSupportFragmentManager(), skuItemList, purchaseItems);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    protected void showMessageView(String message) {
        super.showMessageView();
        TextView textView = (TextView) findViewById(R.id.viewMessage_messageTextView);
        textView.setText(message);
    }

    protected void showEmptyView(String message) {
        super.showEmptyView();
        TextView textView = (TextView) findViewById(R.id.viewEmpty_messageTextView);
        textView.setText(message);
    }

    protected void showNetworkErrorView(String message) {
        super.showNetworkErrorView();
        TextView textView = (TextView) findViewById(R.id.viewNetworkError_messageTextView);
        textView.setText(message);
        findViewById(R.id.viewNetworkError_retryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNetworkErrorRetryButtonClick();
            }
        });
    }

    protected void showErrorDialog(String title, String message, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog_AppCompat_Light));
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelable);
        builder.show();
    }

    @Override
    protected void onIabInitPurchaseServiceError(PurchaseServiceError error) {
        showInitIabServiceErrorView();
    }

    @Override
    protected void onIabInitPurchaseServiceSuccess() {
        //Step 2. Request Purchased Item List - Shark.M.Lin
        requestIabPurchasedItemWebService();
    }

    @Override
    protected void onRequestIabPurchasedItemResponseSuccess(List<IabPurchaseEntity> purchasedItemList) {
        if (!purchasedItemList.isEmpty()) {
            unConsumePurchaseList.addAll(purchasedItemList);
            showLoadingDialog("Check Un Consume Purchase", "Please Wait", true);
            //If have un-consume purchase item, request purchase finish web service
            requestPurchaseFinishWebService(unConsumePurchaseList.get(0));
        }
        //Step 3. Request API Purchasable Item List - Shark.M.Lin
        requestPurchaseItemWebService();
    }

    @Override
    protected void onRequestIabPurchasedItemResponseError() {
        showGetIabPurchaseItemErrorView();
    }


    private void requestPurchaseItemWebService() {
        String memberId = LoginManager.getInstance().getLoginResult().getMemberId();
        PurchaseItemListTask task = new PurchaseItemListTask(this, memberId);
        startWebServiceTask(task);
    }

    @Override
    public void onPurchaseItemListTaskSuccess(List<PurchaseItemEntity> purchaseItemEntityList) {
        List<String> skuList = new ArrayList<>();
        for (PurchaseItemEntity item : purchaseItems) {
            skuList.add(item.getSku());
        }
        this.purchaseItems.clear();
        this.purchaseItems.addAll(purchaseItemEntityList);
        this.purchaseItemIds.clear();
        this.purchaseItemIds.addAll(skuList);
        //Step 4. Request IAB Purchasable Item List - Shark.M.Lin
        requestIabItemListWebService(this.purchaseItemIds);
    }

    @Override
    public void onPurchaseItemListTaskEmpty() {
        showEmptyView("There have no purchasable item");
    }

    @Override
    public void onPurchaseItemListTaskError(int returnCode, String systemDescription) {
        showMessageView(systemDescription);
    }

    @Override
    public void onPurchaseItemListTaskNetworkError(WebServiceErrorType errorType) {
        showNetworkErrorView(generateNetworkErrorDescription(errorType));
    }

    @Override
    protected void onIabItemListResponseSuccess(List<IabSkuItemEntity> inAppSkuDetailsList, List<IabSkuItemEntity> subSkuDetailsList) {
        if ((inAppSkuDetailsList == null || inAppSkuDetailsList.isEmpty()) &&
                (subSkuDetailsList == null || subSkuDetailsList.isEmpty())) {
            showEmptyView("There have no purchasable Item");
            return;
        }
        for (PurchaseItemEntity item : purchaseItems) {
            if (inAppSkuDetailsList != null) {
                for (IabSkuItemEntity skuItem : inAppSkuDetailsList) {
                    if (skuItem.getSku().equalsIgnoreCase(item.getSku())) {
                        skuItemList.add(skuItem);
                    }
                }
            }
            if (subSkuDetailsList != null) {
                for (IabSkuItemEntity skuItem : subSkuDetailsList) {
                    if (skuItem.getSku().equalsIgnoreCase(item.getSku())) {
                        skuItemList.add(skuItem);
                    }
                }
            }
        }
        showContentContainer();
        bindPurchaseItemRecyclerView();
    }

    @Override
    protected void onIabItemListResponseError() {
        showGetIabPurchaseItemErrorView();
    }

    @Override
    protected void onIabPurchaseResponseSuccess(IabPurchaseEntity purchaseEntity) {
        requestPurchaseFinishWebService(purchaseEntity);
    }

    @Override
    protected void onIabPurchaseResponseError() {
        hideLoadingDialog();
        String title = "There is an error with the items.";
        String message = "Connection fails. Please check the network connection.";
        showErrorDialog(title, message, false);
    }

    private void requestPurchaseFinishWebService(IabPurchaseEntity iabPurchaseEntity) {
        String memberId = LoginManager.getInstance().getLoginResult().getMemberId();
        PurchaseFinishTask task = new PurchaseFinishTask(this, memberId, iabPurchaseEntity);
        startWebServiceTask(task);
    }


    @Override
    public void onPurchaseFinishTaskSuccess(IabPurchaseEntity purchase) {
        hideLoadingDialog();
        Toast.makeText(this, "Purchase Success", Toast.LENGTH_LONG).show();
        if (!purchase.getItemType().equalsIgnoreCase("subs")) {
            unConsumePurchaseList.add(purchase);
            requestConsumingPurchaseWebService(unConsumePurchaseList.get(0).getToken());
        }
    }

    @Override
    public void onPurchaseFinishTaskConsumeAgain(IabPurchaseEntity purchase) {
        hideLoadingDialog();
        if (!purchase.getItemType().equalsIgnoreCase("subs")) {
            unConsumePurchaseList.add(purchase);
            //Step 7. Iab Consume
            requestConsumingPurchaseWebService(unConsumePurchaseList.get(0).getToken());
        }
    }

    @Override
    public void onPurchaseFinishTaskError(int resultCode, String message, IabPurchaseEntity purchase) {
        hideLoadingDialog();
        showErrorDialog("Purchase Error", message, true);
    }

    @Override
    public void onPurchaseFinishTaskNetworkError(WebServiceErrorType errorType, IabPurchaseEntity purchase) {
        hideLoadingDialog();
        String title = "Purchase Error";
        String message = "Connection fails. Please check the network connection.";
        showErrorDialog(title, message, false);
    }

    @Override
    protected void onConsumingPurchaseResponseSuccess(String token) {
        hideLoadingDialog();
        if (unConsumePurchaseList.size() > 0) {
            unConsumePurchaseList.remove(0);
        }
        if (unConsumePurchaseList.size() > 0) {
            requestConsumingPurchaseWebService(unConsumePurchaseList.get(0).getToken());
            return;
        }
    }

    @Override
    protected void onConsumingPurchaseResponseError(String token) {
        //do nothing - Shark.M.Lin
    }

    @Override
    protected String generateSignatureBase64() {
        //FIXME
        return "Please_Change_Your_Base64_Ket";
    }

    protected void startWebServiceTask(WebServiceTask task) {
        startWebServiceTask(new VolleyWebServiceWorker(task, this));
    };

    protected String generateNetworkErrorDescription(WebServiceErrorType errorType) {
        String description = "Service Error ";
        switch (errorType) {
            case WEB_IO_ERROR:
                description = description + "(I)";
                break;
            case JSON_ERROR:
                description = description + "(J)";
                break;
            case WEB_SERVER_ERROR:
                description = description + "(S)";
                break;
            case WEB_CLIENT_ERROR:
                description = description + "(C)";
                break;
            case UNKNOWN_ERROR:
                description = description + "(U)";
                break;
        }
        return description;
    }

    @Override
    public void onPurchaseItemClick(int index) {
        //Step 5. Click Purchasable Item
        if (isClickBlock()) {
            return;
        }
        purchaseSkuItem = skuItemList.get(index);
        String sku = purchaseSkuItem.getSku();
        if (debug) {
            Log.e("Purchase", "onItemClick index: " + index);
            Log.e("Purchase", "onItemClick item " + sku);
        }
        //Step 6. Request Iab Purchasable
        //Fixme Payload
        String payload = "your_payload_id";
        if (purchaseSkuItem.getType().equalsIgnoreCase(ITEM_TYPE_SUBS)) {
            requestIabPurchaseWebService(ITEM_TYPE_SUBS, sku, payload);
        } else {
            requestIabPurchaseWebService(ITEM_TYPE_IN_APP, sku, payload);
        }
    }

    protected void onNetworkErrorRetryButtonClick() {
        showLoadingView();
        requestPurchaseItemWebService();
    }
}