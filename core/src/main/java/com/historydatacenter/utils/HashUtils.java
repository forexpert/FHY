package com.historydatacenter.utils;

import java.lang.reflect.Array;

/**
 * Useful methods for helping to write .hashCode() methods
 */

// taken from http://www.javapractices.com/topic/TopicAction.do?Id=28
   
@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
public class HashUtils {

    private HashUtils() {}

    /**
    * An initial value for a <code>hashCode</code>, to which is added contributions
    * from fields. Using a non-zero value decreases collisons of <code>hashCode</code>
    * values.
    */
    public static final int HASH_UTILS_SEED = 23;

    // package scope for testing support
    static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm( int aSeed ){
        return fODD_PRIME_NUMBER * aSeed;
    }

    private static boolean isArray(Object aObject){
        return aObject.getClass().isArray();
    }

    /**
     * Extend a hash code by considering a boolean value.
     *
     * @param aSeed the hash code accumulator
     * @param aBoolean the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed, boolean aBoolean ) {
        return firstTerm( aSeed ) + ( aBoolean ? 1 : 0 );
    }

    /**
     * Extend a hash code by considering a byte value.
     *
     * @param aSeed the hash code accumulator
     * @param aByte the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , byte aByte ) {
        return firstTerm( aSeed ) + (int) aByte;
    }

    /**
     * Extend a hash code by considering a char value.
     *
     * @param aSeed the hash code accumulator
     * @param aChar the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed, char aChar ) {
        return firstTerm( aSeed ) + (int) aChar;
    }

    /**
     * Extend a hash code by considering a double value.
     *
     * @param aSeed the hash code accumulator
     * @param aDouble the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , double aDouble ) {
        return hash( aSeed, Double.doubleToLongBits(aDouble) );
    }

    /**
     * Extend a hash code by considering a float value.
     *
     * @param aSeed the hash code accumulator
     * @param aFloat the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , float aFloat ) {
        return hash( aSeed, Float.floatToIntBits(aFloat) );
    }

    /**
     * Extend a hash code by considering an int value.
     *
     * @param aSeed the hash code accumulator
     * @param aInt the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , int aInt ) {
        return firstTerm( aSeed ) + aInt;
    }

    /**
     * Extend a hash code by considering a long value.
     *
     * @param aSeed the hash code accumulator
     * @param aLong the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , long aLong ) {
        return firstTerm(aSeed)  + (int)( aLong ^ (aLong >>> 32) );
    }

    /**
     * Extend a hash code by considering a short value.
     *
     * @param aSeed the hash code accumulator
     * @param aShort the value to add to the hash code
     * @return the updated hash code accumulator
     */
    public static int hash( int aSeed , short aShort ) {
        return firstTerm(aSeed)  + (int) aShort;
    }

    /**
     * Extend a hash code by considering an Object.
     *
     * <code>aObject</code> is a possibly-null object field, and possibly an array.
     *
     * If <code>aObject</code> is an array, then each element may be a primitive
     * or a possibly-null object.
     *
     * @param aSeed hashCode accumulator value before hashing aObject
     * @param aObject object to hash into hashCode accumulator value
     *
     * @return hashCode accumulator value after hashing aObject
     */
    public static int hash( int aSeed , Object aObject ) {
        int result = aSeed;
        if ( aObject == null) {
            result = hash(result, 0);
        }
        else if ( ! isArray(aObject) ) {
            result = hash(result, aObject.hashCode());
        }
        else {
            int length = Array.getLength(aObject);
            for ( int idx = 0; idx < length; ++idx ) {
                Object item = Array.get(aObject, idx);
                //recursive call!
                result = hash(result, item);
            }
        }
        return result;
    }

    // for testing code coverage
    static void testConstruct() {
        new HashUtils();
    }
}
