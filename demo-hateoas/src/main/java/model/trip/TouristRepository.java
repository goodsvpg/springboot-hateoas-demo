package model.trip;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TouristRepository extends JpaRepository<Tourist, Long>{
	Optional<Tourist> findByUserEmail(String userEmail);
}
