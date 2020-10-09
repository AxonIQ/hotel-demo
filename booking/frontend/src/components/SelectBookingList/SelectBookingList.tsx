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
import type { RoomBooking } from "../../services/room/room";
import format from "date-fns/format";
import Radio from "@material-ui/core/Radio";
import RadioGroup from "@material-ui/core/RadioGroup";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import FormControl from "@material-ui/core/FormControl";

type SelectBookingListProps = {
  bookings: RoomBooking[];
  selectedBookingId: null | string;
  onSelectedBooking: (bookingId: string) => void;
};

export const SelectBookingList = (props: SelectBookingListProps) => (
  <FormControl component="fieldset">
    <RadioGroup
      name="checkinBooking"
      value={props.selectedBookingId}
      onChange={(event) => props.onSelectedBooking(event.target.value)}
    >
      {props.bookings.map((booking) => (
        <FormControlLabel
          key={booking.id}
          value={booking.id}
          control={<Radio />}
          label={`${format(
            new Date(booking.startDate),
            "dd/MM/yyyy"
          )} - ${format(new Date(booking.endDate), "dd/MM/yyyy")}`}
        />
      ))}
    </RadioGroup>
  </FormControl>
);
