package com.atex.plugins.newsstand.catalog.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * An issue.
 */
@XmlRootElement
public class Issue {

    private String id;
    private String issueCode;
    private String year;
    private String label;
    private String sku;
    private String language;
    private boolean published;
    private boolean free;
    private Date releaseDate;
    private String releaseId;
    private String releaseRank;
    private boolean summary;
    private boolean preview;
    private Date latestExport;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(final String issueCode) {
        this.issueCode = issueCode;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(final String sku) {
        this.sku = sku;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(final boolean published) {
        this.published = published;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(final boolean free) {
        this.free = free;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(final String releaseId) {
        this.releaseId = releaseId;
    }

    public String getReleaseRank() {
        return releaseRank;
    }

    public void setReleaseRank(final String releaseRank) {
        this.releaseRank = releaseRank;
    }

    public boolean isSummary() {
        return summary;
    }

    public void setSummary(final boolean summary) {
        this.summary = summary;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(final boolean preview) {
        this.preview = preview;
    }

    public Date getLatestExport() {
        return latestExport;
    }

    public void setLatestExport(final Date latestExport) {
        this.latestExport = latestExport;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("issueCode", issueCode)
                      .add("year", year)
                      .add("label", label)
                      .add("published", published)
                      .add("free", free)
                      .add("releaseDate", releaseDate)
                      .toString();
    }
}
