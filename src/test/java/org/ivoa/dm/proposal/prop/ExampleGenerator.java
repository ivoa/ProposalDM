package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.ProposalCycle;

public interface ExampleGenerator {

    ObservingProposal getProposal();

    ProposalCycle getCycle();

}
