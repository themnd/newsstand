package com.atex.plugins.newsstand.catalog;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.catalog.data.Publisher;
import com.atex.plugins.newsstand.util.MD5Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Parse a catalog.
 *
 * @author mnova
 */
public class CatalogParser {

    private static final Logger LOGGER = Logger.getLogger(CatalogParser.class.getName());

    private static final int QUERY_TIMEOUT = 30;

    private final File uri;

    public CatalogParser(final File uri) {
        this.uri = checkNotNull(uri);
    }

    public Catalog open() throws IOException {

        final Catalog catalog = new Catalog();
        catalog.setPath(uri.getAbsolutePath());
        catalog.setMd5(md5());
        catalog.setPublications(getPublications());
        return catalog;

    }

    String md5() throws IOException {

        LOGGER.fine("MD5 of " + uri.getAbsolutePath());

        checkFileExists();

        return MD5Util.md5(Files.newInputStream(Paths.get(uri.getAbsolutePath())));
    }

    List<Publication> getPublications() throws IOException {

        LOGGER.fine("Parsing " + uri.getAbsolutePath());

        checkFileExists();

        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Cannot find org.sqlite.JDBC driver: " + e.getMessage(), e);
            throw new IOException(e);
        }

        // create a database connection
        try (final Connection connection = DriverManager.getConnection(getJDBCURI())) {

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(QUERY_TIMEOUT);  // set timeout to 30 sec.

            final Map<String, Publisher> publishers = getPublishers(statement);
            final Map<String, Publication> magazines = getPublications(statement, publishers);
            addIssueToPublications(statement, magazines);

            return Lists.newArrayList(magazines.values());
        }
        catch (SQLException e) {

            // if the error message is "out of memory",
            // it probably means no database file is found
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new IOException(e);

        }
    }

    private void checkFileExists() throws FileNotFoundException {
        if (!uri.exists()) {
            throw new FileNotFoundException("Cannot find " + uri.getAbsolutePath());
        }
    }

    private Map<String, Publisher> getPublishers(final Statement statement) throws SQLException {
        final Map<String, Publisher> publishers = Maps.newHashMap();
        final ResultSet rs = statement.executeQuery("select id, name from publisher");
        while (rs.next()) {
            final String id = Strings.nullToEmpty(rs.getString("id"));
            final String name = rs.getString("name");
            final Publisher publisher = new Publisher(id, name);
            LOGGER.info(publisher.toString());
            publishers.put(id, publisher);
        }
        return publishers;
    }

    private Map<String, Publication> getPublications(final Statement statement, final Map<String, Publisher> publishers)
            throws SQLException {

        final Map<String, Publication> magazines = Maps.newHashMap();
        final ResultSet rs = statement.executeQuery("select id, publisher_id, name, default_language from magazine");
        while (rs.next()) {
            final String id = Strings.nullToEmpty(rs.getString("id"));
            final String pubId = Strings.nullToEmpty(rs.getString("publisher_id"));
            final String name = rs.getString("name");
            final String defLanguage = rs.getString("default_language");
            final Publisher publisher = publishers.get(pubId);
            if (publisher == null) {
                LOGGER.severe("magazine " + id + " does not have a valid publisher");
                continue;
            }
            final Publication publication = new Publication(publisher, id, name, defLanguage);
            LOGGER.info(publication.toString());
            magazines.put(id, publication);
        }
        return magazines;
    }

    private void addIssueToPublications(final Statement statement, final Map<String, Publication> publications)
            throws SQLException {

        final ResultSet rs = statement.executeQuery("select id, issue_code, magazine_id, year, label, sku, language," +
                "published, free, release_date, release_id, release_rank, summary, preview, latest_export " +
                "from issue " +
                "where NOT(test) " +
                "order by release_date DESC");
        while (rs.next()) {
            final String id = Integer.toString(rs.getInt("id"));
            final String magazineId = rs.getString("magazine_id");
            final Publication publication = publications.get(magazineId);
            if (magazineId == null) {
                LOGGER.severe("issue " + id + " does not have a valid magazine");
                continue;
            }
            final Issue issue = new Issue();
            issue.setId(id);
            issue.setIssueCode(Strings.nullToEmpty(rs.getString("issue_code")));
            issue.setYear(rs.getString("year"));
            issue.setLabel(rs.getString("label"));
            issue.setSku(rs.getString("sku"));
            issue.setLanguage(rs.getString("language"));
            issue.setPublished(rs.getBoolean("published"));
            issue.setFree(rs.getBoolean("free"));
            issue.setReleaseDate(rs.getDate("release_date"));
            issue.setReleaseId(Integer.toString(rs.getInt("release_id")));
            issue.setReleaseRank(Integer.toString(rs.getInt("release_rank")));
            issue.setSummary(rs.getBoolean("summary"));
            issue.setPreview(rs.getBoolean("preview"));
            issue.setLatestExport(rs.getTimestamp("latest_export"));
            LOGGER.info(issue.toString());
            publication.addIssue(issue);
        }
    }

    private String getJDBCURI() {
        return String.format("jdbc:sqlite:%s", uri.getAbsolutePath());
    }

}
