/*
 * Created on 25 Oct 2024 
 * Copyright 2024 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.ivoa.dm.proposal.management.Observatory.createObservatory;
import static org.ivoa.dm.proposal.management.ProposalCycle.createProposalCycle;
import static org.ivoa.dm.proposal.prop.SpectralWindowSetup.createSpectralWindowSetup;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.ivoa.dm.ivoa.Ivoid;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.proposal.management.AllocatedBlock;
import org.ivoa.dm.proposal.management.AllocatedProposal;
import org.ivoa.dm.proposal.management.AllocationGrade;
import org.ivoa.dm.proposal.management.AvailableResources;
import org.ivoa.dm.proposal.management.Backend;
import org.ivoa.dm.proposal.management.Filter;
import org.ivoa.dm.proposal.management.Instrument;
import org.ivoa.dm.proposal.management.InstrumentKind;
import org.ivoa.dm.proposal.management.ObservationConfiguration;
import org.ivoa.dm.proposal.management.Observatory;
import org.ivoa.dm.proposal.management.ObservingMode;
import org.ivoa.dm.proposal.management.ProposalReview;
import org.ivoa.dm.proposal.management.Resource;
import org.ivoa.dm.proposal.management.ResourceType;
import org.ivoa.dm.proposal.management.Reviewer;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.proposal.management.Telescope;
import org.ivoa.dm.stc.coords.PolStateEnum;

/**
 *  .
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 25 Oct 2024
 */
public class NOTexample extends BaseObservatoryExample implements TACFunctions {

    private static final double c = 299792458.0;        
    /**
     * @param centwl in nm
     * @param fwhm in nm
     * @return
     */
    private SpectralWindowSetup fromWl( double centwl, double fwhm)
    {
        //FIXME - converting to frequency range is not really natural - perhaps need wavelength spedifier
            return createSpectralWindowSetup(sw -> {
            sw.start = new RealQuantity(c/(centwl-fwhm)/1e9, ghz);
            sw.end = new RealQuantity(c/(centwl+fwhm)/1e9, ghz);
            sw.polarization = PolStateEnum.V ; //FIXME - just plain wrong!
            sw.isSkyFrequency = true;
            sw.spectralResolution = new RealQuantity(sw.end.getValue()-sw.start.getValue(), ghz); //Indicate that resolution is not important - set same as width.
        });
    }
    
    
     private org.ivoa.dm.proposal.management.Filter makeFilter(String name, double centwl, double fwhm) {
         return new org.ivoa.dm.proposal.management.Filter(name, name, fromWl(centwl,fwhm));
     }
    
     Telescope NOT = createTelescope("NOT",5327476.3372,-1719196.424,3051336.866);
     Telescope[] telescopes = {
             NOT
     };
     private Observatory observatory;
     
     Backend backend = new Backend("standard pipeline", true);
    private ObservingMode obsModes[];
    private AllocationGrade grades[];
    private ResourceType observingTime;
   
     public NOTexample() {
         
          
             Instrument alfosc = Instrument.createInstrument( i -> {i.name="ALFOSC"; //FIXME ALFOSC can be spetroscopie
                i.reference="http://www.not.iac.es/instruments/alfosc/";
                i.kind=InstrumentKind.CONTINUUM; });
             Instrument notcam =  Instrument.createInstrument( i -> {i.name="NOTCAM"; 
                i.reference="http://www.not.iac.es/instruments/notcam/";
                i.kind=InstrumentKind.CONTINUUM; });
             Instrument fies =  Instrument.createInstrument( i -> {i.name="FIES"; 
                i.reference="http://www.not.iac.es/instruments/fies/";
                i.kind=InstrumentKind.SPECTROSCOPIC; });
              Instrument mosca =  Instrument.createInstrument( i -> {i.name="MOSCA"; 
                i.reference="http:/www.not.iac.es/instruments/mosaic/";
                i.kind=InstrumentKind.CONTINUUM; });
               Instrument stancam = Instrument.createInstrument( i -> {i.name="STANCAM"; 
                i.reference="http://www.not.iac.es/instruments/stancam/";
                i.kind=InstrumentKind.CONTINUUM; });
               Instrument dipoluf =Instrument.createInstrument( i -> {i.name="DIPol-UF"; 
                i.reference="http://www.not.iac.es/instruments/dipol-uf/";
                i.kind=InstrumentKind.CONTINUUM; });
               Instrument sofin = Instrument.createInstrument( i -> {i.name="SOFIN"; 
                i.reference="http://www.not.iac.es/instruments/sofin/";
                i.kind=InstrumentKind.CONTINUUM; });
               
               
                
          Instrument[] instruments = {     
             alfosc,notcam, fies, mosca, stancam, dipoluf, sofin   
                
          };
          
          
         
          observatory = createObservatory(obs -> {
            obs.address = "on earth";
            obs.ivoid = new Ivoid("ivo://obs/anobs");
            obs.homePage="https://www.not.iac.es";
            obs.name = "Nordic Optical Telescope";
            obs.telescopes = Arrays.asList(telescopes);
            
            obs.instruments = Arrays.asList(instruments);
            obs.backends =Arrays.asList(backend);

        });
          
        observingTime = new ResourceType("observing time", "hours");

        
        
        //Filters
        Filter u_Str_351_34 = makeFilter("u_Str 351_34", 331, 34);
        Filter v_Str_411_18 = makeFilter("v_Str 411_18", 411, 18);
        Filter b_Str_467_18 = makeFilter("b_Str 467_18", 467, 18);
        Filter y_Str_547_22 = makeFilter("y_Str_547_22", 547, 22);
        Resource availableObservingTime = new Resource(observingTime, 100 * 24.0);   
        obsModes = new ObservingMode[] {
             makeObsMode("ALFOSC U Filter", alfosc, u_Str_351_34),
             makeObsMode("ALFOSC V Filter", alfosc, v_Str_411_18),
             makeObsMode("ALFOSC B Filter", alfosc, b_Str_467_18),
             makeObsMode("ALFOSC Y Filter", alfosc, y_Str_547_22),
             makeObsMode("MOSCA U Filter", mosca, u_Str_351_34),
             makeObsMode("MOSCA V Filter", mosca, v_Str_411_18),
             makeObsMode("MOSCA B Filter", mosca, b_Str_467_18),
             makeObsMode("MOSCA Y Filter", mosca, y_Str_547_22),
             
        };
         grades = new AllocationGrade[] {
                 new AllocationGrade("OK", "this is a placeholder for now")
                // FIXME do not know about grades....
         };
         
          cycle = createProposalCycle(cycleCommonSetup().andThen(cy -> {
            cy.observatory = observatory;
            cy.title = "Period 71";
            cy.instructions = "https://www.not.iac.es/observing/proposals/P71/";
            cy.possibleGrades = Arrays.asList(grades);
            cy.observingModes = Arrays.asList(obsModes);
            cy.availableResources = new AvailableResources(Arrays.asList(availableObservingTime));//IMPL is there one too many layers of encapsulation here?

        }));
        
     }

    private ObservingMode makeObsMode(String name,  Instrument i, Filter f){
        return new ObservingMode(name, name,
                NOT, i, new Filter(f), backend);//IMPL note new filter contained in each
    }
    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.TACFunctions#submitProposal(org.ivoa.dm.proposal.prop.ObservingProposal)
     */
    @Override
    public SubmittedProposal submitProposal(ObservingProposal frozenProposal) {
        List<ObservationConfiguration> obsConfigs = makeList(
                new ObservationConfiguration(frozenProposal.getObservations(),obsModes[0])
                );
        
        final SubmittedProposal submittedProposal = new SubmittedProposal( frozenProposal, obsConfigs,new GregorianCalendar(2022, 3, 14).getTime(),  false, new GregorianCalendar(2022, 4, 30).getTime(),  null );
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
          final AllocatedProposal allocatedProposal = AllocatedProposal.createAllocatedProposal(ap -> {
              ap.submitted = p;
      
              ap.allocation = makeList(
                    AllocatedBlock.createAllocatedBlock(
                          a -> {
                              a.grade = grades[0];
                              a.mode = obsModes[0];//FIXME really need to go through all the modes in the proposal.
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


