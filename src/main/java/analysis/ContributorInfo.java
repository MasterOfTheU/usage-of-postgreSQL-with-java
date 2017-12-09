package analysis;

/**
 * This class provides information about a person that makes contributions to repository.
 */
public class ContributorInfo {

    private long id;
    private String name;
    private int amountOfCommits;

    public ContributorInfo() {}

    public ContributorInfo(long id, String name, int amountOfCommits) {
        this.id = id;
        this.name = name;
        this.amountOfCommits = amountOfCommits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmountOfCommits() {
        return amountOfCommits;
    }

    public void setAmountOfCommits(int amountOfCommits) {
        this.amountOfCommits = amountOfCommits;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        StringBuilder contributorInfo = new StringBuilder();
        contributorInfo.append("\n\t\t\t").append(name)
                       .append("\n\t\t\tNumber of commits: ").append(amountOfCommits)
                       .append("\n\t\t\t");

        return contributorInfo.toString();
    }
}
