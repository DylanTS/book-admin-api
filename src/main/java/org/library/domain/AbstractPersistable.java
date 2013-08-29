package org.library.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.Persistable;

/**
 * Borrows heavily from Spring's {@link org.springframework.data.jpa.domain.AbstractPersistable},
 * but was changed so that we can specify the IDs ourselves without the ID dynamically generated.
 * This was necessary because of the initial creation done in the data.json file.
 * 
 * @author dylants
 * 
 * @param <ID>
 *            The ID type
 */
@MappedSuperclass
public abstract class AbstractPersistable<ID extends Serializable> implements Persistable<ID> {

    private static final long serialVersionUID = -89787050266927411L;

    @Id
    @GeneratedValue(generator = "IdOrGenerated")
    @GenericGenerator(name = "IdOrGenerated", strategy = "org.library.persistence.UseExistingOrGenerateIdGenerator")
    protected ID id;

    @JsonIgnore
    public boolean isNew() {
        return null == getId();
    }

    /**
     * @return the id
     */
    public ID getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

}