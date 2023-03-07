package com.ReRollBag.repository;

import com.ReRollBag.domain.entity.Markers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkersRepository extends JpaRepository<Markers, Long> {
    public Markers findMarkersByName(String name);
}
