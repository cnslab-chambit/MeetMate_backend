package com.chambit.meetmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="subway")
@Getter
@Setter
@NoArgsConstructor
public class Subway {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String x;
    private String y;
    private String line;
}
