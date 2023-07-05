import {Add, Check} from "@mui/icons-material";
import {IconButton, Paper, Table, TableBody, TableCell, TableHead, TableRow} from "@mui/material";
import React, {useCallback, useEffect, useState} from "react";
import {addRoomToInventory, getRooms, RoomOverviewData} from "../../services/room/room";

const NoRoomText = () => {
    return <Paper sx={{padding: 4}}><p>There are currently no rooms in the hotel. You can add new rooms with the form on the left.</p></Paper>
}

const RoomsTable = ({rooms} : {rooms: RoomOverviewData[]}) => {
    const addToInventory = useCallback(async (roomId: string) => {
        await addRoomToInventory({id: roomId})
    }, [])

    // noinspection TypeScriptValidateTypes: Specified by MUI demo
    return <Table>
        <TableHead>
            <TableRow>
                <TableCell>Room Number</TableCell>
                <TableCell>Description</TableCell>
                <TableCell align={"center"}>Added to inventory</TableCell>
                <TableCell align={"center"}>Added to booking</TableCell>
                <TableCell align={"center"}>Actions</TableCell>
            </TableRow>
        </TableHead>
        <TableBody>
            {rooms.map(room => (
                <TableRow key={room.id}>
                    <TableCell>{room.roomNumber}</TableCell>
                    <TableCell>{room.description}</TableCell>
                    <TableCell align={"center"}>{room.addedToInventory ? <Check/> : '' }</TableCell>
                    <TableCell align={"center"}>{room.addedToBooking ? <Check/> : '' }</TableCell>
                    <TableCell align={"center"}>{!room.addedToInventory && <IconButton title={"Add to Inventory"} onClick={() => addToInventory(room.id)}><Add/></IconButton>}</TableCell>
                </TableRow>
                ))}
        </TableBody>

    </Table>
}

export const RoomInventory = () => {
    const [rooms, setRooms] = useState([] as RoomOverviewData[])

    useEffect(() => {
        const interval = setInterval(async () => {
            setRooms(await getRooms())
        }, 1000)
        return () => clearInterval(interval)
    })

    return <div>
        {rooms.length === 0 && <NoRoomText/>}
        {rooms.length > 0 && <RoomsTable rooms={rooms} />}
    </div>
}
