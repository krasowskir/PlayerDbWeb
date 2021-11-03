create database playerdb;
create user richard with encrypted password 'test123';
grant all privileges on database playerdb to richard;

\c playerdb;

create table players (
    vorname varchar(255),
    alter integer,
    primary key (vorname)
);

grant all privileges on table players to richard;

insert into players values('richard', 30);
insert into players values('lidia', 33);