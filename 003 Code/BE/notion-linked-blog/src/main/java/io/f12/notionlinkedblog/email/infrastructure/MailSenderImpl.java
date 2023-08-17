package io.f12.notionlinkedblog.email.infrastructure;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import io.f12.notionlinkedblog.email.service.port.MailSender;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
	private final JavaMailSender mailSender;

	@Override
	public void send(SimpleMailMessage mail) {
		mailSender.send(mail);
	}
}
