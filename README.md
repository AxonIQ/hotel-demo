# AxonIQ hotel demo

It's the implementation of the example used in this blog post: [https://eventmodeling.org/posts/what-is-event-modeling/](https://eventmodeling.org/posts/what-is-event-modeling)

Event modeling adopts Event Storming sticky notes. The final piece was the UI/UX aspects to complete what more resembles a movie story board (white board - or digital white board). While Event Storming focuses in discovering the problem space, Event Modeling creates a blueprint for a solution.

[Download specification](demo.pdf)

## Prerequisites

- Java 11

## Running the application(s) locally

**Requirements**

> You can [download](https://download.axoniq.io/axonserver/AxonServer.zip) a ZIP file with AxonServer as a standalone JAR. This will also give you the AxonServer CLI and information on how to run and configure the server.
>
> Alternatively, you can run the following command to start AxonServer in a Docker container:
>
> ```
> docker run -d --name axonserver -p 8024:8024 -p 8124:8124 axoniq/axonserver
> ```

```shell script
mvn clean verify
```

### Booking

```shell script
cd booking/
mvn spring-boot:run
```

- UI: [http://localhost:8080](http://localhost:8080)
- Swagger specification of REST/HTTP endpoints: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- RSocket [readme](booking/README.md)

### Inventory

```shell script
cd inventory/
mvn spring-boot:run
```

- UI: [http://localhost:8081](http://localhost:8081)
- Swagger specification of REST/HTTP endpoints: [http://localhost:8081/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Running all on Kubernetes

Deployment on Kubernetes (CaaS) is documented in [.k8s/Readme.md](./.k8s/README.md).
It demonstrates the usage of Axon Server EE in a multi-context setup with access control enabled. If you want to actually run Axon Server EE you need a valid license file.

---

Created with :heart: by [AxonIQ](http://axoniq.io)

[axon]: https://axoniq.io/
