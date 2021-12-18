alter TABLE coaches ADD CONSTRAINT C_FOREIGN_TEAM FOREIGN KEY (teamId) REFERENCES teams (id) ON UPDATE CASCADE;


insert into coaches values (1, 1, to_date('01 Juli 2021', 'DD Mon YYYY'));
insert into coaches values (3, 2, to_date('01 Juni 2021', 'DD Mon YYYY'));
insert into coaches values (2, 3, to_date('01 Aug 2021', 'DD Mon YYYY'));

