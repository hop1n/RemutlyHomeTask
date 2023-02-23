package com.remitly.test.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remitly.test.calculator.Exceptions.EmptyCurrencyInfoException;
import com.remitly.test.calculator.model.CurrencyType;
import com.remitly.test.calculator.model.RequestCurrencyInfo;
import com.remitly.test.calculator.model.ResponseCurrencyInfo;
import com.remitly.test.calculator.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.remitly.test.calculator.util.Constants.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CalculatorApplicationTest {

    @Autowired
    private MockMvc mockMvc;
    private BigDecimal actualMid;

    @BeforeEach
    void setUp() throws EmptyCurrencyInfoException {
        RestTemplate restTemplate = new RestTemplate();
        Optional<ResponseCurrencyInfo> jsonCurrencyInfoOptional = Optional.ofNullable(restTemplate
                .getForObject(API_NBP_URL, ResponseCurrencyInfo.class));
        actualMid = BigDecimal.valueOf(jsonCurrencyInfoOptional
                .orElseThrow(() -> new EmptyCurrencyInfoException(EMPTY_CURRENCY_INFO_EXC))
                .getRates()
                .get(0)
                .getMid());
    }

    @Test
    public void testShowExchangeInfoPlnToGbpSuccess() throws Exception {
        RequestCurrencyInfo requestCurrencyInfo = new RequestCurrencyInfo(CurrencyType.PLN, "40");
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestCurrencyInfo);
        MvcResult mvcResult =
                this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(content().json(String.valueOf((requestCurrencyInfo.getCount()).divide(actualMid,
                                Constants.ROUNDING_SCALE, RoundingMode.HALF_UP))))
                        .andReturn();

        assertEquals("application/json", mvcResult.getResponse().getContentType());
    }

    @Test
    public void testShowExchangeInfoGbpToPlnSuccess() throws Exception {
        RequestCurrencyInfo requestCurrencyInfo = new RequestCurrencyInfo(CurrencyType.GBP, "12");
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestCurrencyInfo);
        MvcResult mvcResult =
                this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk())
                        .andExpect(content().json(String.valueOf(requestCurrencyInfo.getCount().multiply(actualMid)
                                .setScale(Constants.ROUNDING_SCALE, RoundingMode.HALF_UP))))
                        .andReturn();

        assertEquals("application/json", mvcResult.getResponse().getContentType());
    }

    @Test
    public void testShowExchangeInfoNegativeArgumentBadRequest() throws Exception {
        RequestCurrencyInfo requestCurrencyInfo = new RequestCurrencyInfo(CurrencyType.GBP, "-1");
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(requestCurrencyInfo);
        MvcResult mvcResult =
                this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertNotNull(result.getResolvedException()))
                        .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(MIN_COUNT_EXCEPTION)))
                        .andReturn();

        assertNull(mvcResult.getResponse().getContentType());
    }

    @Test
    public void testShowExchangeInfoInvalidCurrencyTypeBadRequest() throws Exception {
        String jsonStringBody = "{\"currencyName\": \"invalid\", \"count\": \"1\"}";
        MvcResult mvcResult =
                this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonStringBody))
                        .andExpect(status().isBadRequest())
                        .andReturn();
        assertNull(mvcResult.getResponse().getContentType());
    }

    @Test
    public void testShowExchangeInfoCountIsEmptyBadRequest() throws Exception {
        String jsonStringBody = "{\"currencyName\": \"PLN\", \"count\": \"\"}";
        MvcResult mvcResult =
        this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                        .content(jsonStringBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertNotNull(result.getResolvedException()))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(BLANK_COUNT_EXCEPTION)))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(MIN_COUNT_EXCEPTION)))
                .andReturn();
        assertNull(mvcResult.getResponse().getContentType());
    }

    @Test
    public void testShowExchangeInfoCountIsBlankBadRequest() throws Exception {
        String jsonStringBody = "{\"currencyName\": \"PLN\", \"count\": \"   \"}";
        MvcResult mvcResult =
                this.mockMvc.perform(get("/api/exchange").contentType(MediaType.APPLICATION_JSON)
                                .content(jsonStringBody))
                        .andExpect(status().isBadRequest())
                        .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                        .andExpect(result -> assertNotNull(result.getResolvedException()))
                        .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(BLANK_COUNT_EXCEPTION)))
                        .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(MIN_COUNT_EXCEPTION)))
                        .andReturn();
        assertNull(mvcResult.getResponse().getContentType());
    }
}
