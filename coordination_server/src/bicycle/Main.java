package bicycle;

import bicycle.database.Database;

/**
 * Main for running one Coordinator with input id
 * run cmd at directory \net_bicycles\coordination_server\bin
 * cmd: java -cp .; first jar ; second jar net_bicycles_coordinator_server.Main coordinator_id
 * Two jar file is in folder coordinator_server
 * coordinator_id is a integer which in database, currently we only have 1,2,3,4,5
 * 
 * @author Luigi
 *
 */

public class Main {
	public static final Database.Credentials DB_LOGIN =
			new Database.Credentials( "localhost",
					                  3306,
					                  "bicycle",
					                  "root",
					                  null );
	
	public static void main( String... args ) {
		if ( args.length < 1 ) {
			System.out.println( "Usage: java bicycle.Main [id]" );
		} else {
			int id = Integer.parseInt(args[0]);
			new Coordinator( id, DB_LOGIN );
		}
	}
}
