package io.f12.notionlinkedblog.api.user;

import static io.f12.notionlinkedblog.api.EmailApiController.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.f12.notionlinkedblog.domain.user.dto.signup.UserSignupRequestDto;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserApiControllerTests {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper om;

	@DisplayName("이메일 기반 회원가입")
	@Nested
	class SignUpByEmailTests {
		@DisplayName("정상 케이스")
		@Nested
		class SuccessCase {
			@DisplayName("회원가입 성공")
			@Test
			void success() throws Exception {
				//given
				final String url = "/api/users/email/signup";
				UserSignupRequestDto userSignupRequestDto = UserSignupRequestDto.builder()
					.username("test")
					.email("test@gmail.com")
					.password("1234")
					.build();
				String requestBody = om.writeValueAsString(userSignupRequestDto);
				MockHttpSession mockHttpSession = new MockHttpSession();
				mockHttpSession.setAttribute(emailVerifiedAttr, "verified");

				//when
				ResultActions resultActions = mockMvc.perform(
					post(url)
						.content(requestBody)
						.contentType(MediaType.APPLICATION_JSON)
						.session(mockHttpSession));

				//then
				resultActions.andExpect(status().isCreated());
			}
		}
	}
}
