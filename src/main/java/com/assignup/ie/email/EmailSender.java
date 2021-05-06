package com.assignup.ie.email;

public interface EmailSender {
	void send(String to, String subject, String email);
}
