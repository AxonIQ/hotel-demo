package io.axoniq.demo.hotel.booking.command.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booking.command")
public class BookingCommandProperties {
    private Integer snapshotTriggerThresholdRoom;
    private Integer snapshotTriggerThresholdAccount;
    private Integer snapshotTriggerThresholdPayment;

    public Integer getSnapshotTriggerThresholdRoom() {
        return snapshotTriggerThresholdRoom;
    }

    public void setSnapshotTriggerThresholdRoom(Integer snapshotTriggerThresholdRoom) {
        this.snapshotTriggerThresholdRoom = snapshotTriggerThresholdRoom;
    }

    public Integer getSnapshotTriggerThresholdAccount() {
        return snapshotTriggerThresholdAccount;
    }

    public void setSnapshotTriggerThresholdAccount(Integer snapshotTriggerThresholdAccount) {
        this.snapshotTriggerThresholdAccount = snapshotTriggerThresholdAccount;
    }

    public Integer getSnapshotTriggerThresholdPayment() {
        return snapshotTriggerThresholdPayment;
    }

    public void setSnapshotTriggerThresholdPayment(Integer snapshotTriggerThresholdPayment) {
        this.snapshotTriggerThresholdPayment = snapshotTriggerThresholdPayment;
    }
}
