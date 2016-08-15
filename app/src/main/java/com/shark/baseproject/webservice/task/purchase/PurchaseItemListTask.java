package com.shark.baseproject.webservice.task.purchase;

import com.google.gson.reflect.TypeToken;
import com.shark.base.webservice.HttpMethod;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.webservice.ResponseDataEntity;
import com.shark.baseproject.webservice.WebServiceHost;
import com.shark.baseproject.webservice.factory.HeaderFactory;
import com.shark.baseproject.webservice.task.product.entitiy.ProductItemEntity;
import com.shark.baseproject.webservice.task.purchase.entity.PurchaseItemEntity;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shark0 on 2016/8/15.
 */
public class PurchaseItemListTask extends WebServiceTask<ResponseDataEntity<List<PurchaseItemEntity>>> {

    PurchaseItemTaskListener listener;
    private String memberId;

    public PurchaseItemListTask(PurchaseItemTaskListener listener, String memberId) {
        this.listener = listener;
        this.memberId = memberId;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String generateServiceUrl() {
        //FIXME change your api host and path - Shark.M.Lin
        return WebServiceHost.getServiceHost() + "your_purchase_list_api_path";
    }

    @Override
    public Map<String, String> generateHttpHeaders() {
        HashMap<String, String> headers =  HeaderFactory.generateHeaders(HeaderFactory.ContentType.Json);
        headers.put("memberId", memberId);
        return headers;
    }

    @Override
    public Type generateResultType() {
        return new TypeToken<List<ProductItemEntity>>(){}.getType();
    }

    @Override
    public void onTaskSucceed(ResponseDataEntity<List<PurchaseItemEntity>> entity) {
        if(entity.getResultCode() == 0) {
            boolean isEmpty = (entity.getData() == null) || (entity.getData().isEmpty());
            if(isEmpty) {
                listener.onPurchaseItemListTaskEmpty();
            } else {
                listener.onPurchaseItemListTaskSuccess(entity.getData());
            }
        } else {
            listener.onPurchaseItemListTaskError(entity.getResultCode(), entity.getDescription());
        }
    }

    @Override
    public void onTaskFailed(WebServiceErrorType errorType) {
        listener.onPurchaseItemListTaskNetworkError(errorType);
    }


    public interface PurchaseItemTaskListener {
        void onPurchaseItemListTaskSuccess(List<PurchaseItemEntity> purchaseItemEntityList);

        void onPurchaseItemListTaskEmpty();

        void onPurchaseItemListTaskError(int returnCode, String systemDescription);

        void onPurchaseItemListTaskNetworkError(WebServiceErrorType errorType);
    }
}
