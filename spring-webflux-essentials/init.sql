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