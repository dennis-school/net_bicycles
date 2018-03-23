import { setInterval } from 'timers';

// Bicycle location information that is relevant to the user
// Structure obtained from the server
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

class View {
  private readonly map: google.maps.Map;
  private readonly infoDiv: HTMLDivElement;
  private readonly nameDiv: HTMLDivElement;
  private readonly numAvailableDiv: HTMLDivElement;
  private readonly numEmptyDiv: HTMLDivElement;
  private readonly buttonAvailable: HTMLButtonElement;
  private readonly buttonEmpty: HTMLButtonElement;

  private readonly locations: BicycleLocation[];
  private readonly markers: google.maps.Marker[];

  private selectedLocation: string;
  private isShowingAvailability: boolean;

  public constructor( map: google.maps.Map ) {
    this.map = map;
    this.infoDiv = < HTMLDivElement > document.getElementById( 'info' );
    this.nameDiv = < HTMLDivElement > document.getElementById( 'name' );
    this.numAvailableDiv = < HTMLDivElement > document.getElementById( 'num-available' );
    this.numEmptyDiv = < HTMLDivElement > document.getElementById( 'num-empty' );
    this.buttonAvailable = < HTMLButtonElement > document.getElementById( 'button-available' );
    this.buttonEmpty = < HTMLButtonElement > document.getElementById( 'button-empty' );

    this.locations = [];
    this.markers = [];

    this.isShowingAvailability = true;

    this.buttonAvailable.addEventListener( 'click', ( ) => {
      this.updateShowAvailability( true );
    } );
    this.buttonEmpty.addEventListener( 'click', ( ) => {
      this.updateShowAvailability( false );
    } );
  }

  public addLocation( l: BicycleLocation ): void {
    let index = this.locations.findIndex( loc => loc.name == l.name );
    if ( index == -1 ) {
      let marker = new google.maps.Marker( {
        position: { lat: l.latitude, lng: l.longitude },
        map: this.map,
        label: this.isShowingAvailability ? `${l.numAvailable}` : `${l.numEmpty}`,
        title: l.name
      } );

      this.locations.push( l );
      this.markers.push( marker );

      google.maps.event.addListener( marker, 'click', ( ) => {
        this.showInfo( l );
      } );
    } else {
      this.locations[ index ] = l;
      this.markers[ index ].setLabel( this.isShowingAvailability ? `${l.numAvailable}` : `${l.numEmpty}` )

      if ( this.selectedLocation && this.selectedLocation === this.locations[ index ].name ) {
        this.selectedLocation = this.locations[ index ].name;
        this.showInfo( this.locations[ index ] );
      }
    }
  }

  public showInfo( location: BicycleLocation ): void {
    this.selectedLocation = location.name;
  
    this.infoDiv.classList.remove( 'disabled' );
    this.nameDiv.innerText = location.name;
    this.numAvailableDiv.innerText = location.numAvailable.toString( );
    this.numEmptyDiv.innerText = location.numEmpty.toString( );
  }

  private updateShowAvailability( b: boolean ): void {
    this.isShowingAvailability = b;

    if ( b ) {
      this.buttonAvailable.setAttribute( 'disabled', 'true' );
      this.buttonEmpty.removeAttribute( 'disabled' );
  
      for ( let i = 0; i < this.locations.length; i++ ) {
        this.markers[ i ].setLabel( `${this.locations[ i ].numAvailable}` );
      }
    } else {
      this.buttonAvailable.removeAttribute( 'disabled' );
      this.buttonEmpty.setAttribute( 'disabled', 'true' );
  
      for ( let i = 0; i < this.locations.length; i++ ) {
        this.markers[ i ].setLabel( `${this.locations[ i ].numEmpty}` );
      }
    }
  }
}

function requestAvailability( ): Promise< BicycleLocation[] > {
  return fetch( '/api/available' ).then( res => {
    if ( !res.ok || res.status !== 200 ) {
      throw 'Request failed';
    }
    return res.json( );
  } );
}

function setupMap( ): google.maps.Map {
  var uluru = {lat: 53.216196, lng: 6.566574};
  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 13,
    center: uluru
  });
  return map;
}

function loadScript( src: string ): Promise< void > {
  return new Promise( ( resolve, reject ) => {
    let scriptTag = document.createElement( 'script' );
    scriptTag.addEventListener( 'load', ( ) => resolve( ) );
    scriptTag.addEventListener( 'error', ( ) => reject( ) );
    scriptTag.setAttribute( 'async', 'true' );
    scriptTag.setAttribute( 'defer', 'true' );
    scriptTag.src = 'https://maps.googleapis.com/maps/api/js';
    document.head.appendChild( scriptTag );
  } );
}

document.addEventListener( 'DOMContentLoaded', ev => {
  let pMap = loadScript( 'https://maps.googleapis.com/maps/api/js' ).then( ( ) => setupMap( ) );
  let pAvailability = requestAvailability( );

  Promise.all( [ pMap, pAvailability ] ).then( ( [ map, lockers ] ) => {
    let view = new View( map );

    for ( let l of lockers ) {
      view.addLocation( l );
    }

    setInterval( ( ) => {
      requestAvailability( ).then( lockers => {
        for ( let l of lockers ) {
          view.addLocation( l );
        }
      } );
    }, 1000 );
  } );
} );