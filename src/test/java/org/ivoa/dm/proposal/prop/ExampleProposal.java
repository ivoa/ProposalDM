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

import java.time.LocalDate;
import java.util.List;

import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.StringIdentifier;
import org.ivoa.dm.proposal.prop.coords.Mjd;
import org.ivoa.dm.proposal.prop.coords.Polarization;
import org.javastro.ivoacore.pgsphere.types.Point;

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
                                      c.coord = CelestialPosition.createCelestialPosition(p->{
                                         p.sourceCoordinates = new Point(45.0,60.0);
                                         p.referenceFrame = ICRS_SYS;
                                      });
                                      c.coordUnit = degrees;
                                      c.positionEpoch = new Mjd(61046.0);
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
                                                    sw.polarization = Polarization.CIRCULAR;
    
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
                                                    sw.polarization = Polarization.CIRCULAR;
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
                                          new TimingWindow( todate(LocalDate.of(2023, 1, 1)),  todate(LocalDate.of(2023, 1, 10)), "t constraint", false)
                                          );
                                 
                              }
    
    
                        ));
                  pr.observations = obs;
              }
        ));
        return proposal;
    }

}


