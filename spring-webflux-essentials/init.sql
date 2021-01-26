create schema anime;

create table anime.anime
(
    id serial not null,
    name varchar not null
);

create unique index anime_id_uindex
    on anime.anime (id);

alter table anime.anime
    add constraint anime_pk
        primary key (id);


INSERT INTO anime.anime (id, name) VALUES (DEFAULT, 'Naruto');

create table anime.devdojo_user
(
    id serial not null,
    name varchar(255) not null,
    username varchar(100) not null,
    password varchar(150) not null,
    authorities varchar(150) not null
);

create unique index devdojo_user_id_uindex
    on anime.devdojo_user (id);

alter table anime.devdojo_user
    add constraint devdojo_user_pk
        primary key (id);

INSERT INTO anime.devdojo_user (name, username, password, authorities) VALUES ('Nelson Rodrigues', 'nelson', '{bcrypt}$2a$10$hCtC/ZVDQ7cJjUVPo6vUeeqQ7avyw6tns9Zkj8FK.bY0RqSmjdpFy', 'ROLE_USER,ROLE_ADMIN');
INSERT INTO anime.devdojo_user (name, username, password, authorities) VALUES ('User', 'user', '{bcrypt}$2a$10$hCtC/ZVDQ7cJjUVPo6vUeeqQ7avyw6tns9Zkj8FK.bY0RqSmjdpFy', 'ROLE_USER');
