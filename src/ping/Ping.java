package ping;

import javax.swing.*;
import java.sql.*;

/**
 * Postgres/JDBC 'ping'.  Connects to a database, and prints
 * out the software version of the server.
 * 
 * @author Robin Garner
 * @date 11 Oct, 2007
 * password masking method from https://stackoverflow.com/questions/8297199/masking-password-input-field-from-console-in-eclipse-java
 */
public class Ping {

  /**
   * Main method.  This program takes 4 arguments on the command line
   *   host       - The host name of the database server
   *   dbname     - Name of the database
   *   username   - Postgres username for the connection
   *   password   - Postgres password
   * 
   * @param args Command-line arguments from the shell.
   */
  public static void main(String[] args) {
    if (args.length != 3) {
      System.err.println("Usage: Ping host db user pwd");
      System.exit(1);
    }
    String host = args[0];
    String database = args[1];
    String username = args[2];
//    String password = args[3];
    
    try {
      /* Dynamically load the JDBC driver */
      System.out.println("Loading postgres driver");
      Class.forName("org.postgresql.Driver"); //load the driver
      
      /* Connect to the Postgres database */
      System.out.println("Connecting to database //"+host+"/"+database);

      //masked password input
      final String password, message = "Enter your password";
      final JPasswordField pf = new JPasswordField();
      password = JOptionPane.showConfirmDialog( null, pf, message,
              JOptionPane.OK_CANCEL_OPTION,
              JOptionPane.QUESTION_MESSAGE ) == JOptionPane.OK_OPTION ?
              new String( pf.getPassword() ) : "";

      Connection db = DriverManager.getConnection(
          "jdbc:postgresql://"+host+"/"+database,
          username,
          password); //connect to the db
      
      try {
        /* Ask the database for its metadata */
        DatabaseMetaData dbmd = db.getMetaData(); //get MetaData to confirm connection
        System.out.println("Connection to "+dbmd.getDatabaseProductName()+" "+
            dbmd.getDatabaseProductVersion()+" successful.\n");
      } finally {
        /* Close the connection */
        db.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

}
