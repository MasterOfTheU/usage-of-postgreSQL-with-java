package analysis;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class provides methods for creating connection to database and making queries.
 */
public class PostgreSQLJDBC implements AutoCloseable{

    private static final String URL = "jdbc:postgresql://localhost/GitHubRepData";
    private static final String USERNAME = System.getenv("stpUsername");
    private static final String PASSWORD = System.getenv("stpPassword");
    private Connection connection;
    private PreparedStatement preparedStatement;

    public PostgreSQLJDBC() {
        connectToDB();
    }

    /**
     * Creates a connection to database.
     */
    public void connectToDB() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.printf("Opened connection on %s\n", URL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes connection. In case of success prints out that the connection was closed.
     * @throws SQLException
     */

    public void close() throws SQLException {
        connection.close();
        System.out.println("Connection closed.");
    }

    //region Insert Operations

    public void insertLanguages(TreeMap<Integer, String> languagesMap) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO languages(id, language) VALUES(?,?)");
            for (Map.Entry<Integer, String> entry : languagesMap.entrySet()) {
                preparedStatement.setInt(1, entry.getKey());
                preparedStatement.setString(2, entry.getValue());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while inserting data to 'LANGUAGES' table" + e.getMessage());
        }
    }

    public void insertUsers(LinkedHashSet<User> users) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO users(id, user_name) VALUES(?,?)");
            for (User user : users) {
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while inserting data to 'USERS' table" + e.getMessage());
        }
    }

    public void insertRepos(LinkedHashSet<RepositoryInfo> reposSet, TreeMap<Integer, String> languagesMap) {
        try {
            Integer langID;
            String language;
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO repositories(id, repo_name, url, description, language_id, stars_number, commits_number) VALUES(?,?,?,?,?,?,?)");
            for (RepositoryInfo repository: reposSet) {
                preparedStatement.setLong(1, repository.getId());
                preparedStatement.setString(2, repository.getName());
                preparedStatement.setString(3, repository.getUrl());
                preparedStatement.setString(4, repository.getDescription());
                for (Map.Entry<Integer, String> entry : languagesMap.entrySet()) {
                    langID = entry.getKey();
                    language = entry.getValue();
                    if (language == repository.getLanguage()) {
                        preparedStatement.setInt(5, langID);
                        break;
                    }
                }
                preparedStatement.setInt(6, repository.getAmountOfStars());
                preparedStatement.setInt(7, repository.getTotalCommits());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while inserting data to 'REPOSITORIES' table" + e.getMessage());
        }
    }

    public void insertContributors(LinkedHashSet<ContributorInfo> contributorsSet) {
        try {
            long id;
            String name;
            int amountOfCommits;
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO contributors(id, contributor_name, commits_number) VALUES(?,?,?)");
            for (ContributorInfo contributor : contributorsSet) {
                id = contributor.getId();
                name = contributor.getName();
                amountOfCommits = contributor.getAmountOfCommits();
                preparedStatement.setLong(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setInt(3, amountOfCommits);
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while inserting data to 'CONTRIBUTORS' table" + e.getMessage());
        }

    }

    public void insertOwners(LinkedHashSet<RepositoryOwner> ownersSet) {
        try {
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO repo_owners(user_id, repo_id) VALUES(?,?)");
            for (RepositoryOwner owner : ownersSet) {
                preparedStatement.setLong(1, owner.getUserID());
                preparedStatement.setLong(2, owner.getRepositoryID());
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while inserting data to 'REPO_OWNERS' table" + e.getMessage());
        }
    }

    //endregion

}
