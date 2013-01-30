package com.mengruojun.common.utils;

import java.util.Collection;

/**
 * @author Brien,eman.
 * Some useful utilities for validating API parameters and protecting functions
 * against null objects. 
 */
@SuppressWarnings({"ResultOfObjectAllocationIgnored", "InstantiationOfUtilityClass"})
public class ValidationUtils
{
    /**
     * Private no-arg constructor
     */
    private ValidationUtils() {}

    /**
     * Ensures the object is null.
     * @param object the object that is to be null.
     * @param error the error message that gets thrown if the object is not null.
     */
    public static void validateNull(Object object, String error) {
        if (object != null)
            throw new IllegalArgumentException(error);
    }

    /**
     * Ensures the object is not null.
     * @param object the object that is not to be null.
     * @param error the error message that gets thrown if the object is null.
     */
    public static void validateNotNull(Object object, String error) {
        if (object == null)
            throw new IllegalArgumentException(error);
    }

    /**
     * Ensures the given string is not null and not blank.
     * @param string the string that is not to be empty.
     * @param error the error message that gets thrown if the string is empty.
     */
    public static void validateStringNotEmpty(String string, String error) {
        if (isEmpty(string))
            throw new IllegalArgumentException(error);
    }

    /**
     * A String isEmpty if it is null or is composed entirely of whitespace.
     *
     * @param string The string to test
     * @return true if it's empty, false otherwise.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * Ensures the given collection is not null and not empty.
     * @param collection the collection that is not to be empty.
     * @param error the error message that gets thrown if the collection is empty.
     */
    public static void validateCollectionNotEmpty(Collection collection, String error) {
        if (collection == null || collection.isEmpty())
            throw new IllegalArgumentException(error);
    }

    /**
     * Ensures the given object is contained in the given collection.
     * @param obj the object.
     * @param collection the collection of allowed objects.
     * @param error the error message that gets thrown if the object is not in the collection.
     */
    public static <T> void validateContains(T obj, Collection<T> collection, String error)
    {
        if (obj == null || !collection.contains(obj))
            throw new IllegalArgumentException(error);
    }

    /**
     * Ensures the given string is less than a certain length.  null values pass this test
     * @param string the string that is to be less than the given length.
     * @param length the length of string at which the error is thrown (string.length() == length fails) 
     * @param error the error message that gets thrown if the string is empty.
     */
    public static void validateStringLessThan(String string, int length, String error) {
        if (string != null && string.length() >= length)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that the given integer is not null and not zero. Useful for
     *      verifying arguments like port numbers, etc.
     * @param integer the integer that must not be null or zero.
     * @param error the error message that will be associated with the thrown exception if
     *      the integer is null or zero.
     */
    public static void validateNonZero(Integer integer,String error)
    {
        if (integer == null || integer == 0)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that the given double is not null and not positive. Useful for
     *      verifying arguments like commisions, etc.
     * @param d the double that must not be null or positive.
     * @param error the error message that will be associated with the thrown exception if
     *      the double is null or positive.
     */
    public static void validateNotPositive(Double d, String error)
    {
        if (d == null || d > 0.0)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that the given double is not null and not negative. Useful for
     *      verifying arguments like volume, etc.
     * @param d the double that must not be null or negative.
     * @param error the error message that will be associated with the thrown exception if
     *      the double is null or negative.
     */
    public static void validateNotNegative(Long lval, String error)
    {
        if (lval == null || lval < 0.0)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that the given long is not null and greater than zero. Useful for
     *      verifying arguments like tracker volume, etc.
     * @param lval the long that must not be null and greater than zero.
     * @param error the error message that will be associated with the thrown exception if
     *      the integer is null or zero.
     */
    public static void validateGreaterThanZero(Long lval, String error)
    {
        if (lval == null || lval <= 0)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that a supplied numeric value is between a lower bound and an upper bound (inclusive).
     *
     * @param lower lower legal bound
     * @param upper upper legal bound
     * @param value value to test
     * @param error the text to put into an IllegalArgumentException if the value is out of bounds
     * @throws IllegalArgumentException if the value is out of bounds
     */
    public static void validateNumberBetween(int lower, int upper, int value, String error)
    {
        if (value < lower || value > upper)
            throw new IllegalArgumentException(error);
    }

    /**
     * Null-safe toString for a given object.
     * @param object the object in question.
     * @return the null-safe string representation of the object.
     */
    public static String safeString(Object object) {
        return object == null ? "null" : object.toString();
    }

    /**
     * Null-safe, deep toString method for a collection of objects.
     * @param builder a {@link StringBuilder} which gets appended the values from the given collection
     * @param objects the collection of objects to stringify.
     */
    public static void objectList(StringBuilder builder, Collection objects) {
        if (objects == null)
            return;

        String prefix = "";
        for (Object object : objects) {
            builder.append(prefix);
            builder.append(safeString(object));
            prefix = ",";
        }
    }

    /**
     * Validates that at least one object in the given list is null.
     * @param error the error message associated with the exception thrown if none of the objects are null.
     * @param objects the objects at least one of which must be null.
     * @throws IllegalArgumentException if none of the given objects are null. 
     */
    public static void validateAtLeastOneNull(String error,Object ... objects)
    {
        for (Object object:objects)
        {
            if (object == null) return;
        }
        throw new IllegalArgumentException(error);
    }

    /**
     * Validates that at least one object in the given list is not null.
     * @param error the error message associated with the exception thrown if none of the objects are null.
     * @param objects the objects at least one of which must be null.
     * @throws IllegalArgumentException if none of the given objects are null.
     */
    public static void validateAtLeastOneNotNull(String error,Object ... objects)
    {
        for (Object object:objects)
        {
            if (object != null) return;
        }
        throw new IllegalArgumentException(error);
    }

    /**
     * Validates that the condition is true
     * @param condition condition to test
     * @param error the error message associated with the exception thrown if the condition is not true.
     * @throws IllegalArgumentException If the condition is not true
     */
    public static void validateTrue(boolean condition, String error)
    {
        if (!condition)
            throw new IllegalArgumentException(error);
    }

    /**
     * Validates that two objects are equal.
     * @param obj the object to validate.
     * @param expected the object to compare against.
     * @param error the error message associated with the exception thrown if the two objects aren't equal.
     */
    public static <T> void validateEquals(T obj, T expected, String error)
    {
        if (expected == null) {
            if (obj != null)
                throw new IllegalArgumentException(error);
        }
        else {
            if (!expected.equals(obj))
                throw new IllegalArgumentException(error);
        }
    }

    /**
     * For covergae testing
     */
    static void testConstruct() {
        new ValidationUtils();
    }
}
