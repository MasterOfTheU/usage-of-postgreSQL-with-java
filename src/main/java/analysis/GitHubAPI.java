package analysis;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static analysis.Metrics.*;

/**
 * This class provides methods for computing most starred and most committed repositories.
 */
public class GitHubAPI {
    private static final String TOKEN = System.getenv("GIT_TOKEN");
    private URI URI;
    private OrgJSONConverter orgJSONConverter = new OrgJSONConverter();
    private TreeMap<Integer, String> languagesMap = orgJSONConverter.getAllLanguages();
    private LinkedHashSet<User> usersSet = orgJSONConverter.getAllUsers();
    private LinkedHashSet<RepositoryInfo> reposSet = orgJSONConverter.getAllRepositories();
    private LinkedHashSet<RepositoryOwner> ownersSet = orgJSONConverter.getAllOwners();
    private PostgreSQLJDBC dbConnector = new PostgreSQLJDBC();

    public ArrayList<RepositoryInfo> getMostStarredRepos(String sinceYYYYMMDD, String untilYYYYMMDD) {
        startMetrics();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        ArrayList<RepositoryInfo> listOfStarredRepos = null;
        try {
            int pageNumber = 1;
            CloseableHttpClient httpClient;
            HttpGet httpGet;
            CloseableHttpResponse httpResponse;
            HttpEntity httpEntity;
            String responseString;
            while (true) {
                if (pageNumber == 6) break;
                URI = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.github.com")
                        .setPath("/search/repositories")
                        .setParameter("q", "stars:10000..*")
                        .setParameter("q", "created:" + sinceYYYYMMDD + ".." + untilYYYYMMDD)
                        .setParameter("page", String.valueOf(pageNumber))
                        .setParameter("per_page", "50")
                        .setParameter("sort", "stars")
                        .setParameter("order", "desc")
                        .build();
                httpClient = HttpClients.createDefault();
                httpGet = new HttpGet(URI);
                System.out.println(URI);
                httpGet.setHeader("Authorization", "token " + TOKEN);
                httpGet.setHeader("Accept", "application/vnd.github.v3+json");
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                if (httpEntity == null) {
                    System.out.println("End of search.");
                    break;
                }
                responseString = EntityUtils.toString(httpEntity);
                if (Objects.equals(responseString, "[]")) break;
                listOfStarredRepos = orgJSONConverter.computeMostStarredRepos(responseString);
                pageNumber++;
            }
            assert listOfStarredRepos != null;
            listOfStarredRepos.sort(Comparator.comparing(RepositoryInfo::getAmountOfStars).reversed());

            printMethodName(methodName);
            stopMetrics();
            gatherPerformance(methodName);
            return listOfStarredRepos;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return listOfStarredRepos;
    }

    public ArrayList<RepositoryInfo> getMostCommittedRepos(String sinceYYYYMMDD, String untilYYYYMMDD) {
        startMetrics();
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        ArrayList<RepositoryInfo> listOfCommittedRepos = null;
        try {
            int pageNumber = 1;
            CloseableHttpClient httpClient;
            HttpGet httpGet;
            CloseableHttpResponse httpResponse;
            HttpEntity httpEntity;
            String responseString;
            while (true) {
                if (pageNumber == 6) break;
                URI = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.github.com")
                        .setPath("/search/repositories")
                        .setParameter("q", "created:"+sinceYYYYMMDD+".."+untilYYYYMMDD)
                        .setParameter("page", String.valueOf(pageNumber))
                        .setParameter("per_page", "50")
                        .build();
                httpClient = HttpClients.createDefault();
                httpGet = new HttpGet(URI);
                System.out.println(URI);
                httpGet.setHeader("Authorization", "token " + TOKEN);
                httpGet.setHeader("Accept", "application/vnd.github.v3+json");
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                if (httpEntity == null) {
                    System.out.println("End of search.");
                    break;
                }
                responseString = EntityUtils.toString(httpEntity);
                if (Objects.equals(responseString, "[]")) break;
                listOfCommittedRepos = orgJSONConverter.computeMostCommittedRepos(responseString);
                pageNumber++;
            }
            assert listOfCommittedRepos != null;
            listOfCommittedRepos.sort(Comparator.comparing(RepositoryInfo::getTotalCommits).reversed());

            printMethodName(methodName);
            stopMetrics();
            gatherPerformance(methodName);
            return listOfCommittedRepos;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return listOfCommittedRepos;
    }

    /**
     * Inserts all collected data into database.
     * @throws SQLException In case that connection was not closed.
     */
    public void insertDataToDatabase() throws SQLException {
        dbConnector.insertUsers(usersSet);
        dbConnector.insertLanguages(languagesMap);
        dbConnector.insertRepos(reposSet, languagesMap);
        dbConnector.insertOwners(ownersSet);
        dbConnector.close();
    }
}
