package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Notices;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticesRepository extends JpaRepository<Notices, Long> {
    public Notices findTopByUpdatedAtOrderByUpdatedAt();
}
