package com.ReRollBag.domain.entity;

import com.ReRollBag.enums.MarkerType;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Markers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markersId;

    @Column(name = "name")
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "markerType")
    private MarkerType markerType;
}
