-- 유저--
insert into users(id, username, email, password)
values (999999, 'test', 'test@gmail.com', '$2a$10$mLXRFrihpPDDc/iKr/Sqz.pcl6zqz45dyNCWhL8sCeZYo.jcZj1CW');
-- 포스트 --
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description) --포스트 1번, 후순위--
values (999999, 999999, 'testTitle', 'testContent2', 0, FORMATDATETIME('2023-05-06 00:00:00', 'yyyy-MM-dd'), true,
        'description1');
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description) --포스트 2번--
values (1000000, 999999, 'testTitle2', 'testContent2', 100, FORMATDATETIME('2023-05-06 00:00:01', 'yyyy-MM-dd'), true,
        'description2');
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description) --포스트 3번, 2번보다 오래전--
values (1000001, 999999, 'testTitle3', 'testContent2', 100, FORMATDATETIME('2023-04-06 00:00:00', 'yyyy-MM-dd'), true,
        'description3');
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description) --포스트 4번--
values (1000002, 999999, 'testTitle4', 'testContent2', 200, FORMATDATETIME('2023-04-06 00:00:01', 'yyyy-MM-dd'), true,
        'description4');
insert into posts(id, user_id, title, content, view_count, created_at, is_public, description) --포스트 5번--
values (1000003, 999999, 'testTitle5', 'testContent2', 5000, FORMATDATETIME('2023-04-06 00:00:02', 'yyyy-MM-dd'), true,
        'description5');
-- 댓글 --
insert into comments(id, user_id, post_id, content, depth) --부모 댓글, 포스트 1번--
values (999999, 999999, 999999, 'testParentComment', 0);
insert into comments(id, user_id, post_id, content, depth, parent_id) --자식 댓글, 포스트 1번--
values (9999999, 999999, 999999, 'testChildComment', 1, 999999);
-- 좋아요 --
insert into likes(id, post_id, user_id)
values (999999, 999999, 999999);
insert into likes(id, post_id, user_id)
values (1000010, 999999, 999999);
insert into likes(id, post_id, user_id)
values (1000009, 999999, 999999);
insert into likes(id, post_id, user_id)
values (1000000, 1000000, 999999);
insert into likes(id, post_id, user_id)
values (1000001, 1000001, 999999);
insert into likes(id, post_id, user_id)
values (1000002, 1000002, 999999);
insert into likes(id, post_id, user_id)
values (1000003, 1000002, 999999);
insert into likes(id, post_id, user_id)
values (1000004, 1000002, 999999);
insert into likes(id, post_id, user_id)
values (1000005, 1000003, 999999);
insert into likes(id, post_id, user_id)
values (1000006, 1000003, 999999);
insert into likes(id, post_id, user_id)
values (1000007, 1000003, 999999);
insert into likes(id, post_id, user_id)
values (1000008, 1000003, 999999);
