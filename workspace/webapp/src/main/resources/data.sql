insert into users (username, password, enabled) values ('user', 'user', true);
insert into users (username, password, enabled) values ('jw', 'jw', true);
insert into users (username, password, enabled) values ('j', 'j', true);
insert into users (username, password, enabled) values ('autogen', 'autogen', true);

insert into authorities (username, authority) values ('user', 'ROLE_ADMIN');
insert into authorities (username, authority) values ('jw', 'ROLE_ADMIN');
insert into authorities (username, authority) values ('j', 'ROLE_ADMIN');
insert into authorities (username, authority) values ('autogen', 'ROLE_ADMIN');
