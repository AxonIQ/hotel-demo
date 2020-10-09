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
