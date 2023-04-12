package com.chambit.meetmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Search {
    @Id
    private Long id;
    private String address;
    private String name;
    private String place_url;
    @Column(name = "x")
    private Double latitude;
    @Column(name = "y")
    private Double longitude;
    private Integer category_id;
}
