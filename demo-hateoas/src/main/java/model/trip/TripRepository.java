package model.trip;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long>{
	Collection<Trip> findByTripUserEmail(String userEmail);
}
