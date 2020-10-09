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

import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import {
  getCleaningSchedule,
  GetCleaningScheduleResponse,
  prepareRoom,
} from "../../services/room/room";
import FormGroup from "@material-ui/core/FormGroup";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import {
  RoomToCleanItem,
  RoomToClean,
} from "../../components/RoomToCleanItem/RoomToCleanItem";
import Grid from "@material-ui/core/Grid";

export const RoomsToClean = () => {
  const [
    cleaningSchedule,
    setCleaningSchedule,
  ] = useState<GetCleaningScheduleResponse | null>(null);

  const history = useHistory();
  const roomsToClean = new Set<RoomToClean>();

  useEffect(() => {
    // Initial get
    getCleaningSchedule().then((result) => setCleaningSchedule(result));

    // And then do the get every 5 seconds
    const intervalId = setInterval(
      () => getCleaningSchedule().then((result) => setCleaningSchedule(result)),
      5000
    );
    return () => clearInterval(intervalId);
  }, []);

  if (!cleaningSchedule) {
    return null;
  }

  return (
    <>
      <Typography variant="h5">Rooms To Clean: </Typography>
      <form
        noValidate
        onSubmit={async (event) => {
          event.preventDefault();

          const getRoomPromises: Promise<Response>[] = [];
          roomsToClean.forEach(async ({ roomNumber, bookingId }) => {
            getRoomPromises.push(prepareRoom({ roomNumber }, { bookingId }));
          });

          await Promise.all(getRoomPromises);
          alert("All selected rooms cleaned succesfully!");
          history.push("checkout");
        }}
      >
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <FormGroup>
                {cleaningSchedule.map((cleaningScheduleItem) => (
                  <RoomToCleanItem
                    key={`room-${cleaningScheduleItem.roomNumber}`}
                    cleaningScheduleItem={cleaningScheduleItem}
                    onChange={(checked, roomToClean) => {
                      roomsToClean.delete(roomToClean);
                      checked && roomsToClean.add(roomToClean);
                    }}
                  />
                ))}
              </FormGroup>
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
        onClick={() => history.push(`checkout`)}
      >
        Go to Checkout {"->"}
      </Button>
    </>
  );
};
