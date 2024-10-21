package org.ivoa.dm.proposal.prop;
/*
 * Created on 13/02/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import static org.ivoa.dm.proposal.management.ProposalCycle.createProposalCycle;
import static org.ivoa.dm.proposal.management.Observatory.createObservatory;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ivoa.dm.ivoa.Ivorn;
import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.AllocationGrade;
import org.ivoa.dm.proposal.management.AvailableResources;
import org.ivoa.dm.proposal.management.Backend;
import org.ivoa.dm.proposal.management.Instrument;
import org.ivoa.dm.proposal.management.Observatory;
import org.ivoa.dm.proposal.management.ObservingMode;
import org.ivoa.dm.proposal.management.Resource;
import org.ivoa.dm.proposal.management.ResourceType;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.proposal.management.Telescope;
import org.ivoa.dm.proposal.management.TelescopeArray;
import org.ivoa.dm.proposal.management.TelescopeArrayMember;

/**
 * 
 * Exercising the model with an example based on Las Cumbres Observatory  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 19 Oct 2024
 */
public class LCOExample extends BaseObservatoryExample implements TACFunctions {

     Telescope[] telescopes = {};
     private Observatory observatory;
   
     public LCOExample() {
         
          Instrument[] instruments = {
               //FIXME add instruments
          };
          Backend backend = new Backend("standard pipeline", true);
          
          final TelescopeArray LCOTels = new TelescopeArray("LCO",
                  Stream.of(telescopes).map(t -> new TelescopeArrayMember(t)).collect(Collectors.toList()));
          
          observatory = createObservatory(obs -> {
            obs.address = "on earth";
            obs.ivoid = new Ivorn("ivo://obs/anobs");
            obs.name = "Las Cumbres Observatory";
            obs.telescopes = Arrays.asList(telescopes);
            
            obs.arrays = Arrays.asList(LCOTels);
            obs.instruments = Arrays.asList(instruments);
            obs.backends =Arrays.asList(backend);

        });
          
        ResourceType observingTime = new ResourceType("observing time", "hours");

        Resource availableObservingTime = new Resource(observingTime, 100 * 24.0);   
        ObservingMode obsModes[] = {
                // FIXME add observing modes
        };
         AllocationGrade grades[] = {
                // FIXME add grades
         };
         
          cycle = createProposalCycle(cycleCommonSetup().andThen(cy -> {
            cy.observatory = observatory;
            cy.possibleGrades = Arrays.asList(grades);
            cy.observingModes = Arrays.asList(obsModes);
            cy.availableResources = new AvailableResources(Arrays.asList(availableObservingTime));//IMPL is there one too many layers of encapsulation here?

        }));
        
     }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.TACFunctions#submitProposal(org.ivoa.dm.proposal.prop.ObservingProposal)
     */
    @Override
    public SubmittedProposal submitProposal(ObservingProposal p) {
        // TODO Auto-generated method stub
        throw new  UnsupportedOperationException("TACFunctions.submitProposal() not implemented");
        
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.TACFunctions#allocateProposal(org.ivoa.dm.proposal.management.SubmittedProposal)
     */
    @Override
    public AllocatedProposal allocateProposal(SubmittedProposal p) {
        // TODO Auto-generated method stub
        throw new  UnsupportedOperationException("TACFunctions.allocateProposal() not implemented");
        
    }

   
}
