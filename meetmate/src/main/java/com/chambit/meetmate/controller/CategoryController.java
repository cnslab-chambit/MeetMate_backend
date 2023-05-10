package com.chambit.meetmate.controller;

import com.chambit.meetmate.service.CategorySearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {

    @Autowired
    private CategorySearchService categorySearchService;


    @RequestMapping("/categorySearch")
    public String categorySearch() throws JsonProcessingException {
        /*
        Double xleft = 126.734086;
        Double yleft = 37.715133;
        Double xright = 127.269311;
        Double yright = 37.413294;
        System.out.println("카테고리 검색");
        //categorySearchService.saveCategorySearch(xleft, yleft, xright, yright);
         */
        return "Initialization completed!";

    }
}
