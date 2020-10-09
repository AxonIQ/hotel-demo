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

import React, { useState, useEffect } from "react";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import Typography from "@material-ui/core/Typography";
import {
  checkoutRoom,
  GetCheckoutScheduleResponse,
  getCheckoutSchedule,
} from "../../services/room/room";
import { CheckoutScheduleItem } from "../../components/CheckoutScheduleItem/CheckoutScheduleItem";
import { useHistory } from "react-router-dom";

export const Checkout = () => {
  const [checkoutSchedule, setCheckoutSchedule] = useState<
    GetCheckoutScheduleResponse
  >([]);

  const history = useHistory();

  const roomsToCheckout = new Set<{ roomNumber: string; bookingId: string }>();
  useEffect(() => {
    getCheckoutSchedule().then((result) => setCheckoutSchedule(result));

    // And then do the get every 5 seconds
    const intervalId = setInterval(
      () => getCheckoutSchedule().then((result) => setCheckoutSchedule(result)),
      5000
    );
    return () => clearInterval(intervalId);
  }, []);

  return (
    <>
      <Typography variant="h5">Checkout</Typography>
      <form
        noValidate
        onSubmit={async (event) => {
          event.preventDefault();

          if (roomsToCheckout.size === 0) {
            return alert("Please select a room to checkout!");
          }

          try {
            const getRoomPromises: Promise<Response>[] = [];
            roomsToCheckout.forEach(async ({ roomNumber, bookingId }) => {
              getRoomPromises.push(checkoutRoom({ roomNumber }, { bookingId }));
            });

            await Promise.all(getRoomPromises);
            alert(`Checked out of the selected rooms succesfully!`);
            const newCheckoutSchedule = await getCheckoutSchedule();
            setCheckoutSchedule(newCheckoutSchedule);
          } catch (error) {
            alert(error.message);
          }
        }}
      >
        <Grid container spacing={2}>
          {checkoutSchedule.map((checkoutItem) => (
            <Grid item xs={12}>
              <Grid item lg={4} sm={12}>
                <CheckoutScheduleItem
                  roomNumber={checkoutItem.roomNumber}
                  bookingId={checkoutItem.bookings[0].id}
                  endDate={checkoutItem.bookings[0].endDate}
                  onChange={(checked, roomCheckoutData) => {
                    roomsToCheckout.delete(roomCheckoutData);
                    checked && roomsToCheckout.add(roomCheckoutData);
                  }}
                />
              </Grid>
            </Grid>
          ))}

          <Grid item xs={12}>
            <div className="register__actions">
              <Button type="submit" color="primary" variant="contained">
                Checkout
              </Button>
            </div>
          </Grid>
        </Grid>
      </form>
      <Button
        style={{ marginTop: "10px" }}
        type="button"
        color="primary"
        variant="outlined"
        onClick={() => history.push(`payment-overview`)}
      >
        Go to Payment Overview {"->"}
      </Button>
    </>
  );
};
