package com.historydatacenter.utils;

/**
 * Useful methods for helping to write .equals() methods.  Domain objects should use these methods to
 * construct their own equals() methods as such:
 *
 * public class Address {
 *     public boolean equals(Object obj) {
 *         if (this == obj)
 *             return true;
 *
 *         if (!(obj instanceof Address))
 *             return false;
 *
 *         Address obj2 = (Address) obj;
 *         return entitiesAreEqual(contactableEntity, obj2.contactableEntity) &&
 *                 areEqual(address1, obj2.address1) &&
 *                 areEqual(address2, obj2.address2) &&
 *                 areEqual(city, obj2.city) &&
 *                 areEqual(stateOrProvince, obj2.stateOrProvince) &&
 *                 areEqual(postalCode, obj2.postalCode) &&
 *                 areEqual(countryCode, obj2.countryCode);
 *     }
 * }
 */

// taken from http://www.javapractices.com/topic/TopicAction.do?Id=17

public class EqualsUtils {

    /** floating point comparison tolerance */
    public static final double EPSILON = 0.0000001;

    private EqualsUtils() {}

    /**
     * Compare two objects to determine whether they are equal or not.  Objects are equal if they are both null,
     * or if object1.equals(object2) is true.
     *
     * @param object1 The first object to compare
     * @param object2 The sceond object to compare
     * @return equality status
     */
    public static boolean areEqual(Object object1, Object object2) {
        return (object1 == null) ? (object2 == null) : object1.equals(object2);
    }

    /**
     * Compare two Float objects to determine whether they are equal or not.
     *
     * @param float1 The first Float object to compare
     * @param float2 The sceond Float object to compare
     * @return equality status
     */
    public static boolean areEqual(Float float1, Float float2) {
        if (float1 == null || float2 == null) {
            return float1 == null && float2 == null;
        }
        return areEqual(float1.floatValue(), float2.floatValue());
    }

    /**
     * Compare two Double objects to determine whether they are equal or not.
     *
     * @param double1 The first Double object to compare
     * @param double2 The sceond Double object to compare
     * @return equality status
     */
    public static boolean areEqual(Double double1, Double double2) {
        if (double1 == null || double2 == null) {
            return double1 == null && double2 == null;
        }
        return areEqual(double1.doubleValue(), double2.doubleValue());
    }

    /**
     * Compare two primitive booleans for equality
     *
     * @param boolean1 First value to compare
     * @param boolean2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(boolean boolean1, boolean boolean2) {
        return boolean1 == boolean2;
    }

    /**
     * Compare two primitive bytes for equality
     *
     * @param byte1 First value to compare
     * @param byte2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(byte byte1, byte byte2) {
        return byte1 == byte2;
    }

    /**
     * Compare two primitive chars for equality
     *
     * @param char1 First value to compare
     * @param char2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(char char1, char char2) {
        return char1 == char2;
    }

    /**
     * Compare two primitive doubles for equality
     *
     * @param double1 First value to compare
     * @param double2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(double double1, double double2) {
        // check that the two floats are equal within an acceptable range, due to floating point in-precision
        return Math.abs(double1 - double2) < EPSILON;
    }

    /**
     * Compare two primitive floats for equality
     *
     * @param float1 First value to compare
     * @param float2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(float float1, float float2) {
        //noinspection ImplicitNumericConversion
        // check that the two floats are equal within an acceptable range, due to floating point in-precision
        return Math.abs(float1 - float2) < EPSILON;
    }

    /**
     * Compare two primitive ints for equality
     *
     * @param int1 First value to compare
     * @param int2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(int int1, int int2) {
        return int1 == int2;
    }

    /**
     * Compare two primitive longs for equality
     *
     * @param long1 First value to compare
     * @param long2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(long long1, long long2) {
        return long1 == long2;
    }

    /**
     * Compare two primitive shorts for equality
     *
     * @param short1 First value to compare
     * @param short2 Second value to compare
     * @return equality status
     */
    public static boolean areEqual(short short1, short short2) {
        return short1 == short2;
    }

    // for testing code coverage
    static void testConstruct() {
        new EqualsUtils();
    }
}
