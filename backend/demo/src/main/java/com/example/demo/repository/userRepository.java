package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.userEntity;


	public interface userRepository extends JpaRepository<userEntity, Long> {
		
		
		userEntity findByEmail(String email); 

	    boolean existsByEmail(String email); // to prevent duplicate
	}


