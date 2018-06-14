package com.example.a7000.assignment4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 7000 on 3/10/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "StockAppDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "StockWatch";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static final String PRICE = "stockPrice";
    private static final String PRICE_CHANGE = "priceChange";
    private static final String PERCENT_CHANGE = "percentChange";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique," +
                    COMPANY + " TEXT ," +
                    PRICE + "REAL , " +
                    PRICE_CHANGE + "REAL, " +
                    PERCENT_CHANGE + "REAL)";

    private static final String SQL_DELETE_ENTRIES =
            "Table Dropped if it exists ";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_DELETE_ENTRIES);
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding " + stock.getSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());
        values.put(PRICE, stock.getPrice());
        values.put(PRICE_CHANGE, stock.getPriceChange());
        values.put(PERCENT_CHANGE, stock.getChangePercentage());

        database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: Add Complete");
    }


    public void deleteStock(Stock symbol) {
        SQLiteDatabase db = getReadableDatabase();
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(TABLE_NAME, "SYMBOL = ?",
                new String[] {String.valueOf(symbol)});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public List<Stock> loadStocks() {
        SQLiteDatabase db =getWritableDatabase();
        db=getReadableDatabase();
        Log.d(TAG, " loadStocks: Load all symbol-company entries from DB");
        Stock stock;
        List<Stock> stockList = new ArrayList<>();
        
       /* Cursor cursor = database.query(TABLE_NAME,  // The table to query
                new String[]{ SYMBOL, COMPANY, PRICE, PRICE_CHANGE, PERCENT_CHANGE },
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null);*/ // The sort order; // The columns to return);
        Log.d("Databse"," here ");
        Cursor cursor =db.rawQuery("select * from "+TABLE_NAME, null);
        if (cursor != null) { // Only proceed if cursor is not null
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                stock = new Stock();
                stock.setSymbol(cursor.getString(0));
                stock.setCompany(cursor.getString(1));
                stock.setPrice(Double.parseDouble(cursor.getString(2)));
                stock.setPriceChange(Double.parseDouble(cursor.getString(3)));
                stock.setChangePercentage(Double.parseDouble(cursor.getString(4)));
                stockList.add(stock);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stockList;
    }
}