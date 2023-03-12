package com.chambit.meetmate.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chambit.meetmate.entity.SearchDocument;
import com.chambit.meetmate.entity.SearchSameName;
import com.chambit.meetmate.repository.CategoryRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class CategorySearchService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${kakao.rest.api.key}")
    private String apikey;

    public void saveCategorySearch(Double xleft, Double yleft, Double xright, Double yright) throws JsonProcessingException {
        int page = 1;
        boolean isEnd=false;

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://dapi.kakao.com/v2/local/search/category.json?category_group_code=CT1&rect=" + xleft + "," + yleft + "," + xright + "," + yright;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apikey);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page", page)
                .queryParam("size", 15);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode meta = root.path("meta");
        int totalCount = meta.path("total_count").asInt();
        int pageableCount = meta.path("pageable_count").asInt();
        isEnd = meta.path("is_end").asBoolean();
        JsonNode documents;
        List<SearchDocument> searchDocuments;
        System.out.println("총 " + totalCount + "개 중 " + pageableCount + "개 데이터를 가져왔습니다. is_end: " + isEnd);
        if(totalCount>45){
            // 좌표 4등분하여 재귀호출
            double midX = (xleft + xright) / 2;
            double midY = (yleft + yright) / 2;
            saveCategorySearch(xleft, yleft, midX, midY);
            saveCategorySearch(midX, yleft, xright, midY);
            saveCategorySearch(xleft, midY, midX, yright);
            saveCategorySearch(midX, midY, xright, yright);
        }else{
            page=1;
            isEnd=false;
            while(!isEnd){
                builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("page", page)
                        .queryParam("size", 15);
                response = restTemplate.exchange(
                        builder.toUriString(),
                        HttpMethod.GET,
                        entity,
                        String.class
                );
                objectMapper = new ObjectMapper();
                root = objectMapper.readTree(response.getBody());
                meta = root.path("meta");
                totalCount = meta.path("total_count").asInt();
                pageableCount = meta.path("pageable_count").asInt();
                isEnd = meta.path("is_end").asBoolean();
                documents = root.path("documents");
                searchDocuments = new ArrayList<>();
                for (JsonNode document : documents) {
                    SearchDocument searchDocument = new SearchDocument();
                    searchDocument.setPlaceName(document.path("place_name").asText());
                    searchDocument.setCategoryGroupCode(document.path("category_group_code").asText());
                    searchDocument.setCategoryGroupName(document.path("category_group_name").asText());
                    searchDocument.setCategoryName(document.path("category_name").asText());
                    searchDocument.setPhone(document.path("phone").asText());
                    searchDocument.setAddressName(document.path("address_name").asText());
                    searchDocument.setRoadAddressName(document.path("road_address_name").asText());
                    searchDocument.setPlaceUrl(document.path("place_url").asText());
                    searchDocument.setDistance(document.path("distance").asText());
                    searchDocument.setX(document.path("x").asDouble());
                    searchDocument.setY(document.path("y").asDouble());
                    if(!searchDocument.getAddressName().contains("서울")){
                        continue;
                    }
                    // same_name 정보 저장
                    JsonNode sameName = document.path("same_name");
                    if (!sameName.isMissingNode()) {
                        SearchSameName searchSameName = new SearchSameName();
                        searchSameName.setKeyWord(sameName.asText());
                        searchDocument.setSameName(searchSameName);
                    }
                    System.out.println("총 " + totalCount + "개 중 " + pageableCount + "개 데이터를 가져왔습니다. is_end: " + isEnd);
                    if (sameName.size() > 0) {
                        System.out.println("검색어와 일치하는 이름의 업체가 " + sameName.size() + "개가 있습니다.");
                    }
                    searchDocuments.add(searchDocument);
                }
                categoryRepository.saveAll(searchDocuments);
                page++;
            }
        }
    }
}
