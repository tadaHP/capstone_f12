insert into users(id, username, email, password)
values (999999, 'test', 'test@gmail.com', '$2a$10$mLXRFrihpPDDc/iKr/Sqz.pcl6zqz45dyNCWhL8sCeZYo.jcZj1CW');
insert into posts(id, user_id, title, content, view_count)
values (999999, 999999, 'testTitle', 'testContent', 0);
insert into comments(id, user_id, post_id, content, depth) --부모 댓글--
values (999999, 999999, 999999, 'testParentComment', 0);
insert into comments(id, user_id, post_id, content, depth, parent_id) --자식 댓글--
values (9999999, 999999, 999999, 'testChildComment', 1, 999999);
insert into posts(id, user_id, title, content)
values (1000000, 999999, 'testTitle2', 'testContent2');
insert into posts(id, user_id, title, content)
values (1000001, 999999, 'testTitle3', 'testContent3');
insert into posts(id, user_id, title, content)
values (1000002, 999999, 'testTitle4', 'testContent4');