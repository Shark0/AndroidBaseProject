# AndroidBaseProject

AndroidBaseProject主要是協助Android開發者方便處理網路連線跟Activity的溝通。

## 網路連線
在Base Module com.shark.base.webservice Package中，主要由三個角色來處理，三個角色分別是WebServiceTask、WebServiceWorker、WebServiceManager。

### WebServiceTask
負責定義連線工作的內容，以及定義工作完成的結果。

#### 定義API連線需要用到的參數內容
* Http Method (Get、Post、Put、Delete)
* Headers (Worker額外使用非UI Thread做相關處裡)
* Service API Url
* Body (Worker額外使用非UI Thread做相關處裡)
* Result Entity Type

##### 定義工作結果
根據Reult Entity以及開發者自訂的Interface Listener，回傳網路是否連線成功、工作是否成功給Activity做進一步的處理。

### WebServiceWorker
根據Task內容，執行連線工作，並回報連線結果。開發者可以根據自己的要求，使用Volley、OKHttp、或自己用UrlConnection來實作。
App Module中的範例是使用Volley來實作，可參考come.shark.baseproject.webservice.worker.VolleyWebServiceWorker。

### WebServiceManager
透過Tag管理Worker何時開始工作，結束工作。

## 頁面處理
在Base Module中，我們透過BaseActivity、BaseFragment來協助開發者跟Service構通，並處理溝通期間的UI變化。開發者要自行定義4個View，Content View、Loding View、Message View、Empty View、Network Error View，將4個View的Id設定進BaseActivity或BaseFragment之後，可以透過Show/Hide方法控制頁面在連線時UI的變化。

### 連線
開發者只要定義好Task，以及回報Task結果的Interface，就可以指定Worker並透過startWebService來執行連線工作。

### 連線的UI處裡
* Loading View:在一進入到Activity/Fragment時，需要透過Service API取得顯示頁面的資料，在這段期間可以showLoadingView()。

* Content View:在Activity/Fragment取得排版需要的資料時，f可以showContentContainer()。

* Message View:在Activity/Fragment跟Service API溝通成功時，但使用者有身分問題時，需要跟使用者講解理由，可以透過showMessageView()來顯示內容。

* Empty View:在Activity/Fragment跟Service API溝通成功時，但是沒有資料時，可以透過showEmptyView()來顯示內容，將使用者引導到可以增加資料的頁面。

* Network Error View:在Activity/Fragment跟ServiceAPI溝通失敗時，需要顯示失敗原因以及重新連線按鈕，可以透過showNetworkErrorView()來顯示內容，將使用者引導到可以增加資料的頁面。

### 範例

#### LoginActivity
* 位置: App Module中的come.shark.baseproject.activity.login.LoginActivity

* 範例內容: Demo使用者在頁面操作時的連線處理

#### ProductActivity
* 位置: App Module中的come.shark.baseproject.activity.product.ProductActivity

* 範例內容: Demo使用者在頁面開始時抓取顯示資料的連線處理，以及資料有分頁時的連線處理

#### PurchaseActivity
* 位置: App Module中的come.shark.baseproject.activity.purchase.PurchaseActivity

* 範例內容: Demo使用者使用IAB購買產品時，在頁面開始時抓取顯示資料，以及頁面操作時的連線處理。
