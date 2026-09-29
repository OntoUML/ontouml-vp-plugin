package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.controllers.LlmModelsController;
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
import it.unibz.inf.ontouml.vp.model.SourceReadMode;
import it.unibz.inf.ontouml.vp.model.SourceReadScope;
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

  private JPanel _optionsPanel;
  private JCheckBox _chckbxEnableLlm;
  private JPanel _llmOptionsPanel;
  private JPanel _searchProvidersGroup;

  /**
   * Width of the label column of every row in the References & Research tab, so each of its
   * inputs starts at the same x — across its groups and the rows between them — instead of each
   * group lining up only with itself, as its own GridBagLayout would have it.
   */
  private int _researchLabelWidth;
  private JComboBox<LlmProvider> _cmbLlmProvider;
  private JPasswordField _txtLlmApiKey;
  private JTextField _txtLlmBaseUrl;
  private JComboBox<String> _cmbLlmModel;
  private JButton _btnLoadModels;
  private JLabel _lblModelsStatus;
  private JComboBox<SourceReadScope> _cmbSourceReadScope;
  private JSpinner _spnMaxSourcesToRead;
  private JComboBox<SourceReadMode> _cmbSourceReadMode;
  private JSpinner _spnMaxCharactersPerSource;
  private JComboBox<SearchMode> _cmbSearchMode;
  private final Map<SearchProvider, JCheckBox> _searchProviderBoxes = new LinkedHashMap<>();
  private final Map<SearchProvider, JTextField> _searchCredentialFields = new LinkedHashMap<>();
  private JButton _btnTestSearch;
  private JButton _btnTestLlm;
  private JLabel _lblLlmTestStatus;
  private JLabel _lblSearchTestStatus;
  private JLabel _lblWebResearchSpec;
  private JLabel _lblDocsTestStatus;

  private JCheckBox _chckbxDelayBetweenCalls;
  private JSpinner _spnDelaySeconds;

  private JCheckBox _chckbxDebugMode;
  private JTextField _txtDebugLogDirectory;
  private JLabel _lblDebugLogPath;
  private JPanel _debugLogPanel;

  private JCheckBox _chckbxReadLocalFiles;
  private JCheckBox _chckbxUseDocling;
  private JTextField _txtDoclingCommand;
  private JTextField _txtPdftotextCommand;
  private JButton _btnTestDocling;
  private JButton _btnTestPdftotext;
  private JLabel _lblDoclingWait;
  private Timer _doclingWaitTimer;
  private int _doclingWaitDots;

  private static final Color TEST_SUCCESS_COLOR = new Color(0, 128, 0);
  private static final String SEARCH_DISABLED_HINT =
      "Web research is disabled — sources will be unverified.";

  private static final Color TEST_FAILURE_COLOR = Color.RED.darker();

  private JButton _btnApply;
  private JButton _btnResetDefaults;

  private JLabel pluginVersion;

  private IDialog _dialog;

  /** ConfigurationsView constructor. */
  public ConfigurationsView(ProjectConfigurations configurations) {
    setSize(new Dimension(740, 580));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {700};
    // the first row is empty: it only keeps a gap between the dialog's edge and the first option
    gridBagLayout.rowHeights = new int[] {GAP, 420, 25, 0};
    gridBagLayout.columnWeights = new double[] {1.0};
    // the options row takes the slack, so the groups keep their height and the dialog can shrink
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    _optionsPanel = new JPanel();

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
    _chckbxEnableCustomServer.setToolTipText(
        "Sends model verification and gUFO/JSON export requests to the server address typed to"
            + " the right instead of the plugin's default OntoUML Server.");
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
    _txtServerAddress.setToolTipText(
        "URL of the OntoUML Server instance to use, e.g. http://localhost:8080. Only read while"
            + " the checkbox to the left is on.");
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
    _chckbxEnableAutoColoring.setToolTipText(
        "Colours a class automatically by its OntoUML stereotype as soon as one is applied.");
    GridBagConstraints gbc__chckbxEnableAutoColoring = new GridBagConstraints();
    gbc__chckbxEnableAutoColoring.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableAutoColoring.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableAutoColoring.gridx = 0;
    gbc__chckbxEnableAutoColoring.gridy = 0;
    _smartPanel.add(_chckbxEnableAutoColoring, gbc__chckbxEnableAutoColoring);

    _chckbxEnableSmartModelling = new JCheckBox("Enable Smart Modelling");
    _chckbxEnableSmartModelling.setToolTipText(
        "Offers OntoUML-aware suggestions while modelling, such as constraining a new"
            + " generalization to the stereotypes it may actually connect.");
    GridBagConstraints gbc__chckbxEnableSmartModelling = new GridBagConstraints();
    gbc__chckbxEnableSmartModelling.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableSmartModelling.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableSmartModelling.gridx = 0;
    gbc__chckbxEnableSmartModelling.gridy = 1;
    _smartPanel.add(_chckbxEnableSmartModelling, gbc__chckbxEnableSmartModelling);

    JPanel _llmPanel = new JPanel();
    _llmPanel.setBorder(BorderFactory.createTitledBorder("AI Settings"));
    // the box fills the dialog's width by default; nothing inside it has a column with a positive
    // weight of its own, so that width is not passed down to the fields themselves — they keep to
    // their own natural size regardless of how wide this is
    _optionsPanel.add(_llmPanel, stretchedRow(3));
    _llmPanel.setLayout(new GridBagLayout());

    _chckbxEnableLlm = new JCheckBox("Enable LLM Integration");
    _chckbxEnableLlm.setToolTipText(
        "Turns every AI feature on or off: the settings below, and the \"Suggest Metaclass (AI)...\""
            + " and \"Document or improve documentation...\" actions on a class.");
    _chckbxEnableLlm.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });

    final GridBagConstraints gbcEnableLlm = new GridBagConstraints();
    gbcEnableLlm.anchor = GridBagConstraints.NORTHWEST;
    gbcEnableLlm.insets = cell();
    gbcEnableLlm.gridx = 0;
    gbcEnableLlm.gridy = 0;
    gbcEnableLlm.gridwidth = 2;
    _llmPanel.add(_chckbxEnableLlm, gbcEnableLlm);

    // everything else lives in here, so the whole group can be shown or hidden as one with the
    // checkbox above, instead of every field having to be walked and toggled by hand
    _llmOptionsPanel = new JPanel(new BorderLayout());

    final GridBagConstraints gbcOptions = new GridBagConstraints();
    gbcOptions.anchor = GridBagConstraints.NORTHWEST;
    gbcOptions.gridx = 0;
    gbcOptions.gridy = 1;
    gbcOptions.gridwidth = 2;
    _llmPanel.add(_llmOptionsPanel, gbcOptions);

    // three tabs instead of one long stack, so a setting is found by its subject rather than by
    // scrolling: the provider connection itself, everything about reading references (cited
    // sources, web research, the services and converters behind it), and the texts sent to the
    // model. Each tab keeps the fix that stops its fields from stretching to the dialog's width —
    // no weight on either column, so a row's value cell keeps to its column's own widest sibling.
    final JPanel llmProviderTab = new JPanel(new GridBagLayout());
    ((GridBagLayout) llmProviderTab.getLayout()).columnWeights = new double[] {0.0, 0.0};
    int providerRow = 0;

    final JPanel referencesResearchTab = new JPanel(new GridBagLayout());
    ((GridBagLayout) referencesResearchTab.getLayout()).columnWeights = new double[] {0.0, 0.0};
    int researchRow = 0;

    final JPanel promptsTab = new JPanel(new GridBagLayout());
    ((GridBagLayout) promptsTab.getLayout()).columnWeights = new double[] {0.0, 0.0};
    int promptsRow = 0;

    _cmbLlmProvider = new JComboBox<>(LlmProvider.values());
    _cmbLlmProvider.setToolTipText(
        "The LLM API this plugin talks to. Picking one fills in its Base URL and a starting Model"
            + " below; \"Other\" is any OpenAI-compatible endpoint of your own.");
    _cmbLlmProvider.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            applySelectedProviderDefaults();
            updateComponentsStatus();
          }
        });
    keepWholeText(_cmbLlmProvider);
    addFormRow(llmProviderTab, providerRow++, "Provider:", _cmbLlmProvider, false);

    _txtLlmApiKey = new JPasswordField();
    _txtLlmApiKey.setToolTipText(
        "The provider's API key, sent as \"Authorization: Bearer ...\". Not needed for a local"
            + " provider such as Ollama or LM Studio. Stored in this workspace's own"
            + " configuration file, not synced anywhere.");
    addFormRow(llmProviderTab, providerRow++, "API Key:", _txtLlmApiKey, true);

    _txtLlmBaseUrl = new JTextField();
    _txtLlmBaseUrl.setToolTipText(
        "The endpoint this provider's chat completions are called at. Filled in for every"
            + " provider above except \"Other\", which needs it typed here.");
    addFormRow(llmProviderTab, providerRow++, "Base URL:", _txtLlmBaseUrl, true);

    // an editable combo: typing a model by hand still works exactly as a text field would, and
    // "Fetch models" turns it into a picker for providers whose GET /models call succeeds
    _cmbLlmModel = new JComboBox<>();
    _cmbLlmModel.setEditable(true);
    _cmbLlmModel.setToolTipText(
        "Type a model name, or press \"Fetch models\" to pick from what the provider currently"
            + " offers.");

    _btnLoadModels = new JButton("Fetch models");
    _btnLoadModels.setToolTipText(
        "Calls GET {base URL}/models with the provider, key and URL above, and offers the result"
            + " here — the same endpoint every listed provider exposes next to /chat/completions.");
    _btnLoadModels.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            loadModels();
          }
        });
    _lblModelsStatus = newStatusLabel();

    final JPanel modelRow = new JPanel(new GridBagLayout());
    final GridBagConstraints gbcModelCombo = new GridBagConstraints();
    gbcModelCombo.anchor = GridBagConstraints.NORTHWEST;
    gbcModelCombo.fill = GridBagConstraints.HORIZONTAL;
    gbcModelCombo.weightx = 1.0;
    gbcModelCombo.insets = new Insets(0, 0, 0, GAP);
    gbcModelCombo.gridx = 0;
    modelRow.add(_cmbLlmModel, gbcModelCombo);
    final GridBagConstraints gbcLoadModels = new GridBagConstraints();
    gbcLoadModels.anchor = GridBagConstraints.NORTHWEST;
    gbcLoadModels.insets = new Insets(0, 0, 0, GAP);
    gbcLoadModels.gridx = 1;
    modelRow.add(_btnLoadModels, gbcLoadModels);
    final GridBagConstraints gbcModelsStatus = new GridBagConstraints();
    gbcModelsStatus.anchor = GridBagConstraints.NORTHWEST;
    gbcModelsStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcModelsStatus.weightx = 1.0;
    gbcModelsStatus.gridx = 2;
    modelRow.add(_lblModelsStatus, gbcModelsStatus);
    addFormRow(llmProviderTab, providerRow++, "Model:", modelRow, true);

    // the test sits with the connection it exercises: provider, key, URL and model
    _btnTestLlm = new JButton("Test");
    _btnTestLlm.setToolTipText(
        "Sends a minimal request with the provider, key, URL and model above to validate them.");
    _btnTestLlm.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            testLlmConnection();
          }
        });
    _lblLlmTestStatus = newStatusLabel();
    addFormRow(llmProviderTab, providerRow++, "", statusRow(_btnTestLlm, _lblLlmTestStatus), true);

    // the widest label of the tab sets the column for all of them; "Command:" sits indented under
    // its checkbox, so it needs the indent on top of its own text
    _researchLabelWidth =
        Math.max(
            widestLabel(
                "Retrieve:", "Read at most:", "Consume:", "Cut after:", "pdftotext:", "Web research:"),
            widestLabel("Command:") + INDENT);

    // both about reading what is already cited, before any search of new material happens — kept
    // next to each other, and ahead of web research
    final JPanel citedSourcesGroup = buildCitedSourcesPanel();
    addGroup(referencesResearchTab, researchRow++, citedSourcesGroup);
    addGroup(referencesResearchTab, researchRow++, buildDocumentsPanel());

    // rows outside the groups start further left than rows inside them, by the groups' border
    final int tabLabelWidth =
        _researchLabelWidth + citedSourcesGroup.getBorder().getBorderInsets(citedSourcesGroup).left;

    // the web research mode and the test of it, side by side: the test checks what is chosen here
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
    keepWholeText(_cmbSearchMode);

    _btnTestSearch = new JButton("Test search");
    _btnTestSearch.setToolTipText(
        "Checks the mode chosen here: with \"Search via the model\" it fails unless the answer"
            + " comes with the pages it was based on; with \"Search API\" it runs a sample query.");
    _btnTestSearch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            testSearch();
          }
        });
    _lblSearchTestStatus = newStatusLabel();

    final JPanel webResearchRow = new JPanel(new GridBagLayout());
    final GridBagConstraints gbcMode = new GridBagConstraints();
    gbcMode.anchor = GridBagConstraints.NORTHWEST;
    gbcMode.insets = new Insets(0, 0, 0, GAP);
    gbcMode.gridx = 0;
    webResearchRow.add(_cmbSearchMode, gbcMode);
    final GridBagConstraints gbcTestSearch = new GridBagConstraints();
    gbcTestSearch.anchor = GridBagConstraints.NORTHWEST;
    // takes the row's spare width, so the two sit on its left instead of centered in it
    gbcTestSearch.weightx = 1.0;
    gbcTestSearch.gridx = 1;
    webResearchRow.add(_btnTestSearch, gbcTestSearch);
    addFormRow(
        referencesResearchTab, researchRow++, "Web research:", webResearchRow, true, tabLabelWidth);

    // below the combo, not beside it: this text can be a whole error message from the provider,
    // which would otherwise force the row (and the group) wider to make room for it
    addFormRow(referencesResearchTab, researchRow++, "", _lblSearchTestStatus, true);

    // a precise account of the mode currently chosen, not just its one-line combo label: this is
    // read once, when deciding, so it can afford to spell out exactly what it sends and to whom —
    // updateComponentsStatus() swaps the text as the combo above changes
    _lblWebResearchSpec = new JLabel(webResearchExplanation(SearchMode.NONE));
    _lblWebResearchSpec.setEnabled(false);

    addFormRow(referencesResearchTab, researchRow++, "", _lblWebResearchSpec, true);

    // the group below uses the whole width of its tab
    _searchProvidersGroup = buildSearchProvidersPanel();
    addGroup(referencesResearchTab, researchRow++, _searchProvidersGroup);
    addVerticalGlue(referencesResearchTab, researchRow);

    addGroup(llmProviderTab, providerRow++, buildPacingPanel());
    addVerticalGlue(llmProviderTab, providerRow);

    addGroup(promptsTab, promptsRow++, buildPromptsPanel());
    addVerticalGlue(promptsTab, promptsRow);

    final JTabbedPane llmTabs = new JTabbedPane();
    llmTabs.addTab("LLM Provider", llmProviderTab);
    llmTabs.addTab("References & Research", referencesResearchTab);
    llmTabs.addTab("Prompts", promptsTab);
    llmTabs.addTab("Debugging", buildDebugPanel());
    _llmOptionsPanel.add(llmTabs, BorderLayout.CENTER);

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
    _btnApply.setToolTipText("Saves every setting on this screen and closes it.");
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
    _btnResetDefaults.setToolTipText(
        "Puts every setting on this screen back to its default value. Takes effect only once"
            + " \"Save\" is pressed.");
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

  /** The model currently typed or selected, trimmed of surrounding whitespace. */
  private String modelText() {
    final Object edited = _cmbLlmModel.getEditor().getItem();
    final String text = edited == null ? "" : edited.toString();

    return text.trim();
  }

  private void setModelText(String model) {
    _cmbLlmModel.setSelectedItem(model == null ? "" : model);
  }

  /**
   * Calls {@link LlmModelsController#listModels}, using what is typed in the form (not the saved
   * configuration), and offers the result in the combo. Whatever was typed before is kept: it is
   * re-selected if it is one of the models returned, and otherwise simply stays as the combo's
   * (editable) text, exactly as if nothing had been fetched.
   */
  private void loadModels() {
    final LlmConfigurations probe = probeConfiguration();
    final String typed = modelText();

    _btnLoadModels.setEnabled(false);
    setStatus(_lblModelsStatus, Color.GRAY, "Loading models...");

    new SwingWorker<List<String>, Void>() {
      @Override
      protected List<String> doInBackground() throws Exception {
        return LlmModelsController.listModels(probe);
      }

      @Override
      protected void done() {
        _btnLoadModels.setEnabled(true);

        try {
          final List<String> models = get();

          _cmbLlmModel.removeAllItems();

          for (String model : models) {
            _cmbLlmModel.addItem(model);
          }

          setModelText(typed);
          setStatus(
              _lblModelsStatus,
              TEST_SUCCESS_COLOR,
              models.size() + " model" + (models.size() == 1 ? "" : "s") + " loaded.");
        } catch (Exception e) {
          final Throwable cause = e.getCause() != null ? e.getCause() : e;
          final String message = cause.getMessage();
          setStatus(
              _lblModelsStatus,
              TEST_FAILURE_COLOR,
              message == null || message.isEmpty() ? "Could not load models." : message);
          e.printStackTrace();
        }
      }
    }.execute();
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

    if (LlmProvider.isDefaultModel(modelText())) {
      setModelText(provider.getDefaultModel());
    }

    setLlmTestStatus(Color.GRAY, " ");
  }

  /** The one gap used between neighbouring components, horizontally and vertically. */
  private static final int GAP = 6;

  /** Insets of every cell inside a group: half the gap on each side, so neighbours are a gap apart. */
  private static Insets cell() {
    return new Insets(GAP / 2, GAP / 2, GAP / 2, GAP / 2);
  }

  /** How far a checkbox's text starts from its left edge, past the box itself. */
  private static final int INDENT = 18;

  /** Insets of a cell that belongs to the checkbox above it, lined up under that checkbox's text. */
  private static Insets indentedCell() {
    return new Insets(GAP / 2, GAP / 2 + INDENT, GAP / 2, GAP / 2);
  }

  /** The preferred width of the widest of these texts, laid out as a plain label. */
  private static int widestLabel(String... texts) {
    int widest = 0;

    for (String text : texts) {
      widest = Math.max(widest, new JLabel(text).getPreferredSize().width);
    }

    return widest;
  }

  /** A label held to the given width (its natural one when that is 0), so a column can be shared. */
  private static JLabel formLabel(String text, int width) {
    final JLabel label = new JLabel(text);

    if (width > 0) {
      final int height = label.getPreferredSize().height;
      label.setPreferredSize(new Dimension(width, height));
      label.setMinimumSize(new Dimension(width, height));
    }

    return label;
  }

  /**
   * Keeps a combo box from ever being narrower than its widest entry. Left to the layout it can be
   * squeezed until the text is cut, which is what made these two look broken when the dialog shrank.
   */
  private static void keepWholeText(JComboBox<?> combo) {
    combo.setMinimumSize(combo.getPreferredSize());
  }

  /**
   * A status line that gives way to its neighbours. Its text can be a whole error message from the
   * provider, and if that set the width of the group the dialog could not be made narrower than the
   * message; so it asks for little, shows what fits followed by an ellipsis, and keeps the full text
   * as its tooltip.
   */
  private static JLabel newStatusLabel() {
    final JLabel label = new JLabel(" ");
    final int height = label.getPreferredSize().height;

    label.setPreferredSize(new Dimension(80, height));
    label.setMinimumSize(new Dimension(0, height));

    return label;
  }

  /** A button and its verdict on one line, the verdict taking whatever width is left. */
  private static JPanel statusRow(JButton button, JLabel status) {
    final JPanel row = new JPanel(new GridBagLayout());

    final GridBagConstraints gbcButton = new GridBagConstraints();
    gbcButton.anchor = GridBagConstraints.NORTHWEST;
    gbcButton.insets = new Insets(0, 0, 0, GAP);
    gbcButton.gridx = 0;
    row.add(button, gbcButton);

    final GridBagConstraints gbcStatus = new GridBagConstraints();
    gbcStatus.anchor = GridBagConstraints.NORTHWEST;
    gbcStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcStatus.weightx = 1.0;
    gbcStatus.gridx = 1;
    row.add(status, gbcStatus);

    return row;
  }

  /** One row of a group: a label, then its component — stretched to the width, or as wide as it is. */
  private static void addFormRow(
      JPanel panel, int row, String label, JComponent component, boolean stretch) {
    addFormRow(panel, row, label, component, stretch, 0);
  }

  /** The same row, with its label held to {@code labelWidth} so it lines up with other groups. */
  private static void addFormRow(
      JPanel panel, int row, String label, JComponent component, boolean stretch, int labelWidth) {
    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.NORTHWEST;
    gbcLabel.insets = cell();
    gbcLabel.gridx = 0;
    gbcLabel.gridy = row;
    panel.add(formLabel(label, labelWidth), gbcLabel);

    final GridBagConstraints gbcComponent = new GridBagConstraints();
    gbcComponent.anchor = GridBagConstraints.NORTHWEST;
    gbcComponent.fill = stretch ? GridBagConstraints.HORIZONTAL : GridBagConstraints.NONE;
    gbcComponent.weightx = 1.0;
    gbcComponent.insets = cell();
    gbcComponent.gridx = 1;
    gbcComponent.gridy = row;
    panel.add(component, gbcComponent);
  }

  /** A titled group placed under the fields of the panel, across both of its columns. */
  private static void addGroup(JPanel panel, int row, JPanel group) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(GAP, 0, 0, 0);
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    panel.add(group, gbc);
  }

  /**
   * Claims whatever vertical space is left over in a panel that is a whole tab's content, so that
   * space collects here instead of pushing every row down to look vertically centered — a tab's
   * content otherwise gets exactly that: with no row asking for the leftover height, GridBagLayout
   * centers the whole block in it, which is why "Provider:" used to float well below the tab strip.
   * Existing rows keep their own spacing untouched; only the empty space past the last one moves.
   */
  private static void addVerticalGlue(JPanel panel, int row) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.VERTICAL;
    panel.add(Box.createGlue(), gbc);
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
    gbc.insets = new Insets(GAP, 0, 0, 0);
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
   * How far to look for the sources cited in the model, and how much of each to actually read,
   * before any web research happens — reading a norm end to end is itself a request per source (or
   * several, for a long one), which is a cost of its own regardless of whether the model then
   * searches the web on top of it.
   */
  private JPanel buildCitedSourcesPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder("References cited in the model"));

    int row = 0;

    _cmbSourceReadScope = new JComboBox<>(SourceReadScope.values());
    _cmbSourceReadScope.setToolTipText(
        "How far to look for the references a class cites, directly or through what surrounds"
            + " it. Reading none of them is the cheapest option; every reference found still gets"
            + " listed by name in the prompt, just not opened.");
    _cmbSourceReadScope.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateComponentsStatus();
          }
        });
    keepWholeText(_cmbSourceReadScope);
    addFormRow(panel, row++, "Retrieve:", _cmbSourceReadScope, false, _researchLabelWidth);

    _spnMaxSourcesToRead =
        new JSpinner(
            new SpinnerNumberModel(
                LlmConfigurations.DEFAULT_MAX_SOURCES_TO_READ,
                1,
                LlmConfigurations.MAX_SOURCES_TO_READ_LIMIT,
                1));
    _spnMaxSourcesToRead.setToolTipText(
        "Read at most this many references per class, even when more are cited — each one is a"
            + " request of its own (sometimes several, for a long one).");
    addFormRow(
        panel,
        row++,
        "Read at most:",
        withUnit(_spnMaxSourcesToRead, "references"),
        false,
        _researchLabelWidth);

    _cmbSourceReadMode = new JComboBox<>(SourceReadMode.values());
    _cmbSourceReadMode.setToolTipText(
        "How much of one source is read. \"" + SourceReadMode.MAP_REDUCE.getLabel() + "\" is what"
            + " this has always done: a long source costs one request per part instead of one, but"
            + " nothing in it is skipped.");
    _cmbSourceReadMode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateComponentsStatus();
          }
        });
    keepWholeText(_cmbSourceReadMode);
    addFormRow(panel, row++, "Consume:", _cmbSourceReadMode, false, _researchLabelWidth);

    _spnMaxCharactersPerSource =
        new JSpinner(
            new SpinnerNumberModel(
                LlmConfigurations.DEFAULT_MAX_CHARACTERS_PER_SOURCE,
                LlmConfigurations.MIN_CHARACTERS_PER_SOURCE,
                LlmConfigurations.MAX_CHARACTERS_PER_SOURCE_LIMIT,
                1000));
    _spnMaxCharactersPerSource.setToolTipText(
        "Only read under \"" + SourceReadMode.TRUNCATE.getLabel() + "\": everything past this many"
            + " characters of a source is simply not seen. The default is generous for an ordinary"
            + " page and deliberately short of what a long norm would need — that case is what the"
            + " other option is for.");
    addFormRow(
        panel,
        row++,
        "Cut after:",
        withUnit(_spnMaxCharactersPerSource, "characters"),
        false,
        _researchLabelWidth);

    return panel;
  }

  /**
   * A precise account of one web research mode — not just its one-line combo label — spelling out
   * exactly what it sends and to whom. Only the chosen mode's own text is shown at a time, so this
   * reads as a specific answer to "what does this do", not three paragraphs to sift through.
   */
  private static String webResearchExplanation(SearchMode mode) {
    final String text;

    switch (mode) {
      case MODEL:
        text =
            "<b>Search via the model:</b> the provider itself searches the web while answering —"
                + " this plugin sends no query and sees no result, and it only works on a model"
                + " built for it (e.g. OpenRouter with :online, a Perplexity model).";
        break;
      case SEARCH_API:
        text =
            "<b>Search API:</b> this plugin runs one query per class through the services below,"
                + " retrieves each result's page itself, and gives the model only the text it"
                + " actually retrieved.";
        break;
      case NONE:
      default:
        text =
            "<b>No research:</b> nothing is looked up; every source in the documentation comes"
                + " from the model's own training and is marked (não verificado).";
    }

    return "<html><div style='width:370px'>" + text + "</div></html>";
  }

  /**
   * A spinner followed by a plain-text unit, the two treated as one component for layout. The gap
   * goes only between them: a FlowLayout would also put one before the spinner, and start it
   * further right than the combo boxes above and below it.
   */
  private static JPanel withUnit(JSpinner spinner, String unit) {
    final JPanel row = new JPanel();
    row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
    row.add(spinner);
    row.add(Box.createHorizontalStrut(GAP));
    row.add(new JLabel(unit));
    return row;
  }

  private SourceReadScope selectedSourceReadScope() {
    final Object selected = _cmbSourceReadScope.getSelectedItem();
    return selected instanceof SourceReadScope
        ? (SourceReadScope) selected
        : LlmConfigurations.DEFAULT_SOURCE_READ_SCOPE;
  }

  private SourceReadMode selectedSourceReadMode() {
    final Object selected = _cmbSourceReadMode.getSelectedItem();
    return selected instanceof SourceReadMode
        ? (SourceReadMode) selected
        : LlmConfigurations.DEFAULT_SOURCE_READ_MODE;
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
      box.setToolTipText(
          "Search with " + provider.getLabel() + " when researching a class's documentation.");
      box.addChangeListener(
          new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              updateComponentsStatus();
            }
          });
      _searchProviderBoxes.put(provider, box);

      final GridBagConstraints gbcBox = new GridBagConstraints();
      gbcBox.anchor = GridBagConstraints.NORTHWEST;
      gbcBox.insets = cell();
      gbcBox.gridx = 0;
      gbcBox.gridy = row;
      panel.add(box, gbcBox);

      final JLabel credentialLabel = new JLabel(provider.getCredentialLabel() + ":");
      final GridBagConstraints gbcLabel = new GridBagConstraints();
      gbcLabel.anchor = GridBagConstraints.NORTHWEST;
      gbcLabel.insets = cell();
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
      gbcCredential.anchor = GridBagConstraints.NORTHWEST;
      gbcCredential.fill = GridBagConstraints.HORIZONTAL;
      gbcCredential.weightx = 1.0;
      gbcCredential.insets = cell();
      gbcCredential.gridx = 2;
      gbcCredential.gridy = row;
      panel.add(credential, gbcCredential);

      row++;
    }

    return panel;
  }

  /**
   * The texts sent to the model: the button opens the editor for the classification prompts, the
   * decision tree and the documentation template and prompt. No titled group around it — the tab
   * already says what it is about. The hint goes under the button, not beside it: side by side
   * they were the widest row of every tab, and forced the dialog to scroll sideways.
   */
  private JPanel buildPromptsPanel() {
    final JPanel panel = new JPanel(new GridBagLayout());

    final JButton button = new JButton("Edit prompts & decision tree...");
    button.setToolTipText(
        "Reads and edits the prompts, the decision tree and the documentation template.");
    button.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            ApplicationManager.instance()
                .getViewManager()
                .showDialog(new LlmPromptSettingsView());
          }
        });

    final JLabel hint = new JLabel("What the model is told when suggesting a metaclass or documenting.");
    hint.setEnabled(false);

    final GridBagConstraints gbcButton = new GridBagConstraints();
    gbcButton.anchor = GridBagConstraints.NORTHWEST;
    gbcButton.insets = cell();
    gbcButton.gridx = 0;
    gbcButton.gridy = 0;
    panel.add(button, gbcButton);

    final GridBagConstraints gbcHint = new GridBagConstraints();
    gbcHint.anchor = GridBagConstraints.NORTHWEST;
    gbcHint.weightx = 1.0;
    gbcHint.insets = cell();
    gbcHint.gridx = 0;
    gbcHint.gridy = 1;
    panel.add(hint, gbcHint);

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
    gbcBox.anchor = GridBagConstraints.NORTHWEST;
    gbcBox.insets = cell();
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
    _spnDelaySeconds.setToolTipText("How long to wait after each call before making the next.");

    final GridBagConstraints gbcSpinner = new GridBagConstraints();
    gbcSpinner.anchor = GridBagConstraints.NORTHWEST;
    gbcSpinner.insets = cell();
    gbcSpinner.gridx = 1;
    gbcSpinner.gridy = 0;
    panel.add(_spnDelaySeconds, gbcSpinner);

    final GridBagConstraints gbcUnit = new GridBagConstraints();
    gbcUnit.anchor = GridBagConstraints.NORTHWEST;
    gbcUnit.weightx = 1.0;
    gbcUnit.insets = cell();
    gbcUnit.gridx = 2;
    gbcUnit.gridy = 0;
    panel.add(new JLabel("seconds"), gbcUnit);

    final String noteText =
        "Whatever is set here, a 429 (too many requests) from the provider makes the plugin"
            + " wait "
            + LlmRequestPacing.RATE_LIMIT_WAIT_SECONDS
            + " s and try again, up to "
            + LlmRequestPacing.MAX_RATE_LIMIT_RETRIES
            + " times.";
    final JLabel note = new JLabel("<html><div style='width:370px'>" + noteText + "</div></html>");
    note.setToolTipText(noteText);
    note.setForeground(Color.GRAY);

    final GridBagConstraints gbcNote = new GridBagConstraints();
    gbcNote.anchor = GridBagConstraints.NORTHWEST;
    gbcNote.fill = GridBagConstraints.HORIZONTAL;
    gbcNote.weightx = 1.0;
    gbcNote.insets = indentedCell();
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
    // no titled group: it would only repeat the name of the tab it is the whole content of
    final JPanel panel = new JPanel(new GridBagLayout());

    _chckbxDebugMode = new JCheckBox("Debug mode — record every conversation to disk");
    _chckbxDebugMode.setToolTipText(
        "Keeps the whole session's exchanges (prompts, answers, and how each reference was"
            + " retrieved) and writes them to a file. Off by default: a prompt carries your model.");
    _chckbxDebugMode.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });

    final GridBagConstraints gbcBox = new GridBagConstraints();
    gbcBox.anchor = GridBagConstraints.NORTHWEST;
    gbcBox.insets = cell();
    gbcBox.gridx = 0;
    gbcBox.gridy = 0;
    gbcBox.gridwidth = 3;
    panel.add(_chckbxDebugMode, gbcBox);

    // the folder means nothing while debug mode is off, so it is hidden along with it rather than
    // merely greyed out — one component to show or hide, instead of the field and the path label
    // having to agree on it separately
    _debugLogPanel = new JPanel(new GridBagLayout());

    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.NORTHWEST;
    gbcLabel.insets = cell();
    gbcLabel.gridx = 0;
    gbcLabel.gridy = 0;
    _debugLogPanel.add(new JLabel("Log folder:"), gbcLabel);

    _txtDebugLogDirectory = new JTextField();
    _txtDebugLogDirectory.setToolTipText(
        "Leave empty to use the default folder, shown below, beside the plugin's configuration"
            + " file in the Visual Paradigm workspace.");

    final GridBagConstraints gbcField = new GridBagConstraints();
    gbcField.anchor = GridBagConstraints.NORTHWEST;
    gbcField.fill = GridBagConstraints.HORIZONTAL;
    gbcField.weightx = 1.0;
    gbcField.insets = cell();
    gbcField.gridx = 1;
    gbcField.gridy = 0;
    _debugLogPanel.add(_txtDebugLogDirectory, gbcField);

    _lblDebugLogPath = newStatusLabel();
    _lblDebugLogPath.setForeground(Color.GRAY);

    final GridBagConstraints gbcPath = new GridBagConstraints();
    gbcPath.anchor = GridBagConstraints.NORTHWEST;
    gbcPath.fill = GridBagConstraints.HORIZONTAL;
    gbcPath.weightx = 1.0;
    gbcPath.insets = cell();
    gbcPath.gridx = 0;
    gbcPath.gridy = 1;
    gbcPath.gridwidth = 2;
    _debugLogPanel.add(_lblDebugLogPath, gbcPath);

    final GridBagConstraints gbcDebugLogPanel = new GridBagConstraints();
    gbcDebugLogPanel.anchor = GridBagConstraints.NORTHWEST;
    gbcDebugLogPanel.fill = GridBagConstraints.HORIZONTAL;
    gbcDebugLogPanel.weightx = 1.0;
    gbcDebugLogPanel.insets = indentedCell();
    gbcDebugLogPanel.gridx = 0;
    gbcDebugLogPanel.gridy = 1;
    gbcDebugLogPanel.gridwidth = 3;
    panel.add(_debugLogPanel, gbcDebugLogPanel);

    addVerticalGlue(panel, 2);

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
   * <p>Docling is off by default and each converter has a test button beside its path, because the
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
    gbcLocal.anchor = GridBagConstraints.NORTHWEST;
    gbcLocal.insets = cell();
    gbcLocal.gridx = 0;
    gbcLocal.gridy = 0;
    gbcLocal.gridwidth = 3;
    panel.add(_chckbxReadLocalFiles, gbcLocal);

    // pdftotext first: it is what reads a PDF unless Docling is switched on below, so it applies
    // regardless of that checkbox and does not belong under it
    _txtPdftotextCommand = new JTextField();
    _btnTestPdftotext =
        addConverterRow(
            panel,
            1,
            "pdftotext:",
            false,
            _txtPdftotextCommand,
            "Name or full path of pdftotext (poppler). Used for PDF when Docling is off.",
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                testConverter(false);
              }
            });

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
    gbcDocling.anchor = GridBagConstraints.NORTHWEST;
    gbcDocling.insets = cell();
    gbcDocling.gridx = 0;
    gbcDocling.gridy = 2;
    gbcDocling.gridwidth = 3;
    panel.add(_chckbxUseDocling, gbcDocling);

    // indented under the checkbox it belongs to, and only enabled while that checkbox is on
    _txtDoclingCommand = new JTextField();
    _btnTestDocling =
        addConverterRow(
            panel,
            3,
            "Command:",
            true,
            _txtDoclingCommand,
            "Name or full path of the docling executable.",
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                testConverter(true);
              }
            });

    // shown only while the test above is actually running: Docling's first conversion downloads
    // layout models and can take minutes, and with nothing on screen changing in the meantime a
    // slow test and a hung one look identical
    _lblDoclingWait = new JLabel(" ");
    _lblDoclingWait.setForeground(Color.YELLOW.darker());
    _lblDoclingWait.setVisible(false);

    final GridBagConstraints gbcDoclingWait = new GridBagConstraints();
    gbcDoclingWait.anchor = GridBagConstraints.NORTHWEST;
    gbcDoclingWait.insets = indentedCell();
    gbcDoclingWait.gridx = 0;
    gbcDoclingWait.gridy = 4;
    gbcDoclingWait.gridwidth = 3;
    panel.add(_lblDoclingWait, gbcDoclingWait);

    _lblDocsTestStatus = newStatusLabel();

    final GridBagConstraints gbcStatus = new GridBagConstraints();
    gbcStatus.anchor = GridBagConstraints.NORTHWEST;
    gbcStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcStatus.weightx = 1.0;
    gbcStatus.insets = indentedCell();
    gbcStatus.gridx = 0;
    gbcStatus.gridy = 5;
    gbcStatus.gridwidth = 3;
    panel.add(_lblDocsTestStatus, gbcStatus);

    return panel;
  }

  /** Cycles the "Processing…" label under the Docling field through one, two and three dots. */
  private void startDoclingWaitAnimation() {
    _lblDoclingWait.setVisible(true);
    _doclingWaitDots = 0;

    _doclingWaitTimer =
        new Timer(
            450,
            e -> {
              _doclingWaitDots = _doclingWaitDots % 3 + 1;
              _lblDoclingWait.setText(
                  "Processando com Docling — a primeira execução pode demorar vários minutos"
                      + repeat('.', _doclingWaitDots));
            });
    _doclingWaitTimer.setInitialDelay(0);
    _doclingWaitTimer.start();
  }

  private void stopDoclingWaitAnimation() {
    if (_doclingWaitTimer != null) {
      _doclingWaitTimer.stop();
      _doclingWaitTimer = null;
    }

    _lblDoclingWait.setVisible(false);
    _lblDoclingWait.setText(" ");
  }

  private static String repeat(char c, int times) {
    final StringBuilder result = new StringBuilder(times);

    for (int i = 0; i < times; i++) {
      result.append(c);
    }

    return result.toString();
  }

  /**
   * A label, the path field and its test button — the same shape for both converters. An indented
   * row gives up the indent from its label's width, so its field still starts where every other
   * input of the tab does.
   */
  private JButton addConverterRow(
      JPanel panel,
      int row,
      String label,
      boolean indented,
      JTextField field,
      String tooltip,
      ActionListener test) {
    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.NORTHWEST;
    gbcLabel.insets = indented ? indentedCell() : cell();
    gbcLabel.gridx = 0;
    gbcLabel.gridy = row;
    panel.add(
        formLabel(label, indented ? _researchLabelWidth - INDENT : _researchLabelWidth), gbcLabel);

    field.setToolTipText(tooltip);

    final GridBagConstraints gbcField = new GridBagConstraints();
    gbcField.anchor = GridBagConstraints.NORTHWEST;
    gbcField.fill = GridBagConstraints.HORIZONTAL;
    gbcField.weightx = 1.0;
    gbcField.insets = cell();
    gbcField.gridx = 1;
    gbcField.gridy = row;
    panel.add(field, gbcField);

    final JButton button = new JButton("Test command");
    button.setToolTipText("Runs the converter on a small file to prove it works.");
    button.addActionListener(test);

    final GridBagConstraints gbcButton = new GridBagConstraints();
    gbcButton.anchor = GridBagConstraints.NORTHWEST;
    gbcButton.insets = cell();
    gbcButton.gridx = 2;
    gbcButton.gridy = row;
    panel.add(button, gbcButton);

    return button;
  }

  /**
   * Runs the converter named in the form — not the saved one — so a path can be corrected and
   * checked before saving.
   *
   * <p>Docling's report always goes into a dialog, success or failure: the status line alone
   * would not fit the install command a failure needs, and Docling's first run legitimately takes
   * minutes, which is what {@link #startDoclingWaitAnimation()} is for. pdftotext is quick and, on
   * success, self-explanatory, so its dialog is worth the interruption only on failure — and that
   * one dialog is our own (not {@link ViewManagerUtils#simpleDialog}), so it can be centered on
   * the screen rather than wherever Visual Paradigm would otherwise place it.
   */
  private void testConverter(boolean docling) {
    final LlmConfigurations probe = probeConfiguration();
    final JButton button = docling ? _btnTestDocling : _btnTestPdftotext;
    final String name = docling ? "Docling" : "pdftotext";

    // a path typed and tested at once would otherwise be looked up under the previous verdict
    ExternalCommand.forgetResolutions();

    button.setEnabled(false);
    setStatus(_lblDocsTestStatus, Color.GRAY, "Testing " + name + "...");

    if (docling) {
      startDoclingWaitAnimation();
    }

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() {
        return docling
            ? DocumentExtractor.describeDoclingAvailability(probe)
            : DocumentExtractor.describePdftotextAvailability(probe);
      }

      @Override
      protected void done() {
        button.setEnabled(!docling || _chckbxUseDocling.isSelected());

        if (docling) {
          stopDoclingWaitAnimation();
        }

        String report;

        try {
          report = get();
        } catch (Exception e) {
          report = "The test could not be run: " + e.getMessage();
          e.printStackTrace();
        }

        final boolean worked = report.contains("Conversão de teste concluída");
        setStatus(
            _lblDocsTestStatus,
            worked ? TEST_SUCCESS_COLOR : TEST_FAILURE_COLOR,
            name + (worked ? " OK" : " não está utilizável"));

        if (docling) {
          ViewManagerUtils.simpleDialog(name + "\n\n" + report);
        } else if (!worked) {
          showCenteredReport(name, report);
        }
      }
    }.execute();
  }

  /** A report dialog centered on the screen, regardless of where Visual Paradigm's own dialogs land. */
  private void showCenteredReport(String title, String message) {
    final JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
    final JDialog dialog = pane.createDialog(null, title);

    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
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
      // the button is disabled in this mode; this only guards a call from somewhere else
      setSearchTestStatus(Color.GRAY, SEARCH_DISABLED_HINT);
      return;
    }

    final LlmConfigurations probe = probeConfiguration();
    final String query = "Lei 14.133/2021 planalto.gov.br";

    _btnTestSearch.setEnabled(false);
    setSearchTestStatus(Color.GRAY, "Testing web research...");

    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        if (mode == SearchMode.SEARCH_API) {
          final List<SearchResult> results = WebSearchController.search(probe, query, 3);

          return results.isEmpty()
              ? ""
              : results.size() + " result(s) — first: " + results.get(0).getTitle();
        }

        return describeModelSearch(LlmAccessController.testModelSearch(probe));
      }

      @Override
      protected void done() {
        _btnTestSearch.setEnabled(true);

        try {
          final String outcome = get();

          if (outcome.isEmpty()) {
            setSearchTestStatus(TEST_FAILURE_COLOR, "The search returned no results.");
          } else if (outcome.startsWith(MODEL_NOT_SEARCHING)) {
            setSearchTestStatus(TEST_FAILURE_COLOR, outcome);
          } else if (mode == SearchMode.SEARCH_API) {
            setSearchTestStatus(TEST_SUCCESS_COLOR, "Search OK — " + abbreviate(outcome));
          } else {
            setSearchTestStatus(TEST_SUCCESS_COLOR, outcome);
          }
        } catch (Exception e) {
          final Throwable cause = e.getCause() != null ? e.getCause() : e;
          final String message = cause.getMessage();
          setSearchTestStatus(
              TEST_FAILURE_COLOR, message == null || message.isEmpty() ? "Test failed." : message);
          e.printStackTrace();
        }

        // the status line only carries a verdict; the log shows what was asked and what came back,
        // including every service the rotation tried
        showTestDetails();
      }
    }.execute();
  }

  private static final String MODEL_NOT_SEARCHING = "Not working: ";

  /**
   * The verdict on "Search via the model". It is a failure unless the provider returned the pages it
   * consulted: an answer that merely contains a URL proves nothing, since a model without search
   * names pages from memory, and for a well-known law names the right one.
   */
  private static String describeModelSearch(LlmAccessController.ModelSearchProbe probe) {
    if (!probe.isSearching()) {
      return MODEL_NOT_SEARCHING
          + "this provider/model answered without citing any page, so it is not searching the web."
          + " Use a search-capable model (OpenRouter with :online, Perplexity, an OpenAI"
          + " *-search model) or switch to \"Search API\".";
    }

    return "Search OK — "
        + probe.getCitations().size()
        + " page(s) cited by the provider, first: "
        + abbreviate(probe.getCitations().get(0));
  }

  /** A configuration built from what is typed in the form, so a test runs before saving. */
  private LlmConfigurations probeConfiguration() {
    final LlmConfigurations probe = new LlmConfigurations();

    probe.setProvider(selectedProvider());
    probe.setApiKey(new String(_txtLlmApiKey.getPassword()));
    probe.setCustomBaseUrl(_txtLlmBaseUrl.getText().trim());
    probe.setModel(modelText().trim());
    probe.setSourceReadScope(selectedSourceReadScope());
    probe.setMaxSourcesToRead((Integer) _spnMaxSourcesToRead.getValue());
    probe.setSourceReadMode(selectedSourceReadMode());
    probe.setMaxCharactersPerSource((Integer) _spnMaxCharactersPerSource.getValue());
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
    setStatus(_lblLlmTestStatus, color, message);
  }

  private void setSearchTestStatus(Color color, String message) {
    setStatus(_lblSearchTestStatus, color, message);
  }

  private static void setStatus(JLabel label, Color color, String message) {
    label.setForeground(color);
    label.setText(message);
    label.setToolTipText(message);
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
    llmConfigurations.setLlmIntegrationEnabled(_chckbxEnableLlm.isSelected());
    llmConfigurations.setProvider(selectedProvider());
    llmConfigurations.setApiKey(new String(_txtLlmApiKey.getPassword()));
    llmConfigurations.setCustomBaseUrl(_txtLlmBaseUrl.getText());
    llmConfigurations.setModel(modelText());
    llmConfigurations.setSourceReadScope(selectedSourceReadScope());
    llmConfigurations.setMaxSourcesToRead((Integer) _spnMaxSourcesToRead.getValue());
    llmConfigurations.setSourceReadMode(selectedSourceReadMode());
    llmConfigurations.setMaxCharactersPerSource((Integer) _spnMaxCharactersPerSource.getValue());
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
    _chckbxEnableLlm.setSelected(llmConfigurations.isLlmIntegrationEnabled());
    _cmbLlmProvider.setSelectedItem(llmConfigurations.getProvider());
    _txtLlmApiKey.setText(llmConfigurations.getApiKey());
    _txtLlmBaseUrl.setText(llmConfigurations.getEffectiveBaseUrl());
    setModelText(llmConfigurations.getModel());
    _cmbSourceReadScope.setSelectedItem(llmConfigurations.getSourceReadScope());
    _spnMaxSourcesToRead.setValue(llmConfigurations.getMaxSourcesToRead());
    _cmbSourceReadMode.setSelectedItem(llmConfigurations.getSourceReadMode());
    _spnMaxCharactersPerSource.setValue(llmConfigurations.getMaxCharactersPerSource());
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

    _chckbxEnableLlm.setSelected(LlmConfigurations.DEFAULT_LLM_INTEGRATION_ENABLED);
    _cmbLlmProvider.setSelectedItem(LlmConfigurations.DEFAULT_PROVIDER);
    _txtLlmApiKey.setText("");
    _txtLlmBaseUrl.setText(LlmConfigurations.DEFAULT_PROVIDER.getBaseUrl());
    setModelText(LlmConfigurations.DEFAULT_PROVIDER.getDefaultModel());
    _cmbSourceReadScope.setSelectedItem(LlmConfigurations.DEFAULT_SOURCE_READ_SCOPE);
    _spnMaxSourcesToRead.setValue(LlmConfigurations.DEFAULT_MAX_SOURCES_TO_READ);
    _cmbSourceReadMode.setSelectedItem(LlmConfigurations.DEFAULT_SOURCE_READ_MODE);
    _spnMaxCharactersPerSource.setValue(LlmConfigurations.DEFAULT_MAX_CHARACTERS_PER_SOURCE);
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
    setSearchTestStatus(Color.GRAY, " ");
    setStatus(_lblDocsTestStatus, Color.GRAY, " ");
  }

  /** Updates enable/editable status of components based on their information. */
  private void updateComponentsStatus() {
    final boolean llmEnabled = _chckbxEnableLlm.isSelected();

    if (_llmOptionsPanel.isVisible() != llmEnabled) {
      _llmOptionsPanel.setVisible(llmEnabled);
      _optionsPanel.revalidate();
      _optionsPanel.repaint();
    }

    _chckbxEnableCustomServer.setEnabled(true);
    _txtServerAddress.setEnabled(_chckbxEnableCustomServer.isSelected());
    _chckbxEnableAutoColoring.setEnabled(true);
    _chckbxEnableSmartModelling.setEnabled(true);

    // everything from here to the end of this block is inside the AI Settings box, and reads
    // nothing while the integration switch above is off
    if (llmEnabled) {
      final LlmProvider provider = selectedProvider();
      // the URL is only typed for "Other"; known providers take it from LlmProvider,
      // and local ones need no credentials
      _cmbLlmProvider.setEnabled(true);
      _txtLlmApiKey.setEnabled(provider.requiresApiKey());
      _txtLlmBaseUrl.setEnabled(provider.hasCustomBaseUrl());
      _cmbLlmModel.setEnabled(true);
      _btnLoadModels.setEnabled(true);

      // the sources cited in the model only get read at all outside of "Don't retrieve"
      final boolean readCitedSources = selectedSourceReadScope() != SourceReadScope.NONE;
      _cmbSourceReadScope.setEnabled(true);
      _spnMaxSourcesToRead.setEnabled(readCitedSources);
      _cmbSourceReadMode.setEnabled(readCitedSources);
      // the character cutoff only means anything in the truncating mode
      _spnMaxCharactersPerSource.setEnabled(
          readCitedSources && selectedSourceReadMode() == SourceReadMode.TRUNCATE);

      // the search provider and its key only mean anything when the plugin does the searching
      final boolean usesSearchApi = selectedSearchMode() == SearchMode.SEARCH_API;
      _cmbSearchMode.setEnabled(true);
      _lblWebResearchSpec.setText(webResearchExplanation(selectedSearchMode()));

      final boolean researchIsOff = selectedSearchMode() == SearchMode.NONE;
      _btnTestSearch.setEnabled(!researchIsOff);

      if (researchIsOff) {
        // only ever overwrite a status line that is this same placeholder, never a real verdict
        if (_lblSearchTestStatus.getText().equals(SEARCH_DISABLED_HINT)
            || _lblSearchTestStatus.getText().trim().isEmpty()) {
          setSearchTestStatus(Color.GRAY, SEARCH_DISABLED_HINT);
        }
      } else if (_lblSearchTestStatus.getText().equals(SEARCH_DISABLED_HINT)) {
        setSearchTestStatus(Color.GRAY, " ");
      }

      if (_searchProvidersGroup.isVisible() != usesSearchApi) {
        // the search services and their own API keys only mean anything in this mode
        _searchProvidersGroup.setVisible(usesSearchApi);
        _llmOptionsPanel.revalidate();
        _llmOptionsPanel.repaint();
      }

      for (SearchProvider searchProvider : SearchProvider.values()) {
        final JCheckBox box = _searchProviderBoxes.get(searchProvider);
        box.setEnabled(usesSearchApi);
        // a credential is only worth typing for a service that is switched on
        _searchCredentialFields.get(searchProvider).setEnabled(usesSearchApi && box.isSelected());
      }

      // Docling's command and its test only mean anything with Docling switched on; pdftotext is
      // what reads a PDF otherwise, so it stays live either way
      final boolean doclingOn = _chckbxUseDocling.isSelected();
      _chckbxReadLocalFiles.setEnabled(true);
      _chckbxUseDocling.setEnabled(true);
      _txtDoclingCommand.setEnabled(doclingOn);
      _btnTestDocling.setEnabled(doclingOn);
      _txtPdftotextCommand.setEnabled(true);

      _chckbxDelayBetweenCalls.setEnabled(true);
      _spnDelaySeconds.setEnabled(_chckbxDelayBetweenCalls.isSelected());

      // Debugging is now one more tab of this same group, so it is hidden along with the rest
      // whenever the switch above is off — no need for it to stay live independently of it.
      final boolean debugOn = _chckbxDebugMode.isSelected();
      _chckbxDebugMode.setEnabled(true);

      if (_debugLogPanel.isVisible() != debugOn) {
        _debugLogPanel.setVisible(debugOn);
        _llmOptionsPanel.revalidate();
        _llmOptionsPanel.repaint();
      }

      refreshDebugLogPath();
    }
  }
}
