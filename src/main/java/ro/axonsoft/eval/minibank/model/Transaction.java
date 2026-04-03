package ro.axonsoft.eval.minibank.model;

import jakarta.persistence.*;
import ro.axonsoft.eval.minibank.model.enums.CurrencyType;
import ro.axonsoft.eval.minibank.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    private String counterpartyIban;

    @Column(nullable = false)
    private Long transferId;

    @Column(nullable = false)
    private Long accountId;


    public Transaction() {}

    public Transaction(Long accountId, TransactionType type, BigDecimal amount,
                       CurrencyType currency, BigDecimal balanceAfter,
                       String counterpartyIban, Long transferId) {
        this.accountId = accountId;
        this.timestamp = Instant.now();
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.balanceAfter = balanceAfter;
        this.counterpartyIban = counterpartyIban;
        this.transferId = transferId;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public CurrencyType getCurrency() { return currency; }
    public void setCurrency(CurrencyType currency) { this.currency = currency; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getCounterpartyIban() { return counterpartyIban; }
    public void setCounterpartyIban(String counterpartyIban) { this.counterpartyIban = counterpartyIban; }

    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}