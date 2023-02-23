package com.remitly.test.calculator.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

import static com.remitly.test.calculator.util.Constants.*;

public class RequestCurrencyInfo {
    private final CurrencyType currencyName;
    @NotBlank(message = BLANK_COUNT_EXCEPTION)
    @Min(value = 0, message = MIN_COUNT_EXCEPTION)
    private final String count;

    public RequestCurrencyInfo(CurrencyType currencyName, String count) {
        this.currencyName = currencyName;
        this.count = count;
    }

    public CurrencyType getCurrencyName() {
        return currencyName;
    }

    public BigDecimal getCount() {
        return new BigDecimal(count);
    }

    @Override
    public String toString() {
        return "RequestCurrencyInfo{" +
                "currency='" + currencyName + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
