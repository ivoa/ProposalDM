package org.ivoa.dm.proposal.prop;
/*
 * Created on 17/03/2022 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import static org.ivoa.dm.stc.coords.RealCartesianPoint.createRealCartesianPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Consumer;

import org.ivoa.dm.ivoa.Ivoid;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.StringIdentifier;
import org.ivoa.dm.proposal.management.CommitteeMember;
import org.ivoa.dm.proposal.management.ProposalCycle;
import org.ivoa.dm.proposal.management.Reviewer;
import org.ivoa.dm.proposal.management.TAC;
import org.ivoa.dm.proposal.management.TacRole;
import org.ivoa.dm.proposal.management.Telescope;
import org.ivoa.dm.stc.coords.SpaceFrame;
import org.ivoa.dm.stc.coords.SpaceSys;
import org.ivoa.dm.stc.coords.StdRefLocation;
import org.ivoa.vodml.stdtypes.Unit;

public abstract class BaseExample {
    /** SPACE_SYS.
     */
    //IMPL - removed the CartesianCoordSpace from the definitions below because you have to include the axis definitions which is a pain....
    protected  final  SpaceSys GEO_SYS = new SpaceSys().withFrame(new SpaceFrame(new StdRefLocation("TOPOCENTER"), "ICRS", null, ""));//FIXME - this should really define the frame better - STC coords library should have some standard model instances...

    protected   final  SpaceSys ICRS_SYS = new SpaceSys().withFrame(new SpaceFrame(new StdRefLocation("TOPOCENTER"), "ICRS", null, ""));//FIXME - this should really define the frame better - STC coords library  should have some standard model instances...
    protected   final Organization[] institutes = {
            new Organization("org", "org address",new Ivoid("ivo://org/anorg"), null),//TODO is null same as not setting?
            new Organization("org2", "org2 address",new Ivoid("ivo://org/org2"), null)

    };

    protected  final Person[] people = {
            new Person("John Flamsteed", "pi@unreal.not.email", institutes[0], new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-003")),
            new Person("George Airy", "coi@unreal.not.email", institutes[1], new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-004")),
            new Person("Edmond Halley", "tacchair@unreal.not.email", institutes[1], new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-005")),
            new Person("James Bradley", "tacmamber@unreal.not.email", institutes[0], new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-006")),
            new Person("Nevil Maskelyne ", "reviewer@unreal.not.email", institutes[1], new StringIdentifier("https://notreallyorcid.org/0000-0001-0002-007")),

    };
    protected  List<Investigator> investigators = makeList(
            new Investigator ( people[0], InvestigatorKind.PI, false ),
            new Investigator ( people[1], InvestigatorKind.COI, true  ));

    protected  Reviewer[] reviewers = {new Reviewer(people[2]),
            new Reviewer(people[3]),
            new Reviewer(people[4])// reviewer not on TAC
    };

    protected TAC tac = new TAC( makeList(
            new CommitteeMember( reviewers[0], TacRole.CHAIR ),
            new CommitteeMember ( reviewers[1], TacRole.SCIENCEREVIEWER)
            ));


   
    //some units
    protected static final  Unit metres = new Unit("m");
    protected static final Unit degrees = new Unit("degrees");
    protected static final Unit arcsec = new Unit("arcsec");
    protected static final Unit ghz = new Unit("GHz");
    protected static final Unit khz = new Unit("kHz");
 
   

    protected  Consumer<ObservingProposal.ObservingProposalBuilder> proposalCommonSetup() {
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
            c.code="EM01";
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
     * make a read-write list.
     * 
     * @param <T> list type
     * @param a objects 
     * @return a list
     */
    protected static <T> List<T> makeList(T... a) {
        return new ArrayList<T>(Arrays.asList(a));
    }
 
 
}
