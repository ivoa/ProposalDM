package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.ivoa.dm.proposal.management.*;
import org.ivoa.dm.stc.coords.CartesianCoordSpace;
import org.ivoa.dm.stc.coords.SpaceFrame;
import org.ivoa.dm.stc.coords.SpaceSys;
import org.ivoa.dm.stc.coords.StdRefLocation;
import org.ivoa.dm.ivoa.Ivorn;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.StringIdentifier;
import org.ivoa.vodml.stdtypes.Unit;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;

import static org.ivoa.dm.stc.coords.RealCartesianPoint.createRealCartesianPoint;

public abstract class BaseExample implements ExampleGenerator {
    /** SPACE_SYS.
     */
    protected final  SpaceSys GEO_SYS = new SpaceSys(new CartesianCoordSpace(),new SpaceFrame(new StdRefLocation("TOPOCENTRE"), "ICRF", null, ""));//FIXME - this should really define the frame better - STC coords library should have some standard model instances...

    protected final  SpaceSys ICRS_SYS = new SpaceSys(new CartesianCoordSpace(),new SpaceFrame(new StdRefLocation("TOPOCENTRE"), "ICRS", null, ""));//FIXME - this should really define the frame better - STC coords library  should have some standard model instances...
    protected Organization[] institutes = {
            new Organization("org", "org address",new Ivorn("ivo://org/anorg"), null),//TODO is null same as not setting?
            new Organization("org2", "org2 address",new Ivorn("ivo://org/org2"), null)

    };

    protected Person[] people = {
            new Person("John Flamsteed", "pi@unreal.not.email", new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-003"), institutes[0]),
            new Person("George Airy", "coi@unreal.not.email", new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-004"), institutes[1]),
            new Person("Edmond Halley", "tacchair@unreal.not.email", new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-005"), institutes[1]),
            new Person("James Bradley", "tacmamber@unreal.not.email", new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-006"), institutes[0]),
            new Person("Nevil Maskelyne ", "reviewer@unreal.not.email", new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-007"), institutes[1]),

    };
    protected List<Investigator> investigators = Arrays.asList(
            new Investigator ( InvestigatorKind.PI, false, people[0] ),
            new Investigator ( InvestigatorKind.COI, true, people[1] ));

    protected Reviewer[] reviewers = {new Reviewer(people[2]),
            new Reviewer(people[3]),
            new Reviewer(people[4])// reviewer not on TAC
    };

    protected TAC tac = new TAC( Arrays.asList(
            new CommitteeMember( TacRole.CHAIR, reviewers[0]),
            new CommitteeMember ( TacRole.SCIENCEREVIEWER, reviewers[1])
            ));
    
    //some units
    protected static final  Unit metres = new Unit("m");
    protected static final Unit degrees = new Unit("degrees");

    protected static final Unit arcsec = new Unit("arcsec");


    protected Consumer<ObservingProposal.ObservingProposalBuilder> proposalCommonSetup() {
        return pr -> {
            pr.kind = ProposalKind.STANDARD;
            pr.title = "Observing the stars";
            pr.summary = "measure parallax and spectra";
            pr.investigators = investigators ;
            pr.scientificJustification = new Justification("this will be a great contribution to our knowledge of the universe", TextFormats.ASCIIDOC);
            pr.technicalJustification = new Justification("we can do this nowadays!", TextFormats.ASCIIDOC);

        };
    }
    protected Consumer<ProposalCycle.ProposalCycleBuilder> cycleCommonSetup() {
        return c -> {
            c.submissionDeadline = new GregorianCalendar(2022, 3, 15).getTime();
            c.observationSessionStart = new GregorianCalendar(2022, 6, 1).getTime();
            c.observationSessionEnd = new GregorianCalendar(2022, 9, 1).getTime();
            c.title="test cycle";
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
        return new Telescope(name, null, createRealCartesianPoint(p ->{
            p.x = new RealQuantity(x, metres);
            p.y = new RealQuantity(y, metres);
            p.z = new RealQuantity(z, metres);
            p.coordSys = GEO_SYS;
        }));

    }
    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.ExampleGenerator#getICRF()
     */
    @Override
    public SpaceSys getICRF() {
        return getICRF();
    }
    /**
     * {@inheritDoc}
     * overrides @see org.ivoa.dm.proposal.prop.ExampleGenerator#getICRS()
     */
    @Override
    public SpaceSys getICRS() {
        return ICRS_SYS;
        
    }

}
