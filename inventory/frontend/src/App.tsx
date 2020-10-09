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
import { CreateRoom } from "./components/CreateRoom/CreateRoom";
import { AddRoomToInventory } from "./components/AddRoomToInventory/AddRoomToInventory";
import Grid from "@material-ui/core/Grid";
import Container from "@material-ui/core/Container";

function App() {
  return (
    <Container>
      <Grid container spacing={2}>
        <Grid item lg={6} sm={6}>
          <CreateRoom />
        </Grid>
        <Grid item lg={6} sm={6}>
          <AddRoomToInventory />
        </Grid>
      </Grid>
    </Container>
  );
}

export default App;
