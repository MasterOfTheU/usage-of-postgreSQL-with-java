package analysis;

public class ContributorInfo {

    private String name;
    private String profileURL;
    private int amountOfCommits;

    /**
     * This class provides information about a person that makes contributions to repository.
     */
    public ContributorInfo() {}

    public ContributorInfo(String name, String profileURL, int amountOfCommits) {
        this.name = name;
        this.profileURL = profileURL;
        this.amountOfCommits = amountOfCommits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public int getAmountOfCommits() {
        return amountOfCommits;
    }

    public void setAmountOfCommits(int amountOfCommits) {
        this.amountOfCommits = amountOfCommits;
    }

    @Override
    public String toString() {
        StringBuilder contributorInfo = new StringBuilder();
        contributorInfo.append("\n\t\t\t").append(name)
                       .append("\n\t\t\tProfile URL: ").append(profileURL)
                       .append("\n\t\t\tNumber of commits: ").append(amountOfCommits)
                       .append("\n\t\t\t");

        return contributorInfo.toString();
    }
}
