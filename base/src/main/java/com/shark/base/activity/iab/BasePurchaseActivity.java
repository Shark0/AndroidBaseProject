package com.shark.base.activity.iab;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.google.gson.Gson;
import com.shark.base.activity.BaseActivity;
import com.shark.base.activity.iab.entity.IabPurchaseEntity;
import com.shark.base.activity.iab.entity.IabPurchaseItemListResultEntity;
import com.shark.base.activity.iab.entity.IabSkuItemEntity;
import com.shark.base.activity.iab.security.IabSecurity;
import com.shark.base.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shark0 on 2016/4/25.
 */
public abstract class BasePurchaseActivity extends BaseActivity {

    protected final int REQUEST_CODE_PURCHASE = 2001;

    protected int initIabErrorContainerId = -1;
    protected int getIabPurchaseItemErrorContainerId = -1;

    protected enum PurchaseServiceError {DISCONNECT, NOT_SUPPORT}

    protected final String ITEM_TYPE_IN_APP = "inapp";
    protected final String ITEM_TYPE_SUBS = "subs";
    protected final String IN_APP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    protected final String IN_APP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";
    protected final String RESPONSE_CODE = "RESPONSE_CODE";

    private Context applicationContext;
    private String signatureBase64;
    private ServiceConnection serviceConnection;
    private IInAppBillingService inAppBillingService;

    private AsyncTask requestPurchasedItemTask;
    private AsyncTask requestPurchaseItemListTask;
    private AsyncTask requestConsumingPurchaseTask;

    private Gson gson = new Gson();
    private long lastClickTime = 0;

    private String purchaseItemType;
    protected boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationContext = getApplicationContext();
        signatureBase64 = generateSignatureBase64();
    }

    @Override
    protected void onDestroy() {
        if (requestPurchasedItemTask != null) {
            requestPurchasedItemTask.cancel(true);
        }
        if (requestPurchaseItemListTask != null) {
            requestPurchaseItemListTask.cancel(true);
        }
        if (requestConsumingPurchaseTask != null) {
            requestConsumingPurchaseTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PURCHASE) {
            onPurchaseActivityResult(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onPurchaseActivityResult(Intent data) {
        if (data == null) {
            onIabPurchaseResponseError();
            return;
        }

        int responseCode = data.getIntExtra(RESPONSE_CODE, 0);
        if (debug) {
            Log.e("BasePurchase", "onPurchaseActivityResult responseCode: " + responseCode);
        }
        if (responseCode == 1 || responseCode == 6) {
            //1: User Cancel - Shark.M.Lin
            //6: Version Error, Not Tester - Shark.M.Lin
            return;
        }
        String purchaseData = data.getStringExtra(IN_APP_PURCHASE_DATA);
        String dataSignature = data.getStringExtra(IN_APP_DATA_SIGNATURE);

        if (debug) {
            Log.e("BasePurchase", "onPurchaseActivityResult data: " + data.toString());
            Log.e("BasePurchase", "onPurchaseActivityResult purchaseData: " + purchaseData);
            Log.e("BasePurchase", "onPurchaseActivityResult dataSignature: " + dataSignature);
            Log.e("BasePurchase", "onPurchaseActivityResult response code: " + responseCode);
            Log.e("BasePurchase", "onPurchaseActivityResult extra: " + data.getExtras());
        }
        if (StringUtil.isEmpty(purchaseData) || StringUtil.isEmpty(dataSignature)) {
            onIabPurchaseResponseError();
            return;
        }

        try {
            IabPurchaseEntity purchaseEntity = gson.fromJson(purchaseData, IabPurchaseEntity.class);
            purchaseEntity.setOriginalJson(purchaseData);
            purchaseEntity.setSignature(dataSignature);
            purchaseEntity.setItemType(purchaseItemType);
            onIabPurchaseResponseSuccess(purchaseEntity);
        } catch (Exception e) {
            e.printStackTrace();
            onIabPurchaseResponseError();
        }
    }

    protected void showLoadingView() {
        super.showLoadingView();
        hideInitIabServiceErrorView();
        hideGetIabPurchaseItemErrorView();
    }

    protected void showMessageView() {
        super.showMessageView();
        hideInitIabServiceErrorView();
        hideGetIabPurchaseItemErrorView();
    }

    protected void showEmptyView() {
        super.showEmptyView();
        hideInitIabServiceErrorView();
        hideGetIabPurchaseItemErrorView();
    }

    protected void showNetworkErrorView() {
        super.showNetworkErrorView();
        hideInitIabServiceErrorView();
        hideGetIabPurchaseItemErrorView();
    }

    protected void showInitIabServiceErrorView() {
        if(initIabErrorContainerId == -1) {
            return;
        }
        View initIabServiceErrorView = findViewById(initIabErrorContainerId);
        if(initIabServiceErrorView != null) {
            initIabServiceErrorView.setVisibility(View.VISIBLE);
        }
        hideContentContainer();
        hideLoadingView();
        hideNetworkErrorView();
        hideEmptyView();
        hideGetIabPurchaseItemErrorView();
    }

    protected void hideInitIabServiceErrorView() {
        if(initIabErrorContainerId == -1) {
            return;
        }
        View initIabServiceErrorView = findViewById(initIabErrorContainerId);
        if(initIabServiceErrorView != null) {
            initIabServiceErrorView.setVisibility(View.GONE);
        }
    }

    protected void showGetIabPurchaseItemErrorView() {
        if(getIabPurchaseItemErrorContainerId == -1) {
            return;
        }
        View getIabPurchasedItemErrorView = findViewById(getIabPurchaseItemErrorContainerId);
        if(getIabPurchasedItemErrorView != null) {
            getIabPurchasedItemErrorView.setVisibility(View.VISIBLE);
        }
        hideContentContainer();
        hideLoadingView();
        hideNetworkErrorView();
        hideEmptyView();
        hideInitIabServiceErrorView();
    }

    protected void hideGetIabPurchaseItemErrorView() {
        if(getIabPurchaseItemErrorContainerId == -1) {
            return;
        }
        View getIabPurchasedItemErrorView = findViewById(getIabPurchaseItemErrorContainerId);
        if(getIabPurchasedItemErrorView != null) {
            getIabPurchasedItemErrorView.setVisibility(View.GONE);
        }
    }

    protected void initIabPurchaseService() {
        if (debug) {
            Log.e("BasePurchase", "initIabPurchaseService");
        }
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                String packageName = applicationContext.getPackageName();
                try {
                    inAppBillingService = IInAppBillingService.Stub.asInterface(service);
                    int inAppPurchaseResponse = inAppBillingService.isBillingSupported(3, packageName, ITEM_TYPE_IN_APP);

                    int subPurchaseResponse = inAppBillingService.isBillingSupported(3, packageName, ITEM_TYPE_SUBS);
                    if (subPurchaseResponse == 0 || inAppPurchaseResponse == 0) {
                        onIabInitPurchaseServiceSuccess();
                    } else {
                        onIabInitPurchaseServiceError(PurchaseServiceError.NOT_SUPPORT);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                onIabInitPurchaseServiceError(PurchaseServiceError.DISCONNECT);
            }
        };
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        applicationContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    protected abstract void onIabInitPurchaseServiceError(PurchaseServiceError error);

    protected abstract void onIabInitPurchaseServiceSuccess();

    protected void requestIabPurchasedItemWebService() {
        if (debug) {
            Log.e("BasePurchase", "requestIabPurchasedItemWebService");
        }

        requestPurchasedItemTask = new AsyncTask<String, Void, List<Bundle>>() {
            @Override
            protected List<Bundle> doInBackground(String... params) {
                Bundle inAppOwnedItems = null;
                Bundle subOwnedItems = null;
                List<Bundle> ownedItems = new ArrayList<>();
                try {
                    inAppOwnedItems = inAppBillingService.getPurchases(3, getPackageName(), ITEM_TYPE_IN_APP, null);
                    subOwnedItems = inAppBillingService.getPurchases(3, getPackageName(), ITEM_TYPE_SUBS, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                ownedItems.add(inAppOwnedItems);
                ownedItems.add(subOwnedItems);
                return ownedItems;
            }

            @Override
            protected void onPostExecute(List<Bundle> ownedItems) {
                Bundle inAppOwnedItems = ownedItems.get(0);
                Bundle subOwnedItems = ownedItems.get(1);

                if (inAppOwnedItems == null || subOwnedItems == null) {
                    onRequestIabPurchasedItemResponseError();
                    return;
                }

                int inAppResponse = inAppOwnedItems.getInt("RESPONSE_CODE");
                int subResponse = subOwnedItems.getInt("RESPONSE_CODE");
                if (debug) {
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService inApp response: " + inAppResponse);
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService sub response: " + inAppResponse);
                }
                if (inAppResponse != 0 || subResponse != 0) {
                    onRequestIabPurchasedItemResponseError();
                    return;
                }

                ArrayList<String> inAppPurchaseDataList = inAppOwnedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> inAppSignatureList = inAppOwnedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                if(inAppPurchaseDataList == null || inAppSignatureList == null || (inAppPurchaseDataList.size() != inAppSignatureList.size())) {
                    if (debug) {
                        Log.e("BasePurchase", "requestIabPurchasedItemWebService generate in app purchase item error");
                    }
                    onRequestIabPurchasedItemResponseError();
                    return;
                }
                if (debug) {
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService inAppPurchaseDataList size: " + inAppPurchaseDataList.size());
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService inAppSignatureList size: " + inAppSignatureList.size());
                }
                List<IabPurchaseEntity> purchasedItemList = new ArrayList();
                for (int i = 0; i < inAppPurchaseDataList.size(); ++i) {
                    String purchaseJson = inAppPurchaseDataList.get(i);
                    if (debug) {
                        Log.e("BasePurchase: ", "json: " + purchaseJson);
                    }
                    String signature = inAppSignatureList.get(i);
                    if (IabSecurity.verifyPurchase(signatureBase64, purchaseJson, signature)) {
                        IabPurchaseEntity purchaseEntity = gson.fromJson(purchaseJson, IabPurchaseEntity.class);
                        purchaseEntity.setItemType(ITEM_TYPE_IN_APP);
                        purchaseEntity.setOriginalJson(purchaseJson);
                        purchaseEntity.setSignature(signature);
                        purchasedItemList.add(purchaseEntity);
                    }
                }

                ArrayList<String> subPurchaseDataList = subOwnedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                ArrayList<String> subSignatureList = subOwnedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
                if(subPurchaseDataList == null || subSignatureList == null || (subPurchaseDataList.size() != subSignatureList.size())) {
                    if (debug) {
                        Log.e("BasePurchase", "requestIabPurchasedItemWebService generate sub purchase item error");
                    }
                    onRequestIabPurchasedItemResponseError();
                    return;
                }
                if (debug) {
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService subPurchaseDataList size: " + subPurchaseDataList.size());
                    Log.e("BasePurchase", "requestIabPurchasedItemWebService subSignatureList size: " + subSignatureList.size());
                }
                for (int i = 0; i < subPurchaseDataList.size(); ++i) {
                    String purchaseJson = subPurchaseDataList.get(i);
                    if (debug) {
                        Log.e("BasePurchase: ", "json: " + purchaseJson);
                    }
                    String signature = subSignatureList.get(i);
                    if (IabSecurity.verifyPurchase(signatureBase64, purchaseJson, signature)) {
                        IabPurchaseEntity purchaseEntity = gson.fromJson(purchaseJson, IabPurchaseEntity.class);
                        purchaseEntity.setItemType(ITEM_TYPE_SUBS);
                        purchaseEntity.setOriginalJson(purchaseJson);
                        purchaseEntity.setSignature(signature);
                        purchasedItemList.add(purchaseEntity);
                    }
                }

                onRequestIabPurchasedItemResponseSuccess(purchasedItemList);
            }
        }.execute();
    }

    protected abstract void onRequestIabPurchasedItemResponseSuccess(List<IabPurchaseEntity> purchasedItemList);

    protected abstract void onRequestIabPurchasedItemResponseError();

    protected void requestIabItemListWebService(final ArrayList<String> skuList) {
        if (debug) {
            Log.e("BasePurchase", "requestIabItemListWebService");
        }
        requestPurchaseItemListTask = new AsyncTask<String, Void, IabPurchaseItemListResultEntity>() {
            @Override
            protected IabPurchaseItemListResultEntity doInBackground(String[] params) {
                IabPurchaseItemListResultEntity resultEntity = new IabPurchaseItemListResultEntity();
                Bundle skuItemListBundle = new Bundle();
                skuItemListBundle.putStringArrayList("ITEM_ID_LIST", skuList);
                try {
                    Bundle inAppSkuDetails = inAppBillingService.getSkuDetails(3, getPackageName(), ITEM_TYPE_IN_APP, skuItemListBundle);
                    int inAppResponse = inAppSkuDetails.getInt("RESPONSE_CODE");
                    if (debug) {
                        Log.e("BasePurchase", "requestIabItemListWebService inAppResponse: " + inAppResponse);
                    }
                    if (inAppResponse == 0) {
                        List<IabSkuItemEntity> inAppSkuItemList = new ArrayList<>();
                        ArrayList<String> inAppSkuJsonResponseList = inAppSkuDetails.getStringArrayList("DETAILS_LIST");
                        for (String skuJson : inAppSkuJsonResponseList) {
                            IabSkuItemEntity iabSkuItemEntity = gson.fromJson(skuJson, IabSkuItemEntity.class);
                            iabSkuItemEntity.setRealPrice(iabSkuItemEntity.getMicrosPrice() / 1000000);
                            String title = iabSkuItemEntity.getTitle();
                            int appNameStartIndex = title.indexOf("(");
                            if (appNameStartIndex != -1) {
                                String appName = title.substring(appNameStartIndex);
                                title = title.replace(appName, "");
                                iabSkuItemEntity.setTitle(title);
                            }
                            inAppSkuItemList.add(iabSkuItemEntity);
                        }
                        resultEntity.setInAppSkuItemList(inAppSkuItemList);
                    }
                    Bundle subSkuDetails = inAppBillingService.getSkuDetails(3, getPackageName(), ITEM_TYPE_SUBS, skuItemListBundle);
                    int subResponse = subSkuDetails.getInt("RESPONSE_CODE");
                    if (debug) {
                        Log.e("BasePurchase", "requestIabItemListWebService subResponse: " + subResponse);
                    }
                    if (subResponse == 0) {
                        List<IabSkuItemEntity> subSkuItemList = new ArrayList<>();
                        ArrayList<String> subSkuJsonResponseList = subSkuDetails.getStringArrayList("DETAILS_LIST");
                        for (String skuJson : subSkuJsonResponseList) {
                            IabSkuItemEntity iabSkuItemEntity = gson.fromJson(skuJson, IabSkuItemEntity.class);
                            iabSkuItemEntity.setRealPrice(iabSkuItemEntity.getMicrosPrice() / 1000000);
                            String title = iabSkuItemEntity.getTitle();
                            int appNameStartIndex = title.indexOf("(");
                            if (appNameStartIndex != -1) {
                                String appName = title.substring(appNameStartIndex);
                                title = title.replace(appName, "");
                                iabSkuItemEntity.setTitle(title);
                            }
                            subSkuItemList.add(iabSkuItemEntity);
                        }
                        resultEntity.setSubSkuItemList(subSkuItemList);
                    }
                    int response = (inAppResponse == 0 || subResponse == 0) ? 0 : -1;
                    resultEntity.setResponse(response);
                    return resultEntity;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(IabPurchaseItemListResultEntity resultEntity) {
                if (resultEntity == null) {
                    onIabItemListResponseError();
                    return;
                }
                onIabItemListResponseSuccess(resultEntity.getInAppSkuItemList(), resultEntity.getSubSkuItemList());
            }
        }.execute();
    }

    protected abstract void onIabItemListResponseSuccess(List<IabSkuItemEntity> inAppSkuDetailsList, List<IabSkuItemEntity> subSkuDetailsList);

    protected abstract void onIabItemListResponseError();

    protected void requestIabPurchaseWebService(String itemType, String sku, String payload) {
        String packageName = applicationContext.getPackageName();
        if (debug) {
            Log.e("BasePurchase", "requestIabPurchaseWebService");
            Log.e("BasePurchase", "itemType: " + itemType);
            Log.e("BasePurchase", "sku: " + sku);
            Log.e("BasePurchase", "payload: " + payload);
            Log.e("BasePurchase", "packageName: " + packageName);
        }

        try {
            this.purchaseItemType = itemType;
            Bundle bundle = inAppBillingService.getBuyIntent(3, packageName, sku, itemType, payload);
            if (debug) {
                Log.e("BasePurchase", "requestIabPurchaseWebService bundle: " + bundle);
            }
            PendingIntent pendingIntent = bundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(),
                        REQUEST_CODE_PURCHASE, new Intent(), 0, 0, 0);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            onIabPurchaseResponseError();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            onIabPurchaseResponseError();
        }
    }

    protected abstract void onIabPurchaseResponseSuccess(IabPurchaseEntity purchaseEntity);

    protected abstract void onIabPurchaseResponseError();

    protected void requestConsumingPurchaseWebService(final String token) {
        if (debug) {
            Log.e("BasePurchase", "requestConsumingPurchaseWebService");
        }
        requestConsumingPurchaseTask = new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    int response = inAppBillingService.consumePurchase(3, getPackageName(), token);
                    if (debug) {
                        Log.e("BasePurchase", "requestConsumingPurchaseWebService response: " + response);
                    }
                    return response;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer response) {
                if (response == 0) {
                    onConsumingPurchaseResponseSuccess(token);
                } else {
                    onConsumingPurchaseResponseError(token);
                }
            }
        }.execute();
    }

    protected abstract void onConsumingPurchaseResponseSuccess(String token);

    protected abstract void onConsumingPurchaseResponseError(String token);

    protected abstract String generateSignatureBase64();

    protected boolean isClickBlock() {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - lastClickTime > 1000) {
            lastClickTime = currentClickTime;
            return false;
        }
        return true;
    }

    public int getInitIabErrorContainerId() {
        return initIabErrorContainerId;
    }

    public void setInitIabErrorContainerId(int initIabErrorContainerId) {
        this.initIabErrorContainerId = initIabErrorContainerId;
    }

    public int getGetIabPurchaseItemErrorContainerId() {
        return getIabPurchaseItemErrorContainerId;
    }

    public void setGetIabPurchaseItemErrorContainerId(int getIabPurchaseItemErrorContainerId) {
        this.getIabPurchaseItemErrorContainerId = getIabPurchaseItemErrorContainerId;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}