import * as express from 'express';
import { BicycleDatabase, MysqlCredentials } from './database';

export const app = express( );

let db = new BicycleDatabase( new MysqlCredentials( '192.168.1.31', 'root', '', 'bicycle' ) );

app.use( setupAvailable( '/available' ) );
app.use( setupStatistics( '/statistics' ) );

app.all( '*', ( req, res ) => {
  res.status( 404 );
  res.contentType( 'text/plain' );
  res.send( '404 - Not found' );
} );

// The availability API
function setupAvailable( endpoint: string ): express.Router {
  const router = express.Router( );
  
  router.get( endpoint, ( req, res, next ) => {
    db.getBicycleAvailability( ).then( locations => {
      res.contentType( 'application/json' );
      res.send( JSON.stringify( locations ) );
    } ).catch( err => {
      res.status( 500 );
      res.contentType( 'text/plain' );
      res.send( 'An error occurred' );
      
      console.log( 'Error', err );
    } );
  } );

  return router;
}

function setupStatistics( endpoint: string ): express.Router {
  const router = express.Router( );

  router.get( [ endpoint, endpoint + '/:limit', endpoint + '/:timestamp/:limit' ], ( req, res, next ) => {
    let timestampStr: string = req.params.timestamp;
    let timestamp: number = typeof timestampStr !== 'undefined' ? parseInt( <string> timestampStr ) : undefined;

    let limitStr: string = req.params.limit;
    let limit: number = typeof limitStr !== 'undefined' ? parseInt( <string> limitStr ) : undefined;

    db.getStatistics( limit, timestamp ).then( locations => {
      res.contentType( 'application/json' );
      res.send( JSON.stringify( locations ) );
    } ).catch( err => {
      res.status( 500 );
      res.contentType( 'text/plain' );
      res.send( 'An error occurred' );

      console.log( 'Error', err );
    } );
  } );

  return router;
}