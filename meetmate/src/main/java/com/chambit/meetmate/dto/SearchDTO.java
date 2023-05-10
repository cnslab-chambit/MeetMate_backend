package com.chambit.meetmate.dto;

import com.chambit.meetmate.entity.Place;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchDTO {
    private String category_name;
    private List<Place> searchList;
}