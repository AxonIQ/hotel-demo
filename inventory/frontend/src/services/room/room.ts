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
