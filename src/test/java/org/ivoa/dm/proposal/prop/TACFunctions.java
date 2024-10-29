/*
 * Created on 20 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.SubmittedProposal;

/**
 *  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Oct 2024
 */
public interface TACFunctions {

ProposalCycle getCycle();

/**
 * Submit a proposal.
 * @param p the proposal to submit. It should be assumed that this has already been cloned.
 * @return The submitted proposal.
 */
SubmittedProposal submitProposal(ObservingProposal p);


/**
 * Allocate a proposal.
 * @param p
 * @return
 */
AllocatedProposal allocateProposal(SubmittedProposal p);
}


