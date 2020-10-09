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

import { fetchWrapper } from "../fetchWrapper";

type AddRoomBody = {
  roomNumber: string;
  description: string;
};
export async function addRoom(registerObject: AddRoomBody) {
  return await fetchWrapper.post("/rooms", registerObject);
}

export type RoomBooking = {
  id: string;
  startDate: string;
  endDate: string;
};
export type GetRoomResponse = {
  roomNumber: string;
  roomDescription: string;
  // "EMPTY" | "BOOKED"
  roomStatus: string;
  bookings: RoomBooking[];
  myFailedBookings: RoomBooking[];
};
export async function getRoom(roomNumber: string): Promise<GetRoomResponse> {
  const response = await fetchWrapper.get(`/rooms/${roomNumber}/availability`);
  return response.json();
}

export async function getRoomForAccount(
  roomNumber: string,
  accountId: string
): Promise<GetRoomResponse> {
  const response = await fetchWrapper.get(
    `/rooms/${roomNumber}/account/${accountId}/availability`
  );
  return response.json();
}

type BookRoomProps = {
  roomNumber: string;
};
type BookRoomBody = {
  startDate: string;
  endDate: string;
  accountID: string;
};
export async function bookRoom(props: BookRoomProps, body: BookRoomBody) {
  return await fetchWrapper.post(`/rooms/${props.roomNumber}/booked`, body);
}

export type CleaningScheduleItem = {
  roomNumber: string;
  deadlines: string[];
};
export type GetCleaningScheduleResponse = CleaningScheduleItem[];
export async function getCleaningSchedule(): Promise<
  GetCleaningScheduleResponse
> {
  const response = await fetchWrapper.get("/rooms/cleaningschedule");
  return await response.json();
}

type PrepareRoomProps = {
  roomNumber: string;
};
type PrepareRoomBody = {
  bookingId: string;
};
export async function prepareRoom(
  props: PrepareRoomProps,
  body: PrepareRoomBody
) {
  return await fetchWrapper.post(`/rooms/${props.roomNumber}/prepared`, body);
}

type CheckinRoomProps = {
  roomNumber: string;
};
type CheckinRoomBody = {
  bookingId: string;
};
export async function checkinRoom(
  props: CheckinRoomProps,
  body: CheckinRoomBody
) {
  return await fetchWrapper.post(`/rooms/${props.roomNumber}/checkedin`, body);
}

type CheckoutRoomProps = {
  roomNumber: string;
};
type CheckoutRoomBody = {
  bookingId: string;
};
export async function checkoutRoom(
  props: CheckoutRoomProps,
  body: CheckoutRoomBody
) {
  return await fetchWrapper.post(`/rooms/${props.roomNumber}/checkedout`, body);
}

export type GetCheckoutScheduleResponse = Array<{
  roomNumber: string;
  bookings: RoomBooking[];
}>;
export async function getCheckoutSchedule(): Promise<
  GetCheckoutScheduleResponse
> {
  const result = await fetchWrapper.get("/rooms/checkoutschedule");
  return await result.json();
}
