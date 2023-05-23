package com.chambit.meetmate.repository;

import com.chambit.meetmate.entity.Place;
import com.chambit.meetmate.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayRepository extends JpaRepository<Subway,Integer> {
    @Query(value = "SELECT * FROM subway WHERE ( 6371 * acos( cos( radians(:latitude) ) * cos( radians( y ) ) * cos( radians( x ) - radians(:longitude) ) + sin( radians(:latitude) ) * sin( radians( y ) ) ) ) <= :radius", nativeQuery = true)
    List<Subway> findByLocationWithinRadius(@Param("longitude") double longitude, @Param("latitude") double latitude, @Param("radius") double radius);
}
