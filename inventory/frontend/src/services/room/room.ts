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

type CreateRoomBody = {
  roomNumber: string;
  description: string;
};
export async function createRoom(body: CreateRoomBody) {
  return await fetchWrapper.post("http://localhost:8081/rooms", body);
}

type AddRoomToInventoryProps = {
  roomNumber: string;
};
export async function addRoomToInventory(props: AddRoomToInventoryProps) {
  return await fetchWrapper.post(
    `http://localhost:8081/rooms/${props.roomNumber}/ininventory`
  );
}
