package io.f12.notionlinkedblog.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.f12.notionlinkedblog.domain.user.User;
import io.f12.notionlinkedblog.domain.user.dto.info.UserSearchDto;
import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;
import io.f12.notionlinkedblog.repository.user.UserDataRepository;
import io.f12.notionlinkedblog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

	private final UserRepository userRepository;
	private final UserDataRepository userDataRepository;
	private final PasswordEncoder passwordEncoder;

	public Long signupByEmail(UserSignupRequestDto requestDto) {
		checkEmailIsDuplicated(requestDto.getEmail());

		requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		User newUser = requestDto.toEntity();
		User savedUser = userRepository.save(newUser);

		return savedUser.getId();
	}

	@Transactional(readOnly = true)
	public UserSearchDto getUserInfo(Long id) {
		return userDataRepository.findUserById(id).orElseThrow(() -> new IllegalArgumentException("회원ID가 존재하지 않습니다."));
	}

	public Long editUserInfo(Long id, String username, String email, String password,
		String profile, String blogTitle, String githubLink,
		String instagramLink, String introduction) {
		User findUser = userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("회원ID가 존재하지 않습니다."));

		findUser.editProfile(username, email, password, profile,
			blogTitle, githubLink, instagramLink, introduction);
		return id;
	}

	public void removeUser(Long id) {
		userDataRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("회원ID가 존재하지 않습니다."));
		userDataRepository.deleteById(id);
	}

	private void checkEmailIsDuplicated(final String email) {
		boolean isPresent = userRepository.findByEmail(email).isPresent();
		if (isPresent) {
			throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
		}
	}
}
