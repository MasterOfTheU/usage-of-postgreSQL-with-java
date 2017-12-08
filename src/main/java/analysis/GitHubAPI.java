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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

import static analysis.Metrics.*;

/**
 * This class provides methods for computing most starred and most committed repositories.
 */
public class GitHubAPI {
    private static final String TOKEN = System.getenv("GIT_TOKEN");
    private URI URI;

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
                URI = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.github.com")
                        .setPath("/search/repositories")
                        .setParameter("q", "stars:10000..*")
                        .setParameter("q", "created:" + sinceYYYYMMDD + ".." + untilYYYYMMDD)
                        .setParameter("page", String.valueOf(pageNumber))
                        .setParameter("per_page", "100")
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
                listOfStarredRepos = new OrgJSONConverter().computeMostStarredRepos(responseString);
                pageNumber++;
            }
            assert listOfStarredRepos != null;
            listOfStarredRepos.sort(Comparator.comparing(RepositoryInfo::getAmountOfStars).reversed());

            printMethodName(methodName);
            stopMetrics();
            gatherPerformance(methodName);
            return listOfStarredRepos;
        } catch (JSONException e) {
            e.getMessage();
        } catch (IOException e) {
            e.getMessage();
        } catch (URISyntaxException e) {
            e.getMessage();
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
                URI = new URIBuilder()
                        .setScheme("https")
                        .setHost("api.github.com")
                        .setPath("/search/repositories")
                        .setParameter("q", "created:"+sinceYYYYMMDD+".."+untilYYYYMMDD)
                        .setParameter("page", String.valueOf(pageNumber))
                        .setParameter("per_page", "100")
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
                listOfCommittedRepos = new OrgJSONConverter().computeMostCommittedRepos(responseString);
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
            e.getMessage();
        }
        catch (IOException e) {
            e.getMessage();
        }
        catch (URISyntaxException e) {
            e.getMessage();
        }

        return listOfCommittedRepos;
    }

}
