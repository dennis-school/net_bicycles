
class Statistic {
  timestamp: number;
  value: number;

  public constructor( timestamp: number, value: number ) {
    this.timestamp = timestamp;
    this.value = value;
  }
}

function requestStatistics( ): Promise< Statistic[] > {
  return fetch( '/api/statistics/30' ).then( res => {
    if ( !res.ok || res.status !== 200 ) {
      throw 'Request failed';
    }
    return res.json( );
  } );
}

function formatTimestamp( v: number ): string {
  let date = new Date( v * 1000 );
  let year = date.getFullYear( );
  let month = ( '0' + ( date.getMonth( ) + 1 ) ).substr( -2 );
  let day = ( '0' + date.getDate( ) ).substr( -2 );

  let hour = date.getHours( );
  let minutes = ( '0' + date.getMinutes( ) ).substr( -2 );
  let seconds = ( '0' + date.getSeconds( ) ).substr( -2 );

  return `${year}-${month}-${day} ${hour}:${minutes}:${seconds}`;
}

document.addEventListener( 'DOMContentLoaded', ( ) => {
  let tableContainer = <HTMLDivElement> document.getElementById( 'table-container' );

  function buildTable( statistics: Statistic[] ) {
    statistics.reverse( );

    let table = document.createElement( 'table' );
    let header = document.createElement( 'tr' );
    let headerTimestamp = document.createElement( 'th' );
    headerTimestamp.innerText = 'Date + time';
    let headerValue = document.createElement( 'th' );
    headerValue.innerText = '#transactions';
    headerValue.style.textAlign = 'right';
    header.appendChild( headerTimestamp );
    header.appendChild( headerValue );
    table.appendChild( header );
    
    for ( let statistic of statistics ) {
      let row = document.createElement( 'tr' );
      let rowTimestamp = document.createElement( 'td' );
      rowTimestamp.innerText = formatTimestamp( statistic.timestamp );
      let rowValue = document.createElement( 'td' );
      rowValue.classList.add( 'number' );
      rowValue.innerText = statistic.value.toString( );
      row.appendChild( rowTimestamp );
      row.appendChild( rowValue );
      table.appendChild( row );
    }

    for ( let c of tableContainer.childNodes ) {
      tableContainer.removeChild( c );
    }
    tableContainer.appendChild( table );
  }

  requestStatistics( ).then( statistics => {
    buildTable( statistics );

    setInterval( ( ) => requestStatistics( ).then( statistics => buildTable( statistics ) ), 5000 );
  } );
} );
