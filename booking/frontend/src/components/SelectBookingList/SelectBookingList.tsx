import React from "react";
import { RoomBooking } from "../../services/room/room";
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
