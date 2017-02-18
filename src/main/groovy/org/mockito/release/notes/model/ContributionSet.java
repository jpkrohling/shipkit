package org.mockito.release.notes.model;

import java.util.Collection;

/**
 * A set of contributions
 */
public interface ContributionSet {

    /**
     * all commits in given contribution set, spanning all authors
     */
    Collection<Commit> getAllCommits();

    /**
     * all tickets referenced in commit messages
     */
    Collection<String> getAllTickets();

    /**
     * all contributions in the set
     */
    Collection<Contribution> getContributions();
}