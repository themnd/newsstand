package com.atex.plugins.newsstand;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.polopoly.cm.app.policy.CheckboxPolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.model.DescribesModelType;

/**
 * Element policy.
 */
@DescribesModelType
public class NewsstandPolicy extends BaselinePolicy {

    private static final Logger LOGGER = Logger.getLogger(NewsstandPolicy.class.getName());

    private static final String SHOWNEWSPAPERS = "showNewspapers";
    private static final String SHOWMAGAZINES = "showMagazines";
    private static final String SHOWCOLLATERALS = "showCollaterals";

    public boolean isShowNewspapers() {
        return getCheckboxValue(SHOWNEWSPAPERS, true);
    }

    public boolean isShowMagazines() {
        return getCheckboxValue(SHOWMAGAZINES, true);
    }

    public boolean isShowCollaterals() {
        return getCheckboxValue(SHOWCOLLATERALS, true);
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

}
