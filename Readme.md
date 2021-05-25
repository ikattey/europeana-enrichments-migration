# Enrichments database migration

This Spring Batch application was written to help migrate data  between the Enrichments and Entity Management systems at Europeana.


## Requirements

* Java 11
* Postgres database (to be used as the Spring Batch JobRepository)

The following command will start a Postgres instance locally using Docker:

```
docker run -d \
-e POSTGRES_PASSWORD=s3cretPw! \
-p 5432:5432 \
postgres
```



## Setup

Create `src/main/resources/enrichments-migration.user.properties` and configure the following application settings:

- `entity-management.url`: URL for the Entity Management instance to run against
- `entities-csv.directory`: Directory containing CSV files. Requires trailing slash
- `entity-management-migration-token`: Bearer token to be included in migration requests. This is configured on the Entity Management instance.
-  Spring datasource settings :
   ```
   spring.datasource.initialization-mode=always
   spring.datasource.platform=postgres
   spring.datasource.url=jdbc:postgresql://<host>:<port>/<db>
   spring.datasource.username=<user>
   spring.datasource.password=<password>

## Run

The application has a Tomcat web server that is embedded in Spring-Boot.

Either select the `App` class in your IDE and 'run' it

or

go to the application root where the pom.xml is located and excute  
`./mvnw spring-boot:run` (Linux, Mac OS) or `mvnw.cmd spring-boot:run` (Windows)

## Logging
Logs are written to the console and also to a rolling file in `./logs` by default. This can be changed in `src/main/resources/log4j2.xml`.