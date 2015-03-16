package com.atex.plugins.newsstand.catalog.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A catalog of magazines.
 */
public class Catalog {

    private String md5 = "";
    private String path = "";
    private List<Magazine> magazines = Lists.newArrayList();

    /**
     * Get the md5 of the catalog.
     *
     * @return a not null String.
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Set the md5 of the catalog.
     *
     * @param md5 a not null String.
     */
    public void setMd5(final String md5) {
        this.md5 = checkNotNull(md5);
    }

    /**
     * Get the path of the catalog.
     *
     * @return a not null string.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the path of the catalog.
     *
     * @param path a not null string.
     */
    public void setPath(final String path) {
        this.path = checkNotNull(path);
    }

    /**
     * Get the list of magazines.
     *
     * @return a not null list.
     */
    public List<Magazine> getMagazines() {
        return magazines;
    }

    /**
     * Set the list of magazines.
     *
     * @param magazines a not null list.
     */
    public void setMagazines(final List<Magazine> magazines) {
        this.magazines = checkNotNull(magazines);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("md5", md5)
                      .add("path", path)
                      .add("magazines", magazines.size())
                      .toString();
    }
}
