package com.assignup.ie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignup.ie.entity.AppUser;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	AppUser findByEmail(String username);
	boolean existsByEmail(String email);

}
