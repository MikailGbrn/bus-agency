package com.gibran.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gibran.spring.model.Agency;
import com.gibran.spring.model.User;


@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

	Agency findByOwner(User owner);

	@Query(value = "SELECT DISTINCT * FROM agency WHERE owner_user_id = :owner", nativeQuery = true)
	Agency findByOwnerUser(Long owner);
	
    Agency findByOwnerId(Long owner_id);
}
