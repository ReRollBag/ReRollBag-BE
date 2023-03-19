package com.ReRollBag.domain.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class ReturningMarkers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long markersId;

    @Column(name = "name")
    private String name;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "imageUrl")
    private String imageUrl;
}
