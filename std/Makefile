# ivoatex Makefile.  The ivoatex/README for the targets available.

# short name of your document (edit $DOCNAME.tex; would be like RegTAP)
DOCNAME = ProposalDM

# count up; you probably do not want to bother with versions <1.0
DOCVERSION = 0.6

# Publication date, ISO format; update manually for "releases"
DOCDATE = ???

# What is it you're writing: NOTE, WD, PR, REC, PEN, or EN
DOCTYPE = WD

# An e-mail address of the person doing the submission to the document
# repository (can be empty until a make upload is being made)
AUTHOR_EMAIL=paul.harrison@manchester.ac.uk

# Source files for the TeX document (but the main file must always
# be called $(DOCNAME).tex
SOURCES = $(DOCNAME).tex role_diagram.pdf gitmeta.tex

# List of image files to be included in submitted package (anything that
# can be rendered directly by common web browsers)
FIGURES = role_diagram.svg

# List of PDF figures (figures that must be converted to pixel images to
# work in web browsers).
VECTORFIGURES = generated/proposaldm.vo-dml.png generated/proposalManagement.vo-dml.png

# Additional files to distribute (e.g., CSS, schema files, examples...)
AUX_FILES =

-include ivoatex/Makefile

ivoatex/Makefile:
	@echo "*** ivoatex submodule not found.  Initialising submodules."
	@echo
	git submodule update --init

generated/proposaldm.vo-dml.png: generated/proposaldm.vo-dml.svg
	rsvg-convert  $< -o $@

generated/proposalManagement.vo-dml.png: generated/proposalManagement.vo-dml.svg
	rsvg-convert  $< -o $@