# Kafka extension workshop

## Requirements

Make sure you have the following tools available:
- Java 11+ installed
- Maven 3+ installed
- JAVA IDE of choice
- Docker and Docker Compose (included in Docker Desktop)

## Introduction

For this workshop we will use the booking application. Be sure to read the [README](README.md) if you are not yet familiar.
For the booking application we are going to build an integration with a promotion service.
The promotion service uses [Apache Kafka](https://kafka.apache.org/) to integrate with other applications.
As message format it uses [Cloud Events](https://cloudevents.io/).
Specifically for this assignment the promo service will produce a `PromoBookingAttempt` and expects either a `PromoBookingSucceeded` or `PromoBookingFailed` with the same `promoBookingId` as reply.
All the classes for the interaction can be found in the Kotlin [events.kt](promo-api/src/main/kotlin/io/axoniq/demo/hotel/promo/events.kt) file.
Once the preparation is done, the tasks can be done in order.
In case you run into trouble you can check the [kafka_extension_solutions](https://github.com/AxonIQ/hotel-demo/tree/kafka_extension_solutions) branch for the solutions, each task is in a separate commit.

## Preparation

Before we can build the services we need, we first need to install the APIs to our local maven repo.
- For the booking-api, execute `mvn -f booking-api clean install`.
- For the promo-api, execute `mvn -f promo-api clean install`.

In order to build the integration service we need the following thing to be running:
- Axon Server, easy to set up with the [docker compose file](docker-compose.yml). It should be accessible on `localhost:8124`, the ui is available at `localhost:8024` which is useful to search events, and see which applications are connected.
- Kafka broker, or something with a similar API. Easiest with the [docker compose file](docker-compose.yml), which uses [Redpanda](https://redpanda.com/). It should be accessible on `localhost:9092`.
- The booking service, with the `demo` profile, this will create 20 rooms, which are also used in the promo mock application. The easiest is to use the IntelliJ run configuration. But it can also be started using `mvn spring-boot:run -Dspring-boot.run.profiles=demo` from the `/booking` folder.
- The promo mock. This doesn't use Spring Boot and can be started either from the IntelliJ run configuration, or with `mvn compile exec:java -Dexec.mainClass="io.axoniq.demo.hotel.promo_mock.PromoMockApplication"` from the `promo-mock` folder.

In case of unexpected behaviour be sure to check the logs. In some cases it might be convenient to tear it all down and start with a clean state again.

## Tasks

### 1. Getting familiar with the integration application

In the `/integration` folder you can find the start of the integration application.
You can start it from the IntelliJ run configuration, or running `mvn spring-boot:run` as it's a Spring Boot program.

As it uses the [Kafka extension](https://github.com/AxonFramework/extension-kafka) with the `axon-kafka-spring-boot-starter` included as dependency, most of the beans will be auto created.
At this point we did not set any topics, so the default `Axon.Events` will be used. Both the `publisher`, reading events from Axon server and publishing to Kafka, as the `fetcher`, reading events from Kafka using the [`PromoEventProcessor`](/integration/src/main/java/io/axoniq/demo/hotel/promo_integration/event/PromoEventProcessor.java) are enabled via the [application.yml](/integration/src/main/resources/application.yml).

Some important details:
- The `fetcher` will put the axon events on Kafka as [CloudEvents](https://cloudevents.io/). From the booking demo this should be events like ['RoomBookedEvent'](/booking-api/src/main/kotlin/io/axoniq/demo/hotel/booking/command/api/events.kt).
- The events are read by the `publisher` which will then log them, if no events are logged, check if all the pieces are running well, especially Axon Server and the booking service.
- Since [Jaeger](https://www.jaegertracing.io/) is used, which adds `uber-trace-id` to the metadata which is not compatible with Cloud Event extension names, a mapping was added in [`MessageConverterConfig`](/integration/src/main/java/io/axoniq/demo/hotel/promo_integration/configuration/MessageConverterConfig.java).

### 2. Connect to the correct Kafka topics

It's time to get our hands dirty by changing the Kafka topics to read and write from.
While we could change the default by setting the `axon.kafka.default-topic` property, since we need a different topic for the fetcher and the publisher, we both set them using beans.
We can use the [`TopicConstants`](promo-api/src/main/kotlin/io/axoniq/demo/hotel/promo/TopicConstants.kt) fo the correct values.

#### Fetcher

For the fetcher we still only log the events, so we can change the topic without the need to change anything else.
To have an easy start take a look at [`FetcherConfig`](/integration/src/main/java/io/axoniq/demo/hotel/promo_integration/configuration/FetcherConfig.java).
make sure it's always used independent of the profile.

#### Publisher

For the publisher we can already set the `topicResolver` property on the `KafkaPublisher` to only publish the `PromoBookingSucceeded` or `PromoBookingFailed` events.
you can copy the `KafkaPublisher` bean from [github](https://github.com/AxonFramework/extension-kafka/blob/248ca36450a8b4f727addefcbc1aa1767721ee2c/kafka-spring-boot-autoconfigure/src/main/java/org/axonframework/extensions/kafka/autoconfig/KafkaAutoConfiguration.java#L154) to have a start.

Once started after these changes you should see the events from the promo mock being logged.

### 3. Process the events correctly

We need to change the [`PromoEventProcessor`](/integration/src/main/java/io/axoniq/demo/hotel/promo_integration/event/PromoEventProcessor.java) to listen to `PromoBookingAttempt` events, and correctly handle them.
By adding a `QueryGateway`, or the `ReactorQueryGateway` like in the booking service, we are able to find which of the rooms from `PromoBookingAttempt` is still available.
You are free to pick the times of the check-in and check-out when transforming the `BookingDate` to `Instant`, or use the `asCheckIn` and `asCheckOut` functions on `BookingDate`.
Once you have a room that's in the range and has free space, you can try a `BookRoomCommand`, you could just set a static `UUID` for the 'bookingId'
You can now add a `CommandGateway`, or the `ReactorCommandGateway` to send the command, be sure to add the `promoBookingId` as metadata.
The last part should look similar to:
```java
commandGateway.send(command, MetaData.with("promobookingid", event.getPromoBookingId()));
```

### 4. Make sure the publisher publishes events

We would like to get the actual feedback to the promo booking service, but we still have no `PromoBookingSucceeded` or `PromoBookingFailed` events.
One easy, but not good way to do this, is to apply the `PromoBookingSucceeded` or `PromoBookingFailed` events in the [`Room`](/booking/src/main/java/io/axoniq/demo/hotel/booking/command/Room.java) aggregate in the booking service.
We can extract the metadata value using `@MetaDataValue("promobookingid") UUID promoBookingId` as second argument in the handler.
This does pollute the aggregate. A better way could be to pass the promoBookingId to the `RoomBookedEvent` and `RoomBookingRejectedEvent` and mrp those in an event handler, before sending the event to Kafka.
However, for now this was a fast way to complete the integration.

If everything went well, we should observe from the promo mock logging that there are not many request pending for reply.
We might need to reset everything so that previous errors or incomplete things are not in the way