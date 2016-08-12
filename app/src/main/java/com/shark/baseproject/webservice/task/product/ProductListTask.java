package com.shark.baseproject.webservice.task.product;

import com.google.gson.reflect.TypeToken;
import com.shark.base.util.StringUtil;
import com.shark.base.webservice.HttpMethod;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.base.webservice.WebServiceTask;
import com.shark.baseproject.webservice.ResponseDataEntity;
import com.shark.baseproject.webservice.WebServiceHost;
import com.shark.baseproject.webservice.factory.HeaderFactory;
import com.shark.baseproject.webservice.task.product.entitiy.ProductItemEntity;
import com.shark.baseproject.webservice.task.product.entitiy.ProductListResultEntity;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Shark0 on 2016/8/10.
 */
public class ProductListTask extends WebServiceTask<ResponseDataEntity<ProductListResultEntity>> {

    private ProductListTaskListener listener;
    private String nextPage;

    public ProductListTask(ProductListTaskListener listener, String nextPage) {
        this.listener = listener;
        this.nextPage = nextPage;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

    @Override
    public String generateServiceUrl() {
        //FIXME change your api host and path - Shark.M.Lin
        String url = WebServiceHost.getServiceHost() + "your_product_list_api_path";
        if(!StringUtil.isEmpty(nextPage)) {
            url = url + "?nexPage=" + nextPage;
        }
        return url;
    }

    @Override
    public Map<String, String> generateHttpHeaders() {
        return HeaderFactory.generateHeaders(HeaderFactory.ContentType.UrlEncoded);
    }

    @Override
    public Type generateResultType() {
        return new TypeToken<ResponseDataEntity<ProductListResultEntity>>(){}.getType();
    }

    @Override
    public void onTaskSucceed(ResponseDataEntity<ProductListResultEntity> entity) {
        if(entity.getResultCode() != 0) {
            listener.onProductListTaskError(entity.getResultCode(), entity.getDescription());
            return;
        }

        boolean isFirstPage = StringUtil.isEmpty(nextPage);
        boolean isEmpty = (entity.getData() != null) && (entity.getData().getProductItemEntityList() != null)
                && (!entity.getData().getProductItemEntityList().isEmpty());
        boolean hasNextPage = isEmpty && (!StringUtil.isEmpty(entity.getData().getNextPage()));
        if(isFirstPage) {
            if(isEmpty) {
                listener.onProductListTaskFirstPageEmpty();
            } else {
                listener.onProductListTaskFirstPageSuccess(entity.getData().getProductItemEntityList(), hasNextPage, entity.getData().getNextPage());
            }
        } else {
            if(isEmpty) {
                listener.onProductListTaskEmpty();
            } else {
                listener.onProductListTaskSuccess(entity.getData().getProductItemEntityList(), hasNextPage, entity.getData().getNextPage());
            }
        }
    }

    @Override
    public void onTaskFailed(WebServiceErrorType errorType) {
        listener.onProductListTaskNetworkError(errorType, StringUtil.isEmpty(nextPage));
    }

    public static interface ProductListTaskListener {
        public void onProductListTaskFirstPageSuccess(List<ProductItemEntity> productItemEntities, boolean hasNextPage, String nextPage);

        public void onProductListTaskFirstPageEmpty();

        public void onProductListTaskSuccess(List<ProductItemEntity> productItemEntities, boolean hasNextPage, String nextPage);

        public void onProductListTaskEmpty();

        public void onProductListTaskError(int resultCode, String description);

        public void onProductListTaskNetworkError(WebServiceErrorType errorType, boolean isFirstPage);
    }
}
