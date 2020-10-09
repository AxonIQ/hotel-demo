/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
