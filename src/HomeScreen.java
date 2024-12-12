import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class HomeScreen extends JFrame {
    private JPanel mainPanel;
    private JLabel urlLabel;
    private JTextField urlBox;

    private JPanel optionsPanel;
    private CollapsiblePanel advOptionsPanel;

    private JComboBox<String> outputType, outputExtension, outputQuality;
    private JLabel outputTypeLabel, outputExtensionLabel, outputQualityLabel;

    private JPanel outputPanel;
    private JLabel fileLabel;
    private JTextField fileField;
    private JButton browseButton;
    private JFileChooser fileChooser;
    private File currentFile;

    private JButton downloadButton;
    private JProgressBar downloadProgress;

    HomeScreen() {
        super("youtube-dl-gui");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(500, 300));
        initHomeScreen();
    }

    private void initHomeScreen() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("fill"));
        this.add(mainPanel);

        initializeUrlBox();

        initializeOptionsBox();

        initializeOutputBox();

        initializeDownloadBox();

        setTooltips();

        setLayouts();

        //Start in state of disabled options
        disableOptions(true);
    }

    private void setTooltips() {
        urlBox.setToolTipText("Paste any youtube.com url.\n" +
                "Example: https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        outputType.setToolTipText("Download either as a video file (TV), or an audio file (Music).");
        outputExtension.setToolTipText("Set the extension you prefer. If unsure, do not change this option.");
        outputQuality.setToolTipText("Select the output quality, or resolution, you prefer.\n" +
                "Best: Highest quality (likely 1080p) for selected video on YouTube\n" +
                "Worst: Lowest quality (likely 480p) for selected video on Youtube");
        browseButton.setToolTipText("Select the location where the video or audio file will be downloaded to.");
    }

    private void setLayouts() {
        // Options Panel Mig Layout
        optionsPanel.add(outputTypeLabel, "cell 0 0");
        optionsPanel.add(outputType, "cell 1 0");
        optionsPanel.add(advOptionsPanel, "cell 0 1, spanx 2, grow");
        advOptionsPanel.getContentPane().add(outputExtensionLabel, "cell 0 0");
        advOptionsPanel.getContentPane().add(outputExtension, "cell 1 0");
        advOptionsPanel.getContentPane().add(outputQualityLabel, "cell 2 0");
        advOptionsPanel.getContentPane().add(outputQuality, "cell 3 0");

        // Output Panel Mig Layout
        outputPanel.add(fileLabel, "cell 0 0");
        outputPanel.add(fileField, "cell 1 0");
        outputPanel.add(browseButton, "cell 2 0");

        // Main Panel Mig Layout
        mainPanel.add(urlLabel, "cell 0 0");
        mainPanel.add(urlBox, "cell 1 0, growx, pushx");

        mainPanel.add(optionsPanel, "cell 0 1, spanx 2, grow");
        mainPanel.add(outputPanel, "cell 0 2, spanx 2, grow");
        mainPanel.add(downloadButton, "cell 1 3, align right");
        mainPanel.add(downloadProgress, "cell 0 3, spanx 2, growx");
    }

    private void initializeDownloadBox() {
        downloadButton = new JButton("Download");

        downloadButton.addActionListener(e -> {
            initializeDownload();
        });

        downloadProgress = new JProgressBar();
        downloadProgress.setStringPainted(true);
        downloadProgress.setVisible(false);
    }

    private void initializeOutputBox() {
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
    }

    private void initializeOptionsBox() {
        // Options panels
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new MigLayout());
        optionsPanel.setBorder(new TitledBorder("Options"));

        advOptionsPanel = new CollapsiblePanel("Adv. Options");

        // Options Panel Buttons
        outputTypeLabel = new JLabel("Output Type:");
        outputType =  new JComboBox<>(new String[]{"Video", "Audio"});
        outputQualityLabel = new JLabel("Quality:");
        outputQuality =  new JComboBox<>(new String[]{"best", "worst"});
        outputExtensionLabel = new JLabel("Extension:");
        outputExtension  =  new JComboBox<>(new String[]{"mp4", "webm"});

        outputType.addActionListener(e -> {
            String selection = (String) outputType.getSelectedItem();
            outputExtension.removeAllItems();

            if ("Video".equals(selection)) {
                outputExtension.addItem("mp4");
                outputExtension.addItem("webm");
                outputQualityLabel.setVisible(true);
                outputQuality.setVisible(true);
            } else if ("Audio".equals(selection)) {
                outputExtension.addItem("m4a");
                outputExtension.addItem("mp3");
                outputQualityLabel.setVisible(false);
                outputQuality.setVisible(false);
            }
        });

    }

    private void initializeUrlBox() {
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
    }

    private void initializeDownload() {
        downloadProgress.setValue(0);
        inDownloadProgress(true);

        YoutubedlExecutor executor = new YoutubedlExecutor(currentFile, assembleCommand());
        executor.addPropertyChangeListener(new ExecutorListener(downloadProgress, this));
        executor.execute();
    }

    public ArrayList<String> assembleCommand() {
        ArrayList<String> command = new ArrayList<>();
        String url = urlBox.getText();

        File ytdlpath = new File("youtube-dl/yt-dlp.exe");
        command.add(ytdlpath.getAbsolutePath());

        String outputType = (String) this.outputType.getSelectedItem();
        String outputExtension = (String) this.outputExtension.getSelectedItem();
        String outputRes = (String) this.outputQuality.getSelectedItem();

        command.add("youtube-dl/yt-dlp.exe");

        if (outputType.equals("Video")) {
            if (outputRes.equals("best")) {
                command.add("-f");
                command.add(String.format("bestvideo[ext=%s]+bestaudio[ext=m4a]", outputExtension));
            } else if (outputRes.equals("worst")) {
                command.add("-f");
                command.add(String.format("worstvideo[ext=%s]+worstaudio[ext=m4a]", outputExtension));
            }
        } else if (outputType.equals("Audio")) {
            command.add("--extract-audio");
            command.add("--audio-quality");
            command.add("best");
            command.add("-f");
            command.add(String.format("bestaudio[ext=%s]/bestaudio", outputExtension));
        }

        command.add(url);

        return command;
    }

    public void inDownloadProgress(boolean inProg) {
        if(inProg) {
            urlBox.setEnabled(false);
            disableOptions(true);
            downloadButton.setVisible(false);
            downloadProgress.setVisible(true);
        } else {
            urlBox.setEnabled(true);
            disableOptions(false);
            downloadButton.setVisible(true);
            downloadProgress.setVisible(false);

        }
    }

    public void disableOptions(boolean disable) {
        if(disable) {
            outputExtension.setEnabled(false);
            outputType.setEnabled(false);
            outputQuality.setEnabled(false);
            fileField.setEnabled(false);
            browseButton.setEnabled(false);
            downloadButton.setEnabled(false);
        } else {
            outputExtension.setEnabled(true);
            outputType.setEnabled(true);
            outputQuality.setEnabled(true);
            fileField.setEnabled(true);
            browseButton.setEnabled(true);
            downloadButton.setEnabled(true);
        }

    }
}
