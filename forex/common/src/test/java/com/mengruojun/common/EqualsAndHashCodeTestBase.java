package com.mengruojun.common;

import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Facilitate testing of equals() and hashCode() methods
 */
public abstract class EqualsAndHashCodeTestBase extends EqualsTestBase {
  @Test
  public void testHashCode() {
    Collection<Object> equals = getEqualObjects();

    Iterator<Object> it_eq1 = equals.iterator();
    while (it_eq1.hasNext()) {
      Object eq1 = it_eq1.next();

      Iterator<Object> it_eq2 = equals.iterator();
      while (it_eq2.hasNext()) {
        Object eq2 = it_eq2.next();
        assertTrue(eq1.equals(eq2));
        assertEquals("equal objects have unequal hash codes: [" + eq1.toString() + "] and [" +
                eq2.toString() + "]", eq1.hashCode(), eq2.hashCode());
      }
    }

  }

}
