package com.example.a7000.assignment4;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 7000 on 3/10/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "MyAdapter";
    List<Stock> stockList;
    private MainActivity mainAct;
    Context context;

    public MyAdapter(MainActivity mainAct, List<Stock> stockList) {
        this.mainAct = mainAct;
        this.stockList = stockList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_stock, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());
        holder.price.setText(String.format("%.2f", stock.getPrice()));
        holder.priceChange.setText("\u25B2 ("+ String.format("%.2f", stock.getChangePercentage())+"%)");

        if(stock.getChangePercentage() > 0){
            holder.symbol.setTextColor(Color.GREEN);
            holder.company.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.priceChange.setTextColor(Color.GREEN);
        }else{
            holder.symbol.setTextColor(Color.RED);
            holder.company.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.priceChange.setTextColor(Color.RED);
        }

    }
    
    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
