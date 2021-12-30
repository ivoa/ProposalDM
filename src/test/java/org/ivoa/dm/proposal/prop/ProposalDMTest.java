package org.ivoa.dm.proposal.prop;

import org.ivoa.vodml.stdtypes2.Ivorn;
import org.javastro.ivoa.jaxb.DescriptionValidator;
import org.javastro.ivoa.jaxb.JaxbAnnotationMeta;
import org.javastro.ivoa.tests.AbstractJAXBJPATest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.ivoa.dm.proposal.prop.ObservingProposal.createObservingProposal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

/*
 * Created on 15/12/2021 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

class ProposalDMTest extends AbstractJAXBJPATest {
    /** logger for this class */
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(ProposalDMTest.class);
    private ObservingProposal p;

    @BeforeEach
    void setUp() {

        p = createObservingProposal(pr -> {
            pr.code = "pr1";
            pr.dateSubmitted = new Date();
            pr.kind = ProposalKind.STANDARD;
            pr.summary = "a test proposal";
            Organization institute = new Organization("org", "org address",
                    new Ivorn("ivo://org/anorg"));
            pr.investigators = Arrays.asList(new ObservingInvestigator ( InvestigatorKind.PI, new Investigator(
                    "Fred Hoyle", "fred@steady.com.unreal", institute)));

        });

    }

    @AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void jaxbTest() throws JAXBException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
        logger.debug("starting test");
        JAXBContext jc = ProposalModel.contextFactory();
        JaxbAnnotationMeta<ObservingProposal> meta = JaxbAnnotationMeta.of(ObservingProposal.class);
        DescriptionValidator<ObservingProposal> validator = new DescriptionValidator<>(jc, meta);
        DescriptionValidator.Validation validation = validator.validate(p);
        if(!validation.valid) {
            System.err.println(validation.message);
        }
        assertTrue(validation.valid);
        ProposalModel model = new ProposalModel();
        model.addContent(p);
        model.makeRefIDsUnique();
        
        ProposalModel modelin = roundtripXML(jc, model, ProposalModel.class);
        List<ObservingProposal> props = modelin.getContent(ObservingProposal.class);
        assertEquals(1, props.size());
        
    }

}