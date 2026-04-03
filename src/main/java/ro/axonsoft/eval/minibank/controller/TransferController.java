package ro.axonsoft.eval.minibank.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.axonsoft.eval.minibank.model.Transfer;
import ro.axonsoft.eval.minibank.model.dto.TransferResponse;
import ro.axonsoft.eval.minibank.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> performTransfer(@RequestBody Transfer transfer,
                                                            @RequestHeader("X-Idempotency-Key") String key) {
        Transfer savedTransfer = transferService.save(transfer, key);

        return ResponseEntity.status(HttpStatus.CREATED).body(TransferResponse.fromEntity(savedTransfer));
    }

    @GetMapping
    public ResponseEntity<Page<TransferResponse>> getAllTransfers(Pageable pageable) {
        Page<Transfer> transferPage = transferService.getAll(pageable);

        Page<TransferResponse> responsePage = transferPage.map(TransferResponse::fromEntity);

        return ResponseEntity.ok(responsePage);
    }



    @GetMapping("/{id}")
    public ResponseEntity<TransferResponse> getTransfer(@PathVariable Long id) {
        return transferService.findById(id)
                .map(TransferResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}