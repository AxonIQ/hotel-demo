import React, { useState, useEffect } from "react";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import { useHistory, useParams } from "react-router-dom";
import DateFnsUtils from "@date-io/date-fns";
import { MuiPickersUtilsProvider } from "@material-ui/pickers";
import { KeyboardDatePicker } from "@material-ui/pickers";
import {
  bookRoom,
  getRoom,
  RoomBooking,
  getRoomForAccount,
} from "../../services/room/room";
import Typography from "@material-ui/core/Typography";
import { BookingList } from "../../components/BookingList/BookingList";
import { SelectBookingList } from "../../components/SelectBookingList/SelectBookingList";

type BookRoomRouteParams = {
  accountId: string;
  roomNumber: string;
};

type BookRoomForm = {
  startDate: null | string;
  endDate: null | string;
};

export const BookRoom = () => {
  const history = useHistory();
  const { accountId, roomNumber } = useParams<BookRoomRouteParams>();

  const [busyDates, setBusyDates] = useState<RoomBooking[]>([]);
  const [myBookings, setMyBookings] = useState<RoomBooking[]>([]);
  const [myFailedBookings, setMyFailedBookings] = useState<RoomBooking[]>([]);
  const [bookingId, setBookingId] = React.useState<null | string>(null);

  const [formData, setFormData] = useState<BookRoomForm>({
    startDate: null,
    endDate: null,
  });

  useEffect(() => {
    getRoom(roomNumber).then((roomData) => setBusyDates(roomData.bookings));

    getRoomForAccount(roomNumber, accountId).then((roomForAccountData) => {
      setMyFailedBookings(roomForAccountData.myFailedBookings);
      setMyBookings(roomForAccountData.bookings);
    });
  }, [roomNumber, accountId]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    if (!formData.startDate || !formData.endDate) {
      alert("Please select a start and end date!");
      return null;
    }

    try {
      await bookRoom(
        { roomNumber },
        {
          accountID: accountId,
          startDate: formData.startDate,
          endDate: formData.endDate,
        }
      );

      const roomForAccountData = await getRoomForAccount(roomNumber, accountId);
      setMyBookings(roomForAccountData.bookings);
      setMyFailedBookings(roomForAccountData.myFailedBookings);
    } catch (error) {
      alert(error.message);

      getRoomForAccount(roomNumber, accountId).then((roomForAccountData) =>
        setMyFailedBookings(roomForAccountData.myFailedBookings)
      );
    }
  };

  return (
    <>
      <Typography variant="h5">Book a Room</Typography>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          <form noValidate onSubmit={handleSubmit}>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <Grid item xs={12}>
                <Grid item lg={4} sm={12}>
                  <KeyboardDatePicker
                    disableToolbar
                    variant="inline"
                    format="dd/MM/yyyy"
                    margin="normal"
                    label="Date From"
                    name="startDate"
                    value={formData.startDate}
                    onChange={(date) => {
                      if (date) {
                        setFormData({
                          ...formData,
                          startDate: date.toISOString(),
                        });
                      }
                    }}
                    KeyboardButtonProps={{
                      "aria-label": "change date",
                    }}
                  />
                </Grid>
              </Grid>

              <Grid item xs={12}>
                <Grid item lg={4} sm={12}>
                  <KeyboardDatePicker
                    disableToolbar
                    variant="inline"
                    format="dd/MM/yyyy"
                    margin="normal"
                    label="Date To"
                    name="dateTrom"
                    value={formData.endDate}
                    onChange={(date) => {
                      if (date) {
                        setFormData({
                          ...formData,
                          endDate: date.toISOString(),
                        });
                      }
                    }}
                    KeyboardButtonProps={{
                      "aria-label": "change date",
                    }}
                  />
                </Grid>
              </Grid>
            </MuiPickersUtilsProvider>

            <Grid item xs={12}>
              <div className="register__actions">
                <Button type="submit" color="primary" variant="contained">
                  Submit
                </Button>
              </div>
            </Grid>
          </form>
        </Grid>
        <Grid item xs={6}>
          {busyDates.length > 0 && (
            <Grid item xs={12}>
              <Typography>Busy Dates:</Typography>
              <BookingList bookings={busyDates} />
            </Grid>
          )}

          {myBookings.length > 0 && (
            <Grid item xs={12}>
              <Typography>My Bookings:</Typography>
              <div>
                <SelectBookingList
                  bookings={myBookings}
                  selectedBookingId={bookingId}
                  onSelectedBooking={(bookingId) => setBookingId(bookingId)}
                />
              </div>
              {bookingId && (
                <Button
                  onClick={() =>
                    bookingId &&
                    history.push(
                      `/user/${accountId}/checkin/${roomNumber}/${bookingId}`
                    )
                  }
                  variant="outlined"
                  color="primary"
                >
                  Checkin
                </Button>
              )}
            </Grid>
          )}

          {myFailedBookings.length > 0 && (
            <Grid item xs={12} style={{ color: "red", marginTop: "20px" }}>
              <Typography>My Failed Bookings:</Typography>
              <BookingList bookings={myFailedBookings} />
            </Grid>
          )}
        </Grid>
      </Grid>
    </>
  );
};
