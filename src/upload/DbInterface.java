package upload;

import java.sql.*;

/**
 * Database interface methods for the project upload program.
 * 
 * @author Robin Garner
 *
 */
public class DbInterface {

  /**
   * Add a row to the 'Project' table.
   * 
   * @param conn        JDBC connection
   * @param projectName Name of the new project
   * @param pnumber     Project number
   * @param dno         Department number
   * @throws SQLException When something goes wrong
   */
  static void uploadProject(Connection conn, String projectName, int pnumber, int dno) throws SQLException {
    System.out.println("Uploading project "+projectName+", pnumber="+pnumber+", dno="+dno);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("select * from project where pnumber="+pnumber);
    if(rs.next()){
      System.out.println("update an existing project");
      stmt.execute("update project "+"set pname ='"+projectName+"',"+
              "dnum = "+dno+"where pnumber="+pnumber+"");

    }else{
      System.out.println("insert a new project");
      stmt.execute("insert into project (pname,pnumber,plocation,dnum) "+
              "values ('"+projectName+"',"+pnumber+",'<unknown>',"+dno+")");
    }

  }
  
  /**
   * Add a row to the WORKS_ON table
   * 
   * @param conn          JDBC connection
   * @param pnumber       Project Number
   * @param eno           Employee Number
   * @param hours         Hours
   * @throws SQLException When something goes wrong
   */
  static void uploadHours(Connection conn, int pnumber, int ssn, double hours) throws SQLException {
    System.out.println("Uploading works_on ssn="+ssn+", pnumber="+pnumber+", hours="+hours);
    //PreparedStatement stmt = conn.prepareStatement("insert into works_on (pno,ssn,hours) values (?,?,?)");
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery("select * from works_on where pno="+pnumber+"and ssn="+ssn);
    PreparedStatement stmt=null;
    if(rs.next()){
      System.out.println("update an existing item");
      stmt = conn.prepareStatement ("update works_on set hours=hours+ ? where pno = ? and ssn=?");
      stmt.setInt(1, pnumber);
      stmt.setInt(2, ssn);
      stmt.setDouble(3, hours);
      stmt.executeUpdate();

    }else{
      System.out.println("insert a new item");
      stmt = conn.prepareStatement ("insert into works_on (pno,ssn,hours) values (?,?,?)");
      stmt.setInt(1, pnumber);
      stmt.setInt(2, ssn);
      stmt.setDouble(3, hours);
      stmt.executeUpdate();
    }

    st.close();
    rs.close();
    stmt.close();
  }
}
