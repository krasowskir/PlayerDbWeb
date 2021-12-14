create table trainers (
    id serial primary key,
    name varchar(255),
    age integer
);

insert into trainers values (default, 'julian nagelsmann', 34);
insert into trainers values (default, 'marco rose', 45);
insert into trainers values (default, 'jose mourinho', 58);
insert into trainers values (default, 'xavi hernandez', 41);
insert into trainers values (default, 'Mauricio Roberto Pochettino Trossero', 49);

create table teams (
    id serial primary key,
    name varchar(255),
    budget integer,
    logo bytea,
    owner varchar(255),
    trophy_score integer
);

insert into teams values (default, 'FC Bayern München', 100000000, null, 'Oliver Kahn', 9);
insert into teams values (default, 'Futbol Club Barcelona', 90000000, null, 'Joan Laporta', 8);
insert into teams values (default, 'Borussia Dortmund', 50000000, null, 'Hans-Joachim Watzke', 4);
insert into teams values (default, 'Paris Saint-Germain', 200000000, null, 'Nasser Al-Khelaifi', 4);


create table coaches (
    trainerId integer,
    teamId integer,
    since date not null,
    constraint C_FOREIGN_TRAINER foreign key (trainerId) references trainers (id) on delete cascade,
    primary key (trainerId, teamId)
);


