package analysis;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class provides methods for parsing objects from JSON format to RepositoryInfo.
 */
public class OrgJSONConverter {

    /**
     * Token is set second time because we have to make another connection while creating the list of repository contributors.
     * @see OrgJSONConverter#getAllContributors(String)
     */
    private static final String TOKEN = System.getenv("GIT_TOKEN");

    public ArrayList<RepositoryInfo> computeMostStarredRepos(String jsonString) throws URISyntaxException, IOException {
        if (jsonString.length() == 0) return null;
        JSONObject jsonObject = new JSONObject(jsonString);
        RepositoryInfo repository;
        ArrayList<RepositoryInfo> listOfRepos = new ArrayList<>();
        JSONArray items = jsonObject.getJSONArray("items");
        int repCounter = 0;
        for (int i = 0; i < items.length(); i++) {
            repository = new RepositoryInfo();
            repository.setName(items.getJSONObject(i).getString("full_name"));
            repository.setUrl(items.getJSONObject(i).getString("html_url"));
            repository.setDescription((items.getJSONObject(i).isNull("description")) ? "No description found." : items.getJSONObject(i).getString("description"));
            repository.setLanguage((items.getJSONObject(i).isNull("language")) ? "Language is not specified." : items.getJSONObject(i).getString("language"));
            repository.setAmountOfStars(items.getJSONObject(i).getInt("stargazers_count"));
            repository.setContributors(getTopContributors(getAllContributors(items.getJSONObject(i).getString("contributors_url"))));
            repository.setTotalCommits(getTotalNumberOfCommits(getAllContributors(items.getJSONObject(i).getString("contributors_url"))));
            listOfRepos.add(repository);
            repCounter++;
            System.out.printf("%d item analyzed \n", repCounter);
        }

        return listOfRepos;
    }

    /**
     * @param jsonString JSONString that is converted from HttpEntity. String represents an array of repositories that will be converted to Repository objects.
     * @return Returns top 10 repositories by number of commits.
     * @throws URISyntaxException
     * @throws IOException
     */

    public ArrayList<RepositoryInfo> computeMostCommittedRepos(String jsonString) throws URISyntaxException, IOException  {
        if (jsonString.length() == 0) return null;
        JSONObject jsonObject = new JSONObject(jsonString);
        RepositoryInfo repository;
        ArrayList<RepositoryInfo> listOfRepos = new ArrayList<>();
        ArrayList<RepositoryInfo> mostCommittedRepos = new ArrayList<>();
        JSONArray items = jsonObject.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            repository = new RepositoryInfo();
            repository.setName(items.getJSONObject(i).getString("full_name"));
            repository.setUrl(items.getJSONObject(i).getString("html_url"));
            repository.setDescription((items.getJSONObject(i).isNull("description")) ? "No description." : items.getJSONObject(i).getString("description"));
            repository.setLanguage((items.getJSONObject(i).isNull("language")) ? "Language is not specified." : items.getJSONObject(i).getString("language"));
            repository.setAmountOfStars(items.getJSONObject(i).getInt("stargazers_count"));
            repository.setContributors(getTopContributors(getAllContributors(items.getJSONObject(i).getString("contributors_url"))));
            repository.setTotalCommits(getTotalNumberOfCommits(getAllContributors(items.getJSONObject(i).getString("contributors_url"))));
            listOfRepos.add(repository);
        }

        listOfRepos.sort(Comparator.comparing(RepositoryInfo::getTotalCommits).reversed());

        mostCommittedRepos.addAll(listOfRepos);
        return mostCommittedRepos;
    }

    /**
     * @param listOfContributors Gets the list of all contributors in repository.
     * @return Returns number of commits made by contributors.
     */
        public static int getTotalNumberOfCommits(ArrayList<ContributorInfo> listOfContributors) {
        int totalNumberOfCommits = 0;
        if (listOfContributors != null) {
            for (int i = 0; i < listOfContributors.size(); i++) {
                totalNumberOfCommits += listOfContributors.get(i).getAmountOfCommits();
            }
        }
        return totalNumberOfCommits;
        }

    /**
     * @param allContributors Gets the list of all contributors in repository.
     * @return Returns top 5 contributors in repository or all contributors if list size is less than 5.
     */
        public static ArrayList<ContributorInfo> getTopContributors(ArrayList<ContributorInfo> allContributors) {
            ArrayList<ContributorInfo> topContributors = new ArrayList<>();
            if (allContributors == null) return topContributors;
            if (allContributors.size() <= 5 ){
                topContributors.addAll(allContributors);
            } else {
                for (int i = 0; i < 5; i++) {
                    topContributors.add(allContributors.get(i));
                }
            }
            return topContributors;
        }

    /**
     * @param contributorsURLString A string that represents uri string for creating query.
     * @return Returns a sorted in a descending order list by number of commits of all contributors in repository.
     */
    public static ArrayList<ContributorInfo> getAllContributors(String contributorsURLString) {
        ArrayList<ContributorInfo> listOfContributors = null;
        try {
            listOfContributors = new ArrayList<>();
            ContributorInfo contributor;
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(contributorsURLString);
            httpGet.setHeader("Authorization", "token " + TOKEN);
            httpGet.setHeader("Accept", "application/vnd.github.v3+json");
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                String responseString = EntityUtils.toString(httpEntity);
                JSONArray jsonArray = new JSONArray(responseString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (Objects.equals(responseString, "[]")) break;
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    contributor = new ContributorInfo();
                    contributor.setName(jsonObject.getString("login"));
                    contributor.setProfileURL(jsonObject.getString("html_url"));
                    contributor.setAmountOfCommits(jsonObject.getInt("contributions"));
                    listOfContributors.add(contributor);
                }
            } else return null;
        }
        catch (IOException e) {
            e.getMessage();
        }
        catch (ParseException e) {
            e.getMessage();
        }
        catch (JSONException e) {
            e.getMessage();
            e.printStackTrace();
        }

        assert listOfContributors != null;
        listOfContributors.sort(Comparator.comparing(ContributorInfo::getAmountOfCommits).reversed());
        return listOfContributors;
    }
}
