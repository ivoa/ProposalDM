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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract base class that creates entities that would be similar for all proposals.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
public abstract class AbstractProposalTest extends org.ivoa.vodml.testing.AbstractTest {

    /** logger for this class */
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(AbstractProposalTest.class);

    protected ExampleGenerator ex;
    private exampleFactory ef;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {
        ex = ef.create();       
    }
       /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {
    }



    public interface exampleFactory {
        ExampleGenerator create();
    }

    public AbstractProposalTest(exampleFactory ef) {
        this.ef= ef;
    }

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
    void proposalDmJPATest() {
        ObservingProposal prop = propDbInOut();
        assertEquals( 2, prop.getInvestigators().size(),"number of investigators"); // trivial test     
        assertEquals( 1,prop.getObservations().size(), "number of observations");
        Observation obs = prop.getObservations().get(0);
        TechnicalGoal tech = obs.getTechnicalGoal();
        assertNotNull(tech);
        assertEquals( 2,tech.spectrum.size(), "number of spectral setups");
                

    }
    protected ObservingProposal propDbInOut() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name(),ProposalModel.modelDescription.allClassNames());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        ProposalModel model = new ProposalModel();
        model.addContent(proposal);
        model.management().persistRefs(em);
        em.persist(proposal);
        em.getTransaction().commit();

        //flush any existing entities
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();


        //now read in again
        Long id = proposal.getId(); 

        em.getTransaction().begin();
        List<ObservingProposal> props = em.createNamedQuery("ObservingProposal.findById", ObservingProposal.class)
                .setParameter("id", id).getResultList();
        assertEquals(1, props.size());
        ObservingProposal prop = props.get(0);
        em.getTransaction().commit();
        return prop;
    }

    @org.junit.jupiter.api.Test
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

    @org.junit.jupiter.api.Test
    void reviewDmJPATest() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalManagementModel.pu_name(),ProposalManagementModel.modelDescription.allClassNames());
        em.getTransaction().begin();
        ProposalManagementModel model = new ProposalManagementModel();
        final  ProposalCycle cycle = doTacWork();
        model.addContent(cycle);
        model.management().persistRefs(em);
        em.persist(cycle);
        em.getTransaction().commit();
        
                //flush any existing entities
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        //read in again
        Long id = cycle.getId(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.
    
        em.getTransaction().begin();
        List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
                .setParameter("id", id).getResultList();
        assertEquals(1, props.size());
        ProposalCycle pc = props.get(0);
        em.getTransaction().commit();
        assertNotNull(pc);
        
        em.getTransaction().begin();
        Long i = em.createQuery("select count(o) from SubmittedProposal o", Long.class).getSingleResult().longValue();
        em.getTransaction().commit();
        assertEquals( 1,i.longValue(),"number submitted proposals"); // trivial test      
    
    }
    @org.junit.jupiter.api.Test
    void proposalDmJSONTest() throws JsonProcessingException {
        ProposalModel model = new ProposalModel();
        model.addContent(ex.getProposal());
        model.processReferences();
        ProposalModel modelin = modelRoundTripJSONwithTest(model);
        List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
        assertEquals(1, props.size());
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
public void testObservations() {
       ObservingProposal prop = propDbInOut();
       assertEquals( 1, prop.observations.size(), "number of observations");
       Observation obs = prop.observations.get(0);
       assertNotNull(obs.target);
       assertTrue(obs.target.get(0) instanceof CelestialTarget);
       CelestialTarget target = (CelestialTarget)obs.target.get(0);
       SpaceSys cosys = target.sourceCoordinates.getCoordSys();
       assertNotNull(cosys);
       
      
   }
@org.junit.jupiter.api.Test
public void testDeleteTarget() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name(),ProposalModel.modelDescription.allClassNames());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        ProposalModel model = new ProposalModel();
        model.addContent(proposal);
        model.management().persistRefs(em);
        em.persist(proposal);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Observation obs = proposal.observations.get(0);
       assertNotNull(obs.target);
      
       em.remove(obs.target.get(0)); // TODO perhaps really want to investigate list member deletion more...
       em.getTransaction().commit();
       
       
   }
@org.junit.jupiter.api.Test
public void testObservationTarget() {
        jakarta.persistence.EntityManager em = setupH2Db(ProposalModel.pu_name(),ProposalModel.modelDescription.allClassNames());
        em.getTransaction().begin();
        final ObservingProposal proposal = ex.getProposal();
        ProposalModel model = new ProposalModel();
        model.addContent(proposal);
        model.management().persistRefs(em);
        em.persist(proposal);
        em.getTransaction().commit();
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
  
  private ProposalCycle doTacWork() {
        final ProposalCycle cycle = ex.getCycle();
        ObservingProposal prop = ex.getProposal();
        ProposalModel pm = new ProposalModel();
        pm.createContext();
        SubmittedProposal sprop = ex.submitProposal(prop);
        sprop.updateClonedReferences();
        ex.allocateProposal(sprop);
        return cycle;
 
  }

}


