import React, { useState, useEffect } from "react";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {
  getPayments,
  GetPaymentsResponse,
} from "../../services/payment/payment";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";

export const PaymentOverview = () => {
  const [paymentList, setPaymentList] = useState<GetPaymentsResponse>([]);
  useEffect(() => {
    getPayments().then((result) => setPaymentList(result));
  }, []);

  useEffect(() => {
    // Initial get
    getPayments().then((result) => setPaymentList(result));

    // And then do the get every 5 seconds
    const intervalId = setInterval(
      () => getPayments().then((result) => setPaymentList(result)),
      5000
    );
    return () => clearInterval(intervalId);
  }, []);

  if (!paymentList) {
    return null;
  }
  return (
    <>
      <Typography variant="h5">Payment Overview: </Typography>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Grid item sm={12}>
            <TableContainer component={Paper}>
              <Table aria-label="simple table">
                <TableHead>
                  <TableRow>
                    <TableCell>Payment ID</TableCell>
                    <TableCell>Account ID</TableCell>
                    <TableCell align="center">Status</TableCell>
                    <TableCell align="center">Amount</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {paymentList.map((payment) => (
                    <TableRow key={payment.paymentId}>
                      <TableCell component="th" scope="row">
                        {payment.paymentId}
                      </TableCell>
                      <TableCell>{payment.accountId}</TableCell>
                      <TableCell align="center">
                        {payment.paymentStatus}
                      </TableCell>
                      <TableCell align="center">
                        {payment.totalAmount}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </Grid>
      </Grid>
    </>
  );
};
