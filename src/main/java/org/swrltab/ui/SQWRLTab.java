package org.swrltab.ui;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.exceptions.SWRLAPIException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.ui.dialog.SWRLAPIDialogManager;
import org.swrlapi.ui.model.SQWRLQueryEngineModel;
import org.swrlapi.ui.view.SWRLAPIView;
import org.swrlapi.ui.view.queries.SWRLAPIQueriesView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Standalone SWRLAPI-based application that presents a SQWRL editor and query execution graphical interface.
 * <p>
 * The Drools rule engine is used for query execution.
 * <p>
 * To invoke from Maven put <code>org.swrltab.ui.SQWRLTab</code> in the <code>mainClass</code> element of the
 * <code>exec-maven-plugin</code> plugin configuration in the Maven project POM and run with the <code>exec:java</code>
 * goal.
 *
 * @see org.swrlapi.ui.view.queries.SWRLAPIQueriesView
 */
public class SQWRLTab extends JFrame implements SWRLAPIView
{
  private static final long serialVersionUID = 1L;

  private static final String APPLICATION_NAME = "SQWRLTab";
  private static final int APPLICATION_WINDOW_WIDTH = 1000;
  private static final int APPLICATION_WINDOW_HEIGHT = 580;

  private final SWRLAPIQueriesView queriesView;

  public static void main(String[] args)
  {
    String owlFileName = SQWRLTab.class.getClassLoader().getResource("projects/SWRLSimple.owl").getFile();
    File owlFile = new File(owlFileName);

    try {
      // Create an OWL ontology using the OWLAPI
      OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
      OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(owlFile);

      // Create a SQWRL query engine
      SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

      // Create the query engine model, supplying it with the query engine
      SQWRLQueryEngineModel sqwrlQueryEngineModel = SWRLAPIFactory.createSQWRLQueryEngineModel(queryEngine);

      // Create the dialog manager
      SWRLAPIDialogManager dialogManager = SWRLAPIFactory.createDialogManager(sqwrlQueryEngineModel);

      Icon queryEngineIcon = queryEngine.getQueryEngineIcon();

      // Create the view
      SQWRLTab sqwrlTab = new SQWRLTab(sqwrlQueryEngineModel, dialogManager, queryEngineIcon);

      // Make the view visible
      sqwrlTab.setVisible(true);

    } catch (OWLOntologyCreationException e) {
      System.err.println("Error creating OWL ontology from file " + owlFile.getAbsolutePath() + ": " + e.getMessage());
      System.exit(-1);
    } catch (RuntimeException e) {
      System.err.println("Error starting application: " + e.getMessage());
      System.exit(-1);
    }
  }

  public SQWRLTab(SQWRLQueryEngineModel sqwrlQueryEngineModel, SWRLAPIDialogManager dialogManager, Icon queryEngineIcon)
      throws SWRLAPIException
  {
    super(APPLICATION_NAME);
    this.queriesView = createAndAddSWRLAPIQueriesView(sqwrlQueryEngineModel, dialogManager, queryEngineIcon);
  }

  @Override
  public void update()
  {
    this.queriesView.update();
  }

  private SWRLAPIQueriesView createAndAddSWRLAPIQueriesView(SQWRLQueryEngineModel sqwrlQueryEngineModel,
      SWRLAPIDialogManager dialogManager, Icon queryEngineIcon) throws SWRLAPIException
  {
    SWRLAPIQueriesView queriesView = new SWRLAPIQueriesView(sqwrlQueryEngineModel, dialogManager,
        queryEngineIcon);
    Container contentPane = getContentPane();

    contentPane.setLayout(new BorderLayout());
    contentPane.add(queriesView);
    setSize(APPLICATION_WINDOW_WIDTH, APPLICATION_WINDOW_HEIGHT);

    return queriesView;
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

  @SuppressWarnings("unused")
  private static void Usage()
  {
    System.err.println("Usage: " + SQWRLTab.class.getName() + " <owlFileName>");
    System.exit(1);
  }
}
