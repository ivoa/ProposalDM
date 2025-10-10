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

import jakarta.xml.bind.JAXBException;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.proposal.management.TAC;

import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Order;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;


/**
 * Tests concepts needed for e-MERLIN configuration.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
class EmerlinExampleTest extends AbstractProposalTest {


   public EmerlinExampleTest() {
      super(ProposalManagementModel.modelDescription,new EmerlinFactory());
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
                  + "            \"role\": \"Chair\",\n"
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
                  + "            \"role\": \"ScienceReviewer\",\n"
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
   public void testCopy() {
       
       ProposalModel model = new ProposalModel();
       model.createContext();
       final ObservingProposal proposal = ex.getProposal();
       model.addContent(proposal);
       model.processReferences();
       final ObservingProposal cprop = new ObservingProposal(proposal);
       cprop.updateClonedReferences();
       proposal.observations.get(0).target.get(0).sourceName="changed";
       assertEquals("fictional", cprop.observations.get(0).target.get(0).sourceName);
       
       Observation obs = proposal.getObservations().get(0);
       Observation obs2 = obs.copyMe();
       TargetObservation tobs2 = (TargetObservation) obs2;    
       assertNotNull(tobs2);
   }


   @org.junit.jupiter.api.Test
   public void testListupdate() {

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
            + " and Type(child) = TimingWindow";
      ;


      em.getTransaction().begin();
      TypedQuery<ObservingConstraint> q = em.createQuery(
            qlString,
            org.ivoa.dm.proposal.prop.ObservingConstraint.class
      );

      q.setParameter("pid", obId);
      q.setParameter("cid", conId);

      org.ivoa.dm.proposal.prop.TimingWindow con2 = (TimingWindow) q.getSingleResult();
      tw2.setNote("another change");
      con2.updateUsing(tw2);


      em.merge(con2);
      em.getTransaction().commit();
      em.clear();
      List<ObservingProposal> props2 = em.createQuery("select s from ObservingProposal s", ObservingProposal.class).getResultList();
      assertEquals("another change", ((TimingWindow)props2.get(0).observations.get(0).getConstraints().get(0)).getNote());



   }

   protected ProposalCycle doTacWork() { //TODO this probably needs refactoring with new ordered tests


      ProposalManagementModel pm = new ProposalManagementModel();
      pm.createContext();
      SubmittedProposal sprop = ex.submitProposal(proposal, cycle);
      sprop.updateClonedReferences();
      ex.allocateProposal(sprop);
      return cycle;

   }

   @org.junit.jupiter.api.Test
   public  void testTACQuery() throws JsonProcessingException {
      TypedQuery<TAC> q = em.createQuery("SELECT o FROM TAC o", TAC.class);
      TAC tac = q.getSingleResult();
      System.out.println(tac.getMembers().get(0).getId());
      String json = ProposalManagementModel.jsonMapper().writeValueAsString(tac);
      System.out.println(json);

   }


   @org.junit.jupiter.api.Test
   void reviewJSONTest() throws JsonProcessingException  {


      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(doTacWork());
      model.processReferences();

      ProposalManagementModel modelin = modelRoundTripJSONwithTest(model);
      List<ProposalCycle> revs = modelin.getContent(ProposalCycle.class);
      assertEquals(1, revs.size());

   }

   @org.junit.jupiter.api.Test
   @Order(1010)
   void reviewJaxbTest() throws JAXBException,
         TransformerConfigurationException, ParserConfigurationException,
         TransformerFactoryConfigurationError, TransformerException {
      logger.debug("starting test");
      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(doTacWork());
      model.processReferences();
      validateModel(model);
      ProposalManagementModel modelin = modelRoundTripXMLwithTest(model);
      List<ProposalCycle> revs = modelin.getContent(ProposalCycle.class);
      assertEquals(1, revs.size());

   }

   @Override
   protected void doUrTest() {
      ex = ef.create();

      em.getTransaction().begin();
      final ObservingProposal localproposal = ex.getProposal();
      final ProposalCycle thecycle = ex.getCycle();
      ProposalManagementModel model = new ProposalManagementModel();
      model.addContent(localproposal);
      model.addContent(thecycle);
      model.management().persistRefs(em);
      em.persist(localproposal);
      em.persist(thecycle);
      em.getTransaction().commit();

      //flush any existing entities
      em.clear();
      em.getEntityManagerFactory().getCache().evictAll();

      proposalId = localproposal.getId();
      retrieveProposal();
      cycleId = thecycle.getId();
      retrieveCycle();

   }


}


