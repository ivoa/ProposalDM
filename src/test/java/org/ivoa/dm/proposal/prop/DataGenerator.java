package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Creates some test data that can be imported via sql.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 17 Mar 2022
 */
public class DataGenerator {
    public static void main(String[] args) {
        Map<String, String> props = new HashMap<String, String>();
        // this is done with done to local postgres which obviously needs to be running beforehand.
        String puname = ProposalModel.pu_name();
        props.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/" + puname);
        props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        props.put("hibernate.globally_quoted_identifiers", "true");
        props.put("hibernate.hbm2ddl.schema-generation.script.append", "false");
        props.put("javax.persistence.schema-generation.create-source", "metadata");
        props.put("javax.persistence.schema-generation.database.action", "drop-and-create");
        props.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        props.put("javax.persistence.schema-generation.scripts.create-target", "test.sql");
        props.put("javax.persistence.schema-generation.scripts.drop-target", "test-drop.sql");

        props.put("javax.persistence.jdbc.user", System.getProperty("user.name"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(puname, props);
        EntityManager em = emf.createEntityManager();

        EmerlinExample ex = new EmerlinExample();
        em.getTransaction().begin();
        em.persist(ex.getCycle());
        em.getTransaction().commit();



    }
}
