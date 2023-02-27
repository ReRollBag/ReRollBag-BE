package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Bags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BagsRepository extends JpaRepository<Bags, String> {
    Optional<Bags> findById(String bagsId);
}
