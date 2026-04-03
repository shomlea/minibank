package ro.axonsoft.eval.minibank.service;

import org.springframework.stereotype.Service;
import ro.axonsoft.eval.minibank.model.IdempotencyRecord;
import ro.axonsoft.eval.minibank.repository.IdempotencyRepository;

import java.util.Optional;

@Service
public class IdempotencyServiceImpl implements IdempotencyService {

    private IdempotencyRepository idempotencyRepository;
    public IdempotencyServiceImpl(IdempotencyRepository idempotencyRepository) {
        this.idempotencyRepository = idempotencyRepository;
    }

    @Override
    public void save(IdempotencyRecord record) {
        idempotencyRepository.save(record);
    }

    @Override
    public Optional<IdempotencyRecord> findByRequestKey(String key) {
        return idempotencyRepository.findByRequestKey(key);
    }

    @Override
    public Optional<IdempotencyRecord> findById(String id) {
        return idempotencyRepository.findById(id);
    }
}
