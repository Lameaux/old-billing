package com.euromoby.api.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public abstract class Entity implements Persistable<UUID> {
    @Id
    private UUID id;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Override
    public boolean isNew() {
        if (id != null) {
            return false;
        }

        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        return true;
    }
}
