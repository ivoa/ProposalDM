/*
 * Created on 19 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.ivoa.dm.proposal.prop.ExpectedSpectralLine.createExpectedSpectralLine;
import static org.ivoa.dm.proposal.prop.ObservingProposal.createObservingProposal;
import static org.ivoa.dm.proposal.prop.PerformanceParameters.createPerformanceParameters;
import static org.ivoa.dm.proposal.prop.ScienceSpectralWindow.createScienceSpectralWindow;
import static org.ivoa.dm.proposal.prop.SpectralWindowSetup.createSpectralWindowSetup;
import static org.ivoa.dm.proposal.prop.TargetObservation.createTargetObservation;

import java.util.Date;
import java.util.List;

import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.StringIdentifier;
import org.ivoa.dm.stc.coords.Epoch;
import org.ivoa.dm.stc.coords.EquatorialPoint;
import org.ivoa.dm.stc.coords.PolStateEnum;

/**
 *  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 19 Oct 2024
 */
public class ExampleProposal extends BaseExample implements ProposalGenerator{

    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.ProposalGenerator#getProposal()
     */
    @Override
    public ObservingProposal getProposal() {
    
        final Target target =  CelestialTarget.createCelestialTarget(c -> {
                                      c.sourceName = "fictional";
                                      c.sourceCoordinates = new EquatorialPoint(new RealQuantity(45.0, degrees), new RealQuantity(60.0, degrees), ICRS_SYS);//IMPL it would actually be nice to be able to input sexagesimal - that is the most human readable
                                      c.positionEpoch = new Epoch("J2013.123");//FIXME - this is not really what epoch means
                                  });
        
        final Field field = new TargetField("source1");
        
        //FIXME think about representing Optical and radio technical goals.
        final TechnicalGoal tgoal = TechnicalGoal.createTechnicalGoal(g -> {
                                      g.performance = createPerformanceParameters(p -> {
                                          p.desiredAngularResolution = new RealQuantity(25., arcsec);
                                          p.desiredLargestScale = new RealQuantity(0.1, degrees);
                                          p.representativeSpectralPoint = new RealQuantity(1.5, ghz);
                                      });
                                      g.spectrum = makeList(
                                            createScienceSpectralWindow(ssw -> {
                                                ssw.spectralWindowSetup = createSpectralWindowSetup(sw -> { // continuum
                                                    sw.start = new RealQuantity(1.2, ghz);
                                                    sw.end = new RealQuantity(1.7, ghz);
                                                    sw.spectralResolution = new RealQuantity(0.5, ghz);
                                                    sw.isSkyFrequency = true;
                                                    sw.polarization = PolStateEnum.LL; //IMPL really want a list here - or repeat the whole spectralwindow setup for each poln
    
                                                });
                                            }),
    
                                            createScienceSpectralWindow(ssw -> { // narrow window for line
                                                ssw.expectedSpectralLine = makeList(createExpectedSpectralLine(sl -> {
                                                    sl.restFrequency = new RealQuantity(1.4204058, ghz);
                                                    sl.description = "HI";
                                                    sl.splatalogId = new StringIdentifier("00101");//IMPL is stringIdentifier really useful?
    
                                                }));
                                                ssw.spectralWindowSetup = createSpectralWindowSetup(sw -> {
                                                    sw.start = new RealQuantity(1.41, ghz);
                                                    sw.end = new RealQuantity(1.43, ghz);
                                                    sw.spectralResolution = new RealQuantity(100.0, khz);
                                                    sw.isSkyFrequency = false; // exact freq depends on the source...
                                                    sw.polarization = PolStateEnum.LL;
                                                });
    
                                            })
                                      );
                                  });
        // set up the specific proposal
        ObservingProposal proposal = createObservingProposal(proposalCommonSetup().andThen(pr -> {
            pr.targets = makeList(target);
            pr.fields = makeList(field);
            pr.technicalGoals = makeList(tgoal);
                  List<Observation> obs =makeList( //IMPL note the wrapping in a new ArrayList as otherwise the list is readonly, and we want to add observations in the tests
                        createTargetObservation(t -> {
                                  t.target = makeList(target);
                                  t.field = field;
                                  t.technicalGoal = tgoal;
                                  t.constraints = makeList(
                                          new TimingWindow(new Date(2023, 1, 1), new Date(2023, 1, 10), "t constraint", false)
                                          );
                                 
                              }
    
    
                        ));
                  pr.observations = obs;
              }
        ));
        return proposal;
    }

}


