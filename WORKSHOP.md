# Parameter resolver workshop

Welcome to the parameter resolver workshop

In this workshop you will learn how to apply parameter resolvers.

## Why should I use a parameter resolver?

When using message handlers you can get into the situation that you need to do a lot of code duplication. For instance when you need to check if the user is authorized to apply a command. Or, event handlers that update a table, when getting the entity. You can do this check or fetch the entity in a parameter resolver and add it to the signature of any message handling function.

## Let's start
Before you start, please make sure you have everything you need.

### Pre requirements
Make sure you have the following tools available:
- Java 11+ installed
- Maven 3+ installed
- JAVA IDE of choice

### Start Axon Server 

First you need a running instance of AxonServer. [Here](README.md) you can find more information on how to start AxonServer. 

After starting AxonServer, you should be able to check the dashboard by visiting: `http://localhost:8024`

### Build the workshop project
Run `mvn install` command inside the root folder of the workshop project.
In this way maven will download all needed library into your local repository.

## Game on

Now you are ready to start the labs.
The labs need to be executed in the specified order.
Read the lab description before to start working on it.
It will provide you some hint about how to achieve the lab goal.

For your convenience, in each module you can find a http file to simplify the invocation of the endpoint for the specific lab.
If you are not using IntelliJ, you can invoke the endpoints with a tool of choice (postman, curl, ...).

The hotel demo project is a project to show the capabilities of Axon Framework and AxonServer. The UI consists of 2 flows. The first one is for the hotel manager and the other one is a flow for guests.

A manager can add rooms, see the overall availability, check out, see payments and cleaning schedule. A guest can book a room, check in and pay.

### Lab 1 Create a parameter resolver
Run the HotelBooking application and use the [http request file](room-booking.http) or use the frontend to explore the application.

Looking at the `RoomAvailabilityHandler`  you can see that the `RoomAvailabilityEntityRepository` is defined. Everytime this repository is updated the entity is fetched by calling `roomAvailabilityEntityRepository.getById(event.getRoomNumber())`. 
The goal of this workshop is to create a parameter resolver that fetches this entity beforehand by using a parameter resolver.

Add a component that `implements ParameterResolver<RoomAvailabilityEntity>, ParameterResolverFactory`. **(tip)** You need to Autowire the constructor of the component to make it being picked up by Spring.
- Implement the `public boolean matches(Message<?> message)` method, matching event messages.
- Implement the `public ParameterResolver createInstance(Executable executable, Parameter[] parameters, int i)` method to create an instance when the `RoomAvailabilityEntity` is a parameter in the method signature.
- Implement the `public RoomAvailabilityEntity resolveParameterValue(Message message)` method that checks if the message matches (the first function that was implemented) and returns the`getById()` for the roomId of that message.


### Lab 2 Use the parameter resolver

The next step is to configure the parameter resolver for the hotel booking application. 
In the `RoomAvailabilityHandler` remove all the spots where the parameter resolver can do its job and add the `RoomAvailabilityEntity` to the method signature instead.
Run the HotelBooking application and test your changes using the [http request file](room-booking.http) or the frontend.

### Lab 3 Implement your own parameter resolver

Now you can experiment on your own. You can also resolve the `RoomAvailabilityEntity` in the QueryHandlers in the `RoomAvailabilityHandler` and/or implement a parameter resolver on the command side.

Run the HotelBooking application and test your changes using the [http request file](room-booking.http) or the frontend.


## Congratulations

Great job! You completed the labs!
You can experiment further if you like.

We hope you enjoyed this workshop. Have fun using parameter resolvers with Axon.
