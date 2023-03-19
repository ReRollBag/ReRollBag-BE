package com.ReRollBag.domain.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class RentingMarkers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markersId;

    @Column(name = "name")
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "maxBagsNum")
    private int maxBagsNum;

    @Column(name = "currentBagsNum")
    private int currentBagsNum;

    @Column(name = "imageUrl")
    private String imageUrl;
}
