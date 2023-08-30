package io.f12.notionlinkedblog.medium.series.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import io.f12.notionlinkedblog.post.infrastructure.PostEntity;
import io.f12.notionlinkedblog.post.service.port.QuerydslPostRepository;
import io.f12.notionlinkedblog.series.api.response.SeriesDetailSearchDto;
import io.f12.notionlinkedblog.series.api.response.SeriesSimpleSearchDto;
import io.f12.notionlinkedblog.series.infrastructure.SeriesEntity;
import io.f12.notionlinkedblog.series.service.SeriesServiceImpl;
import io.f12.notionlinkedblog.series.service.port.SeriesRepository;
import io.f12.notionlinkedblog.user.infrastructure.UserEntity;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

	@InjectMocks
	SeriesServiceImpl seriesService;

	@Mock
	QuerydslPostRepository querydslPostRepository;

	@Mock
	SeriesRepository seriesRepository;

	@DisplayName("시리즈 정보 간단 조회")
	@Nested
	class LookUpSeriesInfo {
		@DisplayName("성공 케이스")
		@Test
		void successfulCase() {
			//given
			Long fakeId = 1L;
			Long fakePostAId = 1L;
			Long fakePostBId = 2L;
			Long fakePostCId = 3L;

			UserEntity user = UserEntity.builder()
				.id(fakeId)
				.username("tester")
				.email("test@test.com")
				.password("test123")
				.build();

			SeriesEntity series = SeriesEntity.builder()
				.id(fakeId)
				.title("testSeries")
				.post(new ArrayList<>())
				.build();

			PostEntity postA = PostEntity.builder()
				.user(user)
				.title("titleA")
				.content("contentA")
				.thumbnailName("thumbnailA")
				.storedThumbnailPath("pathA")
				.series(series)
				.build();

			PostEntity postB = PostEntity.builder()
				.user(user)
				.title("titleB")
				.content("contentB")
				.thumbnailName("thumbnailB")
				.storedThumbnailPath("pathB")
				.series(series)
				.build();

			PostEntity postC = PostEntity.builder()
				.user(user)
				.title("titleC")
				.content("contentC")
				.thumbnailName("thumbnailC")
				.storedThumbnailPath("pathC")
				.series(series)
				.build();

			series.addPost(postA);
			series.addPost(postB);
			series.addPost(postC);

			//mock
			given(seriesRepository.findSeriesById(fakeId))
				.willReturn(Optional.of(series));
			//when
			SeriesSimpleSearchDto simpleSeriesInfo = seriesService.getSimpleSeriesInfo(fakeId);
			//then
			assertThat(simpleSeriesInfo).extracting("seriesName").isEqualTo(series.getTitle());
			assertThat(simpleSeriesInfo).extracting("seriesId").isEqualTo(series.getId());
			assertThat(simpleSeriesInfo.getPosts()).size().isEqualTo(series.getPost().size());
		}
	}

	@DisplayName("시리즈 내 포스트 정보 조회")
	@Nested
	class LookUpPostsInfo {

		@DisplayName("포스트 오름차순 조회")
		@Nested
		class LookUpPostsByAsc {

			@DisplayName("성공케이스")
			@Test
			void successfulCase() {
				//given
				Long fakeId = 1L;
				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Long fakePostCId = 3L;

				UserEntity user = UserEntity.builder()
					.id(fakeId)
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				SeriesEntity series = SeriesEntity.builder()
					.id(fakeId)
					.title("testSeries")
					.post(new ArrayList<>())
					.build();

				PostEntity postA = PostEntity.builder()
					.user(user)
					.title("titleA")
					.content("contentA")
					.thumbnailName("thumbnailA")
					.storedThumbnailPath("pathA")
					.series(series)
					.build();

				PostEntity postB = PostEntity.builder()
					.user(user)
					.title("titleB")
					.content("contentB")
					.thumbnailName("thumbnailB")
					.storedThumbnailPath("pathB")
					.series(series)
					.build();

				PostEntity postC = PostEntity.builder()
					.user(user)
					.title("titleC")
					.content("contentC")
					.thumbnailName("thumbnailC")
					.storedThumbnailPath("pathC")
					.series(series)
					.build();

				series.addPost(postA);
				series.addPost(postB);
				series.addPost(postC);

				List<Long> ids = new LinkedList<Long>();
				ids.add(fakePostAId);
				ids.add(fakePostBId);
				ids.add(fakePostCId);
				List<PostEntity> posts = new LinkedList<>();
				posts.add(postA);
				posts.add(postB);
				posts.add(postC);

				PageRequest pageRequest = PageRequest.of(0, 10);
				//mock
				given(querydslPostRepository.findIdsBySeriesIdAsc(fakeId, pageRequest))
					.willReturn(ids);
				given(querydslPostRepository.findByIdsJoinWithSeries(ids))
					.willReturn(posts);
				//when
				SeriesDetailSearchDto seriesDetailSearchDto
					= seriesService.getDetailSeriesInfoOrderByAsc(series.getId(), 0);
				// then
				assertThat(seriesDetailSearchDto.getSeriesId()).isEqualTo(series.getId());
				assertThat(seriesDetailSearchDto.getPagingInfo().getElementSize()).isEqualTo(posts.size());
				assertThat(seriesDetailSearchDto.getPostsInfo().size()).isEqualTo(posts.size());
			}

		}

		@DisplayName("포스트 내림차순 조회")
		@Nested
		class LookUpPostsByDesc {
			@DisplayName("성공케이스")
			@Test
			void successfulCase() {
				//given
				Long fakeId = 1L;
				Long fakePostAId = 1L;
				Long fakePostBId = 2L;
				Long fakePostCId = 3L;

				UserEntity user = UserEntity.builder()
					.id(fakeId)
					.username("tester")
					.email("test@test.com")
					.password("test123")
					.build();

				SeriesEntity series = SeriesEntity.builder()
					.id(fakeId)
					.title("testSeries")
					.post(new ArrayList<>())
					.build();

				PostEntity postA = PostEntity.builder()
					.user(user)
					.title("titleA")
					.content("contentA")
					.thumbnailName("thumbnailA")
					.storedThumbnailPath("pathA")
					.series(series)
					.build();

				PostEntity postB = PostEntity.builder()
					.user(user)
					.title("titleB")
					.content("contentB")
					.thumbnailName("thumbnailB")
					.storedThumbnailPath("pathB")
					.series(series)
					.build();

				PostEntity postC = PostEntity.builder()
					.user(user)
					.title("titleC")
					.content("contentC")
					.thumbnailName("thumbnailC")
					.storedThumbnailPath("pathC")
					.series(series)
					.build();

				series.addPost(postA);
				series.addPost(postB);
				series.addPost(postC);

				List<Long> ids = new LinkedList<Long>();
				ids.add(fakePostAId);
				ids.add(fakePostBId);
				ids.add(fakePostCId);
				List<PostEntity> posts = new LinkedList<>();
				posts.add(postA);
				posts.add(postB);
				posts.add(postC);

				PageRequest pageRequest = PageRequest.of(0, 10);
				//mock
				given(querydslPostRepository.findIdsBySeriesIdDesc(fakeId, pageRequest))
					.willReturn(ids);
				given(querydslPostRepository.findByIdsJoinWithSeries(ids))
					.willReturn(posts);
				//when
				SeriesDetailSearchDto seriesDetailSearchDto
					= seriesService.getDetailSeriesInfoOrderByDesc(series.getId(), 0);
				// then
				assertThat(seriesDetailSearchDto.getSeriesId()).isEqualTo(series.getId());
				assertThat(seriesDetailSearchDto.getPagingInfo().getElementSize()).isEqualTo(posts.size());
				assertThat(seriesDetailSearchDto.getPostsInfo().size()).isEqualTo(posts.size());
			}

		}
	}

}