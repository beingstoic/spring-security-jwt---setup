package com.assignup.ie.service;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.assignup.ie.entity.AppUser;
import com.assignup.ie.entity.ConfirmationToken;
import com.assignup.ie.repository.ConfirmationTokenRepository;

@Service
public class ConfirmationTokenService {

	@Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public ConfirmationToken findToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
    	ConfirmationToken oldToken = confirmationTokenRepository.findByToken(token);
    	oldToken.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
    	return 1;
    }
    
    public ConfirmationToken generateVerificationToken(AppUser user) {
    	ConfirmationToken token = new ConfirmationToken(UUID.randomUUID().toString(), user);
    	confirmationTokenRepository.save(token);
    	return token;
    }
    
    public boolean verifyToken(String token) {
    	ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
		if(confirmationToken == null) {
			throw new IllegalStateException("Invalid token");
		}
		else if(confirmationToken.getExpiresAt().before(new Timestamp(System.currentTimeMillis()))){
			throw new IllegalStateException("Token is expired");
		}
		confirmationToken.getAppUser().setLocked(false);
		confirmationToken.getAppUser().setEnabled(true);
		confirmationToken.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
		confirmationTokenRepository.save(confirmationToken);
		return true;
    }
}
