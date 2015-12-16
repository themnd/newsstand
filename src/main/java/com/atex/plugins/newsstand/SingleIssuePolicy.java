package com.atex.plugins.newsstand;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.polopoly.cm.client.CMException;
import com.polopoly.model.DescribesModelType;

/**
 * The policy for a single issue.
 */
@DescribesModelType
public class SingleIssuePolicy extends BaselinePolicy {

    private static final String CATALOG = "catalog";
    private static final String ISSUE = "issue";

    @Override
    public String getName() throws CMException {

        final StringBuilder sb = new StringBuilder();
        sb.append(getIssue());
        sb.append(" [");
        sb.append(getCatalog());
        sb.append("]");
        return sb.toString();

    }

    public String getCatalog() {
        return getChildValue(CATALOG, "");
    }

    public String getIssue() {
        return getChildValue(ISSUE, "");
    }

}
