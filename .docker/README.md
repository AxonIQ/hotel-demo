# Docker (Compose) deployment

Deployment on local docker daemon, with `docker-compose`

## Build application and Docker image to local Docker Daemon

[Jib](https://github.com/GoogleContainerTools/jib) builds optimized Docker and OCI images for your Java applications without a Docker daemon

```bash
mvn clean verify jib:dockerBuild
```

## `Standard` configuration

You can run the following command to start your application(s) with `standard` (Axon Server SE) edition:

```bash
docker-compose -f .docker/docker-compose.yml up -d
```

#### Delete `Standard` configuration

```bash
docker-compose -f .docker/docker-compose.yml down -v
```

**For more advanced container orchestration check [.k18](../.k8s/README.md)**

---

## application

http://localhost:8080

## axon server

http://localhost:8024
