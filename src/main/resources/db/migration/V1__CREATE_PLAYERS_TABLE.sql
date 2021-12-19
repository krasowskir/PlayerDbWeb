create table players (
    id integer primary key,
    first_name varchar(255),
    alter integer
);

--create table teams (
--    team_name varchar(255),
--    budget integer,
--    primary key (team_name)
--);

--create table belongs_to (
--    player_id serial,
--    team_name varchar(255),
--    constraint C_PLAYER_FOREIGN foreign key (player_id) references players (id) on delete cascade,
--    constraint C_TEAMS_FOREIGN foreign key (team_name) references teams (team_name) on delete cascade,
--    primary key (player_id, team_name)
--);

GRANT ALL ON players TO richard;

ALTER TABLE PLAYERS RENAME COLUMN first_name TO name;
ALTER TABLE PLAYERS ADD COLUMN position VARCHAR(255);
ALTER TABLE PLAYERS ADD COLUMN date_of_birth DATE;
ALTER TABLE PLAYERS ADD COLUMN country_of_birth VARCHAR(255);

INSERT INTO PLAYERS VALUES(1, 'richard', 30, 'offender', '1991-06-20', 'LATVIA');
INSERT INTO PLAYERS VALUES(2, 'arnold', 30, 'onTheBank', '1991-06-20', 'LATVIA');
INSERT INTO PLAYERS VALUES(3, 'lidia', 33, 'midfield', '1988-04-30', 'LATVIA');






