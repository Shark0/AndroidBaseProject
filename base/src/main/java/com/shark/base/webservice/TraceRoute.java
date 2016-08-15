package com.shark.base.webservice;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.shark.base.util.StringUtil;
import com.shark.base.webservice.entity.TraceRouteInfoEntity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TraceRoute {

    private static final String FROM_PING = "From";
    private static final String OPEN_PING = "(";
    private static final String CLOSE_PING = ")";

    private Context context;
    private TraceRouteInfoEntity firstTraceInfo;
    private List<TraceRouteInfoEntity> traceInfoList = new ArrayList<>();

    private String urlToPing;

    public TraceRoute(Context context) {
        this.context = context;
    }

    public List<TraceRouteInfoEntity> execute(String url, int maxTtl) {
        this.urlToPing = url;
        if (!hasConnectivity()) {
            return traceInfoList;
        }
        for (int i = 1; i <= maxTtl; i ++) {
            try {
                TraceRouteInfoEntity info = ping(urlToPing, i);
                if(i == 1) {
                    firstTraceInfo = info;
                    traceInfoList.add(info);
                } else {
                    if(traceInfoList.size() > 0 && traceInfoList.get(traceInfoList.size() -1).getIp().equalsIgnoreCase(info.getIp())) {
                        return traceInfoList;
                    }
                    traceInfoList.add(info);
                    if(!StringUtil.isEmpty(firstTraceInfo.getIp())
                            && firstTraceInfo.getIp().equalsIgnoreCase(info.getIp())
                            ) {
//                        Log.e("TraceRote", "firstTraceInfo: " + firstTraceInfo.getIp() + " , info ip: " + info.getIp());
                        return traceInfoList;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return traceInfoList;
            }
        }
        return traceInfoList;
    }

    private TraceRouteInfoEntity ping(String url, int ttl) throws Exception {
        TraceRouteInfoEntity traceRouteInfoEntity = new TraceRouteInfoEntity();
        Process process;
        String format = "ping -c 1 -t %d ";
        String command = String.format(format, ttl);
        long startTime = System.currentTimeMillis();
        process = Runtime.getRuntime().exec(command + url);
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

        long pingTime = 0;
        String tempResult;
        String result = "";
        while ((tempResult = stdInput.readLine()) != null) {
            result = result + tempResult + "\n";
            pingTime = (System.currentTimeMillis() - startTime);
        }
        process.destroy();
        if(StringUtil.isEmpty(result)) {
            throw new Exception();
        }
        String pingIp = parseIp(result);
        traceRouteInfoEntity.setIp(pingIp);
        traceRouteInfoEntity.setResponseTime(pingTime);
        return traceRouteInfoEntity;
    }

    private String parseIp(String ping) {
        String ip = "";
        if (ping.contains(FROM_PING)) {
            int index = ping.indexOf(FROM_PING);
            ip = ping.substring(index + 5);
            if (ip.contains(OPEN_PING)) {
                int indexOpen = ip.indexOf(OPEN_PING);
                int indexClose = ip.indexOf(CLOSE_PING);
                ip = ip.substring(indexOpen + 1, indexClose);
            } else {
                ip = ip.substring(0, ip.indexOf("\n"));
                if (ip.contains(":")) {
                    index = ip.indexOf(":");
                } else {
                    index = ip.indexOf(" ");
                }
                ip = ip.substring(0, index);
            }
        } else {
            int indexOpen = ping.indexOf(OPEN_PING);
            int indexClose = ping.indexOf(CLOSE_PING);
            ip = ping.substring(indexOpen + 1, indexClose);
        }

        return ip;
    }

    public boolean hasConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
