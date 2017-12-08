package analysis;

import java.sql.*;

/**
 * This class provides methods for creating connection to database and making queries.
 */
public class PostgreSQLJDBC {

    private static final String URL = "jdbc:postgresql://localhost/GitHubRepData";
    private static final String USERNAME = System.getenv("stpUsername");
    private static final String PASSWORD = System.getenv("stpPassword");
    private Connection connection;
    private PreparedStatement preparedStatement;

    public PostgreSQLJDBC() {
        connection = null;
        preparedStatement = null;
    }

    /**
     * Creates a connection to database.
     */
    public void connectToDB() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.printf("Opened connection on %s\n", URL);
        }
        catch (ClassNotFoundException e) {
            e.getMessage();
        }
        catch (SQLException e) {
            e.getCause();
        }
    }

    /**
     * Closes connection. In case of success prints out that the connection was closed.
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        connection.close();
        System.out.println("Connection closed.");
    }
}
