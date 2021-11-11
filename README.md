# PlayerDbWeb

## building the image
```
docker build -t jetty-img .
```

## removing old stuff
```
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
```


## Database

### checking current user
```
\conninfo
```

## Running...
`curl -i -X POST http://localhost:8080/playerdbweb/mein -H "Content-type: application/json" -d '{"name":"waldemar", "alter": 28 }'`