package com.chambit.meetmate.service;

import com.chambit.meetmate.dto.SearchDTO;
import com.chambit.meetmate.entity.Place;
import com.chambit.meetmate.entity.Subway;
import com.chambit.meetmate.repository.CategoryRepository;
import com.chambit.meetmate.repository.SearchRepository;
import com.chambit.meetmate.repository.SubwayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private static final int CATEGORY_COUNT = 5; // 카테고리 수
    private static final int[] CATEGORY_RANGE_MIN = {2, 9, 20, 27, 42}; // 각 카테고리별 시작 카테고리 ID
    private static final int[] CATEGORY_RANGE_MAX = {7, 18, 25, 40, 46}; // 각 카테고리별 마지막 카테고리 ID
    private static final String[] CATEGORY_NAME = {"대형마트", "문화시설", "관광명소", "음식점", "카페"};

    private final SearchRepository searchRepository;
    private final CategoryRepository categoryRepository;
    private final SubwayRepository subwayRepository;

    public Place findCategories(int id) {
//        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Wrong!"));
        return searchRepository.findById(id).orElseThrow(() -> new RuntimeException("Wrong!"));
    }

    public List<SearchDTO> findWithinRadius(double longitude, double latitude) {
        double radius = 0.2; // 반경 200m
//        List<Place> searchList = searchRepository.findByLocationWithinRadius(longitude, latitude, radius);
        List<SearchDTO> resultList = new ArrayList<>(CATEGORY_COUNT);

        for (int i = 0; i < CATEGORY_COUNT; i++) {
            resultList.add(new SearchDTO());
            resultList.get(i).setCategory_name(CATEGORY_NAME[i]);
            resultList.get(i).setSearchList(searchRepository.findByLocationWithinRadiusAndCategoryId(longitude, latitude, radius, CATEGORY_RANGE_MIN[i], CATEGORY_RANGE_MAX[i]));
            log.info(String.valueOf(resultList.get(i).getSearchList().size()));
        }

        return resultList;

//        // 카테고리별로 검색 결과 분류
//        List<List<Place>> categorizedList = new ArrayList<>();
//        for (int i = 0; i < CATEGORY_COUNT; i++) {
//            categorizedList.add(new ArrayList<>());
//        }
//        for (Place search : searchList) {
//            int categoryId = search.getId();
//            for (int i = 0; i < CATEGORY_COUNT; i++) {
//                if (categoryId >= CATEGORY_RANGE_MIN[i] && categoryId <= CATEGORY_RANGE_MAX[i]) {
//                    categorizedList.get(i).add(search);
//                    break;
//                }
//            }
//        }
//
//        // SearchDTO 객체로 변환하여 반환
//        List<SearchDTO> searchDTOList = new ArrayList<>();
//        for (int i = 0; i < CATEGORY_COUNT; i++) {
//            SearchDTO searchDTO = new SearchDTO();
//            searchDTO.setCategory_name(CATEGORY_NAME[i]);
//            searchDTO.setSearchList(categorizedList.get(i));
//            searchDTOList.add(searchDTO);
//        }
//        return searchDTOList;
    }

    private Double calculateSquareAbsCoordinate(String subCoor,double inputCoor){
        double convCoor=Double.parseDouble(subCoor);
        return Math.pow(Math.abs(convCoor-inputCoor),2);
    }
    public List<SearchDTO> find(double longitude, double latitude){
        double radius = 1.0;

        List<Subway> subwayList= new ArrayList<>();

        //범위 내 지하철역 감지될때 까지 찾음
        while(subwayList.size()==0){
            subwayList=subwayRepository.findByLocationWithinRadius(longitude, latitude, radius);
            radius+=1.0;
        }

        double distance=10000000.0;
        Subway subway=null;

        for(Subway s: subwayList){
            //sql 안넣고 max나 min으로 비교하는 방식으로 하면 될듯?
            double d=Math.sqrt(calculateSquareAbsCoordinate(s.getX(),longitude)
                    +calculateSquareAbsCoordinate(s.getY(),latitude));

            if (d<distance){
                distance=d;
                subway=s;
            }
        }

        System.out.println("현재역: "+subway.getName());

        //이후 해당 지하철역 기준으로 범위 탐색
        return findWithinRadius(Double.parseDouble(subway.getX()),Double.parseDouble(subway.getY()));
    }
}
