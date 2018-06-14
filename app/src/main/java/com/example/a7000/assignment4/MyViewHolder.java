package com.example.a7000.assignment4;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by 7000 on 3/10/2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView symbol;
    TextView company;
    TextView price;
    TextView priceChange;

    public MyViewHolder(View view) {

        super(view);
        symbol = (TextView) itemView.findViewById(R.id.Symbol);
        company = (TextView) itemView.findViewById(R.id.Company);
        price = (TextView) itemView.findViewById(R.id.stockPrice);
        priceChange = (TextView) itemView.findViewById(R.id.priceChange);
    }
}
