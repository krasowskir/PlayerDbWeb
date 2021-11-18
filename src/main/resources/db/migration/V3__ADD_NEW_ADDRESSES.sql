create table addresses (
    id serial,
    city varchar(255),
    street varchar(255),
    plz varchar(255),
    country varchar(255),
    primary key (id)
);

insert into addresses values (default, 'Dresden', 'Torgauer Straße 30', '01139', 'GERMANY');
insert into addresses values (default, 'Berlin', 'Düsseldorfer Straße 59', '10707', 'GERMANY');
insert into addresses values (default, 'Altenberg', 'Walter-Richter-Straße 4', '01027', 'GERMANY');