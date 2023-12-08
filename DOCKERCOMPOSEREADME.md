# we use version 2 of docker-compose
version: '2'

# two services : the app (SpringbootRestPostgres app) and db (postgresql image from docker hub)
services:
# app config
  app:
# image name
    image: myspringbootcompose:latest		
# build the image using Dockerfile
    build:
      context: .
# container name
    container_name: app
# app depends on db
    depends_on:
      - db
# environment settings as found previously in application.properties
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/testdb                            # use table as described in environment of db port 5432
      - SPRING_DATASOURCE_USERNAME=postgres                                               # username
      - SPRING_DATASOURCE_PASSWORD=123                                                    # password
      - SPRING_LIQUIBASE_CHANGE_LOG=classpath:db/changelog/db.changelog-master.yaml       # liquibase settings
      - SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect   # hibernate
      - SPRING_JPA_HIBERNATE_DDL_AUTO=none                                                # DDL happens using liquibase  
      - MANAGEMENT_SERVER_PORT=8081                                                       # for the actuator
      - MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=*

# db config
  db:
# image name (comes from Docker Hub)
    image: 'postgres:13.1-alpine'
# container name
    container_name: db
# environment vars
    environment:
      - POSTGRES_USER=postgres    # root user
      - POSTGRES_PASSWORD=123     # password
      - POSTGRES_DB=testdb        # create this table