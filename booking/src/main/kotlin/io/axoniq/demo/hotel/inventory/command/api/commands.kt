package io.axoniq.demo.hotel.inventory.command.api

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class MarkRoomAsAddedToBookingSystemCommand(@TargetAggregateIdentifier val roomId: UUID)

