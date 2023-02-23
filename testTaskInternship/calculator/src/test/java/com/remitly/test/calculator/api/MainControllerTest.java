package com.remitly.test.calculator.api;

import com.remitly.test.calculator.Exceptions.EmptyCurrencyInfoException;
import com.remitly.test.calculator.model.CurrencyType;
import com.remitly.test.calculator.model.Rate;
import com.remitly.test.calculator.model.RequestCurrencyInfo;
import com.remitly.test.calculator.model.ResponseCurrencyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.remitly.test.calculator.util.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest {
    @Mock
    private RestTemplate restTemplate;
    private MainController controller;

    @BeforeEach
    void setUp() {
        controller = new MainController(restTemplate);
    }

    @Test
    public void showExchangeInfoNullResponseFromAPISThrowException() {
        when(restTemplate.getForObject(API_NBP_URL, ResponseCurrencyInfo.class)).thenReturn(null);

        Exception exception = assertThrows(
                EmptyCurrencyInfoException.class,
                () -> controller.showExchangeInfo(new RequestCurrencyInfo(CurrencyType.PLN, "44"))
        );
        String expectedMessage = "Some information field is empty!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void showExchangeInfoSuccess() throws EmptyCurrencyInfoException {
        List<Rate> rates = new ArrayList<>();
        rates.add(new Rate("033/A/NBP/2023","2023-02-16",5.3606));
        ResponseCurrencyInfo mockResponse = new ResponseCurrencyInfo("A", "funt szterling", "GBP", rates);
        when(restTemplate.getForObject(API_NBP_URL, ResponseCurrencyInfo.class)).thenReturn(mockResponse);

        assertEquals(
                BigDecimal.valueOf(8.21),
                controller.showExchangeInfo(new RequestCurrencyInfo(CurrencyType.PLN, "44"))
        );
    }

}
