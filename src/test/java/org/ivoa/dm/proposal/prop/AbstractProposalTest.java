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

import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;
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
    protected  exampleFactory ef;
    protected  EntityManager em;
    protected  Long proposalId;
    protected ObservingProposal proposal;
    protected ModelDescription modelDescription;

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


   public interface exampleFactory {
        ExampleGenerator create();
    }

    public AbstractProposalTest(ModelDescription md,exampleFactory ef) {
       this.modelDescription = md;
       this.ef= ef;
    }

    protected abstract void doUrTest();
    @Test
    @Order(1)
    void UrTest() { // this test has to be done first to make sure that the basic database save has been done
     doUrTest();
    }

    @org.junit.jupiter.api.Test
    @Order(1000)
    void proposalDmJaxbTest() throws JAXBException, TransformerConfigurationException,
    ParserConfigurationException, TransformerFactoryConfigurationError,
    TransformerException {
        logger.debug("starting test");
        
        ProposalModel model = new ProposalModel();
        model.addContent(proposal);
        model.processReferences();
        validateModel(model);

        ProposalModel modelin = modelRoundTripXMLwithTest(model);
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
       assertTrue(obs.target.get(0) instanceof CelestialTarget);
       CelestialTarget target = (CelestialTarget)obs.target.get(0);
       SpaceSys cosys = (SpaceSys) target.sourceCoordinates.getCoordSys();
       assertNotNull(cosys);
       
      
   }
@org.junit.jupiter.api.Test
@Order(1020)
public void testDeleteTarget() {

       Observation obs = proposal.observations.get(0);
       assertNotNull(obs.target);
       em.getTransaction().begin();
       em.remove(obs.target.get(0)); // TODO perhaps really want to investigate list member deletion more...
       em.getTransaction().commit();
       
       
   }
@org.junit.jupiter.api.Test
@Order(1020)
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

  public static ObservingProposal cloneProposal(ObservingProposal p)
  {
        ProposalModel pm = new ProposalModel();
        pm.createContext();//FIXME this is an area where the API for https://github.com/ivoa/vo-dml/issues/42 is not great
        final ObservingProposal frozenProposal = new ObservingProposal(p); // make a copy of the proposal to put into the submitted proposal
        frozenProposal.updateClonedReferences();
        return frozenProposal;
  }
  


}


