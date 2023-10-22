package io.f12.notionlinkedblog.user.api.port;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import io.f12.notionlinkedblog.user.api.response.ProfileImageLinkDto;
import io.f12.notionlinkedblog.user.api.response.ProfileSuccessEditDto;
import io.f12.notionlinkedblog.user.api.response.UserSearchDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBasicInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserBlogTitleEditDto;
import io.f12.notionlinkedblog.user.domain.dto.request.UserSocialInfoEditDto;
import io.f12.notionlinkedblog.user.domain.dto.signup.UserSignupRequestDto;

public interface UserService {

	public Long signupByEmail(UserSignupRequestDto requestDto);

	public UserSearchDto getUserInfo(Long id);

	public void editBasicUserInfo(Long id, UserBasicInfoEditDto editDto);

	public void editUserBlogTitleInfo(Long id, UserBlogTitleEditDto editDto);

	public void editUserSocialInfo(Long id, UserSocialInfoEditDto editDto);

	public ProfileSuccessEditDto editUserProfileImage(Long id, MultipartFile imageFile) throws IOException;

	public void removeUserProfileImage(Long id);

	public void removeUser(Long id);

	public ProfileImageLinkDto getProfileImageUrl(Long id);
}
