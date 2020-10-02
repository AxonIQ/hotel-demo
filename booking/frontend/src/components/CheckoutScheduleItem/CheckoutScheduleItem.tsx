import React from "react";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import format from "date-fns/format";

interface ICheckoutScheduleItemProps {
  roomNumber: string;
  bookingId: string;
  endDate: string;
  onChange: (
    checked: boolean,
    checkoutItem: { roomNumber: string; bookingId: string }
  ) => void;
}
export const CheckoutScheduleItem = (props: ICheckoutScheduleItemProps) => (
  <FormControlLabel
    label={`Room number: ${props.roomNumber}, Booked Until: ${format(
      new Date(props.endDate),
      "dd/MM/yyyy"
    )}`}
    control={
      <Checkbox
        onChange={(event) =>
          props.onChange(event.target.checked, {
            roomNumber: props.roomNumber,
            bookingId: props.bookingId,
          })
        }
        name={`room-${props.roomNumber}`}
        color="primary"
      />
    }
  />
);
