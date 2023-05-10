package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Place, Integer> {

    @Query(value = "SELECT * FROM place WHERE star_rate IS NOT NULL AND category_id IN (SELECT id FROM category WHERE id BETWEEN :categoryIdMin AND :categoryIdMax) AND ( 6371 * acos( cos( radians(:latitude) ) * cos( radians( y ) ) * cos( radians( x ) - radians(:longitude) ) + sin( radians(:latitude) ) * sin( radians( y ) ) ) ) <= :radius ORDER BY star_rate DESC", nativeQuery = true)
    List<Place> findByLocationWithinRadiusAndCategoryId(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radius") double radius, @Param("categoryIdMin") int categoryIdMin, @Param("categoryIdMax") int categoryIdMax);

    @Query(value = "SELECT * FROM category WHERE ST_Distance_Sphere(ST_MakePoint(y, x), ST_MakePoint(:longitude, :latitude)) <= :radius", nativeQuery = true)
    List<Place> findByLocationWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radius") double radius);
}