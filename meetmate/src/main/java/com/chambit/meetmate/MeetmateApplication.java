package com.chambit.meetmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeetmateApplication {

	public static void main(String[] args) {
		SpringApplication.run(MeetmateApplication.class, args);
	}

	// 아래부터 메인 실행시 같이 실행되는 코드
//	@Autowired
//	private CategorySearchService categorySearchService;
//
//	@Autowired
//	private KeywordSearchService keywordSearchService;
//
//	@PostConstruct
//	public void init() throws JsonProcessingException {
//		Double xleft = 126.734086;
//		Double yleft = 37.715133;
//		Double xright = 127.269311;
//		Double yright = 37.413294;
//		//System.out.println("카테고리 검색");
//		//categorySearchService.saveCategorySearch(xleft, yleft, xright, yright);
//		System.out.println("여가시설");
//		keywordSearchService.saveKeywordSearch(xleft, yleft, xright, yright);
//	}

}
