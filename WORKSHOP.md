# Multi tenancy workshop

Welcome to the multi tenancy workshop

In this workshop you will learn how to use one application for multiple tenants.

## Why should I use the multi-tenancy option?

Multi-tenancy is important in cloud computing and during this workshop you will learn how to connect to tenants dynamically, separate data per tenant , and how you can scale these tenants independently

## Let's start
Before you start, please make sure you have everything you need.

### Pre requirements
Make sure you have the following tools available:
- Java 8+ installed
- Maven 3+ installed
- JAVA IDE of choice
- Temporary licence file for Axon Server EE (ask an AxonIQ staff member) or an [AxonIQ Cloud](https://console.cloud.axoniq.io/) account.
- Docker and Docker Compose (included in Docker Desktop).

### Start Axon Server EE in Docker

Because a context per tenant will be created,  Axon Server EE or an AxonIQ Cloud account is needed to do this workshop.
When using Axon Server EE, you need to start local instances of AxonServer. 
An Axon Server EE docker compose file is available in `/axonserver-ee` folder.

Steps to start a cluster of 3 nodes:

* Add the license file to axonserver-ee folder
* Start Docker with `docker compose up` and wait for the cluster to be up (depending on how docker is installed you might need `docker-compose up`, add `-d` to run containers in the background)
* Use `admin` user with `admin` password

Now, you should be able to check the dashboard by visiting: `http://localhost:8024` when logged with admin user, the generated password can be found in the axonserver-ee folder in the `admin.password` file. In the overview page you should see a cluster of 3 nodes.

You can more info [here](axonserver-ee/README.md)

### Build the workshop project
Run `mvn compile -Dskip.npm -Dskip.npx -Dskip.installnodenpm` command inside the root folder of the workshop project.
In this way maven will download all needed libraries into your local repository. Just `mvn compile` if you also want to build the frontend, it's optional for this workshop.

## Game on

Now you are ready to start the labs.
The labs need to be executed in the specified order.
Read the lab description before to start working on that.
It will provide you some hints about how to achieve the lab goal.

For your convenience, you can use the [command api file](booking/src/main/resources/command-multi-tenant-api.http) to simplify the invocation of the endpoint for the specific lab.
For the last labs endpoints from [devops api file](booking/src/main/resources/devops-api.http) are used instead.
If you are not using IntelliJ, you can invoke the endpoints with a tool of choice (postman, curl, ...) of build and use the frontend application.

If you are stuck, you can glance at [workshop-solution](/workshop-solution) for inspiration, or take a look at the [README](https://github.com/AxonFramework/extension-multitenancy/blob/main/README.md) of the extension.
It contains a possible solution of the previous one.

### Lab 1 Explore the Hotel booking application

During this workshop we take the hotel demo booking application as an example and make it multi tenant. This means that one application can serve more than one tenant using separate event stores.
Run the HotelBooking application and use the api files or the frontend to explore the application.

### Lab 2 Add multiple contexts

The next step is to configure multi tenancy for the hotel booking application.
The `multi-tenancy extension` was already added to the `pom.xml`.
The application now uses 1 context. When making the application multi tenant the context name will be the branch of the hotel prepended with `booking-` (for instance booking-hilton).
Go to the AxonServer dashboard and create a contexts with a name `booking-hilton` also add an application and save the token in the application.properties (in `axon.axonserver.token`) . ***Important note*** this application should have `USE_CONTEXT` rights for the `booking-hilton` context and `VIEW_CONFIGURATION` rights for the _admin context.
Add new contexts dynamically (during runtime) by adding a `TenantConnectPredicate` as a bean. You should not have the `axon.axonserver.context` configured property if you choose this option.
You can configure this in [`MultiTenancyConfig`](/booking/src/main/java/io/axoniq/demo/hotel/booking/command/config/MultiTenancyConfig.java). You can see a `TenantConnectPredicate` is already defined you just need to add a correct Predicate.

Create another context e.g. `booking-sheraton`, update the existing application to use this context, after a restart of the app you can [see](http://localhost:8024/#overview) that the app automatically connects to the correct contexts.
Double-check the permissions on the application in case you don't see it connected to all three contexts it should be connected to.

### Lab 3 Route messages to specific tenants

We prepared `RoomCommandMultiTenantController` for so,  you can easily add multi tenancy behavior.
To route messages to the correct tenant the metadata should contain with a tenant specific correlation id. The attribute name is in the TenantConfiguration.TENANT_CORRELATION_KEY.
Add this correlation id to all the commands in this controller and send some commands to test the behavior.
The event will inherit the metadata and belong to the booking-hilton context.

Browse event stores to see the events in separate contexts.

### Lab 4 Multi tenant projections

Now that the event stores are separated we also may want to separate the projections and token stores per tenant.
You can define H2 datasource per tenant:

```java
    @Bean
    public Function<TenantDescriptor, DataSourceProperties> tenantDataSourceResolver() {
        return tenant -> {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl("jdbc:h2:mem:"+tenant.tenantId());
            properties.setDriverClassName("org.h2.Driver");
            properties.setUsername("sa");
            return properties;
        };
    }
```

Run the application and see if the correct projections are updated.

Use [H2 console](http://localhost:8082/) to verify that projections are separated.

Check out how [http://localhost:8080/admin/create-tenant](http://localhost:8080/admin/create-tenant) can help you. It's configured in the `DevOpsController` class. **Important note** the application needs the `CONTEXT_ADMIN` for the `_admin` context for this to work.

### Lab 5 Reset projections

Each tenant has its own event processing group which makes it possible to reset a single tenant. The name of the event processor consists of the processor group name and the tenant name divided by `@`:`processinggroupname@tenantname`
Adjust the [DevopsController](booking/src/main/java/io/axoniq/demo/hotel/booking/management/DevOpsController.java) to be able to reset 1 tenant and all tenants.

## Congratulations

Great job! You completed all the labs!
You can experiment further if you like.

We hope you enjoyed this workshop. Have fun using multi tenancy with Axon.
