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

export type RoomOverviewData = {
  id: string;
  roomNumber: string;
  description: string;
  addedToInventory: boolean;
  addedToBooking: boolean;
}

type CreateRoomBody = {
  roomNumber: string;
  description: string;
};
export async function createRoom(body: CreateRoomBody) {
  return await fetchWrapper.post("/rooms", body);
}

type AddRoomToInventoryProps = {
  id: string;
};
export async function addRoomToInventory(props: AddRoomToInventoryProps) {
  return await fetchWrapper.post(
    `/rooms/${props.id}/ininventory`
  );
}

export async function getRooms(): Promise<RoomOverviewData[]> {
  const response = await fetchWrapper.get('/rooms')
  return await response.json() as RoomOverviewData[]
}
