-- 유저--
insert into users(id, username, email, password) values (999999, 'test', 'test@gmail.com', '$2a$10$mLXRFrihpPDDc/iKr/Sqz.pcl6zqz45dyNCWhL8sCeZYo.jcZj1CW');
-- 시리즈 --
insert into series(id, user_id, title) values (999999, 999999, 'testSeries');
-- 해쉬태그 --
insert into hashtags(id, name) values (999999, 'hashtagA');
insert into hashtags(id, name) values (1000000, 'hashtagB');
insert into hashtags(id, name) values (1000001, 'hashtagC');
-- 포스트 --
--포스트 1번, 후순위--
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description, series_id) values (999999, 999999, 'testTitle', 'testContent1', 0, FORMATDATETIME('2020-05-06 00:00:00', 'yyyy-MM-dd'), true,
        'description1', 999999);
--포스트 2번--
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description, series_id) values (1000000, 999999, 'testTitle2', 'testContent2', 100, FORMATDATETIME('2023-05-06 00:01:01', 'yyyy-MM-dd'), true,
        'description2', 999999);
--포스트 3번, 2번보다 오래전--
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description, series_id) values (1000001, 999999, 'testTitle3', 'testContent3', 100, FORMATDATETIME('2023-04-06 00:00:00', 'yyyy-MM-dd'), true,
        'description3', 999999);
--포스트 4번--
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description, series_id) values (1000002, 999999, 'testTitle4', 'testContent4', 200, FORMATDATETIME('2023-04-06 00:00:01', 'yyyy-MM-dd'), true,
        'description4', 999999);
--포스트 5번--
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description, series_id) values (1000003, 999999, 'testTitle5', 'testContent2', 5000, FORMATDATETIME('2023-04-06 00:00:02', 'yyyy-MM-dd'), true,
        'description5', 999999);
-- 포스트-해쉬태그 --
insert into posts_hashtags(hashtags_id, post_id) values (999999, 999999);
insert into posts_hashtags(hashtags_id, post_id) values (1000000, 999999);
insert into posts_hashtags(hashtags_id, post_id) values (999999, 1000000);
insert into posts_hashtags(hashtags_id, post_id) values (1000001, 1000000);
insert into posts_hashtags(hashtags_id, post_id) values (999999, 1000001);
insert into posts_hashtags(hashtags_id, post_id) values (1000000, 1000001);
insert into posts_hashtags(hashtags_id, post_id) values (1000001, 1000001);

-- 댓글 --
-- insert into comments(id, user_id, post_id, content, depth) values (999999, 999999, 999999, 'testParentComment', 0); --부모 댓글, 포스트 1번--
-- insert into comments(id, user_id, post_id, content, depth, parent_id) (9999999, 999999, 999999, 'testChildComment', 1, 999999); --자식 댓글, 포스트 1번--values --
-- 좋아요 --
insert into likes(id, post_id, user_id) values (999999, 999999, 999999);
insert into likes(id, post_id, user_id) values (1000010, 999999, 999999);
insert into likes(id, post_id, user_id) values (1000009, 999999, 999999);
insert into likes(id, post_id, user_id) values (1000000, 1000000, 999999);
insert into likes(id, post_id, user_id) values (1000001, 1000001, 999999);
insert into likes(id, post_id, user_id) values (1000002, 1000002, 999999);
insert into likes(id, post_id, user_id) values (1000003, 1000002, 999999);
insert into likes(id, post_id, user_id) values (1000004, 1000002, 999999);
insert into likes(id, post_id, user_id) values (1000005, 1000003, 999999);
insert into likes(id, post_id, user_id) values (1000006, 1000003, 999999);
insert into likes(id, post_id, user_id) values (1000007, 1000003, 999999);
insert into likes(id, post_id, user_id) values (1000008, 1000003, 999999);