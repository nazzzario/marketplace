package com.teamchallenge.marketplace.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, columnDefinition = "BINARY(16)")
    private UUID reference;

    @CreationTimestamp
    private ZonedDateTime createdDate;

    @LastModifiedDate
    private ZonedDateTime modifiedDate;

    @PrePersist
    public void prePersist() {
        createdDate = ZonedDateTime.now();
        reference = UUID.randomUUID();
    }

    @PreUpdate
    public void preUpdate() {
        modifiedDate = ZonedDateTime.now();
    }
}
