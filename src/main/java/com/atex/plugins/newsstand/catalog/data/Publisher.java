package com.atex.plugins.newsstand.catalog.data;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

/**
 * A publisher.
 */
@XmlRootElement
public class Publisher {

    private String id;
    private String name;

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

    public Publisher() {
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

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("id", id)
                      .add("name", name)
                      .toString();
    }
}
