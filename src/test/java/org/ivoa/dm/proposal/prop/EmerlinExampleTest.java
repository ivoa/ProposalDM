/*
 * Created on 20 Jan 2022 
 * Copyright 2022 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.hibernate.engine.spi.EffectiveEntityGraph;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.TAC;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;


/**
 * Tests concepts needed for e-MERLIN configuration.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
class EmerlinExampleTest extends AbstractProposalTest {
    /**
     * @param ef
     */
    public EmerlinExampleTest() {
        super(new exampleFactory() {

            @Override
            public ExampleGenerator create() {
                return new EmerlinExample();
            }
        });
       
    }

   @org.junit.jupiter.api.Test 
   public  void testObservations() {
       ObservingProposal prop = propDbInOut();
       assertEquals( 1, prop.observations.size(), "number of observations");
       Observation obs = prop.observations.get(0);
       assertNotNull(obs.target);
       assertTrue(obs.target instanceof CelestialTarget);
      
   }
     @org.junit.jupiter.api.Test 
   public  void testDeleteTarget() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        proposal.persistRefs(em);
        em.persist(proposal);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Observation obs = proposal.observations.get(0);
       assertNotNull(obs.target);
      
       assertTrue(obs.target instanceof CelestialTarget);
       em.remove(obs.target);
       em.getTransaction().commit();
       
       
   }
   
     @org.junit.jupiter.api.Test 
   public  void testDbCreate() {
       
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        proposal.persistRefs(em);
        em.persist(proposal);
        final ProposalCycle cycle = ex.getCycle();
        cycle.persistRefs(em);
        em.persist(cycle);
        em.getTransaction().commit();

   }
   
      @org.junit.jupiter.api.Test 
   public void testJackson() throws JsonProcessingException
   {
   
     
    List<Field> fields = Arrays.asList(new TargetField("first"), new TargetField("second"),
               new Point(new org.ivoa.dm.stc.coords.EquatorialPoint(new RealQuantity(45.0, BaseExample.degrees ), new RealQuantity(45.0, BaseExample.degrees ), ex.getICRS()),"a point") );
       String json = ProposalModel.jsonMapper().writeValueAsString(fields.get(0));
       assertEquals("{\"@type\":\"proposal:TargetField\",\"_id\":0,\"name\":\"first\"}", json);
       System.out.println(json);
       System.out.println(ProposalModel.jsonMapper().writeValueAsString(new TargetField("targetField")));
       
       
   }
      @org.junit.jupiter.api.Test 
      public void testJacksonOjectIds( ) throws JsonProcessingException {
          
          ProposalManagementModel pm = new ProposalManagementModel();
          pm.addContent(ex.getCycle());
          pm.processReferences();
          TAC tac = pm.getContent(ProposalCycle.class).get(0).getTac();
          String json = ProposalManagementModel.jsonMapper().writeValueAsString(tac);
          System.out.println(json);
          TAC ntac = ProposalModel.jsonMapper().readValue(json, TAC.class);
          
          String njn = "{\n"
                  + "    \"members\": [\n"
                  + "        {\n"
                  + "            \"role\": \"CHAIR\",\n"
                  + "            \"member\": {\n"
                  + "                \"_id\": 1,\n"
                  + "                \"person\": {\n"
                  + "                    \"_id\": 1,\n"
                  + "                    \"fullName\": \"TAC Chair\",\n"
                  + "                    \"eMail\": \"tacchair@unreal.not.email\",\n"
                  + "                    \"orcidId\": {\n"
                  + "                        \"value\": \"https://notreallyorcid.org/0000-0001-0002-005\"\n"
                  + "                    },\n"
                  + "                    \"homeInstitute\": {\n"
                  + "                        \"@type\": \"proposal:Organization\",\n"
                  + "                        \"_id\": 1,\n"
                  + "                        \"name\": \"org2\",\n"
                  + "                        \"address\": \"org2 address\",\n"
                  + "                        \"ivoid\": {\n"
                  + "                            \"value\": \"ivo://org/org2\"\n"
                  + "                        },\n"
                  + "                        \"wikiId\": null\n"
                  + "                    }\n"
                  + "                }\n"
                  + "            }\n"
                  + "        },\n"
                  + "        {\n"
                  + "            \"role\": \"SCIENCEREVIEWER\",\n"
                  + "            \"member\": {\n"
                  + "                \"_id\": 2,\n"
                  + "                \"person\": {\n"
                  + "                    \"_id\": 2,\n"
                  + "                    \"fullName\": \"TAC member\",\n"
                  + "                    \"eMail\": \"tacmamber@unreal.not.email\",\n"
                  + "                    \"orcidId\": {\n"
                  + "                        \"value\": \"https://notreallyorcid.org/0000-0001-0002-006\"\n"
                  + "                    },\n"
                  + "                    \"homeInstitute\": {\n"
                  + "                        \"@type\": \"proposal:Organization\",\n"
                  + "                        \"_id\": 2,\n"
                  + "                        \"name\": \"org\",\n"
                  + "                        \"address\": \"org address\",\n"
                  + "                        \"ivoid\": {\n"
                  + "                            \"value\": \"ivo://org/anorg\"\n"
                  + "                        },\n"
                  + "                        \"wikiId\": null\n"
                  + "                    }\n"
                  + "                }\n"
                  + "            }\n"
                  + "        }\n"
                  + "    ]\n"
                  + "}\n";
          
          TAC ntac2 = ProposalModel.jsonMapper().readValue(njn, TAC.class);
                  
      }
   
   @org.junit.jupiter.api.Test 
   public  void testDbQuery() throws JsonProcessingException {
       
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name());
        em.getTransaction().begin();
  
        final ProposalCycle cycle = ex.getCycle();
        cycle.persistRefs(em);
        em.persist(cycle);
        em.getTransaction().commit();
        
       TypedQuery<TAC> q = em.createQuery("SELECT o FROM TAC o", TAC.class);
       TAC tac = q.getSingleResult();
       System.out.println(tac.getMembers().get(0).getId());
       String json = ProposalManagementModel.jsonMapper().writeValueAsString(tac);
       System.out.println(json);

   }
   
   @org.junit.jupiter.api.Test 
   public void testListupdate() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        proposal.persistRefs(em);
        em.persist(proposal);
        em.getTransaction().commit();

        final Observation observation = proposal.observations.get(0);
        final long obId = observation.getId();
        TimingWindow con = (TimingWindow) observation.getConstraints().get(0);

        assertNotNull(con);
        final long conId = con.getId();
        TimingWindow tw2 = new TimingWindow(con);
        tw2.setNote("this is updated");
        con.updateUsing(tw2);
        assertEquals("this is updated", tw2.getNote());
        em.getTransaction().begin();
        em.merge(con);
        em.getTransaction().commit();
        em.clear();
        List<ObservingProposal> props = em.createQuery("select s from ObservingProposal s", ObservingProposal.class).getResultList();
        assertEquals("this is updated", ((TimingWindow)props.get(0).observations.get(0).getConstraints().get(0)).getNote());

      String qlString = "select child from Observation "
            + " parent join parent.constraints child "
            + " where parent._id = :pid and child._id = :cid"
            //  + " and Type(child) = "+ childType.getName()
            ;


       em.getTransaction().begin();
      TypedQuery<TimingWindow> q = em.createQuery(
            qlString,
            org.ivoa.dm.proposal.prop.TimingWindow.class
      );

      q.setParameter("pid", obId);
      q.setParameter("cid", conId);

      org.ivoa.dm.proposal.prop.TimingWindow con2 = q.getSingleResult();
      tw2.setNote("another change");
      con2.updateUsing(tw2);
 
       
        em.merge(con2);
        em.getTransaction().commit();
        em.clear();
        List<ObservingProposal> props2 = em.createQuery("select s from ObservingProposal s", ObservingProposal.class).getResultList();
        assertEquals("another change", ((TimingWindow)props2.get(0).observations.get(0).getConstraints().get(0)).getNote());

        
        
   }
   
   
  
 
}


