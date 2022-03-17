package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates some test data that can be imported via sql.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 17 Mar 2022
 */
public class DataGenerator {
    public static void main(String[] args) {
        Map<String, String> props = new HashMap();
        // this is done with h2 db as that seems to be the only one that has a command to do this to sql insert statements
        String puname = "vodml_coords";
        props.put("javax.persistence.jdbc.url", "jdbc:h2:./h2test2:" + puname + ";DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false;");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        props.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        props.put("hibernate.globally_quoted_identifiers", "true");
        props.put("hibernate.hbm2ddl.schema-generation.script.append", "false");
        props.put("javax.persistence.schema-generation.create-source", "metadata");
        props.put("javax.persistence.schema-generation.database.action", "drop-and-create");
        props.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        props.put("javax.persistence.schema-generation.scripts.create-target", "h2test.sql");
        props.put("javax.persistence.schema-generation.scripts.drop-target", "h2test-drop.sql");

        props.put("javax.persistence.jdbc.user", "sa");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(puname, props);
        EntityManager em = emf.createEntityManager();

        EmerlinExample ex = new EmerlinExample();
        em.getTransaction().begin();
        em.persist(ex.getCycle());
        em.getTransaction().commit();

        Session session = em.unwrap(Session.class);
        session.doWork(new Work() {

            @Override
            public void execute(Connection con) throws SQLException {
               Statement stmt = con.createStatement();
               stmt.execute("SCRIPT simple TO 'testData.sql'");
            }
        });




    }
}
