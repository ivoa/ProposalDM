Motivating Use Cases
====================

The use cases that have driven the design have been divided into two main categories

* [Example Telescopes](telescopeDescriptions.md)
* [Example Observing types](observationDescriptions.md)

In both these cases the Data Model is exercised by creating instances using the Java code generated from the models and running some basic unit tests on serialization. The code is based at [this location](https://github.com/ivoa/ProposalDM/tree/master/src/test/java/org/ivoa/dm/proposal/prop). Writing these tests allows exploration of various aspects of the quality of the design su
ch as;

* verbosity/expressiveness
* ease of manipulation of parts of the model
* contradictions in meaning between different parts of the model.