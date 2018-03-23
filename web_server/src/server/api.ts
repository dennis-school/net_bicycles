import * as express from 'express';
import * as mysql from 'mysql';

type MysqlConnection = any;

// Bicycle location information that is relevant to the user
class BicycleLocation {
  name: string;
  longitude: number;
  latitude: number;
  numAvailable: number;
  numEmpty: number;

  public constructor( name: string, longitude: number, latitude: number, numAvailable: number, numEmpty: number ) {
    this.name = name;
    this.longitude = longitude;
    this.latitude = latitude;
    this.numAvailable = numAvailable;
    this.numEmpty = numEmpty;
  }
}

class MysqlCredentials {
  host: string;
  user: string;
  password: string;
  database: string;

  public constructor( host: string, user: string, password: string, database: string ) {
    this.host = host;
    this.user = user;
    this.password = password;
    this.database = database;
  }
};

class BicycleDatabase {
  private readonly credentials: MysqlCredentials;
  private pool;

  public constructor( credentials: MysqlCredentials ) {
    this.credentials = credentials;
    this.pool = mysql.createPool( {
      connectionLimit: 10,
      host: credentials.host,
      user: credentials.user,
      password: credentials.password,
      database: credentials.database
    } );
  }

  public getBicycleAvailability( ): Promise< BicycleLocation[] > {
    return this.query( 'SELECT name,location_longitude,location_latitude,COUNT(b.id) AS num_available,capacity-COUNT(b.id) AS num_empty FROM locker_set AS s LEFT JOIN bicycle AS b ON ( b.current_locker = s.id ) GROUP BY s.id;').then( results => {
      //return results.map( r => new BicycleLocation( r.name, r.location_longitude, r.location_latitude, r.num_available, r.num_empty ) );
      return results.map( r => new BicycleLocation( r.name, r.location_longitude, r.location_latitude, Math.ceil( Math.random( )  * 50 ), Math.ceil( Math.random( )  * 50 ) ) );
    } );
  }

  private query( q: string ): Promise< any > {
    return new Promise( ( resolve, reject ) => {
      this.pool.query( q, ( err, results ) => {
        if ( err ) {
          reject( err );
        } else {
          resolve( results );
        }
      } );
    } );
  }
}

export const app = express( );

let db = new BicycleDatabase( new MysqlCredentials( 'localhost', 'root', '', 'bicycle' ) );

app.use( setupAvailable( '/available' ) );

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
    } ).catch( ( ) => {
      res.status( 500 );
      res.contentType( 'text/plain' );
      res.send( 'An error occurred' );
    } );
  } );

  return router;
}