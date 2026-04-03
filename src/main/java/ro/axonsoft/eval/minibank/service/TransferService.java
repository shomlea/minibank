package ro.axonsoft.eval.minibank.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ro.axonsoft.eval.minibank.model.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferService {
    Transfer save(Transfer transfer, String key);

    Optional<Transfer> findById(Long id);

    List<Transfer> getAll();

    Page<Transfer> getAll(Pageable pageable);

}
