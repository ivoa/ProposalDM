package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.*;
import org.ivoa.dm.stc.coords.PolStateEnum;
import org.ivoa.dm.ivoa.Ivoid;
import org.ivoa.dm.ivoa.RealQuantity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ivoa.dm.proposal.management.ProposalCycle.createProposalCycle;
import static org.ivoa.dm.proposal.management.Observatory.createObservatory;
import static org.ivoa.dm.proposal.prop.SpectralWindowSetup.createSpectralWindowSetup;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmerlinExample extends BaseObservatoryExample implements TACFunctions {

    
    
    Telescope[] telescopes = {
          createTelescope("Lovell", 3822252.643, -153995.683, 5086051.443),
          createTelescope("Mk2", 3822473.365, -153692.318, 5085851.303),
          createTelescope("Knockin",3859711.503, -201995.077, 5056134.251 ),
          createTelescope("Defford", 3923069.171, -146804.368, 5009320.528),
          createTelescope("Pickmere",3817176.561, -162921.179, 5089462.057),
          createTelescope("Darnhall", 3828714.513, -169458.995, 5080647.749),
          createTelescope("Cambridge", 3919982.752, 2651.982, 5013849.826)

    };
    Set<String > notK = new HashSet<>(Arrays.asList("Lovell","Defford"));
    private Observatory observatory;
    private ObservingMode obsModes[];
    private AllocationGrade grades[];
    private ResourceType observingTime;
    protected SpectralWindowSetup simpleSpecRange(double start, double end) {
        return createSpectralWindowSetup(sw -> {
            sw.start = new RealQuantity(start, ghz);
            sw.end = new RealQuantity(end, ghz);
            sw.polarization = PolStateEnum.V ; //FIXME - trying to express circular polarized
            sw.isSkyFrequency = true;
            sw.spectralResolution = new RealQuantity(end-start, ghz); //Indicate that resolution is not important - set same as width.
        });
    }
    
    protected Filter simpleFilter(String name, double start, double end) {
        return new Filter(name, name, simpleSpecRange(start, end));
    }

    public  EmerlinExample () {
         Instrument[] instruments = {
             
              Instrument.createInstrument( i -> {i.name="L-Band Receiver"; i.kind=InstrumentKind.CONTINUUM; }), 
              Instrument.createInstrument( i -> {i.name="C-Band Receiver"; i.kind=InstrumentKind.CONTINUUM; }),
              Instrument.createInstrument( i -> {i.name="K-Band Receiver"; i.kind=InstrumentKind.CONTINUUM; })
        };
         
         

        Backend backend = new Backend("Widar Correlator", true);

        
        final TelescopeArray eMERLIN = new TelescopeArray("e-MERLIN",
                  Stream.of(telescopes).map(t -> new TelescopeArrayMember(t)).collect(Collectors.toList()));
        final TelescopeArray eMERLINReduced = new TelescopeArray("e-MERLIN (reduced)",
                 Stream.of(telescopes).filter(t -> !notK.contains(t.getName())).map(t -> new TelescopeArrayMember(t)).collect(Collectors.toList()));
       observatory = createObservatory(obs -> {
            obs.address = "on earth";
            obs.ivoid = new Ivoid("ivo://obs/anobs");
            obs.name = "Jodrell Bank";
            obs.homePage = "https://www.e-merlin.ac.uk";
            obs.telescopes = Arrays.asList(telescopes);
            
            obs.arrays = Arrays.asList(eMERLIN,eMERLINReduced);
            obs.instruments = Arrays.asList(instruments);
            obs.backends =Arrays.asList(backend);

        });


        observingTime = new ResourceType("observing time", "hours");

        Resource availableObservingTime = new Resource(observingTime, 100 * 24.0);

        obsModes = new ObservingMode[] {
              new ObservingMode("L-Band", "full e-MERLIN at L-Band",
                     eMERLIN, instruments[0],simpleFilter("L-Band",1.2, 1.7), backend)
              ,new ObservingMode("C-Band", "full e-MERLIN at C-Band", 
                     eMERLIN, instruments[1],simpleFilter("C-Band",4.0, 7.0), backend)
              ,new ObservingMode("K-Band", "reduced e-EMERLIN at K-Band",
                      eMERLINReduced, instruments[2],simpleFilter("K-Band",20.0, 24.0), backend)
        };

        grades = new AllocationGrade[] {
              new AllocationGrade("A", "requires full array to be scheduled"),
              new AllocationGrade("B", "can be schedulted with missing telescopes")

        };
        // set up the proposal cycle
        cycle = createProposalCycle(cycleCommonSetup().andThen(cy -> {
            cy.observatory = observatory;
            cy.title = "Cycle 19";
            cy.instructions = "https://www.e-merlin.ac.uk/observe.html#call";
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
    public SubmittedProposal submitProposal(ObservingProposal frozenProposal) {
          // "submit" proposal
      
        List<ObservationConfiguration> obsConfigs = makeList(
                new ObservationConfiguration(frozenProposal.getObservations(),obsModes[0])
                );
        
        
        final SubmittedProposal submittedProposal = new SubmittedProposal( frozenProposal, "EmCode",obsConfigs,new GregorianCalendar(2022, 3, 14).getTime(),  false, new GregorianCalendar(2022, 4, 30).getTime(),  null );
        cycle.setSubmittedProposals(
              makeList(submittedProposal));

        // "review" proposal
        ProposalReview review = ProposalReview.createProposalReview(pr -> {
                      pr.comment = "it is good";
                      pr.score = 10.0;
                      pr.reviewer = new Reviewer(people[4]);
                      pr.technicalFeasibility = true;
                      pr.reviewDate = new GregorianCalendar(2022, 4, 14).getTime();
                  });

       submittedProposal.addToReviews(review);
       // set that the submitted proposal is ok to be allocated. 
       submittedProposal.setSuccessful(true);

      return submittedProposal;

        
    }

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.TACFunctions#allocateProposal(org.ivoa.dm.proposal.management.SubmittedProposal)
     */
    @Override
    public AllocatedProposal allocateProposal(SubmittedProposal p) {
   // "allocate" proposal

        final AllocatedProposal allocatedProposal = AllocatedProposal.createAllocatedProposal(ap -> {
              ap.submitted = p;
      
              ap.allocation = makeList(
                    AllocatedBlock.createAllocatedBlock(
                          a -> {
                              a.grade = grades[0];
                              a.mode = obsModes[0];
                              Resource res = new Resource(observingTime, 48.0); // IMPL should it be possible to do different units?
                              a.resource = res;
                          }
                    )

              );
          });
        cycle.setAllocatedProposals(makeList(
              allocatedProposal
        ));   
        return allocatedProposal;
    }

    

}
