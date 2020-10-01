import React from "react";
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
import { Link as RouterLink, useHistory, useParams } from "react-router-dom";
import Button from "@material-ui/core/Button";
import "./navigation.scss";

export const Navigation = () => {
  const history = useHistory();
  const { accountId } = useParams();

  return (
    <Typography>
      <Link component={RouterLink} to="/">
        Register
      </Link>
      {accountId && (
        <>
          <Link
            className="navigation__link"
            component={RouterLink}
            to={`/user/${accountId}/add-room`}
          >
            Add Room
          </Link>
          <Link
            className="navigation__link"
            component={RouterLink}
            to={`/user/${accountId}/book-room`}
          >
            Book Room
          </Link>
          <Link
            className="navigation__link"
            component={RouterLink}
            to={`/user/${accountId}/rooms-to-clean`}
          >
            Rooms To Clean
          </Link>
        </>
      )}
      <Button
        className="navigation__logout"
        onClick={() => {
          history.push("/");
        }}
      >
        Log out
      </Button>
    </Typography>
  );
};
