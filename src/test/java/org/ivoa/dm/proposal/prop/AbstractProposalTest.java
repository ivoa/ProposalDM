/*
 * Created on 20 Jan 2022 
 * Copyright 2022 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.ivoa.dm.proposal.prop.BaseExample.makeList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.ivoa.dm.proposal.management.*;
import org.ivoa.dm.stc.coords.SpaceSys;
import org.ivoa.vodml.ModelDescription;
import org.junit.jupiter.api.*;

/**
 * An abstract base class that creates entities that would be similar for all proposals.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // to share some of the database setup
public abstract class AbstractProposalTest extends org.ivoa.vodml.testing.AbstractTest {

    /** logger for this class */
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(AbstractProposalTest.class);

    protected  ExampleGenerator ex;
    protected ExampleFactory ef;
    protected  EntityManager em;
    protected  Long proposalId;
    protected ObservingProposal proposal;
    private final ModelDescription modelDescription;
    protected Long cycleId;
    protected ProposalCycle cycle;
    protected ProposalManagementModel model;

   @BeforeAll
    public void beforeAll() {
       em = setupH2Db(ProposalModel.pu_name(),modelDescription.allClassNames());
    }
    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {
    }
       /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {
    }

   protected void retrieveProposal() {
      em.getTransaction().begin();
      List<ObservingProposal> props = em.createNamedQuery("ObservingProposal.findById", ObservingProposal.class)
            .setParameter("id", proposalId).getResultList();
      assertEquals(1, props.size());
      proposal = props.get(0);
      em.getTransaction().commit();
   }
   protected void retrieveCycle() {
      em.getTransaction().begin();
      List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
            .setParameter("id", cycleId).getResultList();
      assertEquals(1, props.size());
      cycle = props.get(0);
      em.getTransaction().commit();
   }



   public interface ExampleFactory {
        ExampleGenerator create();
    }

    public AbstractProposalTest(ExampleFactory ef) {
       this.ef= ef;
       model = new ProposalManagementModel();
       this.modelDescription = model.management().description();

    }




    @Test
    @Order(1)
    void UrTest() { // this test has to be done first to make sure that the basic database save has been done
       ex = ef.create();

       em.getTransaction().begin();
       proposal = ex.getProposal();
       cycle = ex.getCycle();
       model.addContent(proposal);
       model.addContent(cycle);
       model.management().persistRefs(em);
       em.persist(proposal);
       em.persist(cycle);
       em.getTransaction().commit();
       proposalId = proposal.getId();
       cycleId = cycle.getId();


    }


   /**
    * This test just tests basic XML serialization of a proposal as standalone
    * @throws JAXBException
    * @throws TransformerConfigurationException
    * @throws ParserConfigurationException
    * @throws TransformerFactoryConfigurationError
    * @throws TransformerException
    */
    @org.junit.jupiter.api.Test
    @Order(1000)
    void proposalDmJaxbTest() throws JAXBException, TransformerConfigurationException,
    ParserConfigurationException, TransformerFactoryConfigurationError,
    TransformerException {
        logger.debug("starting test");
        
        ProposalModel model = new ProposalModel();
        model.addContent(ex.getProposal());
        model.processReferences();
        validateModel(model);

        ProposalModel modelin = modelRoundTripXMLwithTest(model);
        List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
        assertEquals(1, props.size());

    }

   /**
    * This tests basic JSON serialization of the proposal as standalone
    * @throws JsonProcessingException
    */
   @org.junit.jupiter.api.Test
   @Order(1000)
   void proposalDmJSONTest() throws JsonProcessingException {
      ProposalModel model = new ProposalModel();
      model.addContent(ex.getProposal());
      model.processReferences();
      ProposalModel modelin = modelRoundTripJSONwithTest(model);
      List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
      assertEquals(1, props.size());
   }

    @org.junit.jupiter.api.Test
    @Order(1010)
    void proposalDmJPATest() {
        assertEquals( 2, proposal.getInvestigators().size(),"number of investigators"); // trivial test
        assertEquals( 1,proposal.getObservations().size(), "number of observations");
        Observation obs = proposal.getObservations().get(0);
        TechnicalGoal tech = obs.getTechnicalGoal();
        assertNotNull(tech);
        assertEquals( 2,tech.spectrum.size(), "number of spectral setups");
    }






  @org.junit.jupiter.api.Test
  @Order(1010)
  void proposalOnlyJSONTest() throws JsonProcessingException {
     ProposalModel model = new ProposalModel();
     ObjectMapper mapper = ProposalModel.jsonMapper();
     model.addContent(ex.getProposal());
     model.processReferences();
     String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(model.getContent(ObservingProposal.class).get(0));
        System.out.println("JSON output"); 
        System.out.println(json);
        ObservingProposal retval = mapper.readValue(json, ObservingProposal.class);
        assertNotNull(retval);
  }
@org.junit.jupiter.api.Test
@Order(1010)
public void testObservations() {
       assertEquals( 1, proposal.observations.size(), "number of observations");
       Observation obs = proposal.observations.get(0);
       assertNotNull(obs.target);
   assertInstanceOf(CelestialTarget.class, obs.target.get(0));
       CelestialTarget target = (CelestialTarget)obs.target.get(0);
       SpaceSys cosys = target.sourceCoordinates.getCoordSys();
       assertNotNull(cosys);
       
      
   }



   @Test
   @Order(1110)
   public  void testTACQuery() throws JsonProcessingException {
      TypedQuery<TAC> q = em.createQuery("SELECT o FROM TAC o", TAC.class);
      TAC tac = q.getSingleResult();
      System.out.println(tac.getMembers().get(0).getId());
      String json = ProposalManagementModel.jsonMapper().writeValueAsString(tac);
      System.out.println(json);

   }

   @Test
   @Order(1120)
   void doTOCworkTest() throws JAXBException,
         ParserConfigurationException,
         TransformerFactoryConfigurationError, TransformerException, JsonProcessingException {
      logger.debug("starting test");
      model.createContext();
      SubmittedProposal submittedProposal = ex.submitProposal(proposal, cycle);
      cycle.setSubmittedProposals(
            makeList(submittedProposal));

      AllocatedProposal allocatedProposal = ex.allocateProposal(submittedProposal);
      cycle.setAllocatedProposals(makeList(
            allocatedProposal
      ));
      cycle.updateClonedReferences();
      //save out the work
      em.getTransaction().begin();
      em.persist(submittedProposal);
      em.persist(allocatedProposal);
      em.getTransaction().commit();

     doSerialization(model);

   }

   public void doSerialization(ProposalManagementModel thisModel) throws JsonProcessingException, JAXBException, ParserConfigurationException, TransformerException {
      validateModel(thisModel);
      ProposalManagementModel modelin = modelRoundTripXMLwithTest(thisModel);
      List<ProposalCycle> revs = modelin.getContent(ProposalCycle.class);
      assertEquals(1, revs.size());

      ProposalManagementModel modelinj = modelRoundTripJSONwithTest(thisModel);
      List<ProposalCycle> revsj = modelinj.getContent(ProposalCycle.class);
      assertEquals(1, revsj.size());

   }

   @org.junit.jupiter.api.Test
   @Order(1210)
   public void retrieveTest() throws JAXBException, ParserConfigurationException, JsonProcessingException, TransformerException {
      //flush any existing entities
      em.clear();
      em.getEntityManagerFactory().getCache().evictAll();
      retrieveProposal();
      retrieveCycle();
      ProposalManagementModel thismodel = new ProposalManagementModel();
      thismodel.addContent(proposal);
      thismodel.addContent(cycle);
      assertEquals(1, cycle.getSubmittedProposals().size());
      assertEquals(1, cycle.getAllocatedProposals().size());
      doSerialization(thismodel);
   }



   @org.junit.jupiter.api.Test
   @Order(1220)
   public void testDeleteTarget() {

      Observation obs = proposal.observations.get(0);
      assertNotNull(obs.target);
      em.getTransaction().begin();
      em.remove(obs.target.get(0)); // TODO perhaps really want to investigate list member deletion more...
      em.getTransaction().commit();


   }

   @org.junit.jupiter.api.Test
   @Order(2110)
   public void testObservationTarget() {

      //copy obs
      em.getTransaction().begin();
      TypedQuery<ObservingProposal> q = em.createQuery("SELECT o FROM ObservingProposal o", ObservingProposal.class);
      List<ObservingProposal> res = q.getResultList();
      ObservingProposal prop = res.get(0);
      prop.forceLoad();
      Observation obs = prop.observations.get(0);
      Observation obs2 = obs.copyMe();
      em.persist(obs2);
      prop.addToObservations(obs2);
      em.merge(prop);
      em.getTransaction().commit();
      //delete target
      em.getTransaction().begin();
      assertNotNull(obs.target);

      em.remove(obs.target.get(0)); // TODO perhaps really want to investigate list member deletion more...
      em.getTransaction().commit();


   }

   public static ObservingProposal cloneProposal(ProposalManagementModel themodel, ObservingProposal p)
  {

        themodel.createContext();//FIXME this is an area where the API for https://github.com/ivoa/vo-dml/issues/42 is not great
        final ObservingProposal frozenProposal = new ObservingProposal(p); // make a copy of the proposal to put into the submitted proposal
        frozenProposal.updateClonedReferences();
        return frozenProposal;
  }
  


}


