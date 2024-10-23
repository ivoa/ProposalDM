/*
 * Created on 22 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;

import jakarta.persistence.EntityManager;

/**
 * Complete example with all example observatories .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 22 Oct 2024
 */
public class FullExample {
    
    
    private  ObservingProposal proposal;
    private  ProposalCycle cycle;
    private ProposalManagementModel model = new ProposalManagementModel();

    /**
     * create full example.
     */
    public FullExample() {
       EmerlinExample emerlin = new EmerlinExample();
       proposal = new ExampleProposal().getProposal();
       cycle = emerlin.getCycle();
       ObservingProposal clonedProposal = AbstractProposalTest.cloneProposal(proposal);
       clonedProposal.setSubmitted(true);   
       SubmittedProposal submittedProposal = emerlin.submitProposal(clonedProposal);
       emerlin.allocateProposal(submittedProposal);
       
    }

    /**
     * Save to an RDB.
     * @param em the entity manager for the RDB.
     */
    public void saveTodB(EntityManager em) {
         
         
         cycle.persistRefs(em);
         em.persist(cycle);
         proposal.persistRefs(em);
         em.persist(proposal);
        
         
         
    }
    
    /**
     * get the management model.
     * @return the model.
     */
    public ProposalManagementModel getManagementModel() {
        model.addContent(cycle);
        return model;
    }
    
    
    /**
     * get the proposal model.
     * @return the model.
     */
    public ProposalModel getProposalModel() {
       ProposalModel pmodel = new ProposalModel();
       pmodel.addContent(proposal);
       return pmodel;
    }

}


