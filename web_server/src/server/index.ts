import * as express from 'express';
import * as http from 'http';
import * as path from 'path';

import { app as appApi } from './api';

const app = express( );
app.set( 'view engine', 'pug' );
app.set( 'views', './views' );

// Bind api to '/api'
app.use( '/api', appApi );

// Bind the 'public_html' directory to '/' for all remaining resources
app.use( express.static( 'public_html' ) );

app.get( '/', ( req, res ) => {
  res.status( 200 );
  res.render( 'user_index' );
} );

app.all( '*', ( req, res ) => {
  res.status( 404 );
  res.contentType( 'text/plain' );
  res.send( '404 - Not found' );
} );

// Start server using 'http'. Useful when later HTTPS is used
http.createServer( app ).listen( process.env.PORT || 8080, ( ) => {
  console.log( 'Running' );
} );
