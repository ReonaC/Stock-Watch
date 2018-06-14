package com.example.a7000.assignment4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, DownloadCallback<String> {

    private RecyclerView recyclerView;
    private DatabaseHandler databaseHandler;
    MyViewHolder myViewHolder;
    MyAdapter myAdapter;
    SwipeRefreshLayout srLayout;
    CardView stockCardView;
    final String APT_KEY = "89dc8ac18cd548a3d74647a81885b50aa48c6734";
    final String SEARCH_TEXT = "&search_text=";
    String stockSearch ="http://www.stocksearchapi.com/api/?api_key=";
    String stockData ="http://finance.google.com/finance/info?client=ig&q=";
    String stockDetails ="http://www.marketwatch.com/investing/stock/";
    List<Stock> stockList;
    List<String> findStockList;
    Stock stock;
    private String userAction = null;
    private static final String SEARCH_ACTION="SEARCH_ACTION";
    private static final String DOWNLOAD_STOCK_DATA="DOWNLOAD_STOCK_DATA";
    private static final String TAG="MainActivity";
    DatabaseHandler DBHandler;
    Map<String,Stock> stockMap;


    public Map<String,Stock> stockMap(List<Stock> stockList){
        stockMap = new HashMap<>();
        Iterator<Stock> stockIterator= stockList.iterator();
        Stock stock;
        while (stockIterator.hasNext()){
            stock = stockIterator.next();
            stockMap.put(stock.getSymbol(),stock);
        }
        return stockMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srLayout = (SwipeRefreshLayout) findViewById(R.id.swiper);
        srLayout.setEnabled(true);

        SwipeRefresh swipeRefresh= new SwipeRefresh(this,stockList);
        srLayout.setOnRefreshListener(swipeRefresh);

        stockSearch = stockSearch + APT_KEY+SEARCH_TEXT;
        DBHandler = new DatabaseHandler(getApplicationContext());

        stockList = restoreStockList();
        stockMap = stockMap(stockList);
        updateStockData(stockMap);

        /*recyclerView = (RecyclerView) findViewById(R.id.activity_main);
        myAdapter = new MyAdapter(this,stockList);
        recyclerView.Adapter(myAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.setLayoutManager(layoutManager);
        MyAdapter.notifyDataSetChanged();*/
    }

    public List<Stock> restoreStockList(){
        stockList = new ArrayList<>();
        stockList = DBHandler.loadStocks();
        return stockList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d("MainActivity", "Inside Menu");
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock :
                if(!isOnline())
                    showAlertDialog("No Network Connection","Stockes Can not be added without \n network connection");
                else{
                    Log.d("in switch", "in else");
                    searchStock();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private void StockSelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String StockSymbolText = input.getText().toString();
                Log.d("MainActivity","test := "+ StockSymbolText);
                if(isDuplicate(StockSymbolText)){
                    showAlertDialog("Duplicate Stock","Stock symbol " + StockSymbolText + " is already displayed" );
                }

                else{
                    String new_stock_search_url =  stockSearch + StockSymbolText;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }*/

    public boolean isDuplicate(String StockSymbolText){
        String parseStr[] = StockSymbolText.split(" - ");
        String symbol = parseStr[0];
        return stockMap.containsKey(symbol);
    }


    boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Log.d(TAG, "Stock clicked " + pos);

        Stock stock = stockList.get(pos);
        String clickedStockSymbol = stock.getSymbol();
        String new_stock_info_url = stockDetails +clickedStockSymbol;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(new_stock_info_url));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recyclerView.getChildLayoutPosition(v);
        Log.d(TAG, " Long Clicked stock " + pos);
        final Stock clickedStock = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove(pos);
                stockMap.remove(clickedStock.getSymbol());
                DBHandler.deleteStock(clickedStock);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage("Delete Stock?");
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public void updateFromDownload(String result) {

        if(result==null || result.length()<20){
            showAlertDialog("Symbol Not Found","No mathcing stock found");
            return;
        }else if(getDownloadAction() !=null) {
            if (getDownloadAction().equals(SEARCH_ACTION)) {
                List<String> searchResultList = readSearchData(result);
                stockSearchBox(searchResultList);

            } else if (getDownloadAction().equals(DOWNLOAD_STOCK_DATA)) {
                result = result.substring(3);
                Stock stock = readData(result);
                Stock newStock = stockMap.get(stock.getSymbol());
                newStock.setPrice(stock.getPrice());
                newStock.setPriceChange(stock.getPriceChange());
                newStock.setChangePercentage(stock.getChangePercentage());
                //MyAdapter.notifyDataSetChanged();
                DBHandler.deleteStock(newStock);
                DBHandler.addStock(newStock);
            } else {

            }
        }

    }

    private void stockSearchBox(List<String> searchResultList) {

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("Select One Stock");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_selectable_list_item);
            arrayAdapter.addAll(searchResultList);

            builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String strName = arrayAdapter.getItem(which);
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
                    builderInner.setMessage(strName);
                    builderInner.setTitle("Stock Added");

                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        Stock newStock;
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.dismiss();
                        }
                    });
                    Log.d("MainActivity"," Select Stock Option");
                    if(!isDuplicate(strName)) {
                        downloadFinancialStockData(strName);
                        builderInner.show();
                    }
                    else{
                        showAlertDialog("Duplicate Stock", " Stock Symbol " + strName + " is already displayed");
                    }
                }
            });
            builderSingle.show();
        }


    public String getDownloadAction() {

        return userAction;
    }

    void downloadFinancialStockData(String strName){
        Stock newStock;
        Log.d("MainActivity","Stock Selected");
        //setDownloadAction(DOWNLOAD_STOCK_DATA);
        String parseStr[] =strName.split(" - ");
        String symbol = parseStr[0];
        String comp_name = parseStr[1];
        String StockData = stockData + symbol;
        newStock = new Stock();

        newStock.setSymbol(symbol);
        newStock.setCompany(comp_name);
        newStock.setPrice(0.0);
        newStock.setPriceChange(0.0);
        newStock.setChangePercentage(0.0);
        stockList.add(newStock);
        stockMap.put(symbol,newStock);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        final String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifiConn = networkInfo.isConnected();
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);

        return networkInfo;
    }

    private Stock readData(String jsonData){
        Stock newStock=null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JsonFile jf = new JsonFile(this);
            List<Stock> stockList =  jf.readData(inputStream);
            newStock = stockList.get(0);
        }catch(Exception e){
            e.printStackTrace();
        }

        return newStock;
    }

    private List<String> readSearchData(String jsonData){
        List<String> CompanyList=null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JsonFile jr= new JsonFile(this);
            CompanyList=jr.readSearchData(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
        return CompanyList;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishDownloading() {

    }

    public void showAlertDialog(String alertTitle, String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(alertTitle);

        final TextView input = new TextView(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(alertMessage);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    void updateStockData(Map<String,Stock> stockMap){
        DownloadFunc dwfn;
        Set<String> SymbolList = stockMap.keySet();
        Iterator<String> ii = SymbolList.iterator();
        while (ii.hasNext()){
            dwfn = new DownloadFunc(this);
            String symbol = ii.next();
            String newStockUrl = stockData + symbol;
            if(isOnline())
                dwfn.execute(newStockUrl);
        }
    }

    public void searchStock(){
        final SearchFunc srfn;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(input);
        srfn = new SearchFunc(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String  m_Text = input.getText().toString();
                Log.d("MainActivity","test := "+m_Text);
                String searchUrl =  stockSearch + m_Text;
                srfn.execute(searchUrl);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
