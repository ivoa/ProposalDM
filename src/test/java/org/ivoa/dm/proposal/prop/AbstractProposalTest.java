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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.ivoa.dm.proposal.management.OfferedCycles;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;
import org.javastro.ivoa.tests.AbstractJAXBJPATest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * An abstract base class that creates entities that would be similar for all proposals.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
public abstract class AbstractProposalTest extends AbstractJAXBJPATest {

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
        JAXBContext jc = ProposalModel.contextFactory();
        JaxbAnnotationMeta<ObservingProposal> meta = JaxbAnnotationMeta.of(ObservingProposal.class);
        DescriptionValidator<ObservingProposal> validator = new DescriptionValidator<>(jc, meta);
        DescriptionValidator.Validation validation = validator.validate(ex.getProposal());
        if(!validation.valid) {
            System.err.println(validation.message);
        }
        assertTrue(validation.valid);
        ProposalModel model = new ProposalModel();
        model.addContent(ex.getProposal());
        model.makeRefIDsUnique();

        ProposalModel modelin = roundtripXML(jc, model, ProposalModel.class);
        List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
        assertEquals(1, props.size());

    }

    @org.junit.jupiter.api.Test
    void proposalDmJPATest() {
        ObservingProposal prop = propDbInOut();
        assertEquals( 2, prop.getInvestigators().size(),"number of investigators"); // trivial test     
        assertEquals( 1,prop.getObservations().size(), "number of observations");
        Observation obs = prop.getObservations().get(0);
        TechnicalGoal tech = obs.getTech();
        assertNotNull(tech);
        assertEquals( 2,tech.spectrum.size(), "number of spectral setups");
                

    }
    protected ObservingProposal propDbInOut() {
        javax.persistence.EntityManager em = setupDB(ProposalModel.pu_name());
        em.getTransaction().begin();
        em.persist(ex.getProposal());
        em.getTransaction().commit();

        //flush any existing entities
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();


        //now read in again
        String id = ex.getProposal().getCode(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.

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
                JAXBContext jc = ProposalManagementModel.contextFactory();
                OfferedCycles oc = new OfferedCycles();
                oc.addCycles(ex.getCycle());
                JaxbAnnotationMeta<OfferedCycles> meta = JaxbAnnotationMeta.of(OfferedCycles.class);
                DescriptionValidator<OfferedCycles> validator = new DescriptionValidator<>(jc, meta);
                DescriptionValidator.Validation validation = validator.validate(oc);
                if(!validation.valid) {
                    System.err.println(validation.message);
                }
                assertTrue(validation.valid);
                ProposalManagementModel model = new ProposalManagementModel();
                model.addContent(oc);
                model.makeRefIDsUnique();
            
                ProposalManagementModel modelin = roundtripXML(jc, model, ProposalManagementModel.class);
                List<OfferedCycles> revs = modelin.getContent(OfferedCycles.class);
                assertEquals(1, revs.size());
                assertEquals(1, revs.get(0).getCycles().size());
            
            }

    @org.junit.jupiter.api.Test
    void reviewDmJPATest() {
        javax.persistence.EntityManager em = setupDB(ProposalModel.pu_name());
        em.getTransaction().begin();
        em.persist(ex.getCycle());
        em.getTransaction().commit();
        
                //flush any existing entities
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();

        //read in again
        Long id = ex.getCycle().getId(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.
    
        em.getTransaction().begin();
        List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
                .setParameter("id", id).getResultList();
        assertEquals(1, props.size());
        ProposalCycle pc = props.get(0);
        em.getTransaction().commit();
        assertEquals( 1, pc.getSubmittedProposals().size(),"number submitted proposals"); // trivial test      
    
    }}


