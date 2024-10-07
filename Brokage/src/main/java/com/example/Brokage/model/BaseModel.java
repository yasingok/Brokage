package com.example.Brokage.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseModel {

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateDate;
}
