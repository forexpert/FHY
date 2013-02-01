package com.mengruojun.common;

import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Facilitate testing of equals() and hashCode() methods
 */
public abstract class EqualsTestBase {

  protected abstract Collection<Object> getEqualObjects();

  protected abstract Collection<Object> getNotEqualObjects();

  @Test
  public void testEquals() {
    Collection<Object> equals = getEqualObjects();

    Iterator<Object> it_eq1 = equals.iterator();
    while (it_eq1.hasNext()) {
      Object eq1 = it_eq1.next();

      assertFalse(eq1.equals(new Object()));
      assertFalse(eq1.equals(null));

      Iterator<Object> it_eq2 = equals.iterator();
      while (it_eq2.hasNext()) {
        Object eq2 = it_eq2.next();
        assertTrue("should be equal but aren't: [" + eq1.toString() + "] and [" + eq2.toString() + "]", eq1.equals(eq2));
        //assertTrue("string representations should be equal: [" + eq1.toString() + "] and [" +
        //        eq2.toString() + "]", eq1.toString().equals(eq2.toString()));
      }
    }

  }

  @Test
  public void testNotEquals() {
    Collection<Object> equals = getEqualObjects();
    Collection<Object> notEquals = getNotEqualObjects();

    Iterator<Object> it_eq = equals.iterator();
    while (it_eq.hasNext()) {
      Object eq1 = it_eq.next();

      Iterator<Object> it_neq = notEquals.iterator();
      while (it_neq.hasNext()) {
        Object neq1 = it_neq.next();
        assertFalse("should not be equal but are: [" + eq1.toString() + "] and [" + neq1.toString() + "]",
                eq1.equals(neq1));
      }
    }

    Iterator<Object> it_neq1 = notEquals.iterator();
    while (it_neq1.hasNext()) {
      Object neq1 = it_neq1.next();

      Iterator<Object> it_neq2 = notEquals.iterator();
      while (it_neq2.hasNext()) {
        Object neq2 = it_neq2.next();
        if (neq1 != neq2)
          assertFalse("should not be equal but are: [" + neq1.toString() + "] and [" + neq2.toString() + "]",
                  neq1.equals(neq2));
      }
    }
  }

}