package com.atex.plugins.newsstand.catalog.data;

import com.google.common.base.Objects;

/**
 * A publisher.
 */
public class Publisher {

    private final String id;
    private final String name;

    /**
     * Constructor.
     *
     * @param id publisher Id.
     * @param name publisher name.
     */
    public Publisher(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Publisher Id.
     *
     * @return a String.
     */
    public String getId() {
        return id;
    }

    /**
     * Publisher name.
     *
     * @return a String.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("name", name)
                      .toString();
    }
}
