package io.f12.notionlinkedblog.service.user;

import static io.f12.notionlinkedblog.exceptions.ExceptionMessages.UserExceptionsMessages.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserEditDto;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

	private final UserDataRepository userDataRepository;
	private final PasswordEncoder passwordEncoder;

	public Long signupByEmail(UserSignupRequestDto requestDto) {
		checkEmailIsDuplicated(requestDto.getEmail());

		requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		User newUser = requestDto.toEntity();
		User savedUser = userDataRepository.save(newUser);

		return savedUser.getId();
	}

	@Transactional(readOnly = true)
	public UserSearchDto getUserInfo(Long id) {
		return userDataRepository.findUserById(id).orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
	}

	public Long editUserInfo(Long id, UserEditDto editDto) {

		User findUser = userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));

		findUser.editProfile(editDto);
		return id;
	}

	public void removeUser(Long id) {
		userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(USER_NOT_EXIST));
		userDataRepository.deleteById(id);
	}

	private void checkEmailIsDuplicated(final String email) {
		boolean isPresent = userDataRepository.findByEmail(email).isPresent();
		if (isPresent) {
			throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
		}
	}
}
