package io.f12.notionlinkedblog.email.service.port;

import org.springframework.mail.SimpleMailMessage;

public interface MailSender {
	void send(SimpleMailMessage mail);
}
