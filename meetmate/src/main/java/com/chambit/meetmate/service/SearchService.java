package com.chambit.meetmate.service;

import com.chambit.meetmate.dto.SearchDTO;
import com.chambit.meetmate.entity.Search;
import com.chambit.meetmate.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {
    private static final int CATEGORY_COUNT = 5; // 카테고리 수
    private static final int[] CATEGORY_RANGE_MIN = {2, 9, 20, 27, 40}; // 각 카테고리별 시작 카테고리 ID
    private static final int[] CATEGORY_RANGE_MAX = {7, 18, 25, 39, 41}; // 각 카테고리별 마지막 카테고리 ID
    private static final String[] CATEGORY_NAME = {"대형마트", "문화시설", "관광명소", "음식점", "카페"};

    @Autowired
    private SearchRepository searchRepository;

    public List<SearchDTO> findWithinRadius(double longitude, double latitude) {
        double radius = 200.0; // 반경 200m
        List<Search> searchList = searchRepository.findByLocationWithinRadius(longitude, latitude, radius);

        // 카테고리별로 검색 결과 분류
        List<List<Search>> categorizedList = new ArrayList<>();
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            categorizedList.add(new ArrayList<>());
        }
        for (Search search : searchList) {
            int categoryId = search.getCategory_id();
            for (int i = 0; i < CATEGORY_COUNT; i++) {
                if (categoryId >= CATEGORY_RANGE_MIN[i] && categoryId <= CATEGORY_RANGE_MAX[i]) {
                    categorizedList.get(i).add(search);
                    break;
                }
            }
        }

        // SearchDTO 객체로 변환하여 반환
        List<SearchDTO> searchDTOList = new ArrayList<>();
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            SearchDTO searchDTO = new SearchDTO();
            searchDTO.setCategory_name(CATEGORY_NAME[i]);
            searchDTO.setSearchList(categorizedList.get(i));
            searchDTOList.add(searchDTO);
        }
        return searchDTOList;
    }
}
