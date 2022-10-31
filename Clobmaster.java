/////////////////////////////////////////
// PutGetClobs is an example application 
// that shows how to work with the JDBC
// API to obtain and put CLOBs to and from
// database columns.
//
// The results of running this program 
// are that there are two CLOB values
// in a new table. Both are identical
// and contain about 500k of repeating 
// text data.
/////////////////////////////////////////
import java.sql.*;

public class PutGetClobs {
   public static void main(String[] args) 
   throws SQLException 
   {
       // Register the native JDBC driver.
       try {
          Class.forName("com.ibm.db2.jdbc.app.DB2Driver");
      } catch (Exception e) {
          System.exit(1);  // Setup error.
      }
          
      // Establish a Connection and Statement with which to work.
      Connection c = DriverManager.getConnection("jdbc:db2:*local");    
      Statement s = c.createStatement();
            Connection c2 = DriverManager.getConnection("jdbc:db2:*local");    
      
      // Clean up any previous run of this application.
      try {
          s.executeUpdate("DROP TABLE CUJOSQL.CLOBTABLE");
      } catch (SQLException e) {
          // Ignore it - assume the table did not exist.
      }

      // Create a table with a CLOB column. The default CLOB column
      // size is 1 MB.
      s.executeUpdate("CREATE TABLE CUJOSQL.CLOBTABLE (COL1 CLOB)");

      // Create a PreparedStatement object that allow you to put
      // a new Clob object into the database.
      PreparedStatement ps = c.prepareStatement("INSERT INTO CUJOSQL.CLOBTABLE VALUES(?)");

      // Create a big CLOB value...
      StringBuffer buffer = new StringBuffer(500000);
      while (buffer.length() < 500000) {
          buffer.append("All work and no play makes Cujo a dull boy.");
      }
      String clobValue = buffer.toString();

      // Set the PreparedStatement parameter. This is not 
      // portable to all JDBC drivers. JDBC drivers do not have 
      // to support setBytes for CLOB columns. This is done to
      // allow you to generate new CLOBs. It also
      // allows JDBC 1.0 drivers a way to work with columns containing
      // Clob data.
      ps.setString(1, clobValue);

      // Process the statement, inserting the clob into the database.
      ps.executeUpdate();

      // Process a query and get the CLOB that was just inserted out of the 
      // database as a Clob object.
      ResultSet rs = s.executeQuery("SELECT * FROM CUJOSQL.CLOBTABLE");
      rs.next();
      Clob clob = rs.getClob(1);

      
      // Put that Clob back into the database through
      // the PreparedStatement.
      ps.setClob(1, clob);
      ps.execute();

      c.close(); // Connection close also closes stmt and rs.
   }
}
