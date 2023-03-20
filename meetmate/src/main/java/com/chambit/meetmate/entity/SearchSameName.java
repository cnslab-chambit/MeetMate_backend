package com.chambit.meetmate.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class SearchSameName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String keyWord;

    @OneToMany(mappedBy = "sameName", cascade = CascadeType.ALL)
    private List<SearchDocument> sameNameList;

    public SearchSameName(Long id, String keyWord, List<SearchDocument> sameNameList) {
        this.id = id;
        this.keyWord = keyWord;
        this.sameNameList = sameNameList;
    }

    public SearchSameName() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public List<SearchDocument> getSameNameList() {
        return sameNameList;
    }

    public void setSameNameList(List<SearchDocument> sameNameList) {
        this.sameNameList = sameNameList;
    }
}
