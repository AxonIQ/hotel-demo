package io.axoniq.demo.hotel.inventory.command.api

import java.util.*

data class RoomCreatedEvent(val roomId: UUID, val roomNumber: Int, val roomDescription: String)
data class RoomAddedToInventoryEvent(val roomId: UUID, val roomNumber: Int, val roomDescription: String)
data class RoomAddedToBookingSystemEvent(val roomId: UUID)
