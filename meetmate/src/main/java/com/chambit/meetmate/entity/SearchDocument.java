package com.chambit.meetmate.entity;

import jakarta.persistence.*;

@Entity
public class SearchDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String placeName;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String categoryName;
    private String phone;
    private String addressName;
    private String roadAddressName;
    private String placeUrl;
    private String distance;
    private Double x;
    private Double y;
    @ManyToOne
    @JoinColumn(name = "same_name_id")
    private SearchSameName sameName;

    public SearchDocument(Long id, String placeName, String categoryGroupCode, String categoryGroupName, String categoryName, String phone, String addressName, String roadAddressName, String placeUrl, String distance, Double x, Double y, SearchSameName sameName) {
        this.id = id;
        this.placeName = placeName;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.categoryName = categoryName;
        this.phone = phone;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.placeUrl = placeUrl;
        this.distance = distance;
        this.x = x;
        this.y = y;
        this.sameName = sameName;
    }

    public SearchDocument(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    public void setCategoryGroupCode(String categoryGroupCode) {
        this.categoryGroupCode = categoryGroupCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getRoadAddressName() {
        return roadAddressName;
    }

    public void setRoadAddressName(String roadAddressName) {
        this.roadAddressName = roadAddressName;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }

    public void setPlaceUrl(String placeUrl) {
        this.placeUrl = placeUrl;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public SearchSameName getSameName() {
        return sameName;
    }

    public void setSameName(SearchSameName sameName) {
        this.sameName = sameName;
    }

    public String getCategoryGroupName() {
        return categoryGroupName;
    }

    public void setCategoryGroupName(String categoryGroupName) {
        this.categoryGroupName = categoryGroupName;
    }
}
