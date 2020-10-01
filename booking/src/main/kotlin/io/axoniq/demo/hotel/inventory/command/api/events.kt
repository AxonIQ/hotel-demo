package io.axoniq.demo.hotel.inventory.command.api

import java.util.*

data class RoomAddedToInventoryEvent(val roomId: UUID, val roomNumber: Int, val roomDescription: String)
