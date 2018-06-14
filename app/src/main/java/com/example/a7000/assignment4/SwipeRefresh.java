package com.example.a7000.assignment4;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import java.util.List;

/**
 * Created by 7000 on 3/18/2017.
 */

public class SwipeRefresh implements SwipeRefreshLayout.OnRefreshListener {
    MainActivity mainActivity;
    List<Stock> stockList;
    final static String LOG_TAG="StockListRefresher";
    public SwipeRefresh(MainActivity mainActivity,List<Stock> stockList) {
        this.mainActivity = mainActivity;
        this.stockList = stockList;
    }


    @Override
    public void onRefresh() {
        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
        if(mainActivity.isOnline())
            mainActivity.updateStockData(mainActivity.stockMap);
        else
            mainActivity.showAlertDialog("No Network Connection","Stockes Can not be added without \n network connection");
        mainActivity.srLayout.setRefreshing(false);
    }


}
