# ProposalDM

## High-level design requirements

The proposal part model is primarily designed from the proposing astronomer's point-of-view, and does not contain all of the technical detail
that would be necessary from the observatory point-of-view to actually perform detailed instrument set-up for instance, though it does
strive to provide all the information necessary to schedule observations. Some top-level requirements that follow from this approach are;


* The science goal is uppermost
   - specify the target objects.
   - necessary physics goals on the observation.
* Specifics of instrumental configuration are not considered - only the broad physics of what the instrument should achieve


In addition, there are some other high level requirements of how the review should work that have driven the design;


* It should be easy to reuse the proposal for many observatories.
* Proposers should be anonymous to the reviewers (who indeed should be anonymous to the proposers).
* Being able to use the proposal information on-line and off-line - to some extent this is the raison d'être of a data model
in providing a language to transfer information between systems.



The proposal management part of the model adds extra information that the observatory needs to specify what resources are available
and to provide a method of scoring the proposal. This is deliberately created as a separate part of the overall model to help
to enforce the separation in functions so that the same proposal might be more easily submitted to several observatories.

The main concepts within the data models are [described in more detail](concepts.md)

## Acknowledgement

<p>
<img src="assets/eu-flag.jpg" style="max-height: 2em;width: auto" alt="EU Flag">  This project has received funding from the European Union's Horizon 2020 research and innovation programme under grant agreement No 101004719
</p>