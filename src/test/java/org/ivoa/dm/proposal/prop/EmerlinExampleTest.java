/*
 * Created on 20 Jan 2022 
 * Copyright 2022 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.ivoa.dm.proposal.prop.ObservingProposal.createObservingProposal;
import static org.ivoa.dm.proposal.prop.PerformanceParameters.createPerformanceParameters;
import static org.ivoa.dm.proposal.prop.ScienceSpectralWindow.createScienceSpectralWindow;
import static org.ivoa.dm.proposal.prop.SpectralWindowSetup.createSpectralWindowSetup;
import static org.ivoa.dm.proposal.management.ProposalCycle.createProposalCycle;
import static org.ivoa.dm.proposal.prop.ExpectedSpectralLine.createExpectedSpectralLine;
import static org.ivoa.dm.proposal.prop.Observatory.createObservatory;
import static org.ivoa.dm.proposal.prop.TargetObservation.createTargetObservation;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.ivoa.dm.proposal.management.AllocatedBlock;
import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.AllocationGrade;
import org.ivoa.dm.proposal.management.AvailableResources;
import org.ivoa.dm.proposal.management.OfferedCycles;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalManagementModel;
import org.ivoa.dm.proposal.management.ProposalReview;
import org.ivoa.dm.proposal.management.Resource;
import org.ivoa.dm.proposal.management.ResourceType;
import org.ivoa.dm.proposal.management.ReviewedProposal;
import org.ivoa.dm.proposal.management.Reviewer;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.stc.coords.Epoch;
import org.ivoa.dm.stc.coords.EquatorialPoint;
import org.ivoa.dm.stc.coords.PolStateEnum;
import org.ivoa.vodml.stdtypes2.Ivorn;
import org.ivoa.vodml.stdtypes2.RealQuantity;
import org.ivoa.vodml.stdtypes2.StringIdentifier;
import org.ivoa.vodml.stdtypes2.Unit;
import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests concepts needed for e-MERLIN configuration.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
class EmerlinExampleTest extends AbstractProposalTest {

    private static final Unit arcsec = new Unit("arcsec");
    private Observatory observatory;
    private Unit ghz = new Unit("GHz");
    private Unit khz = new Unit("kHz");

    protected SpectralWindowSetup simpleSpecRange(double start, double end) {
        return createSpectralWindowSetup(sw -> {
            sw.start = new RealQuantity(start, ghz);
            sw.end = new RealQuantity(end, ghz);
            sw.polarization = PolStateEnum.V ; //FIXME - trying to express circular polarized
            sw.isSkyFrequency = true;
            sw.spectralResolution = new RealQuantity(end-start, ghz); //Indicate that resolution is not important - set same as width.
        });
    }

    /**
     * @throws java.lang.Exception
     */
    @BeforeEach
    void setUp() throws Exception {

        Telescope[] telescopes = {
                createTelescope("Lovell", 3822252.643, -153995.683, 5086051.443),
                createTelescope("Mk2", 3822473.365, -153692.318, 5085851.303),
                createTelescope("Knockin",3859711.503, -201995.077, 5056134.251 ),
                createTelescope("Defford", 3923069.171, -146804.368, 5009320.528),
                createTelescope("Pickmere",3817176.561, -162921.179, 5089462.057),
                createTelescope("Darnhall", 3828714.513, -169458.995, 5080647.749),
                createTelescope("Cambridge", 3919982.752, 2651.982, 5013849.826)

        };
        Set<String >notK = new HashSet<>(Arrays.asList("Lovell","Defford"));

        observatory = createObservatory(obs -> {
            obs.address = "on earth";
            obs.ivoid = new Ivorn("ivo://obs/anobs");
            obs.name = "Jodrell Bank";
            obs.telescopes= Arrays.asList(telescopes);
            obs.arrays = Arrays.asList(new TelescopeArray("e-MERLIN", 
                    Stream.of(telescopes).map(t->new TelescopeArrayMember(t)).collect(Collectors.toList())));

        });


        ResourceType observingTime = new ResourceType("observing time", "hours");

        Resource availableObservingTime = new Resource(100*24.0, observingTime);

        Instrument[] instruments = {
                new Instrument("L-Band Receiver", InstrumentKind.CONTINUUM , simpleSpecRange(1.2, 1.7) ),
                new Instrument("C-Band Receiver", InstrumentKind.CONTINUUM , simpleSpecRange(4.0, 7.0) ),
                new Instrument("K-Band Receiver", InstrumentKind.CONTINUUM , simpleSpecRange(20.0, 24.0) )
        };

        Backend backend = new Backend("Widar Correlator", true);

        //for e-merlin the modes use use the same backend and instrument on all the telescopes
        // there is also the special case of not tying up the Lovell telescope - would be 2 extra modes for L and C Bands
        // what about join
        ObservingMode obsModes[] = {
                new ObservingMode("L-Band", "full e-MERLIN at L-Band", Arrays.stream(telescopes)
                        .map(t -> new ObservingConfiguration(t, instruments[0], backend)).collect(Collectors.toList())),
                new ObservingMode("C-Band", "full e-MERLIN at C-Band", Arrays.stream(telescopes)
                        .map(t -> new ObservingConfiguration(t, instruments[1], backend)).collect(Collectors.toList())),
                new ObservingMode("K-Band", "reduced e-EMERLIN at K-Band", Arrays.stream(telescopes).filter(t->notK.contains(t.name))
                        .map(t -> new ObservingConfiguration(t, instruments[2], backend)).collect(Collectors.toList()))
        };

        AllocationGrade grades[] = {
                new AllocationGrade("A", "requires full array to be scheduled"),
                    new AllocationGrade("B", "can be schedulted with missing telescopes")
                    
        };
        // set up the proposal cycle
        cycle = createProposalCycle(cycleCommonSetup().andThen(cy -> {
            cy.observatory = observatory;
            cy.possibleGrades = Arrays.asList(grades);
            cy.observingModes = Arrays.asList(obsModes);
            cy.availableResources = new AvailableResources(Arrays.asList(availableObservingTime));//IMPL is there one too many layers of encapsulation here?

        }));

        // set up the specific proposal
        proposal = createObservingProposal(proposalCommonSetup().andThen(pr -> {
            List<Observation> obs = Arrays.asList(
                    createTargetObservation(t -> {

                        t.target = CelestialTarget.createCelestialTarget(c -> {
                            c.sourceName = "fictonal";
                            c.sourceCoordinates = new EquatorialPoint(new RealQuantity(45.0, degrees), new RealQuantity(60.0, degrees), ICRS_SYS);//IMPL it would actually be nice to be able to input sexagesimal - that is the most human readable
                            c.positionEpoch = new Epoch("J2013.123");//FIXME - this is not really what epoch means
                        });
                        t.field = new TargetField("source1");
                        t.tech = TechnicalGoal.createTechnicalGoal(g -> {
                            g.performance = createPerformanceParameters(p -> {
                                p.desiredAngularResolution = new RealQuantity(25., arcsec);
                                p.desiredLargestScale = new RealQuantity(0.1, degrees);
                                p.representativeSpectralPoint = new RealQuantity(1.5, ghz);                                
                            });
                            g.spectrum = Arrays.asList( 
                                    createScienceSpectralWindow(ssw->{
                                        ssw.index = 1; //TODO is this of any use?
                                        ssw.spectralWindowSetup = createSpectralWindowSetup(sw -> { // continuum
                                            sw.start = new RealQuantity(1.2, ghz);
                                            sw.end = new RealQuantity(1.7, ghz);
                                            sw.spectralResolution = new RealQuantity(0.5, ghz);
                                            sw.isSkyFrequency = true;
                                            sw.polarization = PolStateEnum.LL; //IMPL really want a list here - or repeat the whole spectralwindow setup for each poln

                                        });
                                    }),

                                    createScienceSpectralWindow(ssw->{ // narrow window for line
                                        ssw.index = 2;
                                        ssw.expectedSpectralLine = Arrays.asList( createExpectedSpectralLine(sl->{
                                            sl.restFrequency = new RealQuantity(1.4204058, ghz);
                                            sl.description = "HI";
                                            sl.splatalogId = new StringIdentifier( "00101") ;//IMPL is stringIdentifier really useful?
                                            
                                        }));
                                        ssw.spectralWindowSetup = createSpectralWindowSetup(sw -> { 
                                            sw.start = new RealQuantity(1.41, ghz);
                                            sw.end = new RealQuantity(1.43, ghz);
                                            sw.spectralResolution = new RealQuantity(100.0, khz );
                                            sw.isSkyFrequency = false; // exact freq depends on the source...
                                            sw.polarization = PolStateEnum.LL;
                                        });

                                    })
                                    );
                        });
                    }


                            ));
            pr.observations = obs;
        }
                ));
        // "submit" proposal 
        cycle.setSubmittedProposals(               
                Arrays.asList(new SubmittedProposal(new GregorianCalendar(2022, 3, 14).getTime(), proposal)));

        // "review" proposal

        cycle.setReviewedProposals(Arrays.asList( ReviewedProposal.createReviewedProposal(r->{
            r.proposal = proposal;
            r.reviewsCompleteDate = new GregorianCalendar(2022, 4, 14).getTime();
            r.successful = true;
            r.reviews = Arrays.asList(ProposalReview.createProposalReview(pr -> {
                pr.comment = "it is good";
                pr.score = 10.0;
                pr.reviewer = new Reviewer(people[4]);
                pr.technicalFeasibility = true;
                pr.reviewDate = new GregorianCalendar(2022, 4, 14).getTime();
            }));
        })
                ));


        // "allocate" proposal

        cycle.setAllocatedProposals(Arrays.asList(
                AllocatedProposal.createAllocatedProposal(ap->{
                    ap.proposal = proposal;
                 
                    ap.allocation = Arrays.asList(
                            AllocatedBlock.createAllocatedBlock(
                                    a -> {
                                        a.grade = grades[0];
                                        a.mode = obsModes[0];
                                        Resource res = new Resource(48.0, observingTime); // IMPL should it be possible to do different units?
                                        a.resource = res;
                                    }
                                    )
                            
                            );
                })
                ));

    }



    /**
     * @throws java.lang.Exception
     */
    @AfterEach
    void tearDown() throws Exception {
    }


    //IMPL - would be nice to push these next two tests up to base class
    @org.junit.jupiter.api.Test
    void reviewJaxbTest() throws JAXBException, TransformerConfigurationException,
    ParserConfigurationException, TransformerFactoryConfigurationError,
    TransformerException {
        logger.debug("starting test");
        JAXBContext jc = ProposalManagementModel.contextFactory();
        OfferedCycles oc = new OfferedCycles();
        oc.addCycles(cycle);
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
        javax.persistence.EntityManager em = setupDB("vodml_coords"); // FIXME - need the vodml generation to tell us this name - also, it is not the most appropriate name in this case....
        em.getTransaction().begin();
        em.persist(cycle);
        em.getTransaction().commit();
        Long id = cycle.getId(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.

        em.getTransaction().begin();
        List<ProposalCycle> props = em.createNamedQuery("ProposalCycle.findById", ProposalCycle.class)
                .setParameter("id", id).getResultList();
        assertEquals(1, props.size());
        ProposalCycle pc = props.get(0);
        em.getTransaction().commit();
        assertEquals( 1, pc.getSubmittedProposals().size(),"number submitted proposals"); // trivial test      

    }


}


