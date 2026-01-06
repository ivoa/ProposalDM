package org.ivoa.dm.proposal.prop;


/*
 * Created on 06/01/2026 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.stc.coords.SpaceSys;

public class BaseGenerator implements ExampleGenerator {
    ExampleProposal prop = new ExampleProposal();
    private final TACFunctions observatory;

   protected BaseGenerator(TACFunctions observatory) {
      this.observatory = observatory;
   }

   @Override
    public ProposalCycle getCycle() {
        return observatory.getCycle();

    }
    @Override
    public ObservingProposal getProposal() {
        return prop.getProposal();
    }

    @Override
    public SpaceSys getICRS() {
        return BaseExample.ICRS_SYS;
    }

    @Override
    public SpaceSys getICRF() {
        return BaseExample.GEO_SYS;

    }

    @Override
    public SubmittedProposal submitProposal(
          ObservingProposal p, ProposalCycle c) {
        return observatory.submitProposal(p,c);


    }

    @Override
    public AllocatedProposal allocateProposal(
          SubmittedProposal p) {
        return observatory.allocateProposal(p);
    }
}
