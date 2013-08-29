package org.library.persistence;

import java.io.Serializable;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.SequenceGenerator;

/**
 * This becomes our strategy for generating IDs for our entities. We want to be able to generate
 * data through the data.json file, and in doing so, need to the ability to specify the ID of the
 * entity we create (so we can access it later). But at the same time, we want to be able to
 * generate the ID when creating these entities from user data. This implementation allows us to do
 * both, and also (hopes to) prevent generating an ID we've already manually specified by adding
 * 1000 to all generated IDs. In this way we hope to "floor" the generated ID number to allow for
 * 1000 entities of test data (if needed).
 * 
 * @author dylants
 * 
 */
public class UseExistingOrGenerateIdGenerator extends SequenceGenerator {

    @Override
    public Serializable generate(SessionImplementor session, Object obj) {
        // pull the ID from the object
        Serializable id = session.getEntityPersister(null, obj).getClassMetadata()
                .getIdentifier(obj, session);
        // if it exists, use it
        if (id != null) {
            return id;
        } else {
            // floor the IDs that are generated at 1000 to allow for test data
            Serializable generatedID = super.generate(session, obj);
            if (generatedID instanceof Long) {
                generatedID = ((Long) generatedID) + 1000L;
            }
            return generatedID;
        }
    }

}
