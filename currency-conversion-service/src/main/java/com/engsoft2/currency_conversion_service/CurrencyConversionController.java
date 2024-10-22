package com.engsoft2.currency_conversion_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

  private CurrencyExchangeProxy proxy;

  public CurrencyConversionController(CurrencyExchangeProxy proxy) {
    this.proxy = proxy;
  }

  @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
  public CurrencyConversion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
      @PathVariable BigDecimal quantity) {
    HashMap<String, String> uriVariables = new HashMap<>();
    uriVariables.put("from", from);
    uriVariables.put("to", to);

    CurrencyConversion response = new RestTemplate().getForObject(
        "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
        CurrencyConversion.class, uriVariables);

    return new CurrencyConversion(response.getId(), from, to, quantity, response.getConversionMultiple(),
        quantity.multiply(response.getConversionMultiple()), response.getEnvironment() + " rest template");
  }

  @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
  public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
      @PathVariable BigDecimal quantity) {
    CurrencyConversion response = proxy.retrieveExchangeValue(from, to);
    return new CurrencyConversion(response.getId(), from, to, quantity, response.getConversionMultiple(),
        quantity.multiply(response.getConversionMultiple()), response.getEnvironment() + " feign");
  }
}
