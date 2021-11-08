create table players (
    id serial primary key,
    first_name varchar(255),
    alter integer
);

create table teams (
    team_name varchar(255),
    budget integer,
    primary key (team_name)
);

create table belongs_to (
    player_id serial,
    team_name varchar(255),
    constraint C_PLAYER_FOREIGN foreign key (player_id) references players (id) on delete cascade,
    constraint C_TEAMS_FOREIGN foreign key (team_name) references teams (team_name) on delete cascade,
    primary key (player_id, team_name)
);

grant all privileges on table players to richard;

insert into players values(DEFAULT, 'richard', 30);
insert into players values(DEFAULT, 'lidia', 33);



