package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Bags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BagsRepository extends JpaRepository<Bags, String> {
}
