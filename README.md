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
`\conninfo`

### execute the migration
`mvn flyway:migrate`