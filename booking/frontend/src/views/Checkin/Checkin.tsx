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

import React, { useState } from "react";
import Grid from "@material-ui/core/Grid";
import { useHistory, useParams } from "react-router-dom";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import { checkinRoom } from "../../services/room/room";

type CheckinRouteParams = {
  accountId: string;
  roomNumber: string;
  bookingId: string;
};

export const Checkin = () => {
  const { accountId, roomNumber, bookingId } = useParams<CheckinRouteParams>();
  const [formData, setFormData] = useState({
    bookingId: bookingId,
    roomNumber: roomNumber,
  });

  const history = useHistory();

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) =>
    setFormData({ ...formData, [event.target.name]: event.target.value });

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      await checkinRoom({ roomNumber }, { bookingId });
      alert(`Checked into room ${roomNumber} succesfully!`);
      history.push(`/user/${accountId}/payment`);
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <>
      <Typography variant="h5">Checkin</Typography>

      <form noValidate onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Room Number"
                name="roomNumber"
                type="number"
                value={roomNumber}
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
                label="Booking ID"
                name="bookingId"
                value={bookingId}
                onChange={handleChange}
                fullWidth
                required
              />
            </Grid>
          </Grid>

          <Grid item xs={12}>
            <div className="register__actions">
              <Button type="submit" color="primary" variant="contained">
                Checkin
              </Button>
            </div>
          </Grid>
        </Grid>
      </form>
    </>
  );
};
