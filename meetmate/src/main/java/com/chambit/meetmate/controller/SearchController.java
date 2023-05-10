package com.chambit.meetmate.controller;

import com.chambit.meetmate.dto.SearchDTO;
import com.chambit.meetmate.entity.Place;
import com.chambit.meetmate.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    // blabla/search?longitude=경도값&latitude=위도값
    @GetMapping("/search")
    public @ResponseBody ResponseEntity<List<SearchDTO>> searchWithinRadius(@RequestParam("longitude") double longitude, @RequestParam("latitude") double latitude) {
        List<SearchDTO> resultList = searchService.findWithinRadius(longitude, latitude);
        return ResponseEntity.ok().body(resultList);
    }

    @GetMapping("/test")
    public @ResponseBody ResponseEntity<Place> searchWithinRadius(@RequestParam("id") int id) {
//        Category resultList = searchService.findCategories(id);
        Place resultList = searchService.findCategories(id);
        return ResponseEntity.ok().body(resultList);
    }
}
