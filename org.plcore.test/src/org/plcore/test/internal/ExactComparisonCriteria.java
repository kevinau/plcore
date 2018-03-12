package org.plcore.test.internal;

import org.plcore.test.Assert;

public class ExactComparisonCriteria extends ComparisonCriteria {
    @Override
    protected void assertElementsEqual(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }
}
