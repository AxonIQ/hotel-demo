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
import { CleaningScheduleItem, getRoom } from "../../services/room/room";
import FormControlLabel from "@material-ui/core/FormControlLabel";
import Checkbox from "@material-ui/core/Checkbox";
import format from "date-fns/format";

export type RoomToClean = {
  roomNumber: string;
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
