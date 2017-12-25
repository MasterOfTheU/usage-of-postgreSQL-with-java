package analysis;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws SQLException {
        //analyseRepositories();
        handleQueries();
    }

    public static boolean analyseRepositories() throws SQLException {
        GitHubAPI githubAPIEntity = new GitHubAPI();
        System.out.println("The information is being processed. Keep calm.");
        //region Most starred repos
        ArrayList<ArrayList<RepositoryInfo> > mostStarredRepos = new ArrayList<>();
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-01-01", "2017-01-07"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-01-08", "2017-01-14"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-01-15", "2017-01-21"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-01-22", "2017-01-28"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-01-29", "2017-02-04"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-02-05", "2017-02-11"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-02-02", "2017-02-18"));
        mostStarredRepos.add(githubAPIEntity.getMostStarredRepos("2017-02-19", "2017-02-25"));
        printRepositories(mostStarredRepos);
        //endregion

        //region Most committed repos
        ArrayList<ArrayList<RepositoryInfo> >mostCommittedRepos = new ArrayList<>();
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2015-02-01", "2015-02-08"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-01-08", "2017-01-14"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-01-15", "2017-01-21"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-01-22", "2017-01-28"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-01-29", "2017-02-04"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-02-05", "2017-02-11"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-02-02", "2017-02-18"));
        mostCommittedRepos.add(githubAPIEntity.getMostCommittedRepos("2017-02-19", "2017-02-25"));
        printRepositories(mostCommittedRepos);
        //endregion

        //Comment this to prevent second write to db
        //githubAPIEntity.insertDataToDatabase();

        return true;
    }

    public static void handleQueries() throws SQLException {
        PostgreSQLJDBC dbConnector = new PostgreSQLJDBC();
        dbConnector.getMostPopularLanguages();
        dbConnector.getMostStarredRepositories();
        dbConnector.getRepositoriesWithAssemblyLanguage();
        dbConnector.getProjectsInSpecifiedLanguage("JavaScript");
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
