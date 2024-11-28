package com.library.management.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class databaseConnection {
   private static final String URL = "jdbc:sqlite:src/main/java/com/library/management/database/library.db";

   //Get Connection Method
   public static Connection getConnection() throws SQLException{
      Connection c = null;
      
      try{
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection(URL);
         System.out.println("Opened database successfully");
      }catch(Exception e ){
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }finally{
      System.out.println("Opened database successfully");
      }
      return c;
   }

   //Close Connection Method
   public static void closeConnection(Connection connection) {
      if (connection != null) {
         try {
             connection.close();
         } catch (SQLException e) {
             System.err.println("Error closing database connection: " + e.getMessage());
         }
     }
   }

   //Authenticate User Method
   public static boolean authenticateUser(String username, String password) {
      try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
      }catch (SQLException e){
         System.err.println("Error authenticating user: " + e.getMessage());
      }
      return false;
   }
}
