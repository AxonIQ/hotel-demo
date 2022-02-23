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

import {AppBar, Card, createTheme, Toolbar, Typography} from "@mui/material";
import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid";
import {ThemeProvider} from '@mui/material/styles';
import React from "react";
import {CreateRoom} from "./components/CreateRoom/CreateRoom";
import {RoomInventory} from "./components/RoomInventory/RoomInventory";


const theme = createTheme({
    palette: {
        primary: {
            main: '#f35c00',
        },
        secondary: {
            main: '#edf2ff',
        },
    },
});

function App() {
    return (
        <ThemeProvider theme={theme}>
            <Container>
                <AppBar position="static">
                    <Toolbar>
                        <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                            Axon Hotel Inventory Manager
                        </Typography>
                    </Toolbar>
                </AppBar>

                <Grid container spacing={2} style={{marginTop: '20px'}}>
                    <Grid item lg={4} sm={4}>
                        <Card sx={{padding: 2, textAlign: 'center', marginBottom: 2}}>
                            <CreateRoom/>
                        </Card>
                        <Card sx={{padding: 2, textAlign: 'center'}}>
                            <Typography variant="h5" sx={{marginBottom: 2}}>Inventory Management</Typography>
                            <Typography variant={"body1"} sx={{marginBottom: 2}}>Welcome to the Axon Hotel Inventory management system!</Typography>
                            <Typography variant={"body1"}>You can create rooms and then add them to the inventory. When the booking service has processed the addition of the room, it will be marked as added to the booking system.</Typography>
                        </Card>
                    </Grid>
                    <Grid item lg={8} sm={8}>
                        <RoomInventory/>
                    </Grid>
                </Grid>
            </Container>
        </ThemeProvider>
    );
}

export default App;
