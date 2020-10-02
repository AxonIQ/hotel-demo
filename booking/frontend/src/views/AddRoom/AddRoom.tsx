import React, { useState } from "react";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import { addRoom } from "../../services/room/room";
import { useHistory } from "react-router-dom";
import Typography from "@material-ui/core/Typography";

export const AddRoom = () => {
  const history = useHistory();
  const [formData, setFormData] = useState({ roomNumber: "", description: "" });

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) =>
    setFormData({ ...formData, [event.target.name]: event.target.value });

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      await addRoom(formData);

      alert(`Successfully added room with number ${formData.roomNumber}`);
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <>
      <Typography variant="h5">Add a Room</Typography>
      <form noValidate onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Room Number"
                name="roomNumber"
                onChange={handleChange}
                fullWidth
                required
                autoFocus
              />
            </Grid>
          </Grid>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Room Description"
                name="description"
                onChange={handleChange}
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
      <Button
        style={{ marginTop: "10px" }}
        type="button"
        color="primary"
        variant="outlined"
        onClick={() => history.push(`rooms-to-clean`)}
      >
        Go to Cleaning Schedule {"->"}
      </Button>
    </>
  );
};
