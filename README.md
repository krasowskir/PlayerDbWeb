# PlayerDbWeb
This application allows you to create players, teams, addresses and games. 
It allows you to simulate different actions and changes in team constalation and players carreers.

## Use cases
- creating players
- adding players to teams
- creating addresses
- relocation of the players

## testing the application
unit + integration + packaging: ``mvn clean install``

running single test: 
```
mvn clean test -Dtest=org.richard.home.dao.PostgresPlayerDAOSpec#"a valid player can be searched in postgresPlayerDAO"
```

displaying the test results and coverage
`target/site/jacoco/index.html`

## building the image
several containers env: `docker-compose up -d --build`

single artifact: `docker build -t jetty-img .`

## removing old stuff
```
docker-compose down
docker stop jetty-cont; docker rm jetty-cont; docker rmi jetty-img
```

## running the image
```
docker-compose up -d --build
docker run -d -p 8080:8080 --name jetty-cont jetty-img
```

## Initializing of the application
The application has to be setup by importing scripts to define the db schema (ddl sql statements). For that
you should run `mvn flyway:migrate`

## analyzing the logs
```
docker logs -f jetty-cont
```

### checking the running application
```
curl -i -X GET http://localhost:8080/playerdbweb/mein
curl -i -X GET http://localhost:8080/playerdbweb/mein?player=richard
curl -i -X GET http://localhost:8080/playerdbweb/mein?alter=28
```


## Database
```
docker exec -it playerdbweb_meinedb_1 psql -U richard playerdb;
\conninfo;
\du;
```

### checking current user
```
\conninfo
```

## Running...
creating a player:
```
curl -i -X POST http://localhost:8080/playerdbweb/mein -H "Content-type: application/json" -d '{"name":"waldemar", "alter": 28 }'
```

updating a player:
```
curl -i -X PUT http://localhost:8080/playerdbweb/mein?name=lidia -H "Content-type: application/json" -d '{"name":"lidia", "alter": 28 }'
```