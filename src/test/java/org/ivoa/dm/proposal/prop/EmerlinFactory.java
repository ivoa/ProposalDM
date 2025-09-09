package org.ivoa.dm.proposal.prop;


/*
 * Created on 09/09/2025 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.stc.coords.SpaceSys;

class EmerlinFactory implements AbstractProposalTest.exampleFactory {

   @Override
   public ExampleGenerator create() {
      return new ExampleGenerator() {
         ExampleProposal prop = new ExampleProposal();
         EmerlinExample emerlin = new EmerlinExample();

         @Override
         public ProposalCycle getCycle() {
            return emerlin.getCycle();

         }

         @Override
         public ObservingProposal getProposal() {
            return prop.getProposal();
         }

         @Override
         public SpaceSys getICRS() {
            return prop.ICRS_SYS;
         }

         @Override
         public SpaceSys getICRF() {
            return prop.GEO_SYS;

         }

         @Override
         public SubmittedProposal submitProposal(
               ObservingProposal p, ProposalCycle c) {
            return emerlin.submitProposal(p,c);


         }

         @Override
         public AllocatedProposal allocateProposal(
               SubmittedProposal p) {
            return emerlin.allocateProposal(p);
         }
      };
   }
}
