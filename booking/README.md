# Booking

```shell script
cd booking/
mvn spring-boot:run
```

- UI: [http://localhost:8080](http://localhost:8080)
- Swagger specification of REST/HTTP endpoints: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## RSocket

Download the [RSocket Client CLI](https://github.com/making/rsc) by Toshiaki Maki into the root folder. There is an [official RSocket CLI](https://github.com/rsocket/rsocket-cli) elsewhere, but Toshiaki’s is a little easier to use.
In the terminal, download the JAR file as follows:

```shell script
wget -O rsc.jar https://github.com/making/rsc/releases/download/0.4.2/rsc-0.4.2.jar
```

### Interaction models

- `Request-Response` — send one message and receive one back.
- `Request-Stream` — send one message and receive a stream of messages back.
- `Channel` — send streams of messages in both directions.
- `Fire-and-Forget` — send a one-way message.

#### Account

##### Register Account - (Request-Response) - `accounts.register`

```shell script
java -jar rsc.jar --request --data "{\"userName\":\"john\",\"password\":\"drowssap\"}" --route accounts.register ws://localhost:7000/rsocket

085081c1-44ef-441e-bee7-8ae274204d51
```

##### Get all Accounts - (Request-Response) - `accounts.all`

```shell script
java -jar rsc.jar --request  --route accounts.all ws://localhost:7000/rsocket
```

##### Subscribe to all Accounts - (Request-Stream) - `accounts.all`

```shell script
java -jar rsc.jar --stream  --route accounts.all ws://localhost:7000/rsocket
```

##### Get an Account - (Request-Response) - `accounts.{accountId}.get`

```shell script
java -jar rsc.jar --request  --route accounts.085081c1-44ef-441e-bee7-8ae274204d51.get ws://localhost:7000/rsocket
```

##### Subscribe to an Account - (Request-Stream) - `accounts.{accountId}.get`

```shell script
java -jar rsc.jar --stream  --route accounts.085081c1-44ef-441e-bee7-8ae274204d51.get ws://localhost:7000/rsocket
```

#### Room

##### Add Room - (Request-Response) - `rooms.add`

```shell script
java -jar rsc.jar --request --data "{\"roomNumber\":\"1\",\"description\":\"Blue Room\"}" --route rooms.add ws://localhost:7000/rsocket

1
```

##### Book Room - (Request-Response) - `rooms.{roomId}.book`

```shell script
java -jar rsc.jar --request --data "{\"startDate\":\"2020-05-17T14:52:13.844Z\",\"endDate\":\"2020-06-17T22:56:59.844Z\",\"accountID\":\"085081c1-44ef-441e-bee7-8ae274204d51\"}" --route rooms.1.book ws://localhost:7000/rsocket
```

##### Mark Room as prepared - (Request-Response) - `rooms.{roomId}.markprepared`

```shell script
java -jar rsc.jar --request --data "{\"bookingId\":\"2cf59dfd-1afe-4b66-9486-e2e0e1b3c234\"}" --route rooms.1.markprepared ws://localhost:7000/rsocket
```

##### Check In Room - (Request-Response) - `rooms.{roomId}.checkin`

```shell script
java -jar rsc.jar --request --data "{\"bookingId\":\"2cf59dfd-1afe-4b66-9486-e2e0e1b3c234\"}" --route rooms.1.checkin ws://localhost:7000/rsocket
```

##### Check Out Room - (Request-Response) - `rooms.{roomId}.checkout`

```shell script
java -jar rsc.jar --request --data "{\"bookingId\":\"2cf59dfd-1afe-4b66-9486-e2e0e1b3c234\"}" --route rooms.1.checkout ws://localhost:7000/rsocket
```

##### Get Room Availability - (Request-Response) - `rooms.{roomId}.account.{accountId}.availability`

```shell script
java -jar rsc.jar --request --route rooms.1.account.085081c1-44ef-441e-bee7-8ae274204d51.availability ws://localhost:7000/rsocket
```

##### Subscribe to Room Availability - (Request-Stream) - `rooms.{roomId}.account.{accountId}.availability`

```shell script
java -jar rsc.jar --stream --route rooms.1.account.085081c1-44ef-441e-bee7-8ae274204d51.availability ws://localhost:7000/rsocket
```

##### Get Rooms Cleaning Schedule - (Request-Response) - `rooms.cleaningschedule`

```shell script
java -jar rsc.jar --request --route rooms.cleaningschedule ws://localhost:7000/rsocket
```

##### Subscribe to Rooms Cleaning Schedule - (Request-Response) - `rooms.cleaningschedule`

```shell script
java -jar rsc.jar --stream --route rooms.cleaningschedule ws://localhost:7000/rsocket
```

#### Payment

##### Pay (Request-Response) - `payments.pay`

```shell script
java -jar rsc.jar --request --data "{\"accountId\":\"085081c1-44ef-441e-bee7-8ae274204d51\",\"totalAmount\":\"600\"}" --route payments.pay ws://localhost:7000/rsocket

e32234b2-5aed-440f-8919-d50377c24086
```

##### Process Payment (Request-Response) - `payments.{paymentId}.process`

```shell script
java -jar rsc.jar --request  --route payments.e32234b2-5aed-440f-8919-d50377c24086.process ws://localhost:7000/rsocket
```

##### Get all Payments - (Request-Response) - `payments.all`

```shell script
java -jar rsc.jar --request --route payments.all ws://localhost:7000/rsocket
```

##### Subscribe to all Payments - (Request-Streeam) - `payments.all`

```shell script
java -jar rsc.jar --stream --route payments.all ws://localhost:7000/rsocket
```

##### Get the Payment - (Request-Response) - `payments.{paymentId}.get`

```shell script
java -jar rsc.jar --request --route payments.e32234b2-5aed-440f-8919-d50377c24086.get ws://localhost:7000/rsocket
```

##### Subscribe to Payment - (Request-Stream) - `payments.{paymentId}.get`

```shell script
java -jar rsc.jar --stream --route payments.e32234b2-5aed-440f-8919-d50377c24086.get ws://localhost:7000/rsocket
```

##### Get Payments for Account - (Request-Response) - `payments.account.{accountId}.get`

```shell script
java -jar rsc.jar --request --route payments.account.085081c1-44ef-441e-bee7-8ae274204d51.get ws://localhost:7000/rsocket
```

##### Subscribe to Payments for Account - (Request-Stream) - `payments.account.{accountId}.get`

```shell script
java -jar rsc.jar --stream --route payments.account.085081c1-44ef-441e-bee7-8ae274204d51.get ws://localhost:7000/rsocket
```
