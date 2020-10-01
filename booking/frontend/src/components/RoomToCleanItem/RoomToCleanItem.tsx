import React, { useState, useEffect } from "react";
import { CleaningScheduleItem, getRoom } from "../../services/room/room";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import format from "date-fns/format";

export type RoomToClean = {
  roomNumber: number;
  bookingId: string;
};

interface IRoomToCleanItemProps {
  cleaningScheduleItem: CleaningScheduleItem;
  onChange: (checked: boolean, roomToClean: RoomToClean) => void;
}
export const RoomToCleanItem = (props: IRoomToCleanItemProps) => {
  const [roomToCleanData, setRoomToCleanData] = useState<null | RoomToClean>(
    null
  );

  useEffect(() => {
    getRoom(props.cleaningScheduleItem.roomNumber).then((roomInfo) => {
      const deadline = props.cleaningScheduleItem.deadlines[0];
      const booking = roomInfo.bookings.find(
        (bookingItem) => bookingItem.startDate === deadline
      );
      if (!booking) {
        return null;
      }
      setRoomToCleanData({
        roomNumber: roomInfo.roomNumber,
        bookingId: booking.id,
      });
    });
  }, [props.cleaningScheduleItem]);

  if (!roomToCleanData) {
    return null;
  }

  return (
    <FormControlLabel
      label={`Room number: ${
        props.cleaningScheduleItem.roomNumber
      }, Deadline: ${format(
        new Date(props.cleaningScheduleItem.deadlines[0]),
        "dd/MM/yyyy"
      )}`}
      control={
        <Checkbox
          onChange={(event) =>
            props.onChange(event.target.checked, roomToCleanData)
          }
          name={`room-${props.cleaningScheduleItem.roomNumber}`}
          color="primary"
        />
      }
    />
  );
};
