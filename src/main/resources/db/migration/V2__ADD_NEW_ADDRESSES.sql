create table addresses (
    id serial,
    city varchar(255),
    street varchar(255),
    plz varchar(255),
    country varchar(255),
    primary key (id)
);

create table lives_in (
    player_id integer,
    address_id integer,
    constraint C_LIVES_PLAYER_FOREIGN foreign key (player_id) references players (id) on delete cascade,
    constraint C_LIVES_ADDRESS_FOREIGN foreign key (address_id) references addresses (id) on delete cascade,
    primary key (player_id, address_id)
);

insert into addresses values (default, 'Dresden', 'Torgauer Straße 30', '01139', 'GERMANY');
insert into addresses values (default, 'Berlin', 'Düsseldorfer Straße 59', '10707', 'GERMANY');
insert into addresses values (default, 'Altenberg', 'Walter-Richter-Straße 4', '01027', 'GERMANY');

