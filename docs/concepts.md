Main Concepts
=============


## ***Purpose of Polaris***

A [Principal Investigator](https://ivoa.github.io/ProposalDM/generated/proposal/Person/) is interested in one or more [Targets](https://ivoa.github.io/ProposalDM/generated/proposal/Target/) and they decide that they want to create an [Observing Proposal](https://ivoa.github.io/ProposalDM/generated/proposal/ObservingProposal/) to study them. They write a [Science Justification](https://ivoa.github.io/ProposalDM/generated/proposal/Justification/) to try to persuade the [Time Allocation Committee](https://ivoa.github.io/ProposalDM/generated/proposalManagement/TAC/) for a particular [Observatory](https://ivoa.github.io/ProposalDM/generated/proposal/Observatory/) to allocate some [Observations](https://ivoa.github.io/ProposalDM/generated/proposal/Observation/) on one or more [Telescopes](https://ivoa.github.io/ProposalDM/generated/proposal/Telescope/) that they operate.

The Polaris system will capture sufficient information to allow the TAC to make an informed decision on time allocation for a proposal. The detailed requirements will be negotiated once an allocation has been made.

This document describes the associated data model used by Polaris in layman’s terms.

## ***Glossary of terms***

## **An Observing Proposal**

+ is created and managed through the Polaris GUI.

+ is identified by its title
+ can be one of three kinds
    + Standard – normal
    + ToO – target of opportunity
    + Survey – part of a large programme
+ consists of at least one of each of the following, unless otherwise indicated
    + [*Title*](#title)
    + [*Summary*](#summary)
    + [*Investigator(s)*](#investigators)
    + [*Justification(s)*](#justifications)
    + [*Target(s)*](#targets)
    + [*Technical Goal(s)*](#technical-goals)
    + [*Field(s)*](#fields)
    + [*Observation(s)*](#observations)
    + [*Supporting Document(s)*](#supporting-documents) (optional)
+ may or may not have been [*submitted*](https://ivoa.github.io/ProposalDM/generated/proposal/Submission from propmgt/)
    + once assembled, a proposal may be submitted to one or more [*Observatories*](#observatory) using the Polaris GUI.
+ may be related to one or more [*other proposals*](https://ivoa.github.io/ProposalDM/generated/proposal/RelatedProposal/)

### **Proposal components**

#### [*Title*](https://ivoa.github.io/ProposalDM/generated/proposal/ObservingProposal/)

#### [*Summary*](https://ivoa.github.io/ProposalDM/generated/proposal/ObservingProposal/)

#### [*Investigator(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Investigator/)

+ is a [*named registered Polaris user*](https://ivoa.github.io/ProposalDM/generated/proposal/Person/) 
+ belongs to a named [*Organization*](#organization)
+ is one of two kinds
    + Principal investigator (PI) – who owns the proposal
    + Co-Investigator – co-opted by the PI to assist in the work (optional)
+ may or may not be involved as part of their PhD

#### [*Justification(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Justification/)

+ come in two types
    + Scientific
    + Technical
+ Can be entered in three formats, one of
    + LaTeX
    + RST
    + ascii
+ must not include information that rightly belongs elsewhere in the model, eg performance parameters

#### [*Target(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Target/)

+ has a unique name
+ is
    + either within the solar system
    + or celestial (not in the solar system) described by
        + equatorial coordinates in degrees
        + epoch (default J2000)
        + proper motion details
+ although the same targets can be mentioned in more than proposal, for the purposes of Polaris, a target belongs to a proposal and must be identified as such.

#### [*Technical Goal(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/TechnicalGoal/)

These are complex objects.

+ each technical goal consists of
    + one, and only one, set of [*Performance Parameters*]((https://ivoa.github.io/ProposalDM/generated/proposal/PerformanceParameters/)) required for the observation to be useful for the science goal
    + one [*Science Spectral Window*](https://ivoa.github.io/ProposalDM/generated/proposal/ScienceSpectralWindow/) (optionally more than one) which consists of two sets of (optional) information
        + [*Spectral Window Setup*](https://ivoa.github.io/ProposalDM/generated/proposal/SpectralWindowSetup/) (range of frequencies, polarization, etc ) - just one if present
        + [*Expected Spectral Line(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/ExpectedSpectralLine/) (zero or more) having
            + an identifier (splatalogId)
            + a description

+ as with Targets, a set of Technical Goals is specific to a proposal, but any other proposal may specify exactly the same goals.


#### [*Field(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Field/)

+ some targets appear close together so can be considered as sitting in a particular field of view, in which case an observation can contain more than one target.
+ can be more precisely described as
    + [*Target Field*](https://ivoa.github.io/ProposalDM/generated/proposal/TargetField/)– as defined in the Target
    + [*Polygonal Map*](https://ivoa.github.io/ProposalDM/generated/proposal/Polygon/)
    + [*Elliptical Map*](https://ivoa.github.io/ProposalDM/generated/proposal/Ellipse/)
    + A single [*Point*](https://ivoa.github.io/ProposalDM/generated/proposal/Point/) on the sky described in various ways,
        + [*Cartesian (x, y, z)*](https://ivoa.github.io/ProposalDM/generated/coords/CartesianPoint/)
        + [*Longitude and Latitude (long, lat, distance from origin)*](https://ivoa.github.io/ProposalDM/generated/coords/LonLatPoint/)
        + [*Generic (x, y, z)*](https://ivoa.github.io/ProposalDM/generated/coords/GenericPoint/)
        + [*Equatorial (longitudinal angle, latitudinal angle)*](https://ivoa.github.io/ProposalDM/generated/coords/EquatorialPoint/)
        + [*Spherical (radius, polar angle, azimuthal angle)*](https://ivoa.github.io/ProposalDM/generated/coords/SphericalPoint/)
        + [*Real Cartesian (x, y, z)*](https://ivoa.github.io/ProposalDM/generated/coords/RealCartesianPoint/)

#### [*Observation(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Observation/)

Like technical goals, observations are complex objects.

+ an observation is an assembly of
    + one or more proposal [*Targets*](#targets)
    + one proposal [*Field*](#fields)
    + one proposal [*Technical Goal*](#technical-goals)
    + optionally one or more [*Observing Constraints*](#observing-constraints) specific to the observation, which are described by
        + [*Timing Constraints*](#timing-constraints), which is one of
            + A [*Timing Window*](#timing-constraints) – when, or when not, to observe
            + A [*Simultaneity Constraint*](#simultaneity-constraint) – a requirement that two or more observations happen at the same time
        + A [*Pointing constraint*](#pointing-constraint) – a limitation of the telescope’s pointing ability
+ it must also specify its purpose, one of
    + Target – some science is being done here
    + Calibration – used for exactly that
+ must not overlap another observation in the same proposal

#### [*Supporting Document(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/SupportingDocument/)

+ these consist of pictures and/ or graphs supplied as supporting evidence for the proposal.
+ they have
    + a title
    + a URL where they can be found


## **An Observatory**

#### [*Observatory*](https://ivoa.github.io/ProposalDM/generated/proposal/Observatory/)

+ details are managed through the Polaris CLI, there is no GUI
+ belongs to a named [*Organization*](#organization)
+ consists of at least one of each of the following types of equipment, unless otherwise indicated
    + [*Telescope(s)*](#telescopes)
    + [*Instrument(s)*](#instruments)
    + [*Backend(s)*](#backends) – eg a correlator
    + [*Telescope Array(s)*](#telescope-arrays) (optional)

+ can cluster its equipment into any number of configurations collated as [*Observing modes*](https://ivoa.github.io/ProposalDM/generated/proposal/ObservingMode/), of which one will be used to make an observation.

### **Observatory resources**

#### [*Telescope(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Telescope/)

+ has a name and location
+ can stand alone or be clustered in arrays

#### [*Instrument(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Instrument/)

+ has a name and description
+ can be attached to a [*Telescope*](#telescopes) – eg is a CCD etc
+ is characterised by its
    + [*Spectral Window Setup*](https://ivoa.github.io/ProposalDM/generated/proposal/SpectralWindowSetup/) (range of frequencies, polarization, etc)
    + kind, which is one of
        + continuum
        + spectroscopic

#### [*Backend(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/Backend/)

+ have a name
+ may be able to work in parallel with other backends
+ do some processing of the observation – eg a correlator

#### [*Telescope Array(s)*](https://ivoa.github.io/ProposalDM/generated/proposal/TelescopeArray/)


## **Associated Details**

### [*Organization*](https://ivoa.github.io/ProposalDM/generated/proposal/Organization/)

### [*Observing Constraints*](https://ivoa.github.io/ProposalDM/generated/proposal/ObservingConstraint/)

### [*Timing Constraints*](https://ivoa.github.io/ProposalDM/generated/proposal/TimingConstraint/)

#### [*Timing Window*](https://ivoa.github.io/ProposalDM/generated/proposal/TimingWindow/)

####  [*Simultaneity Constraint*](https://ivoa.github.io/ProposalDM/generated/proposal/SimultaneityConstraint/)

###   [*Pointing Constraint*](https://ivoa.github.io/ProposalDM/generated/proposal/PointingConstraint/)

## **Detailed Documentation**

Autogenerated documentation for the models

* [ProposalDM](https://ivoa.github.io/ProposalDM/generated/proposaldm.vo-dml.md)
* [ProposalManagementDM](https://ivoa.github.io/ProposalDM/generated/proposalManagement.vo-dml.md)

