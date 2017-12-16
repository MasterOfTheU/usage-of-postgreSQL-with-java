package analysis;

/**
 * This class provides short information about a repository's owner.
 */
public class RepositoryOwner {

    private long userID;
    private long repositoryID;

    public RepositoryOwner() {}

    public RepositoryOwner(long userID, long repositoryID) {
        this.userID = userID;
        this.repositoryID = repositoryID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public long getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(long repositoryID) {
        this.repositoryID = repositoryID;
    }

}
