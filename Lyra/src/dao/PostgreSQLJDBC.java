package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLJDBC {

	public static Connection cnx = null;
	public static String dbName = "spy";
	public static String host = "localhost";
	public static String port = "5432";
	public static String userName = "postgres";
	public static String password = "admin";

	public static Connection getConnexion() {

		if(cnx == null){
			try {
				Class.forName("org.postgresql.Driver");
				cnx = DriverManager
						.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName,
								userName , password);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			System.out.println("Opened database successfully");
		}

		return cnx;
	}


	public static void closeConnection(){
		if(cnx != null){
			try {
				cnx.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
