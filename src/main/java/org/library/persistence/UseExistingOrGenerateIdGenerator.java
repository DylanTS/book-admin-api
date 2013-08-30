package org.library.persistence;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IncrementGenerator;

/**
 * This becomes our strategy for generating IDs for our entities. We want to be able to generate
 * data through the data.json file, and in doing so, need to the ability to specify the ID of the
 * entity we create (so we can access it later). But at the same time, we want to be able to
 * generate the ID when creating these entities from user data. This implementation allows us to do
 * both, by using our supplied ID when generated and incrementing the highest ID of the entity in
 * concern when generating the ID here (using the parent class implementation).
 * 
 * @author dylants
 * 
 */
public class UseExistingOrGenerateIdGenerator extends IncrementGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        // pull the ID from the object
        Serializable id = session.getEntityPersister(null, obj).getClassMetadata()
                .getIdentifier(obj, session);
        // if it exists, use it
        if (id != null) {
            return id;
        } else {
            // use the parent class to generate the next ID to use
            return super.generate(session, obj);
        }
    }

}
