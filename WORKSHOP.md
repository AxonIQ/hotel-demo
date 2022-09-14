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

### Start Axon Server EE in Docker

Because a context per tenant will be created,  Axon Server EE or an AxonIQ Cloud account is needed to do this workshop.
When using Axon Server EE, you need to start local instances of AxonServer. 
An Axon Server EE docker compose file is available in `/axonserver-ee` folder.

Steps to start a cluster of 3 nodes:

* Add the license file to axonserver-ee folder
* Run the create-secrets script (to secure the cluster and generate an admin password)
* Start Docker with `docker compose up` and wait for the cluster to be up
* Run the create-admin script to create the admin user

Now, you should be able to check the dashboard by visiting: `http://localhost:8024` when logged with admin user, the generated password can be found in the axonserver-ee folder in the `admin.password` file. In the overview page you should see a cluster of 3 nodes.

You can more info [here](axonserver-ee/README.md)

### Build the workshop project
Run `mvn install` command inside the root folder of the workshop project.
In this way maven will download all needed libraries into your local repository.

## Game on

Now you are ready to start the labs.
The labs need to be executed in the specified order.
Read the lab description before to start working on that.
It will provide you some hints about how to achieve the lab goal.

For your convenience, you can use the [command api file](booking/src/main/resources/command-api.http) or the [query api file](booking/src/main/resources/query-api.http) to simplify the invocation of the endpoint for the specific lab.
If you are not using IntelliJ, you can invoke the endpoints with a tool of choice (postman, curl, ...) of build and use the frontend application.

If you are stuck, you can glance at the next lab code for some inspirations.
It contains a possible solution of the previous one.

### Lab 1 Explore the Hotel booking application

During this workshop we take the hotel demo booking application as an example and make it multi tenant. This means that one application can serve more than one tenant using separate event stores.
Run the HotelBooking application and use the api files or the frontend to explore the application.

### Lab 2 Add multiple contexts

The next step is to configure multi tenancy for the hotel booking application.
Add the `multi-tenancy extension` to the `pom.xml`.
The application now uses 1 context named `booking`. When making the application multi tenant the context name will be the branch prepended with `booking-` (for instance booking-hilton).
Adding these contexts can be done statically by adding them in AxonServer and adding the context name(s) to the application.properties `axon.axonserver.context` property (this property takes a list). The booking context can be removed.
Another way is to add new contexts dynamically (during runtime) by adding a `TenantConnectPredicate` as a bean. ***Note*** you need to remove the `axon.axonserver.context` property.

Try to configure the contexts in both ways, in AxonConfig a `TenantConnectPredicate` is already defined you just need to add a correct Predicate.

### Lab 3 Route messages to specific tenants

To route messages to the correct tenant they should be enriched with a tenant specific correlation id. The attribute name is in the TenantConfiguration.TENANT_CORRELATION_KEY.
All you need to do is add it to the MetaData of the **initial** message like this:
```
commandGateway.send(new BookRoomCommand(roomNumber, new RoomBooking(roomBookingData.getStartDate(), roomBookingData.getEndDate(), roomBookingData.getAccountID())), MetaData.with(TenantConfiguration.TENANT_CORRELATION_KEY, "booking-hilton"));
```
The event will inherit the metadata and belong to the booking-hilton context.

Add this correlation id to all the commands in this module and send some commands to test the behavior.

Browse event stores to see the events in separate contexts.

### Lab 4 Multi tenant projections

Now that the event stores are separated we also need to separate the projections. You can create a datasource per tenant:

```java
public class AxonConfig {
    @Bean
    public Function<TenantDescriptor, DataSourceProperties> tenantDataSourceResolver() {
        return tenant -> {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setUrl("jdbc:h2:mem:axoniq-booking-"+tenant.tenantId());
            return properties;
        };
    }
}


```

## Congratulations

Great job! You completed all the labs!
You can experiment further if you like.

We hope you enjoyed this workshop. Have fun using multi tenancy with Axon.
