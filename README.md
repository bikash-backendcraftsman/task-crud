# task-crud

Small Spring Boot CRUD API used to practice a Jenkins pipeline. Tasks are stored in an in-memory H2 database.

## Run locally

Requires JDK 21.

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. The H2 console is at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:taskdb`, user `sa`, empty password).

## API

| Method | Path | Body |
| --- | --- | --- |
| `POST` | `/api/tasks` | `{ "title": "Write Jenkinsfile", "description": "optional", "status": "TODO" }` |
| `GET` | `/api/tasks` | |
| `GET` | `/api/tasks/{id}` | |
| `PUT` | `/api/tasks/{id}` | same shape as create |
| `DELETE` | `/api/tasks/{id}` | |

`status` is `TODO`, `IN_PROGRESS`, or `DONE`. If you omit it on create, the task is saved as `TODO`.

```bash
curl -s -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Wire Jenkins","status":"IN_PROGRESS"}'
```

## Tests

```bash
./mvnw test
```

## Jenkins

The `Jenkinsfile` in this folder is the pipeline. It compiles, runs tests, packages the jar, publishes JUnit results, and archives `target/*.jar`.

1. Install Jenkins and give the agent JDK 21 (`java` on `PATH`). Maven is downloaded by `./mvnw`.
2. Put this project in its own Git repository and push it somewhere Jenkins can clone.
3. Create a **Pipeline** job.
4. Under **Pipeline**, choose **Pipeline script from SCM**, point it at that repository, and set **Script Path** to `Jenkinsfile`.
5. Build the job. A green build means checkout, compile, tests, and the packaged jar all succeeded.

The next step after this pipeline is green is a deploy stage (Docker image, then run the container). The `Dockerfile` is already here for that.
