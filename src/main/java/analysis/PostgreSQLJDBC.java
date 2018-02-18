package analysis;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * This class provides methods for creating connection to database and making queries.
 */
public class PostgreSQLJDBC implements AutoCloseable {

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
            Properties prop = new Properties();
            InputStream input = null;
            String dburl = "";
            String dbuser = "";
            String dbpassword = "";
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                dburl = prop.getProperty("dburl");
                dbuser= prop.getProperty("dbuser");
                dbpassword = prop.getProperty("dbpassword");
            } catch (FileNotFoundException e) {
                System.out.println("Could not find config file: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Could not read config file: " + e.getMessage());
            }
            connection = DriverManager.getConnection(dburl, dbuser, dbpassword);
            System.out.printf("Opened connection on %s\n", dburl);
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
                    if (language.equals(repository.getLanguage())) {
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
        String filename = "languages.csv";

        try {
            String query = "SELECT * FROM (SELECT DISTINCT ON (languages.language)  languages.language, " +
                    "count(languages.language) FROM repositories " +
                    "INNER JOIN languages ON repositories.language_id = languages.id " +
                    "GROUP BY languages.language) languages " +
                    "ORDER BY COUNT(languages) DESC " +
                    "LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            FileWriter fileWriter = new FileWriter(filename);
            try (BufferedWriter out = new BufferedWriter(fileWriter)) {
                while (rs.next()) {
                    out.write(rs.getString("language") + ",");
                    out.write(Integer.toString(rs.getInt("count")));
                    out.write(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured while writing data into file: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error occured while selecting data from database: " + e.getMessage());
        }
    }

        public void getMostStarredRepositories() {
        String filename = "most_starred_repos.csv";

        try {
            String query = "SELECT repositories.url, repositories.stars_number FROM repositories " +
                    " GROUP BY repositories.url, repositories.stars_number " +
                    " ORDER BY cast(repositories.stars_number as bigint) DESC " +
                    " LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            FileWriter fileWriter = new FileWriter(filename);
            try (BufferedWriter out = new BufferedWriter(fileWriter)) {
                while (rs.next()) {
                    out.write(rs.getString("url") + ",");
                    out.write(Integer.toString(rs.getInt("stars_number")));
                    out.write(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured while writing data into file: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error occured while selecting data from database: " + e.getMessage());
        }
    }
    //edit query

    public void getRepositoriesWithJavaScriptLanguage() {
        String filename = "js_repos.csv";

        try {
            String query = "SELECT repositories.url, users.user_name\n" +
                    "FROM repositories\n" +
                    "INNER JOIN repo_owners ON repositories.id = repo_owners.repo_id\n" +
                    "INNER JOIN users ON users.id = repo_owners.user_id\n" +
                    "INNER JOIN languages ON repositories.language_id = languages.id \n" +
                    "WHERE languages.language = 'JavaScript'\n" +
                    "GROUP BY repositories.url, users.user_name\n" +
                    "LIMIT 10";
            preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            FileWriter fileWriter = new FileWriter(filename);
            try (BufferedWriter out = new BufferedWriter(fileWriter)) {
                while (rs.next()) {
                    out.write(rs.getString("url") + ",");
                    out.write(rs.getString("user_name"));
                    out.write(System.getProperty("line.separator"));
                }
            }
        } catch (IOException e) {
            System.out.println("Error occured while writing data into file: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error occured while selecting data from database: " + e.getMessage());
        }
    }

    //endregion
}