/*
 * Created on 20 Jan 2022 
 * Copyright 2022 Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License in file LICENSE
 */ 

package org.ivoa.dm.proposal.prop;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.ivoa.dm.proposal.prop.ObservingProposal.ObservingProposalBuilder;
import org.ivoa.dm.stc.coords.CartesianCoordSpace;
import org.ivoa.dm.stc.coords.SpaceFrame;
import org.ivoa.dm.stc.coords.SpaceSys;
import org.ivoa.dm.stc.coords.StdRefLocation;
import org.ivoa.dm.proposal.management.Reviewer;
import org.ivoa.dm.proposal.management.SubmittedProposal;
import org.ivoa.dm.proposal.management.TAC;
import org.ivoa.dm.proposal.management.TacRole;
import org.ivoa.dm.proposal.management.CommitteeMember;
import org.ivoa.dm.proposal.management.OfferedCycles;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.ProposalCycle.ProposalCycleBuilder;
import org.ivoa.vodml.stdtypes2.Ivorn;
import org.ivoa.vodml.stdtypes2.RealQuantity;
import org.ivoa.vodml.stdtypes2.Unit;
import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;
import org.javastro.ivoa.tests.AbstractJAXBJPATest;

import static org.ivoa.dm.proposal.management.ProposalCycle.createProposalCycle;
import static org.ivoa.dm.stc.coords.GeocentricPoint.createGeocentricPoint;

/**
 * An abstract base class that creates entities that would be similar for all proposals.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) 
 * @since 20 Jan 2022
 */
public abstract class AbstractProposalTest extends AbstractJAXBJPATest {

    /** SPACE_SYS.
     */
    protected final SpaceSys GEO_SYS = new SpaceSys(new CartesianCoordSpace(),new SpaceFrame(new StdRefLocation("TOPOCENTRE"), "ICRF", null, ""));//FIXME - this should really define the frame better - STC coords library should have some standard model instances...
    
    protected final SpaceSys ICRS_SYS = new SpaceSys(new CartesianCoordSpace(),new SpaceFrame(new StdRefLocation("TOPOCENTRE"), "ICRS", null, ""));//FIXME - this should really define the frame better - STC coords library  should have some standard model instances...
    /** logger for this class */
    static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
                .getLogger(ProposalDMTest.class);
    protected ObservingProposal proposal;
    
    protected Organization[] institutes = {
            new Organization("org", "org address",new Ivorn("ivo://org/anorg")),
            new Organization("org2", "org2 address",new Ivorn("ivo://org/org2"))
           
    };
    
    protected Person[] people = {
            new Person("PI", "pi@unreal.not.email", institutes[0]),
            new Person("CO-I", "coi@unreal.not.email", institutes[1]),
            new Person("TAC Chair", "tacchair@unreal.not.email", institutes[1]),
            new Person("TAC member", "tacmamber@unreal.not.email", institutes[0]),
            new Person("reviewer", "reviewer@unreal.not.email", institutes[1]),

    };
    protected List<Investigator> investigators = Arrays.asList(
            new Investigator ( InvestigatorKind.PI, people[0] ),
            new Investigator ( InvestigatorKind.COI, people[1] ));
    protected ProposalCycle cycle;
    
    protected Reviewer[] reviewers = {new Reviewer(people[2]),
            new Reviewer(people[3]),
            new Reviewer(people[4])// reviewer not on TAC
            }; 
    
    protected TAC tac = new TAC( Arrays.asList(
            new CommitteeMember ( TacRole.CHAIR, reviewers[0]),
            new CommitteeMember ( TacRole.SCIENCEREVIEWER, reviewers[1])
            ));
    protected Unit metres = new Unit("m");
    protected Unit degrees = new Unit("degrees");
   

    protected Consumer<ObservingProposalBuilder> proposalCommonSetup() {
        return pr -> {
            pr.code = "pr1";
            pr.dateSubmitted = new Date();
            pr.kind = ProposalKind.STANDARD;
            pr.title = "the proposal title";
            pr.summary = "a test proposal";
            pr.investigators = investigators ;
    
        };
    }
    protected Consumer<ProposalCycleBuilder> cycleCommonSetup() {
        return c -> {
                c.submissionDeadline = new GregorianCalendar(2022, 3, 15).getTime();
                c.observationSessionStart = new GregorianCalendar(2022, 6, 1).getTime();
                c.observationSessionEnd = new GregorianCalendar(2022, 9, 1).getTime();
                c.tac = tac; 
        };
        
    }

    /**
     * Create telescope with cartesian coordinates.
     * @param name
     * @param x
     * @param y
     * @param z
     * @return
     */
    protected Telescope createTelescope(String name, double x, double y, double z) {
        return new Telescope(name, createGeocentricPoint(p ->{
            p.x = new RealQuantity(x, metres);
            p.y = new RealQuantity(y, metres);
            p.z = new RealQuantity(z, metres);
            p.coordSys = GEO_SYS;
        }));

    }
 
    
    @org.junit.jupiter.api.Test
    void proposalDmJaxbTest() throws JAXBException, TransformerConfigurationException,
            ParserConfigurationException, TransformerFactoryConfigurationError,
            TransformerException {
                logger.debug("starting test");
                JAXBContext jc = ProposalModel.contextFactory();
                JaxbAnnotationMeta<ObservingProposal> meta = JaxbAnnotationMeta.of(ObservingProposal.class);
                DescriptionValidator<ObservingProposal> validator = new DescriptionValidator<>(jc, meta);
                DescriptionValidator.Validation validation = validator.validate(proposal);
                if(!validation.valid) {
                    System.err.println(validation.message);
                }
                assertTrue(validation.valid);
                ProposalModel model = new ProposalModel();
                model.addContent(proposal);
                model.makeRefIDsUnique();
                
                ProposalModel modelin = roundtripXML(jc, model, ProposalModel.class);
                List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
                assertEquals(1, props.size());
                
            }

  @org.junit.jupiter.api.Test
    void proposalDmJPATest() {
                javax.persistence.EntityManager em = setupDB("vodml_coords"); // FIXME - need the vodml generation to tell us this name - also, it is not the most appropriate name in this case....
        em.getTransaction().begin();
        em.persist(proposal);
        em.getTransaction().commit();
        String id = proposal.getCode(); //FIXME - need to add something in VO-DML gen to know what the "natural key" is for db work.
        
        em.getTransaction().begin();
        List<ObservingProposal> props = em.createNamedQuery("ObservingProposal.findById", ObservingProposal.class)
                .setParameter("id", id).getResultList();
        assertEquals(1, props.size());
        ObservingProposal prop = props.get(0);
        em.getTransaction().commit();
        assertEquals( 2, prop.getInvestigators().size(),"number of investigators"); // trivial test      

    }}


 