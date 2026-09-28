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
  private JComboBox<LlmProvider> _cmbLlmProvider;
  private JPasswordField _txtLlmApiKey;
  private JTextField _txtLlmBaseUrl;
  private JComboBox<String> _cmbLlmModel;
  private JButton _btnLoadModels;
  private JLabel _lblModelsStatus;
  private JComboBox<SearchMode> _cmbSearchMode;
  private final Map<SearchProvider, JCheckBox> _searchProviderBoxes = new LinkedHashMap<>();
  private final Map<SearchProvider, JTextField> _searchCredentialFields = new LinkedHashMap<>();
  private JButton _btnTestSearch;
  private JButton _btnTestLlm;
  private JLabel _lblLlmTestStatus;
  private JLabel _lblSearchTestStatus;
  private JLabel _lblDocsTestStatus;

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
  private static final String SEARCH_DISABLED_HINT =
      "Web research is disabled — sources will be unverified.";

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
    _llmPanel.setBorder(BorderFactory.createTitledBorder("LLM API Settings"));
    // left-aligned and sized to its own content, like every other group on this screen: nothing
    // here has a column with a positive weight, so nothing is stretched to fill the dialog's width
    _optionsPanel.add(_llmPanel, leftAlignedRow(3));
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
    gbcEnableLlm.anchor = GridBagConstraints.WEST;
    gbcEnableLlm.insets = cell();
    gbcEnableLlm.gridx = 0;
    gbcEnableLlm.gridy = 0;
    gbcEnableLlm.gridwidth = 2;
    _llmPanel.add(_chckbxEnableLlm, gbcEnableLlm);

    // everything else lives in here, so the whole group can be shown or hidden as one with the
    // checkbox above, instead of every field having to be walked and toggled by hand
    _llmOptionsPanel = new JPanel(new GridBagLayout());
    // no weight on either column: every row's value cell keeps to its column's own widest
    // component (typically the Model or Web research row) instead of growing into empty dialog
    // space, which is what let the API key and URL fields balloon whenever the dialog was wide
    ((GridBagLayout) _llmOptionsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0};

    final GridBagConstraints gbcOptions = new GridBagConstraints();
    gbcOptions.anchor = GridBagConstraints.WEST;
    gbcOptions.gridx = 0;
    gbcOptions.gridy = 1;
    gbcOptions.gridwidth = 2;
    _llmPanel.add(_llmOptionsPanel, gbcOptions);

    int llmRow = 0;

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
    addFormRow(_llmOptionsPanel, llmRow++, "Provider:", _cmbLlmProvider, false);

    _txtLlmApiKey = new JPasswordField();
    _txtLlmApiKey.setToolTipText(
        "The provider's API key, sent as \"Authorization: Bearer ...\". Not needed for a local"
            + " provider such as Ollama or LM Studio. Stored in this workspace's own"
            + " configuration file, not synced anywhere.");
    addFormRow(_llmOptionsPanel, llmRow++, "API Key:", _txtLlmApiKey, true);

    _txtLlmBaseUrl = new JTextField();
    _txtLlmBaseUrl.setToolTipText(
        "The endpoint this provider's chat completions are called at. Filled in for every"
            + " provider above except \"Other\", which needs it typed here.");
    addFormRow(_llmOptionsPanel, llmRow++, "Base URL:", _txtLlmBaseUrl, true);

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
    gbcModelCombo.fill = GridBagConstraints.HORIZONTAL;
    gbcModelCombo.weightx = 1.0;
    gbcModelCombo.insets = new Insets(0, 0, 0, GAP);
    gbcModelCombo.gridx = 0;
    modelRow.add(_cmbLlmModel, gbcModelCombo);
    final GridBagConstraints gbcLoadModels = new GridBagConstraints();
    gbcLoadModels.insets = new Insets(0, 0, 0, GAP);
    gbcLoadModels.gridx = 1;
    modelRow.add(_btnLoadModels, gbcLoadModels);
    final GridBagConstraints gbcModelsStatus = new GridBagConstraints();
    gbcModelsStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcModelsStatus.weightx = 1.0;
    gbcModelsStatus.gridx = 2;
    modelRow.add(_lblModelsStatus, gbcModelsStatus);
    addFormRow(_llmOptionsPanel, llmRow++, "Model:", modelRow, true);

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
    addFormRow(_llmOptionsPanel, llmRow++, "", statusRow(_btnTestLlm, _lblLlmTestStatus), true);

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
    gbcMode.anchor = GridBagConstraints.WEST;
    gbcMode.insets = new Insets(0, 0, 0, GAP);
    gbcMode.gridx = 0;
    webResearchRow.add(_cmbSearchMode, gbcMode);
    final GridBagConstraints gbcTestSearch = new GridBagConstraints();
    gbcTestSearch.insets = new Insets(0, 0, 0, GAP);
    gbcTestSearch.gridx = 1;
    webResearchRow.add(_btnTestSearch, gbcTestSearch);
    final GridBagConstraints gbcSearchStatus = new GridBagConstraints();
    gbcSearchStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcSearchStatus.weightx = 1.0;
    gbcSearchStatus.gridx = 2;
    webResearchRow.add(_lblSearchTestStatus, gbcSearchStatus);
    addFormRow(_llmOptionsPanel, llmRow++, "Web research:", webResearchRow, true);

    // the groups below use the whole width of this one, a gap apart
    _searchProvidersGroup = buildSearchProvidersPanel();
    addGroup(_llmOptionsPanel, llmRow++, _searchProvidersGroup);
    addGroup(_llmOptionsPanel, llmRow++, buildPromptsPanel());
    addGroup(_llmOptionsPanel, llmRow++, buildPacingPanel());
    addGroup(_llmOptionsPanel, llmRow++, buildDocumentsPanel());
    addGroup(_llmOptionsPanel, llmRow++, buildDebugPanel());

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

  /** Insets of a cell that belongs to the checkbox above it, lined up under that checkbox's text. */
  private static Insets indentedCell() {
    return new Insets(GAP / 2, GAP / 2 + 18, GAP / 2, GAP / 2);
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
    gbcButton.insets = new Insets(0, 0, 0, GAP);
    gbcButton.gridx = 0;
    row.add(button, gbcButton);

    final GridBagConstraints gbcStatus = new GridBagConstraints();
    gbcStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcStatus.weightx = 1.0;
    gbcStatus.gridx = 1;
    row.add(status, gbcStatus);

    return row;
  }

  /** One row of a group: a label, then its component — stretched to the width, or as wide as it is. */
  private static void addFormRow(
      JPanel panel, int row, String label, JComponent component, boolean stretch) {
    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.WEST;
    gbcLabel.insets = cell();
    gbcLabel.gridx = 0;
    gbcLabel.gridy = row;
    panel.add(new JLabel(label), gbcLabel);

    final GridBagConstraints gbcComponent = new GridBagConstraints();
    gbcComponent.anchor = GridBagConstraints.WEST;
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
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(GAP, 0, 0, 0);
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    panel.add(group, gbc);
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
      gbcBox.anchor = GridBagConstraints.WEST;
      gbcBox.insets = cell();
      gbcBox.gridx = 0;
      gbcBox.gridy = row;
      panel.add(box, gbcBox);

      final JLabel credentialLabel = new JLabel(provider.getCredentialLabel() + ":");
      final GridBagConstraints gbcLabel = new GridBagConstraints();
      gbcLabel.anchor = GridBagConstraints.WEST;
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
   * The texts sent to the model, in a group of their own like every other area of these settings:
   * the button opens the editor for the classification prompts, the decision tree and the
   * documentation template and prompt.
   */
  private JPanel buildPromptsPanel() {
    final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
    panel.setBorder(BorderFactory.createTitledBorder("Prompts and decision tree"));

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

    panel.add(button);
    panel.add(hint);

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
    gbcSpinner.anchor = GridBagConstraints.WEST;
    gbcSpinner.insets = cell();
    gbcSpinner.gridx = 1;
    gbcSpinner.gridy = 0;
    panel.add(_spnDelaySeconds, gbcSpinner);

    final GridBagConstraints gbcUnit = new GridBagConstraints();
    gbcUnit.anchor = GridBagConstraints.WEST;
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
    final JLabel note = newStatusLabel();
    note.setText(noteText);
    note.setToolTipText(noteText);
    note.setForeground(Color.GRAY);

    final GridBagConstraints gbcNote = new GridBagConstraints();
    gbcNote.anchor = GridBagConstraints.WEST;
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
    gbcBox.insets = cell();
    gbcBox.gridx = 0;
    gbcBox.gridy = 0;
    gbcBox.gridwidth = 3;
    panel.add(_chckbxDebugMode, gbcBox);

    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.WEST;
    gbcLabel.insets = indentedCell();
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
    gbcField.insets = cell();
    gbcField.gridx = 1;
    gbcField.gridy = 1;
    gbcField.gridwidth = 2;
    panel.add(_txtDebugLogDirectory, gbcField);

    _lblDebugLogPath = newStatusLabel();
    _lblDebugLogPath.setForeground(Color.GRAY);

    final GridBagConstraints gbcPath = new GridBagConstraints();
    gbcPath.anchor = GridBagConstraints.WEST;
    gbcPath.fill = GridBagConstraints.HORIZONTAL;
    gbcPath.weightx = 1.0;
    gbcPath.insets = indentedCell();
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
    gbcLocal.insets = cell();
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
    gbcDocling.insets = cell();
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

    _lblDocsTestStatus = newStatusLabel();

    final GridBagConstraints gbcStatus = new GridBagConstraints();
    gbcStatus.anchor = GridBagConstraints.WEST;
    gbcStatus.fill = GridBagConstraints.HORIZONTAL;
    gbcStatus.weightx = 1.0;
    gbcStatus.insets = indentedCell();
    gbcStatus.gridx = 0;
    gbcStatus.gridy = 4;
    gbcStatus.gridwidth = 3;
    panel.add(_lblDocsTestStatus, gbcStatus);

    return panel;
  }

  /** A label, the path field and its Test button — the same shape for both converters. */
  private JButton addConverterRow(
      JPanel panel, int row, String label, JTextField field, String tooltip, ActionListener test) {
    final GridBagConstraints gbcLabel = new GridBagConstraints();
    gbcLabel.anchor = GridBagConstraints.WEST;
    gbcLabel.insets = indentedCell();
    gbcLabel.gridx = 0;
    gbcLabel.gridy = row;
    panel.add(new JLabel(label), gbcLabel);

    field.setToolTipText(tooltip);

    final GridBagConstraints gbcField = new GridBagConstraints();
    gbcField.fill = GridBagConstraints.HORIZONTAL;
    gbcField.weightx = 1.0;
    gbcField.insets = cell();
    gbcField.gridx = 1;
    gbcField.gridy = row;
    panel.add(field, gbcField);

    final JButton button = new JButton("Test");
    button.setToolTipText("Runs the converter on a small file to prove it works.");
    button.addActionListener(test);

    final GridBagConstraints gbcButton = new GridBagConstraints();
    gbcButton.anchor = GridBagConstraints.WEST;
    gbcButton.insets = cell();
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
    setStatus(_lblDocsTestStatus, Color.GRAY, "Testing " + name + "...");

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
        setStatus(
            _lblDocsTestStatus,
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

    if (!llmEnabled) {
      // nothing below reads while the integration is off
      return;
    }

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
    _cmbLlmModel.setEnabled(true);
    _btnLoadModels.setEnabled(true);

    // the search provider and its key only mean anything when the plugin does the searching
    final boolean usesSearchApi = selectedSearchMode() == SearchMode.SEARCH_API;
    _cmbSearchMode.setEnabled(true);

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
