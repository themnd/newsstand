package com.atex.plugins.newsstand;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.model.DescribesModelType;

/**
 * Element policy.
 */
@DescribesModelType
public class NewsstandPolicy extends BaselinePolicy {

    private static final Logger LOGGER = Logger.getLogger(NewsstandPolicy.class.getName());

    private static final String SHOW_CATALOGS = "showCatalogs";
    private static final String SHOW_NEWSPAPERS = "showNewspapers";
    private static final String SHOW_MAGAZINES = "showMagazines";
    private static final String SHOW_COLLATERALS = "showCollaterals";

    /**
     * Return a list of catalogs.
     *
     * @return a not null String.
     */
    public List<String> getShowCatalogs() {
        return getValueList(SHOW_CATALOGS);
    }

    public boolean isShowNewspapers() {
        return getCheckboxValue(SHOW_NEWSPAPERS, true);
    }

    public boolean isShowMagazines() {
        return getCheckboxValue(SHOW_MAGAZINES, true);
    }

    public boolean isShowCollaterals() {
        return getCheckboxValue(SHOW_COLLATERALS, true);
    }

    private boolean getCheckboxValue(final String name, final boolean defaultValue) {
        try {
            final CheckboxPolicy check = (CheckboxPolicy) getChildPolicy(name);
            if (check != null) {
                return check.getChecked();
            }
        } catch (final CMException e) {
            LOGGER.log(Level.SEVERE, "Cannot get " + name + " value: " + e.getMessage(), e);
        }
        return defaultValue;
    }

    private boolean getCheckboxValue(final String name) {
        return getCheckboxValue(name, false);
    }

    private List<String> getValueList(final String name) {
        final String value = Strings.nullToEmpty(getChildValue(name, ""));
        return Lists.newArrayList(
                Splitter
                        .on(",")
                        .omitEmptyStrings()
                        .trimResults()
                        .split(value));
    }

}
