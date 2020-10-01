import React from "react";
import {
  HashRouter as Router,
  Switch,
  Route,
  useRouteMatch,
} from "react-router-dom";
import { Register } from "./views/Register/Register";
import Container from "@material-ui/core/Container";
import "./App.scss";
import { AddRoom } from "./views/AddRoom/AddRoom";
import { BookRoom } from "./views/BookRoom/BookRoom";
import { RoomsToClean } from "./views/RoomsToClean/RoomsToClean";
import { Checkin } from "./views/Checkin/Checkin";
import { Checkout } from "./views/Checkout/Checkout";
import { Payment } from "./views/Payment/Payment";
import { PickRoom } from "./views/PickRoom/PickRoom";
import { PaymentOverview } from "./views/PaymentOverview/PaymentOverview";

export const App = () => (
  <Router>
    {/* A <Switch> looks through its children <Route>s and
          renders the first one that matches the current URL. */}
    <Switch>
      <Route path="/user/:accountId">
        <div className="app__user-stripe app__user-stripe--guest">Guest</div>
        <Container className="app__container">
          <Guest />
        </Container>
      </Route>
      <Route path="/manager">
        <div className="app__user-stripe app__user-stripe--manager">
          Manager
        </div>
        <Container className="app__container">
          <Manager />
        </Container>
      </Route>
      <Route path="/">
        <Container className="app__container">
          <Register />
        </Container>
      </Route>
    </Switch>
  </Router>
);

function Manager() {
  const { path } = useRouteMatch();
  return (
    <Switch>
      <Route path={`${path}/payment-overview`}>
        <PaymentOverview />
      </Route>
      <Route path={`${path}/checkout`}>
        <Checkout />
      </Route>
      <Route path={`${path}/rooms-to-clean`}>
        <RoomsToClean />
      </Route>
      <Route path={`${path}/add-room`}>
        <AddRoom />
      </Route>
    </Switch>
  );
}

function Guest() {
  const { path } = useRouteMatch();
  return (
    <Switch>
      <Route path={`${path}/payment`}>
        <Payment />
      </Route>
      <Route path={`${path}/checkin/:roomNumber/:bookingId`}>
        <Checkin />
      </Route>
      <Route path={`${path}/book-room/:roomNumber`}>
        <BookRoom />
      </Route>
      <Route path={`${path}/pick-room`}>
        <PickRoom />
      </Route>
    </Switch>
  );
}
