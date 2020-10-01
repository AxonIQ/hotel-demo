package io.axoniq.demo.hotel.inventory.command.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateRoomCommand(@TargetAggregateIdentifier val roomId: UUID, val roomNumber: Int, val roomDescription: String)
data class AddRoomToInventoryCommand(@TargetAggregateIdentifier val roomId: UUID)
data class MarkRoomAsAddedToBookingSystemCommand(@TargetAggregateIdentifier val roomId: UUID)

