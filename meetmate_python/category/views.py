import collections

from rest_framework import viewsets
from rest_framework.response import Response
from rest_framework.decorators import action

from .models import SearchDocument,Place,Category
from .serializers import SearchDocumentSerializer,PlaceSerializer,CategorySerializer
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.common.exceptions import StaleElementReferenceException, NoSuchElementException
from category import settings
import threading
import json
import requests,time,os,json

class KaKaoToDBAPI(viewsets.ModelViewSet):
    queryset = SearchDocument.objects.all()
    serializer_class = SearchDocumentSerializer
    rest_api_key=settings.get_secret("API_KEY")
    url="https://dapi.kakao.com/v2/local/search/category.json"
    def create(self, request, *args, **kwargs):
        serializer=self.get_serializer(data=request)

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data)
        return Response(serializer.error)

    @action(detail=False, methods=['GET'])
    def callAPI(self,request):
        print("collect data")
        category_data=[]
        for code in ["MT1", "CT1", "AT4", "FD6", "CE7"]:
            print("code: ",code)
            category_data+=self.findData(code, 126.734086, 37.715133, 127.269311, 37.413294,"",-1)
        #category_data=self.findData("FD6", 126.734086, 37.715133, 127.269311, 37.413294,"",-1)
        category_data=list(map(dict, collections.OrderedDict.fromkeys(tuple(sorted(d.items())) for d in category_data)))

        for data in category_data:
            context={}
            if "서울" not in data['address_name']:
                continue
            context['place_url'] = data['place_url']
            context['x'] = data['x']
            context['y'] = data['y']
            context['category_group_code'] = data['category_group_code']
            context['category_name'] = data['category_name']
            context['place_name'] = data['place_name']
            context['address'] = data['address_name']

            self.create(context)

        return Response("success")

    def findData(self, code, xleft, yleft, xright, yright,b_url,b_count):
        category_data=[]
        page=1
        params={
            'category_group_code':f"{code}",
            'rect':f"{xleft},{yleft},{xright},{yright}",
            'page':page
        }
        headers={
            'Authorization':"KakaoAK "+self.rest_api_key
        }

        res=requests.get(self.url,headers=headers,params=params)
        totalCount=res.json()['meta']['total_count']
        cur_url = ""

        print("현재좌표",xleft,"," ,yleft,"/" ,xright,",", yright,)

        if totalCount !=0:
            cur_url=res.json()['documents'][0]['place_url']

        if cur_url==b_url and b_count==totalCount:
            return category_data

        if totalCount >45:
            midX=(xleft+xright)/2
            midY=(yleft+yright)/2
            category_data+=self.findData(code,xleft, yleft, midX, midY,cur_url,totalCount)
            category_data+=self.findData(code,midX, yleft, xright, midY,cur_url,totalCount)
            category_data+=self.findData(code,xleft, midY, midX, yright,cur_url,totalCount)
            category_data+=self.findData(code,midX, midY, xright, yright,cur_url,totalCount)
        else:
            category_data.extend(res.json()['documents'])

            while not res.json()['meta']['is_end']:
                page+=1
                params = {
                    'category_group_code': f"{code}",
                    'rect': f"{xleft},{yleft},{xright},{yright}",
                    'page': page
                }
                res = requests.get(self.url, headers=headers, params=params)
                category_data.extend(res.json()['documents'])

        return category_data


class CategorySettingAPI(viewsets.ModelViewSet):
    queryset = Category.objects.all()
    serializer_class = CategorySerializer
    '''
    def create(self, request,*args, **kwargs):
        fkey=False

        if 'parent' in request:
            fkey=True

        serializer = self.get_serializer(data=request)
        serializer.is_valid(raise_exception=True)

        if fkey:
            serializer.save()
        else:
            serializer.save()
        return Response(serializer.data)

    '''
    @action(detail=False, methods=['GET'])
    def setting(self, request):
        category={
            '대형마트':['홈플러스','노브랜드','하나로마트','이마트','코스트코','기타']
            ,'문화시설':['박물관','미술관','전시관','공연장/연극극장','영화관','문화원','과학관','기념관','아쿠아리움','기타']
            ,'관광명소':['문화유산/역사적명소','자연경관','문화/축제','테마거리','전망/조망','기타']
            ,'음식점':['퓨전요리','아시아음식','패스트푸드','도시락','술집','뷔페','중식','일식','간식','양식','치킨','분식','한식','기타']
            ,'카페':['북카페','동물카페','테마카페','카페','기타']}

        for big_category,small_categories in category.items():
            p_serializer=self.get_serializer(data={"name":big_category})
            p_serializer.is_valid(raise_exception=True)
            parent_instance = p_serializer.save()

            for small_category in small_categories:
                s_serializer=self.get_serializer(
                    data={"name":small_category,"parent":parent_instance.id})
                s_serializer.is_valid(raise_exception=True)
                s_serializer.save()

        return Response("category setting success")


class PlaceSettingAPI(viewsets.ModelViewSet):
    queryset = Place.objects.all()
    sdqueryset=SearchDocument.objects.all()
    serializer_class = PlaceSerializer
    options=webdriver.ChromeOptions()
    options.add_argument('headless')
    options.add_argument('window-size=1920x1080')
    options.add_argument("disable-gpu")

    def place_save(self,data,category):
        context={}
        context['place_url']=data['place_url']
        context['x'] = data['x']
        context['y'] = data['y']
        context['place_name'] = data['place_name']
        context['address'] = data['address']
        context['star_rate'] = data['star_rate']
        context['category']=category.id
        serializer=self.get_serializer(data=context)
        serializer.is_valid(raise_exception=True)
        serializer.save()


    def cafe_insert(self,data):
        p_cafe=Category.objects.get(name='카페',parent=None)
        cafes = Category.objects.filter(parent=p_cafe)
        sub_category=[
            ['북카페','만화카페']
            , ['애견', '고양이']
            , ['반지', '공방','라이브','보드','사주','갤러리','키즈','드레스']
            , ['떡', '찻집','디저트','카페']
        ]
        for i in range(len(sub_category)):
            if any(keyword in data['category_name'] for keyword in sub_category[i]):
                self.place_save(data, cafes[i])
                return

        self.place_save(data, cafes[4])

        '''
        if any(keyword in data['category_name'] for keyword in sub_category[0]):
            self.place_save(data,cafes[3])
        elif any(keyword in data['category_name'] for keyword in sub_category[1]):
            self.place_save(data,cafes[2])
        elif any(keyword in data['category_name'] for keyword in sub_category[2]):
            self.place_save(data, cafes[1])
        elif any(keyword in data['category_name'] for keyword in sub_category[3]):
            self.place_save(data,cafes[0])
        else:
            self.place_save(data, cafes[4])
        '''

    def mart_insert(self,data):
        p_mart=Category.objects.get(name='대형마트',parent=None)
        marts = Category.objects.filter(parent=p_mart)
        sub_category = ['홈플러스','노브랜드','하나로','이마트','코스트코']

        for i in range(5):
            if sub_category[i] in  data['category_name']:
                self.place_save(data, marts[i])
                return

        self.place_save(data, marts[5])
        '''
        if '홈플러스' in data['category_name']:
            self.place_save(data,marts[0])
        elif '노브랜드' in data['category_name']:
            self.place_save(data,marts[1])
        elif '하나로' in data['category_name']:
            self.place_save(data,marts[2])
        elif '이마트' in data['category_name']:
            self.place_save(data,marts[3])
        elif '코스트코' in data['category_name']:
            self.place_save(data,marts[4])
        else:
            self.place_save(data, marts[5])
        '''

    def culture_insert(self,data):
        p_culture=Category.objects.get(name='문화시설',parent=None)
        cultures = Category.objects.filter(parent=p_culture)

        sub_category = ['박물관', '미술관', '전시관', '공연장', '영화관','문화원','과학관','기념관','아쿠아리움']

        for i in range(len(sub_category)):
            if sub_category[i] in data['category_name']:
                self.place_save(data, cultures[i])
                return
        self.place_save(data, cultures[9])

    def viewing_insert(self,data):
        p_viewing=Category.objects.get(name='관광명소',parent=None)
        viewings = Category.objects.filter(parent=p_viewing)
        sub_category=[
            ['도성','고궁']
            , ['둘레길', '도보여행','계곡','숲','산','수목원','식물원','호수','저수지'
                ,'섬','온천','관광농원','자연휴양림','강','국립공원']
            , ['도가지','도예촌','유원지','동물원','테마파크','눈썰매장']
            , ['테마거리','먹자골목','카페거리']
            , ['전망대', '천문대']
        ]

        for i in range(len(sub_category)):
            if any(keyword in data['category_name'] for keyword in sub_category[i]):
                self.place_save(data, viewings[i])
                return
        self.place_save(data, viewings[5])
        '''
        if any(keyword in data['category_name'] for keyword in sub_category[0]):
            self.place_save(data,viewings[4])
        elif any(keyword in data['category_name'] for keyword in sub_category[1]):
            self.place_save(data,viewings[0])
        elif any(keyword in data['category_name'] for keyword in sub_category[2]):
            self.place_save(data, viewings[1])
        elif any(keyword in data['category_name'] for keyword in sub_category[3]):
            self.place_save(data,viewings[2])
        elif any(keyword in data['category_name'] for keyword in sub_category[4]):
            self.place_save(data,viewings[3])
        else:
            self.place_save(data, viewings[5])
        '''

    def food_insert(self,data):
        p_food=Category.objects.get(name='음식점',parent=None)
        foods = Category.objects.filter(parent=p_food)

        sub_category = ['퓨전', '아시아음식', '패스트푸드','도시락', '술집', '뷔페', '중식', '일식','간식','양식','치킨','분식','한식']

        for i in range(len(sub_category)):
            if sub_category[i] in data['category_name']:
                self.place_save(data, foods[i])
                return
        self.place_save(data, foods[13])

        '''
        if '퓨전' in data['category_name']:
            self.place_save(data, foods[12])
        elif '아시아음식' in data['category_name']:
            self.place_save(data, foods[9])
        elif '패스트푸드' in data['category_name']:
            self.place_save(data, foods[10])
        elif '도시락' in data['category_name']:
            self.place_save(data, foods[11])
        elif '술집' in data['category_name']:
            self.place_save(data, foods[7])
        elif '뷔페' in data['category_name']:
            self.place_save(data, foods[8])
        elif '중식' in data['category_name']:
            self.place_save(data, foods[6])
        elif '일식' in data['category_name']:
            self.place_save(data, foods[4])
        elif '간식' in data['category_name']:
            self.place_save(data, foods[5])
        elif '양식' in data['category_name']:
            self.place_save(data, foods[2])
        elif '치킨' in data['category_name']:
            self.place_save(data, foods[0])
        elif '분식' in data['category_name']:
            self.place_save(data, foods[1])
        elif '한식' in data['category_name']:
            self.place_save(data, foods[3])
        else:
            self.place_save(data, foods[13])
        '''

    def crawler(self,sub_docs):
        driver = webdriver.Chrome('./chromedriver', options=self.options)
        for doc in sub_docs:
            driver.get(doc['place_url'])
            driver.implicitly_wait(5)
            try:
                star_find = driver.find_element(By.CSS_SELECTOR,
                                                "#mArticle > div.cont_essential > div:nth-child(1) > div.place_details > div > div > a:nth-child(3) > span.color_b")
                doc['star_rate'] = float(star_find.text)
            except (StaleElementReferenceException, NoSuchElementException) as e:
                print("별점 미제공 url 감지, 0점으로 처리합니다.")
                print("현재url", doc['place_url'])
                doc['star_rate'] = 0.0

            print("저장합니다")
            if doc['category_group_code'] == 'MT1':
                self.mart_insert(doc)
            elif doc['category_group_code'] == 'CT1':
                self.culture_insert(doc)
            elif doc['category_group_code'] == 'AT4':
                self.viewing_insert(doc)
            elif doc['category_group_code'] == 'FD6':
                self.food_insert(doc)
            elif doc['category_group_code'] == 'CE7':
                self.cafe_insert(doc)

        driver.close()

    @action(detail=False, methods=['GET'])
    def setting(self,request):
        documents = SearchDocument.objects.values()
        docs_len = len(documents)
        subs_docs_len = docs_len // 8
        threads = []

        for i in range(8):
            if i == 7:
                sub_docs = documents[i * subs_docs_len:]
            else:
                sub_docs = documents[i * subs_docs_len:(i + 1) * subs_docs_len]
            t = threading.Thread(target=self.crawler, args=(sub_docs,))
            t.start()
            threads.append(t)

        for t in threads:
            t.join()

        return Response("설정 완료")