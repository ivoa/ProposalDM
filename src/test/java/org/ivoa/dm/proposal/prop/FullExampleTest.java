/*
 * Created on 22 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.vodml.testing.AutoDBRoundTripTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.TypedQuery;

/**
 * Test the FullExample .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 */
class FullExampleTest extends AutoDBRoundTripTest<ProposalManagementModel, Long,ProposalCycle> {

    /**
     * @throws java.lang.Exception
     */
    @BeforeAll
    static void setUpBeforeClass() throws Exception {

    }

    private FullExample example;

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {

        example = new FullExample();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.vodml.testing.AutoDBRoundTripTest#entityForDb()
     */
    @Override
    public ProposalCycle entityForDb() {
        return example.getManagementModel().getContent(ProposalCycle.class).get(0);
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.vodml.testing.AutoDBRoundTripTest#testEntity(org.ivoa.vodml.jpa.JPAManipulationsForObjectType)
     */
    @Override
    public void testEntity(ProposalCycle e) {
        //TODO some testing        
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.vodml.testing.AutoRoundTripTest#createModel()
     */
    @Override
    public ProposalManagementModel createModel() {
        return example.getManagementModel();
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.vodml.testing.AutoRoundTripTest#testModel(org.ivoa.vodml.VodmlModel)
     */
    @Override
    public void testModel(ProposalManagementModel m) {
      List<ProposalCycle> props = m.getContent(ProposalCycle.class);
      assertTrue(props.size() > 0);
      assertTrue(props.stream().filter(p -> !p.getSubmittedProposals().isEmpty()).count()> 0, "at least one submitted proposal");
    }


    /**
     * test the full DB save - note the transaction needs to be around the 
     */
    @Test
    public void testDBSave() {
        jakarta.persistence.EntityManager em = setupH2Db(example.getManagementModel().management().pu_name(), example.getManagementModel().modelDescription.allClassNames());

        em.getTransaction().begin();
        example.saveTodB(em);
        em.getTransaction().commit();
        dumpDbData(em, "fullexample.sql");
        em.clear(); //there is a hibernate error if we do not do this at least...
        TypedQuery<ObservingProposal> qp = em.createQuery("SELECT o FROM ObservingProposal o ", ObservingProposal.class);
        List<ObservingProposal> proposals = qp.getResultList();
        assertEquals(1,proposals.size(), "number of unsubmitted proposals");
        TypedQuery<SubmittedProposal> qs = em.createQuery("SELECT o FROM SubmittedProposal o ", SubmittedProposal.class);
        List<SubmittedProposal> submittedProposals = qs.getResultList();
        assertEquals(2, submittedProposals.size(), "number of submitted proposals");   
        TypedQuery<Target> qsrc = em.createQuery("SELECT o FROM Target o",Target.class);
        List<Target> targets = qsrc.getResultList();
        assertEquals(3, targets.size(), "number of targets");
       TypedQuery<Person> psrc = em.createQuery("SELECT o FROM Person o", Person.class);
       List<Person> persons = psrc.getResultList();
       assertEquals(5, persons.size(), "number of persons");
       
        
        
    }

}


