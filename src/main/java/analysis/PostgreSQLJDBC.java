package analysis;

import java.sql.*;
import java.util.ArrayList;
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

    //region Queries

    public void getMostPopularLanguages() {
        try {
            String query = "SELECT * FROM (SELECT DISTINCT ON (languages.language)  languages.language, " +
                    "count(languages.language) FROM repositories " +
                    "INNER JOIN languages ON repositories.language_id = languages.id " +
                    "GROUP BY languages.language) languages " +
                    "ORDER BY COUNT(languages) DESC " +
                    "LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("10 Most popular languages: ");
            while (rs.next()) {
                System.out.printf("%s - %d\n", rs.getString("language"), rs.getInt("count"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while selecting data from database " + e.getMessage());
        }
    }

    public void getMostStarredRepositories() {
        try {
            String query = "SELECT repositories.url, repositories.stars_number FROM repositories " +
                    " GROUP BY repositories.url, repositories.stars_number " +
                    " ORDER BY cast(repositories.stars_number as bigint) DESC " +
                    " LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("10 Most starred repositories: ");
            while (rs.next()) {
                System.out.printf("%s - %d\n", rs.getString("url"), rs.getInt("stars_number"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while selecting data from database " + e.getMessage());
        }
    }

    //edit query

    public void getRepositoriesWithAssemblyLanguage() {
        try {
            String query = "SELECT repositories.url, users.user_name, languages.language FROM repositories, users, repo_owners, languages" +
                    " WHERE repositories.id = repo_owners.repo_id and" +
                    " users.id = repo_owners.user_id and" +
                    " languages.language like 'Assembly' " +
                    " LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("10 repositories with Assembly language: ");
            while (rs.next()) {
                System.out.printf("%s - %s - %s\n", rs.getString("url"), rs.getString("user_name"), rs.getString("language"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while selecting data from database " + e.getMessage());
        }
    }

    //edit query

    public void getMostCommittedRepo() {
        try {
            String query = "SELECT users.login,count(repositories.name) FROM (repository_owners " +
                    "INNER JOIN users ON repository_owners.owner_id = users.user_id)" +
                    "INNER JOIN repositories ON repository_owners.repo_id = repositories.repo_id " +
                    "GROUP BY users.login\n" +
                    "ORDER BY COUNT(repositories.name) DESC " +
                    "LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.printf("10 users with most amount of repos");
            while (rs.next()) {
                System.out.printf("%s - %s - %d\n", rs.getString("url"), rs.getString("user_name"), rs.getInt("stars_number"));
            }
        }
        catch (SQLException e) {
            System.out.println("Error occured while selecting data from database " + e.getMessage());
        }
    }

    //endregion


}
