package com.atex.plugins.newsstand.catalog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A magazine.
 */
public class Magazine {

    private final Publisher publisher;
    private final String id;
    private final String name;
    private final String defaultLanguage;
    private List<Issue> issues = Lists.newArrayList();

    /**
     * Constructor.
     *
     * @param publisher the publisher.
     * @param id magazine id.
     * @param name name of the magazine.
     * @param defaultLanguage default language.
     */
    public Magazine(final Publisher publisher, final String id, final String name, final String defaultLanguage) {
        this.publisher = checkNotNull(publisher);
        this.id = id;
        this.name = name;
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Get the publisher.
     *
     * @return a not null publisher.
     */
    public Publisher getPublisher() {
        return publisher;
    }

    /**
     * Get the id.
     *
     * @return a string.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the name.
     *
     * @return a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the default language.
     *
     * @return a string.
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Get the issues.
     *
     * @return a not null list of issues.
     */
    public List<Issue> getIssues() {
        return issues;
    }

    /**
     * Add an issue.
     *
     * @param issue a not null issue.
     */
    public void addIssue(final Issue issue) {
        issues.add(checkNotNull(issue));
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("publisher", publisher)
                      .add("id", id)
                      .add("name", name)
                      .add("defaultLanguage", defaultLanguage)
                      .add("issues", issues.size())
                      .toString();
    }

}
