import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class HomeScreen extends JFrame {
    private JPanel mainPanel;
    private JLabel urlLabel;
    private JTextField urlBox;

    private JPanel optionsPanel;
    private JComboBox<String> outputType, outputExtension, outputRes;
    private JLabel outputTypeLabel, outputExtensionLabel, outputResLabel;

    private JPanel outputPanel;
    private JLabel fileLabel;
    private JTextField fileField;
    private JButton browseButton;
    private JFileChooser fileChooser;
    private File currentFile;

    private JPanel downloadPanel;
    private JButton downloadButton;
    private JProgressBar downloadProgress;

    HomeScreen() {
        super("youtube-dl-gui");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(640, 280));
        initHomeScreen();
    }

    private void initHomeScreen() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("fill"));
        this.add(mainPanel);

        // Url Label
        urlLabel = new JLabel("Video URL:");

        // Url Box
        urlBox = new JTextField();

        urlBox.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toggleDownloadButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toggleDownloadButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toggleDownloadButton();
            }

            private void toggleDownloadButton() {
                disableOptions(urlBox.getText().isEmpty());
            }
        });

        // Options panel
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new MigLayout("nogrid"));
        optionsPanel.setBorder(new TitledBorder("Options"));

        // Options Panel Buttons
        outputTypeLabel = new JLabel("Output Type:");
        outputType =  new JComboBox<>(new String[]{"Video", "Audio"});
        outputResLabel = new JLabel("Resolution:");
        outputRes =  new JComboBox<>(new String[]{"1080p", "720p"});
        outputExtensionLabel = new JLabel("Extension:");
        outputExtension  =  new JComboBox<>(new String[]{".Mp4", ".Mov"});

        outputType.addActionListener(e -> {
            String selection = (String) outputType.getSelectedItem();
            outputExtension.removeAllItems();

            if ("Video".equals(selection)) {
                outputExtension.addItem(".Mp4");
                outputExtension.addItem(".Mov");
                outputResLabel.setVisible(true);
                outputRes.setVisible(true);
            } else if ("Audio".equals(selection)) {
                outputExtension.addItem(".Mp3");
                outputExtension.addItem(".wav");
                outputResLabel.setVisible(false);
                outputRes.setVisible(false);
            }
        });

        // Output Panel
        outputPanel = new JPanel();
        outputPanel.setLayout(new MigLayout("nogrid"));
        outputPanel.setBorder(new TitledBorder("Output"));

        // Init default file location
        String home = System.getProperty("user.home");
        currentFile = new File(home+"/Downloads/");

        //File Browse Label
        fileLabel = new JLabel("Directory: ");

        //File Browse Field

        //TODO: Handle Errors
        fileField =  new JTextField(currentFile.getAbsolutePath());

        // File Browse
        browseButton =  new JButton("Browse...");
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setSelectedFile(currentFile);

        browseButton.addActionListener(e -> {
            int returnVal = fileChooser.showDialog(mainPanel, "Choose Directory");

            if(returnVal ==  JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                fileField.setText(currentFile.getAbsolutePath());
            }
        });

        // Download Panel
        downloadPanel = new JPanel();
        downloadPanel.setLayout(new MigLayout("nogrid"));
        downloadPanel.setBorder(new TitledBorder("Download"));

        downloadButton = new JButton("Download");

        downloadButton.addActionListener(e -> {
            initializeDownload();
        });

        downloadProgress = new JProgressBar();
        downloadProgress.setStringPainted(true);
        downloadProgress.setVisible(false);

        //Start in state of disabled Options
        disableOptions(true);

        // Options Panel Mig Layout
        optionsPanel.add(outputTypeLabel, "cell 0 0");
        optionsPanel.add(outputType, "cell 1 0");
        optionsPanel.add(outputExtensionLabel, "cell 2 0");
        optionsPanel.add(outputExtension, "cell 3 0");
        optionsPanel.add(outputResLabel, "cell 4 0");
        optionsPanel.add(outputRes, "cell 5 0");

        // Output Panel Mig Layout
        outputPanel.add(fileLabel, "cell 0 0");
        outputPanel.add(fileField, "cell 1 0");
        outputPanel.add(browseButton, "cell 2 0");


        // Download Panel Mig Layout
        downloadPanel.add(downloadButton, "cell 0 0");
        downloadPanel.add(downloadProgress, "cell 1 0, growx, pushx");

        // Main Panel Mig Layout
        mainPanel.add(urlLabel, "cell 0 0");
        mainPanel.add(urlBox, "cell 1 0, growx, pushx");

        mainPanel.add(optionsPanel, "cell 0 1, spanx 2, grow");
        mainPanel.add(outputPanel, "cell 0 2, spanx 2, grow");
        mainPanel.add(downloadPanel, "cell 0 3, spanx 2, grow");
    }

    private void initializeDownload() {
        downloadProgress.setValue(0);
        inDownloadProgress(true);
        YoutubedlExecutor executor = new YoutubedlExecutor(currentFile, urlBox.getText());
        executor.addPropertyChangeListener(new ExecutorListener(downloadProgress, this));
        executor.execute();
    }

    public void inDownloadProgress(boolean inProg) {
        if(inProg) {
            urlBox.setEnabled(false);
            disableOptions(true);
            downloadProgress.setVisible(true);
        } else {
            urlBox.setEnabled(true);
            disableOptions(false);
            downloadProgress.setVisible(false);

        }
    }

    public void disableOptions(boolean disable) {
        if(disable) {
            outputExtension.setEnabled(false);
            outputType.setEnabled(false);
            outputRes.setEnabled(false);
            fileField.setEnabled(false);
            browseButton.setEnabled(false);
            downloadButton.setEnabled(false);
        } else {
            outputExtension.setEnabled(true);
            outputType.setEnabled(true);
            outputRes.setEnabled(true);
            fileField.setEnabled(true);
            browseButton.setEnabled(true);
            downloadButton.setEnabled(true);
        }

    }
}
