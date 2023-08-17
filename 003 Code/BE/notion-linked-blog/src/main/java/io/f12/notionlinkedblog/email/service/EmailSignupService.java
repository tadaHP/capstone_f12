package io.f12.notionlinkedblog.email.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.email.service.port.MailSender;
import io.f12.notionlinkedblog.email.service.redis.EmailVerificationTokenService;
import io.f12.notionlinkedblog.entity.verification.EmailVerificationToken;
import io.f12.notionlinkedblog.security.service.SecureRandomService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class EmailSignupService {
	private final MailSender mailSender;
	private final SecureRandomService secureRandomService;
	private final EmailVerificationTokenService emailVerificationTokenService;

	@Transactional(readOnly = true)
	public boolean verifyingCode(final String id, final String code) {
		EmailVerificationToken verificationToken = emailVerificationTokenService.findById(id);
		return verificationToken.getCode().equals(code);
	}

	public String sendMail(final String email) {
		String code = secureRandomService.generateRandomCodeString();
		SimpleMailMessage mail = createVerificationMail(email, code);

		EmailVerificationToken verificationToken = EmailVerificationToken.builder().email(email).code(code).build();
		emailVerificationTokenService.save(verificationToken);

		mailSender.send(mail);

		return verificationToken.getId();
	}

	private SimpleMailMessage createVerificationMail(final String email, String code) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(email);
		message.setSubject("[노션 연동 블로그 서비스] 인증 코드입니다.");
		message.setText("[" + code + "] 메일 확인 인증 코드입니다.");

		return message;
	}
}
