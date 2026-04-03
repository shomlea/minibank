package ro.axonsoft.eval.minibank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.axonsoft.eval.minibank.components.ExchangeRateConfig;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateConfig exchangeRateConfig;

    public ExchangeRateController(ExchangeRateConfig exchangeRateConfig) {
        this.exchangeRateConfig = exchangeRateConfig;
    }

    @GetMapping
    public ResponseEntity<Map<String, BigDecimal>> getRates() {
        return ResponseEntity.ok(exchangeRateConfig.getRates());
    }
}