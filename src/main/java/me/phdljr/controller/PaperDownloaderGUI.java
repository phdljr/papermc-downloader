package me.phdljr.controller;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import me.phdljr.api.PaperApiClient;
import me.phdljr.service.PaperDownloader;

public class PaperDownloaderGUI extends JFrame {

    private final PaperApiClient apiClient = new PaperApiClient();
    private final PaperDownloader downloader = new PaperDownloader();

    private final JComboBox<String> versionBox = new JComboBox<>();
    private final JComboBox<String> buildBox = new JComboBox<>();
    private final JButton downloadButton = new JButton("다운로드");
    private final JTextArea logArea = new JTextArea(10, 40);
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public PaperDownloaderGUI() {
        super("PaperMC Downloader");

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("버전:"));
        topPanel.add(versionBox);
        topPanel.add(new JLabel("빌드:"));
        topPanel.add(buildBox);
        topPanel.add(downloadButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);

        logArea.setEditable(false);
        progressBar.setStringPainted(true);

        loadVersionsAsync();

        versionBox.addActionListener(e -> loadBuildsAsync((String) versionBox.getSelectedItem()));
        downloadButton.addActionListener(e -> downloadJar());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void loadVersionsAsync() {
        new SwingWorker<List<String>, String>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                publish("버전 목록 불러오는 중...");
                return apiClient.getVersions();
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
                return apiClient.getBuilds(version);
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

        String jarName = apiClient.getJarName(version, build);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("다운로드 위치 선택");
        chooser.setSelectedFile(new File(jarName));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            log("다운로드 취소됨");
            return;
        }
        File outFile = chooser.getSelectedFile();

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                String urlStr = apiClient.getDownloadUrl(version, build);
                progressBar.setValue(0);
                publish("다운로드 시작: " + urlStr);
                downloader.download(urlStr, outFile);
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

