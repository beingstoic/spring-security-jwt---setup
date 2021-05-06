package com.assignup.ie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignup.ie.entity.ConfirmationToken;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {

	ConfirmationToken findByToken(String token);

}
