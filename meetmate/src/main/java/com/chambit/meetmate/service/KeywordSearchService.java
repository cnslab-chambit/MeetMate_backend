package com.chambit.meetmate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chambit.meetmate.entity.SearchDocument;
import com.chambit.meetmate.entity.SearchSameName;
import com.chambit.meetmate.repository.KeywordRepository;
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
public class KeywordSearchService {

    @Autowired
    private KeywordRepository keywordRepository;

    @Value("${kakao.rest.api.key}")
    private String apikey;

    public void saveKeywordSearch(Double xleft, Double yleft, Double xright, Double yright) throws JsonProcessingException {
        int page=1;
        Boolean isEnd=false;
        RestTemplate restTemplate = new RestTemplate();
        String keyword = "노래방";
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query="+keyword+"&rect="+xleft+","+yleft+","+xright+","+yright;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apikey);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("page", page);
        ResponseEntity<String> response = restTemplate.exchange(
                builder.build().toUriString(),
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
            saveKeywordSearch(xleft, yleft, midX, midY);
            saveKeywordSearch(midX, yleft, xright, midY);
            saveKeywordSearch(xleft, midY, midX, yright);
            saveKeywordSearch(midX, midY, xright, yright);
        }else{
            page=1;
            isEnd=false;
            while(!isEnd){
                builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("page", page);
                response = restTemplate.exchange(
                        builder.build().toUriString(),
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
                    SearchDocument keywordDocument = new SearchDocument();
                    keywordDocument.setPlaceName(document.path("place_name").asText());
                    keywordDocument.setCategoryGroupCode(document.path("category_group_code").asText());
                    keywordDocument.setCategoryGroupName(document.path("category_group_name").asText());
                    keywordDocument.setCategoryName(document.path("category_name").asText());
                    keywordDocument.setPhone(document.path("phone").asText());
                    keywordDocument.setAddressName(document.path("address_name").asText());
                    keywordDocument.setRoadAddressName(document.path("road_address_name").asText());
                    keywordDocument.setPlaceUrl(document.path("place_url").asText());
                    keywordDocument.setDistance(document.path("distance").asText());
                    keywordDocument.setX(document.path("x").asDouble());
                    keywordDocument.setY(document.path("y").asDouble());
                    if(!keywordDocument.getAddressName().contains("서울")){
                        continue;
                    }
                    // same_name 정보 저장
                    JsonNode sameName = document.path("same_name");
                    if (!sameName.isMissingNode()) {
                        SearchSameName kewordSameName = new SearchSameName();
                        kewordSameName.setKeyWord(sameName.asText());
                        keywordDocument.setSameName(kewordSameName);
                    }
                    System.out.println("총 " + totalCount + "개 중 " + pageableCount + "개 데이터를 가져왔습니다. is_end: " + isEnd);
                    if (sameName.size() > 0) {
                        System.out.println("검색어와 일치하는 이름의 업체가 " + sameName.size() + "개가 있습니다.");
                    }
                    searchDocuments.add(keywordDocument);
                }
                keywordRepository.saveAll(searchDocuments);
                page++;
            }
        }
    }
}
