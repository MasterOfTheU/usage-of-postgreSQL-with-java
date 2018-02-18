package analysis;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws SQLException {
        analyseRepositories();
        handleQueries();
    }

    public static void analyseRepositories() throws SQLException {
        GitHubAPI githubAPIEntity = new GitHubAPI();
        System.out.println("The information is being processed. Keep calm.");
        ArrayList<ArrayList<RepositoryInfo> > mostStarredRepos = new ArrayList<>();
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2018-02-10", "2018-02-17"));
        printRepositories(mostStarredRepos);

        ArrayList<ArrayList<RepositoryInfo> >mostCommittedRepos = new ArrayList<>();
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2018-02-10", "2018-02-17"));
        printRepositories(mostCommittedRepos);

        //Comment this to prevent second write to db
        //githubAPIEntity.insertDataToDatabase();
    }

    public static void handleQueries() throws SQLException {
        PostgreSQLJDBC dbConnector = new PostgreSQLJDBC();
        dbConnector.getMostPopularLanguages();
        dbConnector.getMostStarredRepositories();
        dbConnector.getRepositoriesWithJavaScriptLanguage();
        dbConnector.close();
    }

    private static void printRepositories(ArrayList<ArrayList<RepositoryInfo>> repositories){
        for (ArrayList<RepositoryInfo> rep : repositories) {
            if (rep.size() < 10) {
                for (int i = 0; i < rep.size(); i++) {
                    System.out.println(rep.get(i).toString());
                }
            } else {
                for (int i = 0; i < 10; i++) {
                    System.out.println(rep.get(i).toString());
                }
            }
        }
    }
}
