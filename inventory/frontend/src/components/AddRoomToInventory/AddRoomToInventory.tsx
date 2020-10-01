import React, { useState } from "react";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import { addRoomToInventory } from "../../services/room/room";
import Typography from "@material-ui/core/Typography";

export const AddRoomToInventory = () => {
  const [roomNumber, setRoomNumber] = useState("");

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!roomNumber) {
      alert("Please enter a room number!");
      return;
    }

    try {
      await addRoomToInventory({ roomNumber });

      alert(`Successfully added room with number ${roomNumber} to inventory!`);
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <>
      <Typography variant="h5">Add Room to Inventory</Typography>
      <form noValidate onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Room Number"
                name="roomNumber"
                onChange={(event) => setRoomNumber(event.target.value)}
                fullWidth
                required
                autoFocus
              />
            </Grid>
          </Grid>
          <Grid item xs={12}>
            <Button type="submit" color="primary" variant="contained">
              Submit
            </Button>
          </Grid>
        </Grid>
      </form>
    </>
  );
};
