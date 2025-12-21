package me.phdljr.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.AbstractDocument;
import me.phdljr.api.PaperApiClient;
import me.phdljr.application.port.in.DownloadPaperCommand;
import me.phdljr.application.port.in.DownloadPaperUseCase;
import me.phdljr.application.port.in.QueryBuildsUseCase;
import me.phdljr.application.port.in.QueryJarNameUseCase;
import me.phdljr.application.port.in.QueryVersionsUseCase;
import me.phdljr.application.service.PaperDownloadService;
import me.phdljr.service.PaperDownloader;
import me.phdljr.service.StartFileCreator;

public class PaperDownloaderGUI extends JFrame {

    private final QueryVersionsUseCase versionsUseCase;
    private final QueryBuildsUseCase buildsUseCase;
    private final QueryJarNameUseCase jarNameUseCase;
    private final DownloadPaperUseCase downloadUseCase;

    private final JComboBox<String> versionBox = new JComboBox<>();
    private final JComboBox<String> buildBox = new JComboBox<>();
    private final JButton downloadButton = new JButton("다운로드");
    private final JTextArea logArea = new JTextArea(10, 40);
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JCheckBox startFileCreateCheckBox = new JCheckBox("실행 파일 생성");
    private final JTextField memoryTextField = new JTextField("4");
    private final Font baseFont = UIManager.getFont("Label.font");

    public PaperDownloaderGUI() {
        this(buildDefaultService());
    }

    public PaperDownloaderGUI(PaperDownloadService service) {
        super("PaperMC Downloader");
        this.versionsUseCase = service;
        this.buildsUseCase = service;
        this.jarNameUseCase = service;
        this.downloadUseCase = service;

        setLayout(new BorderLayout());
        setContentPane(buildContent());

        loadVersionsAsync();

        versionBox.addActionListener(e -> loadBuildsAsync((String) versionBox.getSelectedItem()));
        downloadButton.addActionListener(e -> downloadJar());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static PaperDownloadService buildDefaultService() {
        PaperApiClient apiClient = new PaperApiClient();
        PaperDownloader downloader = new PaperDownloader();
        StartFileCreator startFileCreator = new StartFileCreator();
        return new PaperDownloadService(apiClient, downloader, startFileCreator);
    }

    private JPanel buildContent() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(20, 24, 28));
        container.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("PaperMC Downloader");
        title.setForeground(new Color(230, 235, 241));
        title.setFont(primaryFont(20f, Font.BOLD));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        container.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(32, 37, 42));
        formPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        styleComboBox(versionBox);
        styleComboBox(buildBox);
        startFileCreateCheckBox.setBackground(formPanel.getBackground());
        startFileCreateCheckBox.setForeground(new Color(220, 226, 232));
        startFileCreateCheckBox.setFocusPainted(false);
        startFileCreateCheckBox.setSelected(true);

        ((AbstractDocument) memoryTextField.getDocument()).setDocumentFilter(new OnlyNumberFilter());
        memoryTextField.setColumns(3);
        memoryTextField.setFont(primaryFont(13f, Font.PLAIN));
        memoryTextField.setBorder(new MatteBorder(0, 0, 2, 0, new Color(65, 110, 255)));
        memoryTextField.setBackground(new Color(43, 48, 54));
        memoryTextField.setForeground(Color.WHITE);
        memoryTextField.setCaretColor(Color.WHITE);
        memoryTextField.setHorizontalAlignment(JTextField.CENTER);

        addField(formPanel, gbc, 0, "버전", versionBox);
        addField(formPanel, gbc, 1, "빌드", buildBox);
        addField(formPanel, gbc, 2, "메모리(GB)", memoryTextField);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(startFileCreateCheckBox, gbc);

        stylePrimaryButton(downloadButton);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        formPanel.add(downloadButton, gbc);

        logArea.setEditable(false);
        logArea.setFont(primaryFont(12f, Font.PLAIN));
        logArea.setBackground(new Color(18, 20, 24));
        logArea.setForeground(new Color(191, 198, 207));
        logArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new MatteBorder(1, 0, 0, 0, new Color(50, 55, 62)));
        scrollPane.getViewport().setBackground(logArea.getBackground());

        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 26));
        progressBar.setBackground(new Color(36, 40, 46));
        progressBar.setForeground(new Color(93, 174, 255));
        progressBar.setBorder(new MatteBorder(0, 0, 0, 0, container.getBackground()));

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(container.getBackground());
        center.add(formPanel, BorderLayout.NORTH);
        center.add(scrollPane, BorderLayout.CENTER);

        container.add(center, BorderLayout.CENTER);
        container.add(progressBar, BorderLayout.SOUTH);
        return container;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String labelText, java.awt.Component input) {
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(200, 206, 215));
        label.setFont(primaryFont(13f, Font.PLAIN));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.2;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        panel.add(input, gbc);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(new Color(43, 48, 54));
        comboBox.setForeground(Color.WHITE);
        comboBox.setFont(primaryFont(13f, Font.PLAIN));
        comboBox.setBorder(new MatteBorder(0, 0, 2, 0, new Color(65, 110, 255)));
        comboBox.setPreferredSize(new Dimension(120, 28));
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(new Color(65, 110, 255));
        button.setForeground(Color.WHITE);
        button.setFont(primaryFont(14f, Font.BOLD));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(110, 36));
        button.setBorder(new EmptyBorder(8, 14, 8, 14));
    }

    private Font primaryFont(float size, int style) {
        Font fallback = new Font("Segoe UI", style, Math.round(size));
        if (baseFont == null) {
            return fallback;
        }
        return baseFont.deriveFont(style, size);
    }

    private void loadVersionsAsync() {
        new SwingWorker<List<String>, String>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                publish("버전 목록 불러오는 중...");
                return versionsUseCase.getVersions();
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> log(msg));
            }

            @Override
            protected void done() {
                try {
                    List<String> versions = get();
                    versionBox.removeAllItems();
                    for (String version: versions) {
                        versionBox.addItem(version);
                    }

                    log("버전 목록 불러오기 완료");
                } catch (Exception ex) {
                    log("버전 불러오기 실패: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadBuildsAsync(String version) {
        new SwingWorker<List<String>, String>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                publish(version + "빌드 목록 불러오는 중...");
                return buildsUseCase.getBuilds(version);
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> log(msg));
            }

            @Override
            protected void done() {
                try {
                    List<String> builds = get();
                    buildBox.removeAllItems();
                    for (String build: builds) {
                        buildBox.addItem(build);
                    }

                    log(version + "빌드 목록 불러오기 완료");
                } catch (Exception ex) {
                    log(version + "빌드 불러오기 실패: " + ex.getMessage());
                }
            }
        }.execute();
    }


    private void downloadJar() {
        String version = (String) versionBox.getSelectedItem();
        String build = (String) buildBox.getSelectedItem();
        if (version == null || build == null) {
            log("버전과 빌드를 선택하세요.");
            return;
        }

        String jarName = jarNameUseCase.getJarName(version, build);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("다운로드 위치 선택");
        chooser.setSelectedFile(new File(jarName));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            log("다운로드 취소됨");
            return;
        }

        File outFile = chooser.getSelectedFile();
        downloadJarFile(version, build, outFile);
    }

    private void downloadJarFile(String version, String build, File outFile) {
        boolean createStartFile = startFileCreateCheckBox.isSelected();
        String memory = memoryTextField.getText();
        if (memory.isEmpty()) {
            memory = "2";
        }
        String finalMemory = memory;
        boolean finalCreateStartFile = createStartFile;
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                progressBar.setValue(0);
                publish("다운로드 준비 중...");
                downloadUseCase.download(
                    new DownloadPaperCommand(version, build, outFile, finalMemory, finalCreateStartFile));
                SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(true));
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(msg -> log(msg));
            }

            @Override
            protected void done() {
                try {
                    get();
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    log("다운로드 완료: " + outFile.getAbsolutePath());
                    if (finalCreateStartFile) {
                        log("실행 파일 생성 완료 (start.bat)");
                    }
                    log("서버 실행 예시: java -Xms1G -Xmx2G -jar " + outFile.getName() + " nogui");
                } catch (Exception ex) {
                    log("다운로드 실패: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }
}
