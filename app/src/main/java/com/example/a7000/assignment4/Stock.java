package com.example.a7000.assignment4;

import java.io.Serializable;

/**
 * Created by 7000 on 3/10/2017.
 */

public class Stock implements Serializable {
    private String Symbol ;
    private String Company ;
    private double stockPrice ;
    private double priceChange ;
    private double changePercentage;

    public Stock(String Symbol, String Company, double stockPrice, double priceChange, double changePercentage) {
        this.Symbol = Symbol;
        this.Company = Company;
        this.stockPrice = stockPrice;
        this.priceChange = priceChange;
        this.changePercentage = changePercentage;
    }

    public Stock() {
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String Symbol) {
        this.Symbol = Symbol;
    }

    public String getCompany() {
        return Company;
    }

    public void setCompany(String Company) {
        this.Company = Company;
    }

    public double getPrice() {
        return stockPrice;
    }

    public void setPrice(double price) {
        this.stockPrice = stockPrice;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }
}
