/*
 * Created on 22 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import java.util.ArrayList;
import java.util.List;

import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.vodml.VodmlModel;

import jakarta.persistence.EntityManager;

import static org.ivoa.dm.proposal.prop.BaseExample.makeList;
import static org.ivoa.dm.proposal.prop.BaseExample.people;

/**
 * Complete example with all example observatories.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 */
public class FullExample {
    
    
    private final ObservingProposal proposal;
    private final ProposalManagementModel model = new ProposalManagementModel();
    

    private final List<TACFunctions> observatories = new ArrayList<>();
    /**
     * create full example.
     */
    public FullExample() {
        observatories.add(new EmerlinExample());
        observatories.add(new NOTexample());
       
       proposal = new ExampleProposal().getProposal();
       model.addReference(people[4]); //IMPL he is not in a TAC would not be saved otherwise
       for(TACFunctions obs : observatories) {          
           ObservingProposal clonedProposal = AbstractProposalTest.cloneProposal(model,proposal);

           final ProposalCycle cycle = obs.getCycle();

           model.createContext();
           SubmittedProposal submittedProposal = obs.submitProposal(clonedProposal, cycle);
           cycle.setSubmittedProposals(
                 makeList(submittedProposal));

           AllocatedProposal allocatedProposal = obs.allocateProposal(submittedProposal);
           cycle.setAllocatedProposals(makeList(
                 allocatedProposal
           ));
           submittedProposal.updateClonedReferences();
           model.addContent(cycle);
       }
    }

    /**
     * Save to an RDB. Note transactions need to be handled outsite this routine.
     * @param em the entity manager for the RDB.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void saveTodB(EntityManager em) {
        model.management().persistRefs(em);
        em.flush();
        em.persist(proposal);
        for(Class cc:model.management().description().contentClasses())
        {
            for(Object c:model.getContent(cc))
            {
                em.persist(c);
            }
        }
    }
    
    /**
     * get the management model.
     * @return the model.
     */
    public ProposalManagementModel getManagementModel() {
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


