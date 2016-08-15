package com.shark.baseproject.webservice.task.purchase;

import com.google.gson.Gson;
import com.shark.base.activity.iab.entity.IabPurchaseEntity;
import com.shark.base.webservice.HttpMethod;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.webservice.ResponseEntity;
import com.shark.baseproject.webservice.WebServiceHost;
import com.shark.baseproject.webservice.factory.HeaderFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shark0 on 2016/8/15.
 */
public class PurchaseFinishTask extends WebServiceTask<ResponseEntity> {

    private PurchaseFinishTaskListener listener;
    private String memberId;
    private IabPurchaseEntity purchaseEntity;
    private Gson gson = new Gson();

    public PurchaseFinishTask(PurchaseFinishTaskListener listener, String memberId, IabPurchaseEntity purchaseEntity) {
        this.listener = listener;
        this.memberId = memberId;
        this.purchaseEntity = purchaseEntity;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.POST;
    }

    @Override
    public String generateServiceUrl() {
        //FIXME change your api host and path - Shark.M.Lin
        return WebServiceHost.getServiceHost() + "your_purchase_finish_api_path";
    }

    @Override
    public byte[] generateBody() {
        String json = gson.toJson(purchaseEntity);
        byte[] bytes = null;
        try {
            bytes = json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }


    @Override
    public Map<String, String> generateHttpHeaders() {
        HashMap<String, String> headers =  HeaderFactory.generateHeaders(HeaderFactory.ContentType.Json);
        headers.put("memberId", memberId);
        return headers;
    }

    @Override
    public Type generateResultType() {
        return ResponseEntity.class;
    }

    @Override
    public void onTaskSucceed(ResponseEntity entity) {
        switch (entity.getResultCode()) {
            case 0:
                listener.onPurchaseFinishTaskSuccess(purchaseEntity);
                break;
            case 1:
                listener.onPurchaseFinishTaskConsumeAgain(purchaseEntity);
                break;
            default:
                listener.onPurchaseFinishTaskError(entity.getResultCode(), entity.getDescription(), purchaseEntity);
                break;
        }
    }

    @Override
    public void onTaskFailed(WebServiceErrorType errorType) {
        listener.onPurchaseFinishTaskNetworkError(errorType, purchaseEntity);
    }

    public interface PurchaseFinishTaskListener {
        void onPurchaseFinishTaskSuccess(IabPurchaseEntity purchase);

        void onPurchaseFinishTaskConsumeAgain(IabPurchaseEntity purchase);

        void onPurchaseFinishTaskError(int resultCode, String message, IabPurchaseEntity purchase);

        void onPurchaseFinishTaskNetworkError(WebServiceErrorType errorType, IabPurchaseEntity purchase);
    }
}
