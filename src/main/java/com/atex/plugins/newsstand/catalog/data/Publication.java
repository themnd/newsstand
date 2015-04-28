package com.atex.plugins.newsstand.catalog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A publication.
 */
@XmlRootElement
public class Publication {

    private Publisher publisher;
    private String id;
    private String name;
    private String defaultLanguage;
    private List<Issue> issues = Lists.newArrayList();

    /**
     * Constructor.
     *
     * @param publisher the publisher.
     * @param id magazine id.
     * @param name name of the magazine.
     * @param defaultLanguage default language.
     */
    public Publication(final Publisher publisher, final String id, final String name, final String defaultLanguage) {
        this.publisher = checkNotNull(publisher);
        this.id = id;
        this.name = name;
        this.defaultLanguage = defaultLanguage;
    }

    public Publication() {
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

    public void setPublisher(final Publisher publisher) {
        this.publisher = publisher;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDefaultLanguage(final String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public void setIssues(final List<Issue> issues) {
        this.issues = issues;
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
