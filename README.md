ProposalDM
===========

The Proposal Data Model is a IVOA data model for making Observation Proposals. It is being created as project within
The Opticon RadioNet Pilot (https://www.orp-h2020.eu).

The model is described in more detail [here](https://ivoa.github.io/ProposalDM/)

<p>
<img src="https://raw.githubusercontent.com/orppst/guide/main/docs/eu-flag.jpg" height="30" alt="EU Flag"/> This project has received funding from the European Union's Horizon 2020 research and innovation programme under grant agreement No 101004719
</p>

[![main test](https://github.com/ivoa/ProposalDM/actions/workflows/test.yml/badge.svg)](https://github.com/ivoa/ProposalDM/actions/workflows/test.yml/)
[![preview generation](https://github.com/ivoa/ProposalDM/actions/workflows/preview.yml/badge.svg)](https://github.com/ivoa/ProposalDM/actions/workflows/preview.yml/)
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Foss.sonatype.org%2Fcontent%2Frepositories%2Fsnapshots%2Forg%2Fjavastro%2Fivoa%2Fdm%2Fproposaldm%2Fmaven-metadata.xml)



The IVOA standard in development is a Working Draft at version 0.5 and a
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
vodsl can be installed by following [these instructions](https://github.com/ivoa/ProposalDM/wiki/Installing-the-Eclipse-VODSL-editor)

The use cases for the model are tested by writing [java test cases](./src/test/java/org/ivoa/dm/proposal/prop) against the auto-generated code. It is always useful
to add a test for a new observatory to ensure that the model meets its requirements.

Model site documentation (in [the docs directory](docs)) requires that [graphviz](https://gitlab.com/graphviz/graphviz) and [yq](https://github.com/mikefarah/yq) are installed - it can be
tested with

```
gradle testSite
```

You should follow the usual [github flow](https://docs.github.com/en/get-started/quickstart/github-flow) for making actual changes
to the model or standard document. It is also possible to contribute suggestions and errata by opening an [issue](../../issues) 
or less formally a [discussion](../../discussions).

[More detailed information for Developers](https://github.com/ivoa/ProposalDM/wiki/ProposalDM-developer-hints)