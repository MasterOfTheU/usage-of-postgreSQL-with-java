package analysis;

import java.util.ArrayList;

/**
 * This class provides information about repository.
 */
public class RepositoryInfo {

    private String name;
    private String url;
    private String description;
    private String language;
    private int amountOfStars;
    private ArrayList<ContributorInfo> contributors;
    private int totalCommits;

    public RepositoryInfo() {}

    public RepositoryInfo(String name, String url, String description, String language, int amountOfStars) {
        this.name = name;
        this.url = url;
        this.description = description;
        this.language = language;
        this.amountOfStars = amountOfStars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getAmountOfStars() {
        return amountOfStars;
    }

    public void setAmountOfStars(int amountOfStars) {
        this.amountOfStars = amountOfStars;
    }

    public ArrayList<ContributorInfo> getContributors() {
        return contributors;
    }

    public void setContributors(ArrayList<ContributorInfo> contributors) {
        this.contributors = contributors;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    @Override
    public String toString() {
        StringBuilder repositoryInfo = new StringBuilder();
        repositoryInfo.append("REPOSITORY NAME: ").append(name)
                                                  .append("\n\tURL: ").append(url)
                                                  .append("\n\tDescription: ").append(description)
                                                  .append("\n\tLanguage: ").append(language)
                                                  .append("\n\tStars: ").append(amountOfStars)
                                                  .append("\n\tCommits: ").append(totalCommits)
                                                  .append("\n\tTop contributors: ").append(contributors);

        return repositoryInfo.toString();
    }

}
