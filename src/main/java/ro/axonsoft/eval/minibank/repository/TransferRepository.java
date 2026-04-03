package ro.axonsoft.eval.minibank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ro.axonsoft.eval.minibank.model.Transfer;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Page<Transfer> findBySourceIban(String sourceIban, Pageable pageable);
    List<Transfer> findBySourceIban(String sourceIban);
}
