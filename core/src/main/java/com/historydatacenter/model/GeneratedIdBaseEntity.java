package com.historydatacenter.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * A base class that gives a generated Id to a persistable class.
 */
@MappedSuperclass
public abstract class GeneratedIdBaseEntity implements Serializable 
{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    @SuppressWarnings({"UnusedDeclaration"})
    private long version;

    protected GeneratedIdBaseEntity() {}

    /**
     * Get the persisted database Id of this object, or null if this object is not persisted to the database
     * @return the persistence id of this object
     */
    public Long getId() {
        return id;
    }

    /**
     * Should never be used in production -- for testing support only...
     *
     * @param id_val the id to set
     */
    protected void setId(Long id_val) {
        id = id_val;
    }

    /** helpful for displaying if an entity's update was successful */
    public long getVersion()
    {
        return version;
    }
}
