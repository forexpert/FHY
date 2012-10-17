package com.historydatacenter.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.historydatacenter.utils.EqualsUtils.*;
import static com.historydatacenter.utils.HashUtils.*;
import static com.historydatacenter.utils.ValidationUtils.*;

/**
 * An abstract base class to represent String/String attributes that can be attached to other persistent entities.
 *
 * Subclasses should add on via composition a reference to the owning object.  For example, a UserAttribute
 * would extend PersistentAttribute and include a reference to a User object.
 */
@MappedSuperclass
public abstract class PersistentAttribute<OwnerClass> extends GeneratedIdBaseEntity
{
    @Column(nullable=false)
    private String name;
    @Column(nullable=false, length=4096)
    private String value;

    protected abstract void setOwner(OwnerClass owner);

    protected PersistentAttribute() {}

    /**
     * Construct a PersistentAttribute with its name and value.
     *
     * @param name_val the name for this PersistentAttribute
     * @param value_val the value for this PersistentAttribute
     */
    protected PersistentAttribute(String name_val, String value_val) {
        validateStringNotEmpty(name_val, "PersistentAttribute.name must be non-empty");
        validateStringNotEmpty(value_val, "PersistentAttribute.value must be non-empty");

        name = name_val;
        value = value_val;
    }

    /**
     * Get the name of this PersistentAttribute
     *
     * @return the name of this PersistentAttribute
     */
    public String getName() {
        return name;
    }

    /**
     * Get the value of this PersistentAttribute
     *
     * @return the value of this PersistentAttribute
     */
    public String getValue() {
        return value;
    }

    private void setValue(String val) {
        value = val;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        // makes sure that the concrete class is the same -- wouldn't want a UserAttribute
        // being considered equal to a PersonAttribute
        if ((obj == null) || !getClass().equals(obj.getClass()))
            return false;

        PersistentAttribute obj2 = (PersistentAttribute) obj;
        return areEqual(name, obj2.name) &&
                areEqual(value, obj2.value);
    }

    @Override
    public int hashCode() {
        int result = HASH_UTILS_SEED;
        result = hash(result, name);
        result = hash(result, value);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(":[Id=");
        builder.append(safeString(getId()));
        builder.append(",name=");
        builder.append(safeString(name));
        builder.append(",value=");
        builder.append(safeString(value));
        builder.append(']');
        return builder.toString();
    }

    /**
     * Helper method for subclasses to implement getFooAttributes(), as such:
     *
     * <pre>
     * public class User
     * {
     *     private final Set<UserAttribute> userAttributes = new HashSet<UserAttribute>();
     *
     *     public Map<String,String> getUserAttributes()
     *     {
     *         return PersistentAttribute.buildMap(userAttributes);
     *     }
     * }
     * </pre>
     *
     * @param attributeSet the set of objects derived from PersistentAttribute to turn into
     *      a Map<String,String>
     * @return an unmodifiable Map representing the attributes attached to the object
     */
    public static<OwnerClass, AttributeClass extends PersistentAttribute<OwnerClass>>
            Map<String,String> buildMap(Set<AttributeClass> attributeSet)
    {
        Map<String,String> map = new HashMap<String,String>(attributeSet.size());
        for (AttributeClass attribute : attributeSet)
        {
            map.put(attribute.getName(), attribute.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Helper method for subclasses to implement getFooAttribute(String attributeName), as such:
     *
     * <pre>
     * public class User
     * {
     *     private final Set<UserAttribute> userAttributes = new HashSet<UserAttribute>();
     *
     *     public String getUserAttribute(String attributeName)
     *     {
     *         return PersistentAttribute.getAttribute(userAttributes, attributeName);
     *     }
     * }
     * </pre>
     *
     * @param attributeSet the set of objects derived from PersistentAttribute to turn into
     *      a Map<String,String>
     * @param attributeName the name of the desired attribute
     * @return the attribute value or null if the attribute does not exist
     */
    public static<OwnerClass, AttributeClass extends PersistentAttribute<OwnerClass>>
            String getAttribute(Set<AttributeClass> attributeSet, String attributeName)
    {
        for (AttributeClass attribute : attributeSet)
        {
            if (attribute.getName().equals(attributeName))
                return attribute.getValue();
        }
        return null;
    }

    /**
     * Helper method for subclasses to implement setFooAttribute(), as such:
     * <pre>
     * public class User
     * {
     *     private final Set<UserAttribute> userAttributes = new HashSet<UserAttribute>();
     *
     *     public void setUserAttribute(String name, String value)
     *     {
     *         PersistentAttribute.setAttribute(this, userAttributes, name, value, UserAttribute.class);
     *     }
     * }
     * </pre>
     *
     * This function will overwrite any existing attribute of the same name.
     *
     * @param owner the owner of the attribute collection (usually the <code>this</code> pointer of
     *      the caller
     * @param attributeSet the set to modify by adding or replacing the target attribute
     * @param name the attribute name
     * @param value the attribute value
     * @param attributeClass the Class of the PersistentAttribute subclass
     *
     * @return the newly created attribute object (already stored in the attributeSet)
     */
    public static<OwnerClass, AttributeClass extends PersistentAttribute<OwnerClass>>
            AttributeClass setAttribute(OwnerClass owner, Set<AttributeClass> attributeSet, String name,
            String value, Class<? extends AttributeClass> attributeClass)
    {
        validateNotNull(attributeSet, "setAttribute.attributeSet must be non-null");
        validateNotNull(name, "clearExistingAttribute.name must be non-null");
        validateNotNull(value, "clearExistingAttribute.value must be non-null");
        validateNotNull(attributeClass, "clearExistingAttribute.attributeClass must be non-null");

        for (AttributeClass existingAttribute : attributeSet)
        {
            if (existingAttribute.getName().equals(name))
            {
                // we must remove/re-add the existing attribute when changing its
                // value so that it gets filed in the correct hash bucket
                attributeSet.remove(existingAttribute);
                existingAttribute.setValue(value);
                attributeSet.add(existingAttribute);
                return existingAttribute;
            }
        }

        AttributeClass attribute;
        try
        {
            Constructor<? extends AttributeClass> constructor = attributeClass.getConstructor(String.class,
                    String.class);
            attribute = constructor.newInstance(name, value);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("can't instantiate class: " + attributeClass.getName(), e);
        }

        attributeSet.add(attribute);
        attribute.setOwner(owner);

        return attribute;
    }

    /**
     * Helper method for subclasses to implement removeFooAttribute(), as such:
     * <pre>
     * public class User
     * {
     *     private final Set<UserAttribute> userAttributes = new HashSet<UserAttribute>();
     *
     *     public void removeUserAttribute(String name)
     *     {
     *         PersistentAttribute.clearAttribute(userAttributes, name);
     *     }
     * }
     * </pre>
     *
     * @param attributeSet the set to modify by removing the target attribute
     * @param name the attribute name
     */
    public static<OwnerClass, AttributeClass extends PersistentAttribute<OwnerClass>>
            void clearAttribute(Set<AttributeClass> attributeSet, String name)
    {
        validateNotNull(attributeSet, "clearExistingAttribute.attributeSet must be non-null");
        validateNotNull(name, "clearExistingAttribute.name must be non-null");

        for (AttributeClass existingAttribute : attributeSet)
        {
            if (existingAttribute.getName().equals(name))
            {
                attributeSet.remove(existingAttribute);
                existingAttribute.setOwner(null);
                return;
            }
        }
    }
}
