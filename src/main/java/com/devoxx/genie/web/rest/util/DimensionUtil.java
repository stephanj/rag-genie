package com.devoxx.genie.web.rest.util;

public class DimensionUtil {

    /**
     * Check if invalid dimension.
     * @param dimension the dimension
     * @return true if invalid
     */
    public static boolean isInvalidDimension(int dimension) {
        return dimension != 384 && dimension != 512 && dimension != 1024;
    }
}
