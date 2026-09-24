package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.controllers.WebSearchController;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.SearchResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmProvider;
import it.unibz.inf.ontouml.vp.model.SearchMode;
import it.unibz.inf.ontouml.vp.model.SearchProvider;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.DebugLogWriter;
import it.unibz.inf.ontouml.vp.utils.DocumentExtractor;
import it.unibz.inf.ontouml.vp.utils.ExternalCommand;
import it.unibz.inf.ontouml.vp.utils.LlmRequestPacing;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * View for the configurations menu to be embedded on an instance of <code>ConfigurationsDialog
 * </code>.
 *
 * @author Claudenir Fonseca
 */
public class ConfigurationsView extends JPanel {

  private static final long serialVersionUID = 1L;

  private JCheckBox _chckbxEnableCustomServer;
  private JTextField _txtServerAddress;

  private JCheckBox _chckbxEnableAutoColoring;
  private JCheckBox _chckbxEnableSmartModelling;

  private JComboBox<LlmProvider> _cmbLlmProvider;
  private JPasswordField _txtLlmApiKey;
  private JTextField _txtLlmBaseUrl;
  private JTextField _txtLlmModel;
  private JComboBox<SearchMode> _cmbSearchMode;
  private final Map<SearchProvider, JCheckBox> _searchProviderBoxes = new LinkedHashMap<>();
  private final Map<SearchProvider, JTextField> _searchCredentialFields = new LinkedHashMap<>();
  private JButton _btnTestSearch;
  private JButton _btnTestLlm;
  private JLabel _lblLlmTestStatus;

  private JCheckBox _chckbxDelayBetweenCalls;
  private JSpinner _spnDelaySeconds;

  private JCheckBox _chckbxDebugMode;
  private JTextField _txtDebugLogDirectory;
  private JLabel _lblDebugLogPath;

  private JCheckBox _chckbxReadLocalFiles;
  private JCheckBox _chckbxUseDocling;
  private JTextField _txtDoclingCommand;
  private JTextField _txtPdftotextCommand;
  private JButton _btnTestDocling;
  private JButton _btnTestPdftotext;

  private static final Color TEST_SUCCESS_COLOR = new Color(0, 128, 0);
  private static final Color TEST_FAILURE_COLOR = Color.RED.darker();

  private JButton _btnApply;
  private JButton _btnResetDefaults;

  private JLabel pluginVersion;

  private IDialog _dialog;

  /** ConfigurationsView constructor. */
  public ConfigurationsView(ProjectConfigurations configurations) {
    setSize(new Dimension(720, 560));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {700};
    gridBagLayout.rowHeights = new int[] {26, 420, 25, 0};
    gridBagLayout.columnWeights = new double[] {1.0};
    // the options row takes the slack, so the groups keep their height and the dialog can shrink
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JPanel _optionsPanel = new JPanel();

    // the groups outgrew the dialog once the search services were added, so they scroll
    final JScrollPane _optionsScroll = new JScrollPane(_optionsPanel);
    _optionsScroll.setBorder(BorderFactory.createEmptyBorder());
    _optionsScroll.getVerticalScrollBar().setUnitIncrement(16);

    GridBagConstraints gbc__optionsPanel = new GridBagConstraints();
    gbc__optionsPanel.fill = GridBagConstraints.BOTH;
    gbc__optionsPanel.anchor = GridBagConstraints.NORTH;
    gbc__optionsPanel.weightx = 1.0;
    gbc__optionsPanel.weighty = 1.0;
    gbc__optionsPanel.gridx = 0;
    gbc__optionsPanel.gridy = 1;
    add(_optionsScroll, gbc__optionsPanel);
    // GridBagLayout (instead of a FlowLayout) so the groups can follow the dialog's width
    _optionsPanel.setLayout(new GridBagLayout());

    JPanel _ontoUMLServerPanel = new JPanel();
    _optionsPanel.add(_ontoUMLServerPanel, leftAlignedRow(0));
    GridBagLayout gbl__ontoUMLServerPanel = new GridBagLayout();
    gbl__ontoUMLServerPanel.columnWidths = new int[] {290, 370, 0};
    gbl__ontoUMLServerPanel.rowHeights = new int[] {26, 0};
    gbl__ontoUMLServerPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gbl__ontoUMLServerPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _ontoUMLServerPanel.setLayout(gbl__ontoUMLServerPanel);

    _chckbxEnableCustomServer = new JCheckBox("Use custom OntoUML Server instance.");
    _chckbxEnableCustomServer.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__chckbxEnableCustomServer = new GridBagConstraints();
    gbc__chckbxEnableCustomServer.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableCustomServer.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableCustomServer.gridx = 0;
    gbc__chckbxEnableCustomServer.gridy = 0;
    _ontoUMLServerPanel.add(_chckbxEnableCustomServer, gbc__chckbxEnableCustomServer);

    _txtServerAddress = new JTextField();
    GridBagConstraints gbc__txtServerAddress = new GridBagConstraints();
    gbc__txtServerAddress.fill = GridBagConstraints.HORIZONTAL;
    gbc__txtServerAddress.anchor = GridBagConstraints.NORTH;
    gbc__txtServerAddress.gridx = 1;
    gbc__txtServerAddress.gridy = 0;
    _ontoUMLServerPanel.add(_txtServerAddress, gbc__txtServerAddress);
    _txtServerAddress.setText("");
    _txtServerAddress.setColumns(10);

    JPanel _exportPanel = new JPanel();
    _optionsPanel.add(_exportPanel, leftAlignedRow(1));
    GridBagLayout gbl__exportPanel = new GridBagLayout();
    gbl__exportPanel.columnWidths = new int[] {290, 247, 0, 0};
    gbl__exportPanel.rowHeights = new int[] {0, 0};
    gbl__exportPanel.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl__exportPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _exportPanel.setLayout(gbl__exportPanel);

    JPanel _smartPanel = new JPanel();
    _optionsPanel.add(_smartPanel, leftAlignedRow(2));
    GridBagLayout gbl__smartPanel = new GridBagLayout();
    gbl__smartPanel.columnWidths = new int[] {290, 247, 0, 0};
    gbl__smartPanel.rowHeights = new int[] {0, 0};
    gbl__smartPanel.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl__smartPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _smartPanel.setLayout(gbl__smartPanel);

    _chckbxEnableAutoColoring = new JCheckBox("Enable Smart Paint");
    GridBagConstraints gbc__chckbxEnableAutoColoring = new GridBagConstraints();
    gbc__chckbxEnableAutoColoring.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableAutoColoring.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableAutoColoring.gridx = 0;
    gbc__chckbxEnableAutoColoring.gridy = 0;
    _smartPanel.add(_chckbxEnableAutoColoring, gbc__chckbxEnableAutoColoring);

    _chckbxEnableSmartModelling = new JCheckBox("Enable Smart Modelling");
    GridBagConstraints gbc__chckbxEnableSmartModelling = new GridBagConstraints();
    gbc__chckbxEnableSmartModelling.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableSmartModelling.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableSmartModelling.gridx = 0;
    gbc__chckbxEnableSmartModelling.gridy = 1;
    _smartPanel.add(_chckbxEnableSmartModelling, gbc__chckbxEnableSmartModelling);

    JPanel _llmPanel = new JPanel();
    _llmPanel.setBorder(BorderFactory.createTitledBorder("AI Metaclass Suggestion"));
    // the only group that stretches: its fields should use the full width available
    _optionsPanel.add(_llmPanel, stretchedRow(3));
    GridBagLayout gbl__llmPanel = new GridBagLayout();
    gbl__llmPanel.columnWidths = new int[] {120, 0};
    gbl__llmPanel.rowHeights = new int[] {26, 26, 26, 26, 26, 26, 26, 26, 0};
    gbl__llmPanel.columnWeights = new double[] {0.0, 1.0};
    gbl__llmPanel.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    _llmPanel.setLayout(gbl__llmPanel);

    GridBagConstraints gbc__lblLlmProvider = new GridBagConstraints();
    gbc__lblLlmProvider.anchor = GridBagConstraints.WEST;
    gbc__lblLlmProvider.insets = new Insets(0, 0, 0, 5);
    gbc__lblLlmProvider.gridx = 0;
    gbc__lblLlmProvider.gridy = 0;
    _llmPanel.add(new JLabel("Provider:"), gbc__lblLlmProvider);

    _cmbLlmProvider = new JComboBox<>(LlmProvider.values());
    _cmbLlmProvider.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            applySelectedProviderDefaults();
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__cmbLlmProvider = new GridBagConstraints();
    gbc__cmbLlmProvider.fill = GridBagConstraints.HORIZONTAL;
    gbc__cmbLlmProvider.gridx = 1;
    gbc__cmbLlmProvider.gridy = 0;
    _llmPanel.add(_cmbLlmProvider, gbc__cmbLlmProvider);

    GridBagConstraints gbc__lblLlmApiKey = new GridBagConstraints();
    gbc__lblLlmApiKey.anchor = GridBagConstraints.WEST;
    gbc__lblLlmApiKey.insets = new Insets(0, 0, 0, 5);
    gbc__lblLlmApiKey.gridx = 0;
    gbc__lblLlmApiKey.gridy = 1;
    _llmPanel.add(new JLabel("API Key:"), gbc__lblLlmApiKey);

    _txtLlmApiKey = new JPasswordField();
    GridBagConstraints gbc__txtLlmApiKey = new GridBagConstraints();
    gbc__txtLlmApiKey.fill = GridBagConstraints.HORIZONTAL;
    gbc__txtLlmApiKey.gridx = 1;
    gbc__txtLlmApiKey.gridy = 1;
    _llmPanel.add(_txtLlmApiKey, gbc__txtLlmApiKey);

    GridBagConstraints gbc__lblLlmBaseUrl = new GridBagConstraints();
    gbc__lblLlmBaseUrl.anchor = GridBagConstraints.WEST;
    gbc__lblLlmBaseUrl.insets = new Insets(5, 0, 0, 5);
    gbc__lblLlmBaseUrl.gridx = 0;
    gbc__lblLlmBaseUrl.gridy = 2;
    _llmPanel.add(new JLabel("Base URL:"), gbc__lblLlmBaseUrl);

    _txtLlmBaseUrl = new JTextField();
    GridBagConstraints gbc__txtLlmBaseUrl = new GridBagConstraints();
    gbc__txtLlmBaseUrl.fill = GridBagConstraints.HORIZONTAL;
    gbc__txtLlmBaseUrl.insets = new Insets(5, 0, 0, 0);
    gbc__txtLlmBaseUrl.gridx = 1;
    gbc__txtLlmBaseUrl.gridy = 2;
    _llmPanel.add(_txtLlmBaseUrl, gbc__txtLlmBaseUrl);

    GridBagConstraints gbc__lblLlmModel = new GridBagConstraints();
    gbc__lblLlmModel.anchor = GridBagConstraints.WEST;
    gbc__lblLlmModel.insets = new Insets(5, 0, 0, 5);
    gbc__lblLlmModel.gridx = 0;
    gbc__lblLlmModel.gridy = 3;
    _llmPanel.add(new JLabel("Model:"), gbc__lblLlmModel);

    _txtLlmModel = new JTextField();
    GridBagConstraints gbc__txtLlmModel = new GridBagConstraints();
    gbc__txtLlmModel.fill = GridBagConstraints.HORIZONTAL;
    gbc__txtLlmModel.insets = new Insets(5, 0, 0, 0);
    gbc__txtLlmModel.gridx = 1;
    gbc__txtLlmModel.gridy = 3;
    _llmPanel.add(_txtLlmModel, gbc__txtLlmModel);

    GridBagConstraints gbc__lblSearchMode = new GridBagConstraints();
    gbc__lblSearchMode.anchor = GridBagConstraints.WEST;
    gbc__lblSearchMode.insets = new Insets(12, 0, 0, 5);
    gbc__lblSearchMode.gridx = 0;
    gbc__lblSearchMode.gridy = 4;
    _llmPanel.add(new JLabel("Web research:"), gbc__lblSearchMode);

    _cmbSearchMode = new JComboBox<>(SearchMode.values());
    _cmbSearchMode.setToolTipText(
        "How sources are researched when documenting a class. \"Search via the model\" only works"
            + " on providers that support it (e.g. OpenRouter, Perplexity).");
    _cmbSearchMode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__cmbSearchMode = new GridBagConstraints();
    gbc__cmbSearchMode.fill = GridBagConstraints.HORIZONTAL;
    gbc__cmbSearchMode.insets = new Insets(12, 0, 0, 0);
    gbc__cmbSearchMode.gridx = 1;
    gbc__cmbSearchMode.gridy = 4;
    _llmPanel.add(_cmbSearchMode, gbc__cmbSearchMode);

    GridBagConstraints gbc__searchProviders = new GridBagConstraints();
    gbc__searchProviders.fill = GridBagConstraints.HORIZONTAL;
    gbc__searchProviders.insets = new Insets(5, 0, 0, 0);
    gbc__searchProviders.gridx = 0;
    gbc__searchProviders.gridy = 5;
    gbc__searchProviders.gridwidth = 2;
    _llmPanel.add(buildSearchProvidersPanel(), gbc__searchProviders);

    GridBagConstraints gbc__pacing = new GridBagConstraints();
    gbc__pacing.fill = GridBagConstraints.HORIZONTAL;
    gbc__pacing.insets = new Insets(5, 0, 0, 0);
    gbc__pacing.gridx = 0;
    gbc__pacing.gridy = 6;
    gbc__pacing.gridwidth = 2;
    _llmPanel.add(buildPacingPanel(), gbc__pacing);

    GridBagConstraints gbc__documents = new GridBagConstraints();
    gbc__documents.fill = GridBagConstraints.HORIZONTAL;
    gbc__documents.insets = new Insets(5, 0, 0, 0);
    gbc__documents.gridx = 0;
    gbc__documents.gridy = 7;
    gbc__documents.gridwidth = 2;
    _llmPanel.add(buildDocumentsPanel(), gbc__documents);

    GridBagConstraints gbc__debug = new GridBagConstraints();
    gbc__debug.fill = GridBagConstraints.HORIZONTAL;
    gbc__debug.insets = new Insets(5, 0, 0, 0);
    gbc__debug.gridx = 0;
    gbc__debug.gridy = 8;
    gbc__debug.gridwidth = 2;
    _llmPanel.add(buildDebugPanel(), gbc__debug);

    JPanel _llmTestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    _btnTestLlm = new JButton("Test");
    _btnTestLlm.setToolTipText("Sends a minimal request to the endpoint above to validate it.");
    _btnTestLlm.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            testLlmConnection();
          }
        });
    _llmTestPanel.add(_btnTestLlm);

    final JButton _btnEditPrompts = new JButton("Prompts & decision tree...");
    _btnEditPrompts.setToolTipText(
        "Reads and edits the prompts and the decision tree used by the AI classification.");
    _btnEditPrompts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ApplicationManager.instance()
                .getViewManager()
                .showDialog(new LlmPromptSettingsView());
          }
        });
    _btnTestSearch = new JButton("Test search");
    _btnTestSearch.setToolTipText("Checks the web research setting above.");
    _btnTestSearch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            testSearch();
          }
        });
    _llmTestPanel.add(Box.createHorizontalStrut(6));
    _llmTestPanel.add(_btnTestSearch);

    _llmTestPanel.add(Box.createHorizontalStrut(8));
    _llmTestPanel.add(_btnEditPrompts);

    _lblLlmTestStatus = new JLabel(" ");
    _lblLlmTestStatus.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    _llmTestPanel.add(_lblLlmTestStatus);

    GridBagConstraints gbc__llmTestPanel = new GridBagConstraints();
    gbc__llmTestPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc__llmTestPanel.insets = new Insets(8, 0, 0, 0);
    gbc__llmTestPanel.gridx = 1;
    gbc__llmTestPanel.gridy = 9;
    _llmPanel.add(_llmTestPanel, gbc__llmTestPanel);

    JPanel _controlButtonsPanel = new JPanel();
    GridBagConstraints gbc__controlButtonsPanel = new GridBagConstraints();
    gbc__controlButtonsPanel.anchor = GridBagConstraints.SOUTHEAST;
    gbc__controlButtonsPanel.gridx = 0;
    gbc__controlButtonsPanel.gridy = 2;
    add(_controlButtonsPanel, gbc__controlButtonsPanel);
    GridBagLayout gbl__controlButtonsPanel = new GridBagLayout();
    gbl__controlButtonsPanel.columnWidths = new int[] {135, 135, 0};
    gbl__controlButtonsPanel.rowHeights = new int[] {0, 0};
    gbl__controlButtonsPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gbl__controlButtonsPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _controlButtonsPanel.setLayout(gbl__controlButtonsPanel);

    _btnApply = new JButton("Save");
    _btnApply.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateConfigurationsValues(configurations);
            Configurations.getInstance().save();
            _dialog.close();
            OntoUMLPlugin.setConfigWindowOpen(false);
          }
        });
    GridBagConstraints gbc__btnApply = new GridBagConstraints();
    gbc__btnApply.fill = GridBagConstraints.HORIZONTAL;
    gbc__btnApply.insets = new Insets(0, 0, 0, 5);
    gbc__btnApply.gridx = 0;
    gbc__btnApply.gridy = 0;
    _controlButtonsPanel.add(_btnApply, gbc__btnApply);

    _btnResetDefaults = new JButton("Reset Defaults");
    _btnResetDefaults.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            resetComponentsValues();
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__btnResetDefaults = new GridBagConstraints();
    gbc__btnResetDefaults.fill = GridBagConstraints.HORIZONTAL;
    gbc__btnResetDefaults.gridx = 1;
    gbc__btnResetDefaults.gridy = 0;
    _controlButtonsPanel.add(_btnResetDefaults, gbc__btnResetDefaults);

    pluginVersion = new JLabel("plugin version: " + OntoUMLPlugin.PLUGIN_VERSION_RELEASE);
    pluginVersion.setEnabled(false);

    GridBagConstraints gbc__versionLabel = new GridBagConstraints();
    gbc__versionLabel.anchor = GridBagConstraints.SOUTHWEST;
    gbc__versionLabel.gridx = 0;
    gbc__versionLabel.gridy = 2;

    add(pluginVersion, gbc__versionLabel);

    updateComponentsValues(configurations);
    updateComponentsStatus();
  }

  private LlmProvider selectedProvider() {
    final Object selected = _cmbLlmProvider.getSelectedItem();
    return selected instanceof LlmProvider ? (LlmProvider) selected : LlmConfigurations.DEFAULT_PROVIDER;
  }

  /**
   * Fills the URL and model fields from the provider picked in the combo. The model is only
   * replaced while it still holds one of the built-in defaults, so a model the user typed is never
   * discarded. For "Other" the URL is cleared so the user can type their own endpoint.
   */
  private void applySelectedProviderDefaults() {
    final LlmProvider provider = selectedProvider();

    if (provider.hasCustomBaseUrl()) {
      if (LlmProvider.isKnownBaseUrl(_txtLlmBaseUrl.getText())) {
        _txtLlmBaseUrl.setText("");
      }
    } else {
      _txtLlmBaseUrl.setText(provider.getBaseUrl());
    }

    if (LlmProvider.isDefaultModel(_txtLlmModel.getText())) {
      _txtLlmModel.setText(provider.getDefaultModel());
    }

    setLlmTestStatus(Color.GRAY, " ");
  }

  /** Constraints for a group that keeps its own width and stays on the left of the dialog. */
  private static GridBagConstraints leftAlignedRow(int row) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1.0;
    gbc.gridx = 0;
    gbc.gridy = row;
    return gbc;
  }

  /** Constraints for a group that follows the dialog's width as it is resized. */
  private static GridBagConstraints stretchedRow(int row) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.weightx = 1.0;
    gbc.insets = new Insets(5, 0, 0, 0);
    gbc.gridx = 0;
    gbc.gridy = row;
    return gbc;
  }

  /**
   * Validates the LLM endpoint currently typed into the form (not the saved one, so the values can
   * be tried out before saving). The request runs off the event dispatch thread to keep the dialog
   * responsive while it is in flight.
   */
  private void testLlmConnection() {
    final LlmConfigurations probe = probeConfiguration();

    _btnTestLlm.setEnabled(false);
    setLlmTestStatus(Color.GRAY, "Testing...");

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        return LlmAccessController.testConnection(probe);
      }

      @Override
      protected void done() {
        _btnTestLlm.setEnabled(true);

        try {
          final String reply = get();
          setLlmTestStatus(
              TEST_SUCCESS_COLOR,
              reply.isEmpty() ? "Connection OK." : "Connection OK — replied \"" + abbreviate(reply) + "\".");
        } catch (Exception e) {
          final Throwable cause = e.getCause() != null ? e.getCause() : e;
          final String message = cause.getMessage();
          setLlmTestStatus(
              TEST_FAILURE_COLOR, message == null || message.isEmpty() ? "Test failed." : message);
          e.printStackTrace();
        }
      }
    }.execute();
  }

  /**
   * One row per search service: enable as many as wanted, each with its own credential. The
   * searcher tries them top to bottom and moves on when one fails, so a service that is down or
   * out of quota does not stop the research.
   */
  private JPanel buildSearchProvidersPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Search services (tried in this order)"));

    int row = 0;

    for (SearchProvider provider : SearchProvider.values()) {
      final JCheckBox box = new JCheckBox(provider.getLabel());
      box.addChangeListener(
          new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              updateComponentsStatus();
            }
          });
      _searchProviderBoxes.put(provider, box);

      final GridBagConstraints gbcBox = new GridBagConstraints();
      gbcBox.anchor = GridBagConstraints.WEST;
      gbcBox.insets = new Insets(2, 4, 2, 6);
      gbcBox.gridx = 0;
      gbcBox.gridy = row;
      panel.add(box, gbcBox);

      final JLabel credentialLabel = new JLabel(provider.getCredentialLabel() + ":");
      final GridBagConstraints gbcLabel = new GridBagConstraints();
      gbcLabel.anchor = GridBagConstraints.WEST;
      gbcLabel.insets = new Insets(2, 0, 2, 6);
      gbcLabel.gridx = 1;
      gbcLabel.gridy = row;
      panel.add(credentialLabel, gbcLabel);

      final JTextField credential =
          provider.isCredentialSecret() ? new JPasswordField() : new JTextField();
      credential.setToolTipText(
          provider == SearchProvider.SEARXNG
              ? "URL of your SearXNG instance, e.g. http://localhost:8080."
                  + " Its settings.yml must list json under formats."
              : "API key for " + provider.getLabel() + ".");
      _searchCredentialFields.put(provider, credential);

      final GridBagConstraints gbcCredential = new GridBagConstraints();
      gbcCredential.fill = GridBagConstraints.HORIZONTAL;
      gbcCredential.weightx = 1.0;
      gbcCredential.insets = new Insets(2, 0, 2, 4);
      gbcCredential.gridx = 2;
      gbcCredential.gridy = row;
      panel.add(credential, gbcCredential);

      row++;
    }

    return panel;
  }

  /**
   * The pause between calls to the model.
   *
   * <p>Off by default. Documenting one class makes a request per source read plus the documentation
   * itself, which on a provider that allows a handful of requests a minute is enough to be refused
   * on its own. The 429 handling waits and retries whatever is set here; this is for spacing the
   * calls out so it is not needed.
   */
  private JPanel buildPacingPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Rate limiting"));

    _chckbxDelayBetweenCalls = new JCheckBox("Wait between calls to the model");
    _chckbxDelayBetweenCalls.setToolTipText(
        "Pauses this long after each call before making the next. Use it with a provider whose"
            + " free tier allows only a few requests per minute.");
    _chckbxDelayBetweenCalls.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });

    final GridBagConstraints gbcBox = new GridBagConstraints();
    gbcBox.anchor = GridBagConstraints.WEST;
    gbcBox.insets = new Insets(2, 4, 2, 6);
    gbcBox.gridx = 0;
    gbcBox.gridy = 0;
    panel.add(_chckbxDelayBetweenCalls, gbcBox);

    _spnDelaySeconds =
        new JSpinner(
            new SpinnerNumberModel(
                LlmConfigurations.DEFAULT_DELAY_BETWEEN_CALLS_SECONDS,
                1,
                LlmConfigurations.MAX_DELAY_BETWEEN_CALLS_SECONDS,
                1));

    final GridBagConstraints gbcSpinner = new GridBagConstraints();
    gbcSpinner.anchor = GridBagConstraints.WEST;
    gbcSpinner.insets = new Insets(2, 0, 2, 4);
    gbcSpinner.gridx = 1;
    gbcSpinner.gridy = 0;
    panel.add(_spnDelaySeconds, gbcSpinner);

    final GridBagConstraints gbcUnit = new GridBagConstraints();
    gbcUnit.anchor = GridBagConstraints.WEST;
    gbcUnit.weightx = 1.0;
    gbcUnit.insets = new Insets(2, 0, 2, 4);
    gbcUnit.gridx = 2;
    gbcUnit.gridy = 0;
    panel.add(new JLabel("seconds"), gbcUnit);

    final JLabel note =
        new JLabel(
            "Whatever is set here, a 429 (too many requests) from the provider makes the plugin"
                + " wait "
                + LlmRequestPacing.RATE_LIMIT_WAIT_SECONDS
                + " s and try again, up to "
                + LlmRequestPacing.MAX_RATE_LIMIT_RETRIES
                + " times.");
    note.setForeground(Color.GRAY);

    final GridBagConstraints gbcNote = new GridBagConstraints();
    gbcNote.anchor = GridBagConstraints.WEST;
    gbcNote.insets = new Insets(0, 22, 4, 4);
    gbcNote.gridx = 0;
    gbcNote.gridy = 1;
    gbcNote.gridwidth = 3;
    panel.add(note, gbcNote);

    return panel;
  }

  /**
   * Debug mode: keep every exchange and mirror it to disk.
   *
   * <p>The conversation window holds the last 25 exchanges of the session and nothing survives a
   * restart. Documenting one class can produce a dozen — one per source retrieved, one per part of
   * a long document — so the ones worth reading scroll away before anyone opens the window. The
   * path is shown, not just configurable, because a log nobody can find is not a log.
   */
  private JPanel buildDebugPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Debugging"));

    _chckbxDebugMode = new JCheckBox("Debug mode — record every conversation to disk");
    _chckbxDebugMode.setToolTipText(
        "Keeps the whole session's exchanges (prompts, answers, and how each source was"
            + " retrieved) and writes them to a file. Off by default: a prompt carries your model.");
    _chckbxDebugMode.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });

    final GridBagConstraints gbcBox = new GridBagConstraints();
    gbcBox.anchor = GridBagConstraints.WEST;
    gbcBox.insets = new Insets(2, 4, 2, 4);
    gbcBox.gridx = 0;
    gbcBox.gridy = 0;
    gbcBox.gridwidth = 3;
    panel.add(_chckbxDebugMode, gbcBox);

    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.WEST;
    gbcLabel.insets = new Insets(2, 22, 2, 6);
    gbcLabel.gridx = 0;
    gbcLabel.gridy = 1;
    panel.add(new JLabel("Log folder:"), gbcLabel);

    _txtDebugLogDirectory = new JTextField();
    _txtDebugLogDirectory.setToolTipText(
        "Leave empty to use the default folder, shown below, beside the plugin's configuration"
            + " file in the Visual Paradigm workspace.");

    final GridBagConstraints gbcField = new GridBagConstraints();
    gbcField.fill = GridBagConstraints.HORIZONTAL;
    gbcField.weightx = 1.0;
    gbcField.insets = new Insets(2, 0, 2, 6);
    gbcField.gridx = 1;
    gbcField.gridy = 1;
    gbcField.gridwidth = 2;
    panel.add(_txtDebugLogDirectory, gbcField);

    _lblDebugLogPath = new JLabel(" ");
    _lblDebugLogPath.setForeground(Color.GRAY);

    final GridBagConstraints gbcPath = new GridBagConstraints();
    gbcPath.anchor = GridBagConstraints.WEST;
    gbcPath.insets = new Insets(0, 22, 4, 4);
    gbcPath.gridx = 0;
    gbcPath.gridy = 2;
    gbcPath.gridwidth = 3;
    panel.add(_lblDebugLogPath, gbcPath);

    return panel;
  }

  /** The folder that will actually be used, resolved from what is typed right now. */
  private void refreshDebugLogPath() {
    final String typed = _txtDebugLogDirectory.getText().trim();
    final String path = typed.isEmpty() ? DebugLogWriter.describeDirectory() : typed;
    final String current = DebugLogWriter.currentFile();

    _lblDebugLogPath.setText(
        current == null ? "Writes to: " + path : "Writing to: " + current);
    _lblDebugLogPath.setToolTipText(_lblDebugLogPath.getText());
  }

  /**
   * How a source that is not a web page gets read: a file the user attached to a class, and the
   * documents — PDF above all — most norms are actually published as.
   *
   * <p>Docling is off by default and each converter has a Test button beside its path, because the
   * failure they hit in practice is not being found: Visual Paradigm started from the Dock does not
   * inherit the shell's {@code PATH}, so a command that works in the user's terminal is invisible
   * here. Testing turns that into a message instead of documentation that quietly cites nothing.
   */
  private JPanel buildDocumentsPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("Reading sources that are not web pages"));

    _chckbxReadLocalFiles = new JCheckBox("Read references that point to a file on this computer");
    _chckbxReadLocalFiles.setToolTipText(
        "A reference of type File is opened and read like a page. Turn off to keep local content"
            + " from being sent to the model.");

    final GridBagConstraints gbcLocal = new GridBagConstraints();
    gbcLocal.anchor = GridBagConstraints.WEST;
    gbcLocal.insets = new Insets(2, 4, 2, 4);
    gbcLocal.gridx = 0;
    gbcLocal.gridy = 0;
    gbcLocal.gridwidth = 3;
    panel.add(_chckbxReadLocalFiles, gbcLocal);

    _chckbxUseDocling = new JCheckBox("Convert documents with Docling (PDF, Word, Excel, PowerPoint)");
    _chckbxUseDocling.setToolTipText(
        "Docling is a separate program (pip3 install docling). Its first conversion downloads"
            + " layout models and can take several minutes.");
    _chckbxUseDocling.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });

    final GridBagConstraints gbcDocling = new GridBagConstraints();
    gbcDocling.anchor = GridBagConstraints.WEST;
    gbcDocling.insets = new Insets(2, 4, 2, 4);
    gbcDocling.gridx = 0;
    gbcDocling.gridy = 1;
    gbcDocling.gridwidth = 3;
    panel.add(_chckbxUseDocling, gbcDocling);

    _txtDoclingCommand = new JTextField();
    _btnTestDocling =
        addConverterRow(
            panel,
            2,
            "Docling command:",
            _txtDoclingCommand,
            "Name or full path of the docling executable.",
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                testConverter(true);
              }
            });

    _txtPdftotextCommand = new JTextField();
    _btnTestPdftotext =
        addConverterRow(
            panel,
            3,
            "pdftotext command:",
            _txtPdftotextCommand,
            "Name or full path of pdftotext (poppler). Used for PDF when Docling is off.",
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                testConverter(false);
              }
            });

    return panel;
  }

  /** A label, the path field and its Test button — the same shape for both converters. */
  private JButton addConverterRow(
      JPanel panel, int row, String label, JTextField field, String tooltip, ActionListener test) {
    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.WEST;
    gbcLabel.insets = new Insets(2, 22, 2, 6);
    gbcLabel.gridx = 0;
    gbcLabel.gridy = row;
    panel.add(new JLabel(label), gbcLabel);

    field.setToolTipText(tooltip);

    final GridBagConstraints gbcField = new GridBagConstraints();
    gbcField.fill = GridBagConstraints.HORIZONTAL;
    gbcField.weightx = 1.0;
    gbcField.insets = new Insets(2, 0, 2, 6);
    gbcField.gridx = 1;
    gbcField.gridy = row;
    panel.add(field, gbcField);

    final JButton button = new JButton("Test");
    button.setToolTipText("Runs the converter on a small file to prove it works.");
    button.addActionListener(test);

    final GridBagConstraints gbcButton = new GridBagConstraints();
    gbcButton.anchor = GridBagConstraints.WEST;
    gbcButton.insets = new Insets(2, 0, 2, 4);
    gbcButton.gridx = 2;
    gbcButton.gridy = row;
    panel.add(button, gbcButton);

    return button;
  }

  /**
   * Runs the converter named in the form — not the saved one — so a path can be corrected and
   * checked before saving. The verdict goes into a dialog rather than the status line: when it
   * fails, what the user needs is the install command and the reason, not sixty characters of it.
   */
  private void testConverter(boolean docling) {
    final LlmConfigurations probe = probeConfiguration();
    final JButton button = docling ? _btnTestDocling : _btnTestPdftotext;
    final String name = docling ? "Docling" : "pdftotext";

    // a path typed and tested at once would otherwise be looked up under the previous verdict
    ExternalCommand.forgetResolutions();

    button.setEnabled(false);
    setLlmTestStatus(Color.GRAY, "Testing " + name + "...");

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() {
        return docling
            ? DocumentExtractor.describeDoclingAvailability(probe)
            : DocumentExtractor.describePdftotextAvailability(probe);
      }

      @Override
      protected void done() {
        button.setEnabled(true);

        String report;

        try {
          report = get();
        } catch (Exception e) {
          report = "The test could not be run: " + e.getMessage();
          e.printStackTrace();
        }

        final boolean worked = report.contains("Conversão de teste concluída");
        setLlmTestStatus(
            worked ? TEST_SUCCESS_COLOR : TEST_FAILURE_COLOR,
            name + (worked ? " OK" : " não está utilizável"));
        ViewManagerUtils.simpleDialog(name + "\n\n" + report);
      }
    }.execute();
  }

  private static String credentialOf(JTextField field) {
    return field instanceof JPasswordField
        ? new String(((JPasswordField) field).getPassword())
        : field.getText();
  }

  private List<SearchProvider> enabledSearchProviders() {
    final List<SearchProvider> enabled = new ArrayList<>();

    for (Map.Entry<SearchProvider, JCheckBox> entry : _searchProviderBoxes.entrySet()) {
      if (entry.getValue().isSelected()) {
        enabled.add(entry.getKey());
      }
    }

    return enabled;
  }

  private SearchMode selectedSearchMode() {
    final Object selected = _cmbSearchMode.getSelectedItem();
    return selected instanceof SearchMode ? (SearchMode) selected : SearchMode.NONE;
  }

  /**
   * Exercises the web research setting currently in the form. Only the search-API mode can be
   * proven: with search done by the provider all we can do is show what came back and say so.
   */
  private void testSearch() {
    final SearchMode mode = selectedSearchMode();

    if (mode == SearchMode.NONE) {
      setLlmTestStatus(Color.GRAY, "Web research is disabled — sources will be unverified.");
      return;
    }

    final LlmConfigurations probe = probeConfiguration();
    final String query = "Lei 14.133/2021 planalto.gov.br";

    _btnTestSearch.setEnabled(false);
    setLlmTestStatus(Color.GRAY, "Testing web research...");

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        if (mode == SearchMode.SEARCH_API) {
          final List<SearchResult> results = WebSearchController.search(probe, query, 3);

          return results.isEmpty()
              ? ""
              : results.size() + " result(s) — first: " + results.get(0).getTitle();
        }

        return LlmAccessController.testModelSearch(probe);
      }

      @Override
      protected void done() {
        _btnTestSearch.setEnabled(true);

        try {
          final String outcome = get();

          if (outcome.isEmpty()) {
            setLlmTestStatus(TEST_FAILURE_COLOR, "The search returned no results.");
          } else if (mode == SearchMode.SEARCH_API) {
            setLlmTestStatus(TEST_SUCCESS_COLOR, "Search OK — " + abbreviate(outcome));
          } else if (!outcome.contains("http")) {
            // no link back means the provider answered from memory rather than from the web
            setLlmTestStatus(
                TEST_FAILURE_COLOR,
                "No URL in the answer — this provider is probably not searching: "
                    + abbreviate(outcome));
          } else {
            setLlmTestStatus(
                TEST_SUCCESS_COLOR, "Model returned a URL — " + abbreviate(outcome));
          }
        } catch (Exception e) {
          final Throwable cause = e.getCause() != null ? e.getCause() : e;
          final String message = cause.getMessage();
          setLlmTestStatus(
              TEST_FAILURE_COLOR, message == null || message.isEmpty() ? "Test failed." : message);
          e.printStackTrace();
        }

        // the status line only carries a verdict; the log shows what was asked and what came back,
        // including every service the rotation tried
        showTestDetails();
      }
    }.execute();
  }

  /** A configuration built from what is typed in the form, so a test runs before saving. */
  private LlmConfigurations probeConfiguration() {
    final LlmConfigurations probe = new LlmConfigurations();

    probe.setProvider(selectedProvider());
    probe.setApiKey(new String(_txtLlmApiKey.getPassword()));
    probe.setCustomBaseUrl(_txtLlmBaseUrl.getText().trim());
    probe.setModel(_txtLlmModel.getText().trim());
    probe.setSearchMode(selectedSearchMode());
    probe.setSearchProviders(enabledSearchProviders());

    for (Map.Entry<SearchProvider, JTextField> entry : _searchCredentialFields.entrySet()) {
      probe.setSearchCredential(entry.getKey(), credentialOf(entry.getValue()).trim());
    }

    probe.setReadLocalFileReferences(_chckbxReadLocalFiles.isSelected());
    probe.setUseDocling(_chckbxUseDocling.isSelected());
    probe.setDoclingCommand(_txtDoclingCommand.getText().trim());
    probe.setPdftotextCommand(_txtPdftotextCommand.getText().trim());

    return probe;
  }

  /** Opens the conversation log so the request and the answer of a test can be inspected. */
  private void showTestDetails() {
    if (!LlmConversationLog.getInstance().isEmpty()) {
      ApplicationManager.instance()
          .getViewManager()
          .showDialog(new LlmConversationLogView());
    }
  }

  private void setLlmTestStatus(Color color, String message) {
    _lblLlmTestStatus.setForeground(color);
    _lblLlmTestStatus.setText(message);
    _lblLlmTestStatus.setToolTipText(message);
  }

  private static String abbreviate(String text) {
    final String singleLine = text.replaceAll("\\s+", " ").trim();
    return singleLine.length() <= 60 ? singleLine : singleLine.substring(0, 60) + "...";
  }

  /**
   * Sets a direct reference to the container dialog after initialization.
   *
   * @param dialog
   */
  public void setContainerDialog(IDialog dialog) {
    this._dialog = dialog;
  }

  /**
   * Updates project configurations with components' information.
   *
   * @param configurations
   */
  private void updateConfigurationsValues(ProjectConfigurations configurations) {
    configurations.setCustomServerEnabled(_chckbxEnableCustomServer.isSelected());
    configurations.setServerURL(_txtServerAddress.getText());

    configurations.setAutomaticColoringEnabled(_chckbxEnableAutoColoring.isSelected());
    configurations.setSmartModellingEnabled(_chckbxEnableSmartModelling.isSelected());

    final LlmConfigurations llmConfigurations = Configurations.getInstance().getLlmConfigurations();
    llmConfigurations.setProvider(selectedProvider());
    llmConfigurations.setApiKey(new String(_txtLlmApiKey.getPassword()));
    llmConfigurations.setCustomBaseUrl(_txtLlmBaseUrl.getText());
    llmConfigurations.setModel(_txtLlmModel.getText());
    llmConfigurations.setSearchMode(selectedSearchMode());
    llmConfigurations.setSearchProviders(enabledSearchProviders());

    for (Map.Entry<SearchProvider, JTextField> entry : _searchCredentialFields.entrySet()) {
      llmConfigurations.setSearchCredential(entry.getKey(), credentialOf(entry.getValue()));
    }

    llmConfigurations.setReadLocalFileReferences(_chckbxReadLocalFiles.isSelected());
    llmConfigurations.setUseDocling(_chckbxUseDocling.isSelected());
    llmConfigurations.setDoclingCommand(_txtDoclingCommand.getText().trim());
    llmConfigurations.setPdftotextCommand(_txtPdftotextCommand.getText().trim());
    llmConfigurations.setDelayBetweenCalls(_chckbxDelayBetweenCalls.isSelected());
    llmConfigurations.setDelayBetweenCallsSeconds((Integer) _spnDelaySeconds.getValue());
    llmConfigurations.setDebugMode(_chckbxDebugMode.isSelected());
    llmConfigurations.setDebugLogDirectory(_txtDebugLogDirectory.getText().trim());

    // a corrected path must take effect without restarting Visual Paradigm
    ExternalCommand.forgetResolutions();
  }

  /**
   * Updates components with project configurations' information.
   *
   * @param configurations
   */
  private void updateComponentsValues(ProjectConfigurations configurations) {
    _chckbxEnableCustomServer.setSelected(configurations.isCustomServerEnabled());
    _txtServerAddress.setText(configurations.getServerURL());

    _chckbxEnableAutoColoring.setSelected(configurations.isAutomaticColoringEnabled());
    _chckbxEnableSmartModelling.setSelected(configurations.isSmartModellingEnabled());

    final LlmConfigurations llmConfigurations = Configurations.getInstance().getLlmConfigurations();
    _cmbLlmProvider.setSelectedItem(llmConfigurations.getProvider());
    _txtLlmApiKey.setText(llmConfigurations.getApiKey());
    _txtLlmBaseUrl.setText(llmConfigurations.getEffectiveBaseUrl());
    _txtLlmModel.setText(llmConfigurations.getModel());
    _cmbSearchMode.setSelectedItem(llmConfigurations.getSearchMode());

    final List<SearchProvider> enabled = llmConfigurations.getSearchProviders();

    for (SearchProvider provider : SearchProvider.values()) {
      _searchProviderBoxes.get(provider).setSelected(enabled.contains(provider));
      _searchCredentialFields.get(provider).setText(llmConfigurations.getSearchCredential(provider));
    }

    _chckbxReadLocalFiles.setSelected(llmConfigurations.isReadLocalFileReferences());
    _chckbxUseDocling.setSelected(llmConfigurations.isUseDocling());
    _txtDoclingCommand.setText(llmConfigurations.getDoclingCommand());
    _txtPdftotextCommand.setText(llmConfigurations.getPdftotextCommand());

    _chckbxDelayBetweenCalls.setSelected(llmConfigurations.isDelayBetweenCalls());
    _spnDelaySeconds.setValue(llmConfigurations.getDelayBetweenCallsSeconds());

    _chckbxDebugMode.setSelected(llmConfigurations.isDebugMode());
    _txtDebugLogDirectory.setText(llmConfigurations.getDebugLogDirectory());
    refreshDebugLogPath();
  }

  /** Updates components with default values. */
  private void resetComponentsValues() {
    _chckbxEnableCustomServer.setSelected(ProjectConfigurations.DEFAULT_IS_CUSTOM_SERVER_ENABLED);
    _txtServerAddress.setText(ProjectConfigurations.DEFAULT_SERVER_URL);

    _chckbxEnableAutoColoring.setSelected(
        ProjectConfigurations.DEFAULT_IS_AUTOMATIC_COLORING_ENABLED);
    _chckbxEnableSmartModelling.setSelected(
        ProjectConfigurations.DEFAULT_IS_AUTOMATIC_COLORING_ENABLED);

    _cmbLlmProvider.setSelectedItem(LlmConfigurations.DEFAULT_PROVIDER);
    _txtLlmApiKey.setText("");
    _txtLlmBaseUrl.setText(LlmConfigurations.DEFAULT_PROVIDER.getBaseUrl());
    _txtLlmModel.setText(LlmConfigurations.DEFAULT_PROVIDER.getDefaultModel());
    _cmbSearchMode.setSelectedItem(SearchMode.NONE);

    for (SearchProvider provider : SearchProvider.values()) {
      _searchProviderBoxes.get(provider).setSelected(false);
      _searchCredentialFields.get(provider).setText("");
    }

    _chckbxReadLocalFiles.setSelected(true);
    _chckbxUseDocling.setSelected(false);
    _txtDoclingCommand.setText(LlmConfigurations.DEFAULT_DOCLING_COMMAND);
    _txtPdftotextCommand.setText(LlmConfigurations.DEFAULT_PDFTOTEXT_COMMAND);

    _chckbxDelayBetweenCalls.setSelected(false);
    _spnDelaySeconds.setValue(LlmConfigurations.DEFAULT_DELAY_BETWEEN_CALLS_SECONDS);

    _chckbxDebugMode.setSelected(false);
    _txtDebugLogDirectory.setText("");
    refreshDebugLogPath();

    setLlmTestStatus(Color.GRAY, " ");
  }

  /** Updates enable/editable status of components based on their information. */
  private void updateComponentsStatus() {
    _chckbxEnableCustomServer.setEnabled(true);
    _txtServerAddress.setEnabled(_chckbxEnableCustomServer.isSelected());
    _chckbxEnableAutoColoring.setEnabled(true);
    _chckbxEnableSmartModelling.setEnabled(true);
    final LlmProvider provider = selectedProvider();
    // the URL is only typed for "Other"; known providers take it from LlmProvider,
    // and local ones need no credentials
    _cmbLlmProvider.setEnabled(true);
    _txtLlmApiKey.setEnabled(provider.requiresApiKey());
    _txtLlmBaseUrl.setEnabled(provider.hasCustomBaseUrl());
    _txtLlmModel.setEnabled(true);

    // the search provider and its key only mean anything when the plugin does the searching
    final boolean usesSearchApi = selectedSearchMode() == SearchMode.SEARCH_API;
    _cmbSearchMode.setEnabled(true);

    for (SearchProvider searchProvider : SearchProvider.values()) {
      final JCheckBox box = _searchProviderBoxes.get(searchProvider);
      box.setEnabled(usesSearchApi);
      // a credential is only worth typing for a service that is switched on
      _searchCredentialFields.get(searchProvider).setEnabled(usesSearchApi && box.isSelected());
    }

    // both paths and both Test buttons stay live even with Docling switched off: finding out
    // whether a converter works, and where it lives, is exactly what someone does before deciding
    // to turn it on
    _chckbxReadLocalFiles.setEnabled(true);
    _chckbxUseDocling.setEnabled(true);
    _txtDoclingCommand.setEnabled(true);
    _txtPdftotextCommand.setEnabled(true);

    _chckbxDelayBetweenCalls.setEnabled(true);
    _spnDelaySeconds.setEnabled(_chckbxDelayBetweenCalls.isSelected());

    _chckbxDebugMode.setEnabled(true);
    _txtDebugLogDirectory.setEnabled(_chckbxDebugMode.isSelected());
    refreshDebugLogPath();
  }
}
