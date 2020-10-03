import React from "react";
import type { RoomBooking } from "../../services/room/room";
import format from "date-fns/format";

export const BookingList = ({ bookings }: { bookings: RoomBooking[] }) => (
  <ul>
    {bookings.map((booking, index) => (
      <li key={`booking-${index}`}>
        {format(new Date(booking.startDate), "dd/MM/yyyy")} -{" "}
        {format(new Date(booking.endDate), "dd/MM/yyyy")}
      </li>
    ))}
  </ul>
);
