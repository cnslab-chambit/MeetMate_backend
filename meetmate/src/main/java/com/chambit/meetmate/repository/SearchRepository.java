package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    @Query(value = "SELECT * FROM category WHERE ST_Distance_Sphere(ST_MakePoint(y, x), ST_MakePoint(:longitude, :latitude)) <= :radius", nativeQuery = true)
    List<Search> findByLocationWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radius") double radius);
}