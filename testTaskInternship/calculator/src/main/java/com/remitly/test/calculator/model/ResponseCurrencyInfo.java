package com.remitly.test.calculator.model;

import java.util.List;

public class ResponseCurrencyInfo {
    private String table;
    private String currency;
    private String code;
    private List<Rate> rates;

    public ResponseCurrencyInfo() {
    }

    public ResponseCurrencyInfo(String table, String currency, String code, List<Rate> rates) {
        this.table = table;
        this.currency = currency;
        this.code = code;
        this.rates = rates;
    }

    public String getTable() {
        return table;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCode() {
        return code;
    }

    public List<Rate> getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "table='" + table + '\'' +
                ", currencyName='" + currency + '\'' +
                ", currencyCode='" + code + '\'' +
                ", rates=" + rates +
                '}';
    }
}
