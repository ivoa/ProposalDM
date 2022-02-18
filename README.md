ProposalDM
===========

The Proposal Data Model is a IVOA data model for making Observation Proposals. It is being created as project within
The Opticon RadioNet Pilot (https://www.orp-h2020.eu).

The IVOA standard in development is a Working Draft at version 0.2 and a
[![PDF-Preview](https://img.shields.io/badge/Preview-PDF-blue)](../../releases/download/auto-pdf-preview/ProposalDM-draft.pdf)
is automatically produced from this repository when changes are made.

Latest Model Diagrams
--------------------
The proposal part
![proposalDM](../../releases/download/auto-pdf-preview/proposaldm.vo-dml.png)

The proposal management part
![proposalManagementDM](../../releases/download/auto-pdf-preview/proposalManagement.vo-dml.png)


Contributing
-------------

The data model is actually developed in [VODSL](https://github.com/pahjbo/vodsl) in directory [src/main/vodsl](./src/main/vodsl), although the definitive 
representation is of course in [VO-DML](https://github.com/ivoa/vo-dml). An eclipse editor that can act as an IDE for 
vodsl can be installed by following [these instructions](https://github.com/pahjbo/vodsl#installation-of-pre-built-editor)

The use cases for the model are tested by writing [java test cases](./src/test/java/org/ivoa/dm/proposal/prop) against the auto-generated code. It is always useful
to add a test for a new observatory to ensure that the model meets its requirements.

You should follow the usual [github flow](https://docs.github.com/en/get-started/quickstart/github-flow) for making actual changes
to the model or standard document. It is also possible to contribute suggestions and errata by opening an [issue](../../issues)