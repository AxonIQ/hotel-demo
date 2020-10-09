/*
 * Copyright (c) 2020-2020. AxonIQ
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;)
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import React from "react";
import Link from "@material-ui/core/Link";
import Typography from "@material-ui/core/Typography";
import { Link as RouterLink, useHistory, useParams } from "react-router-dom";
import Button from "@material-ui/core/Button";
import "./navigation.scss";

type NavigationRouteParams = {
  accountId: string;
};

export const Navigation = () => {
  const history = useHistory();
  const { accountId } = useParams<NavigationRouteParams>();

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
