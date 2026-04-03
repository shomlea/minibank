package ro.axonsoft.eval.minibank.service;

import ro.axonsoft.eval.minibank.model.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyService {
    void save(IdempotencyRecord record);
    Optional<IdempotencyRecord> findByRequestKey(String key);
    Optional<IdempotencyRecord> findById(String id);
}
