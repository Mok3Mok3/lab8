package upload;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Upload a project and a set of hours from a comma separated file.
 * 
 * File format:
 *   Project name,pnumber,dno
 *   eno,hours
 *   eno,hours
 *   ...
 *   
 * @author Robin Garner
 * password masking method from https://stackoverflow.com/questions/8297199/masking-password-input-field-from-console-in-eclipse-java
 */
public class Upload {
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 4) {
      System.err.println("Usage: UploadProject host db user pwd file");
      System.exit(1);
    }
    
    String host = args[0];
    String database = args[1];
    String username = args[2];
//    String password = args[3];
    String filename = args[3];
    
    try {
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
        upload(new BufferedReader(new FileReader(new File(filename))),db);
      } finally {
        /* Close the connection */
        if(db!=null)
    	  db.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }

    private static void upload(BufferedReader datafile, Connection conn) throws IOException, SQLException {
    /*
     * Read the first line of the input file
     */
    String line = datafile.readLine();
    if (line == null) {
      error("Input file must have a header line");
    }
    
    /* Split the first line into fields: comma separated */
    String[] header = line.split(",");
    if (header.length != 3) {
      error("Header line must have project name, pnumber and dno");
    }
    
    /* Sanitize the fields and save */
    String projectName = header[0].trim();         // The project we are uploading
    int pnumber = parseInt(header[1],"pnumber","Project Number");
    int dno = parseInt(header[2],"dno","Department Number");
    DbInterface.uploadProject(conn, projectName, pnumber, dno);

    while ((line = datafile.readLine()) != null && line.trim().length() > 0) {
      String[] fields = line.split(",");
      if (fields.length != 2) {
        error("works_on line must have employee number and hours");
      }
      int eno = parseInt(fields[0],"eno","Employee Number");
      double hours = parseDouble(fields[1],"hours","Hours");
      DbInterface.uploadHours(conn, pnumber, eno, hours);
    }
    
  }

  private static int parseInt(String field, String fieldName, String desc) {
    try {
      return Integer.valueOf(field);
    } catch (NumberFormatException e) {
      error(fieldName+" must be a valid "+desc+". \""+field+"\" is not a valid integer.");
    }
    return -1; // never happens
  }
  
  private static double parseDouble(String field, String fieldName, String desc) {
    try {
      return Double.valueOf(field);
    } catch (NumberFormatException e) {
      error(fieldName+" must be a valid "+desc+". \""+field+"\" is not a valid double.");
    }
    return -1; // never happens
  }
  

  private static void error(String message) {
    throw new RuntimeException("Runtime Error: "+message);
  }
}
