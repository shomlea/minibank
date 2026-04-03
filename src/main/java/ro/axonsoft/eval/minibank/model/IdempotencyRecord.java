package ro.axonsoft.eval.minibank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class IdempotencyRecord {

    public IdempotencyRecord() {}

    public IdempotencyRecord(String requestKey, String resourceType, Long resourceId) {
        this.requestKey = requestKey;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    @Id
    private String requestKey;

    private String resourceType;

    private Long resourceId;

    public String getRequestKey() {
        return requestKey;
    }
    public void setRequestKey(String requestKey) {
        this.requestKey = requestKey;
    }
    public String getResourceType() {
        return resourceType;
    }
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    public Long getResourceId() {
        return resourceId;
    }
    public void setResourceId(Long resourceId) {this.resourceId = resourceId;}




}