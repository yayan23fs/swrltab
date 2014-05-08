package org.swrltab.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.swrlapi.core.SWRLAPIFactory;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.core.SWRLRuleEngineFactory;
import org.swrlapi.drools.DroolsSWRLRuleEngineCreator;
import org.swrlapi.exceptions.SWRLRuleEngineException;
import org.swrlapi.ext.SWRLAPIOWLOntology;
import org.swrlapi.ui.SWRLTabRulesPane;

public class SWRLTabRules extends JFrame
{
	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_NAME = "SWRLTabRules";

	public static void main(String[] args)
	{
		String owlFileName = "";

		if (args.length == 1) {
			owlFileName = args[0];
		} else
			Usage();

		DefaultPrefixManager prefixManager = createPrefixManager();
		SWRLRuleEngine ruleEngine = createSWRLRuleEngine(owlFileName, prefixManager);
		if (ruleEngine == null) {
			System.err.println("Could not create SWRL rule engine - quitting!");
		}
		SWRLTabRules panel = new SWRLTabRules(ruleEngine, prefixManager);

		panel.setVisible(true);
	}

	public SWRLTabRules(SWRLRuleEngine ruleEngine, DefaultPrefixManager prefixManager)
	{
		super(APPLICATION_NAME);
		createAndAddRuleEnginePanel(ruleEngine, prefixManager);
	}

	private void createAndAddRuleEnginePanel(SWRLRuleEngine ruleEngine, DefaultPrefixManager prefixManager)
	{
		URL ruleEngineIconURL = SWRLTabRules.class.getResource("Drools.gif");
		URL reasonerIconURL = SWRLTabRules.class.getResource("OWL2RL.gif");

		Icon ruleEngineIcon = new ImageIcon(ruleEngineIconURL);
		Icon reasonerIcon = new ImageIcon(reasonerIconURL);

		SWRLTabRulesPane swrlTabRulesPane = new SWRLTabRulesPane(ruleEngine, prefixManager, ruleEngineIcon, reasonerIcon);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(swrlTabRulesPane);
		setSize(1000, 580);
	}

	private static String[] canned = { "swrl.owl", "swrlb.owl", "swrla.owl", "sqwrl.owl", "swrlm.owl", "temporal.owl",
			"swrlx.owl", "swrlxml.owl" };

	private static SWRLRuleEngine createSWRLRuleEngine(String owlFileName, DefaultPrefixManager prefixManager)
	{
		try {
			OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
			for (String can : canned) { // TODO Temporary
				File f = new File("/tmp/" + can);
				ontologyManager.loadOntologyFromOntologyDocument(f);
			}
			File file = new File(owlFileName);
			OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(file);

			SWRLAPIOWLOntology swrlapiOWLOntology = SWRLAPIFactory.createSWRLAPIOWLOntology(ontologyManager, ontology,
					prefixManager);

			SWRLRuleEngineFactory swrlRuleEngineFactory = SWRLAPIFactory.createSWRLRuleEngineFactory();
			swrlRuleEngineFactory.registerRuleEngine(new DroolsSWRLRuleEngineCreator());

			SWRLRuleEngine swrlRuleEngine = swrlRuleEngineFactory.createSWRLRuleEngine(swrlapiOWLOntology);
			return swrlRuleEngine;
		} catch (SWRLRuleEngineException e) {
			System.err.println("Error creating rule engine: " + e.getMessage());
			return null;
		} catch (OWLOntologyCreationException e) {
			System.err.println("Error creating OWL ontology: " + e.getMessage());
			return null;
		} catch (RuntimeException e) {
			System.err.println("Error creating rule engine: " + e.getMessage());
			return null;
		}
	}

	private static DefaultPrefixManager createPrefixManager()
	{
		DefaultPrefixManager prefixManager = new DefaultPrefixManager(
		// "http://swrl.stanford.edu/ontologies/tests/4.3/SWRLSimple.owl#");
		// "http://swrl.stanford.edu/ontologies/tests/4.3/SQWRLCollectionsTests.owl#");
		// "http://swrl.stanford.edu/ontologies/tests/4.3/SQWRLCoreTests.owl#");
		// "http://swrl.stanford.edu/ontologies/tests/4.3/SWRLInferenceTests.owl#");
				"http://swrl.stanford.edu/ontologies/tests/4.3/SWRLCoreTests.owl#");
		prefixManager.setPrefix("swrl:", "http://www.w3.org/2003/11/swrl#");
		prefixManager.setPrefix("swrlb:", "http://www.w3.org/2003/11/swrlb#");
		prefixManager.setPrefix("sqwrl:", "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#");
		prefixManager.setPrefix("swrlm:", "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlm.owl#");
		prefixManager.setPrefix("temporal:", "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#");
		prefixManager.setPrefix("swrlx:", "http://swrl.stanford.edu/ontologies/built-ins/3.3/swrlx.owl#");
		prefixManager.setPrefix("swrlxml:", "http://swrl.stanford.edu/ontologies/built-ins/3.4/swrlxml.owl#");
		prefixManager.setPrefix("swrla:", "http://swrl.stanford.edu/ontologies/3.3/swrla.owl#");

		return prefixManager;
	}

	@Override
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.setVisible(false);
			System.exit(0);
		}
	}

	private static void Usage()
	{
		System.err.println("Usage: " + SWRLTabRules.class.getName() + " <owlFileName>");
		System.exit(1);
	}

}