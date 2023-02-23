package com.remitly.test.calculator.api;

import com.remitly.test.calculator.Exceptions.EmptyCurrencyInfoException;
import com.remitly.test.calculator.model.CurrencyType;
import com.remitly.test.calculator.model.RequestCurrencyInfo;
import com.remitly.test.calculator.model.ResponseCurrencyInfo;

import static com.remitly.test.calculator.util.Constants.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MainController {

    private final RestTemplate restTemplate;

    @Autowired
    public MainController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/exchange")
    public BigDecimal showExchangeInfo(@Valid @RequestBody RequestCurrencyInfo currencyInfo) throws EmptyCurrencyInfoException {
        Optional<ResponseCurrencyInfo> jsonCurrencyInfoOptional = Optional.ofNullable(restTemplate
                .getForObject(API_NBP_URL, ResponseCurrencyInfo.class));
        BigDecimal multiplier = BigDecimal.valueOf(jsonCurrencyInfoOptional
                .orElseThrow(() -> new EmptyCurrencyInfoException(EMPTY_CURRENCY_INFO_EXC))
                .getRates()
                .get(0)
                .getMid());
        BigDecimal returnValue;
        if (currencyInfo.getCurrencyName() == CurrencyType.PLN) {
            returnValue = currencyInfo.getCount()
                    .divide(multiplier, ROUNDING_SCALE, RoundingMode.HALF_UP); // return gbp
        } else {
            returnValue = currencyInfo.getCount().multiply(multiplier)
                    .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP); // return pln
        }
            return returnValue;
    }
}
