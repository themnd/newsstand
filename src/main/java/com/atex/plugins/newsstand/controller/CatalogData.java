package com.atex.plugins.newsstand.controller;

import java.util.List;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.google.common.collect.Lists;

/**
 * Data used for rendering.
 *
 * @author mnova
 */
public class CatalogData {

    private final String code;
    private final Catalog catalog;

    private List<Publication> newspapers = Lists.newArrayList();
    private List<Publication> magazines = Lists.newArrayList();
    private List<Publication> collaterals = Lists.newArrayList();

    public CatalogData(final String code, final Catalog catalog) {
        this.code = code;
        this.catalog = catalog;
    }

    public String getCode() {
        return code;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public List<Publication> getPublications() {
        return catalog.getPublications();
    }

    public List<Publication> getNewspapers() {
        return newspapers;
    }

    public List<Publication> getMagazines() {
        return magazines;
    }

    public List<Publication> getCollaterals() {
        return collaterals;
    }

}
