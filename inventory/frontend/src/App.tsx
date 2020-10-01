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
