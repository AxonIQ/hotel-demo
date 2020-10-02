import React, { useState } from "react";
import TextField from "@material-ui/core/TextField";
import Grid from "@material-ui/core/Grid";
import Button from "@material-ui/core/Button";
import "./register.scss";
import { createAccount } from "../../services/account/account";
import { useHistory, Link } from "react-router-dom";
import Typography from "@material-ui/core/Typography";

export const Register = () => {
  const [formData, setFormData] = useState({ userName: "", password: "" });
  const history = useHistory();

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) =>
    setFormData({ ...formData, [event.target.name]: event.target.value });

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const accountData = await createAccount(formData);
      // /accounts/:id
      history.push(`/user/${accountData.accountId}/pick-room`);
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <>
      <Typography variant="h5">Register a Guest Account</Typography>

      <form noValidate onSubmit={handleSubmit}>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Username"
                name="userName"
                onChange={handleChange}
                fullWidth
                required
                autoFocus
              />
            </Grid>
          </Grid>
          <Grid item xs={12}>
            <Grid item lg={4} sm={12}>
              <TextField
                label="Password"
                name="password"
                type="password"
                onChange={handleChange}
                fullWidth
                required
              />
            </Grid>
          </Grid>

          <Grid item xs={12}>
            <div className="register__actions">
              <Button type="submit" color="primary" variant="contained">
                Submit
              </Button>
            </div>
          </Grid>
        </Grid>
      </form>

      <div>
        <Link
          to="/manager/add-room"
          target="_blank"
          style={{
            textDecoration: "none",
            display: "inline-block",
            marginTop: "10px",
          }}
        >
          <Button type="submit" color="primary" variant="outlined">
            Continue as Manager {"->"}
          </Button>
        </Link>
      </div>
    </>
  );
};
