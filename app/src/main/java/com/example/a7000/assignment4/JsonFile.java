package com.example.a7000.assignment4;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 7000 on 3/18/2017.
 */

public class JsonFile {
    private final static String TAG="JsonFileR/W";
    private Context context;

    public JsonFile(Context context) {

        this.context = context;
    }

    public List<Stock> readData(InputStream in) throws IOException {
        JsonReader jr = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            List<Stock> stock = new ArrayList<Stock>();
            jr.beginArray();
            while (jr.hasNext()) {
                long id = -1;
                String symbol = null;
                double price=0.0;
                double changePrice=0.0;
                double percentChange=0.0;
                Stock newStock;
                jr.beginObject();
                while (jr.hasNext()) {
                    String name = jr.nextName();

                    if (name.equals("id")) {
                        id = jr.nextLong();
                    }else if(name.equals("t")){
                        symbol = jr.nextString();
                    }else if (name.equals("l")) {
                        price = jr.nextDouble();
                    } else if (name.equals("c")) {
                        changePrice = jr.nextDouble();
                    } else if(name.equals("cp")){
                        percentChange = jr.nextDouble();
                    }else{
                        jr.skipValue();
                    }
                }
                newStock = new Stock();
                newStock.setSymbol(symbol);
                newStock.setPrice(price);
                newStock.setPriceChange(changePrice);
                newStock.setChangePercentage(percentChange);

                Log.d(TAG,"ID "+id+" Symbol "+symbol+" Price "+price+" price change "+changePrice+" price per change "+percentChange);
                jr.endObject();
                stock.add(newStock);
            }
            jr.endArray();
            return stock;
        } finally {
            jr.close();
        }
    }

    public List<String> readSearchData(InputStream in) throws IOException {
        JsonReader jr = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            List<String> stock = new ArrayList<>();

            jr.beginArray();
            while (jr.hasNext()) {
                int id = -1;
                String symbol = null;
                String company = null;
                double changePrice=0.0;
                double percentChange=0.0;
                String dialogDisplayString = null;
                jr.beginObject();
                while (jr.hasNext()) {
                    String name = jr.nextName();

                    if (name.equals("company_name")) {
                        company = jr.nextString();
                    }else if(name.equals("company_symbol")){
                        symbol = jr.nextString();
                    }else{
                        jr.skipValue();
                    }
                }

                Log.d(TAG,"company = "+ company +" symbol= "+ symbol);
                jr.endObject();
                dialogDisplayString = symbol +" - "+company ;
                stock.add(dialogDisplayString);
            }
            jr.endArray();
            return stock;
        } finally {
            jr.close();
        }
    }
}
