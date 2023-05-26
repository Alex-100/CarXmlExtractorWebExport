package pro.dralex.CarXmlExtractorWeb.back.fileLoader;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileLoader {

    private final ConcurrentHashMap<String, FileProgressStatus> progressBars = new ConcurrentHashMap<>();
    private final int numberOfThreads;
    private final ExecutorService executorService;
    public static final String avFileName = "avito.xml";
    public  static final String auFileName = "autoru.xml";
    public  static final String drFileName = "drom.xml";


    public FileLoader(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        progressBars.put(avFileName, new FileProgressStatus(0L, 1L, false));
        progressBars.put(auFileName, new FileProgressStatus(0L, 1L, false));
        progressBars.put(drFileName, new FileProgressStatus(0L, 1L, false));
    }

    private class Downloader implements Runnable {
        private final URL url;
        private final String fileName;

        public Downloader(URL url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        private long getUrlFileSize() throws IOException {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(30 * 1000);
                conn.setRequestMethod("GET");
                return conn.getContentLengthLong();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        private void getFile(long fileSize) throws IOException {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            try (
                    BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    FileOutputStream out = new FileOutputStream(fileName)
            ) {
                final byte[] dataBuffer = new byte[1024];
                int bytesRead;
                long totalBytes = 0;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    if(Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    totalBytes += 1024;
                    progressBars.put(fileName, new FileProgressStatus(totalBytes, fileSize, false));
                    out.write(dataBuffer, 0, bytesRead);
                }
                progressBars.put(fileName, new FileProgressStatus(totalBytes, fileSize, true));
            }
        }

        @Override
        public void run() {
            try {
                getFile(getUrlFileSize());
            } catch (Exception e) {
                e.printStackTrace();
                progressBars.put(fileName, new FileProgressStatus(e.getMessage()));
            }
        }
    }
    public void loadFiles(String auUrl, String avUrl, String drUrl) throws IOException {
//        final String auUrl = "https://auto-export.s3.yandex.net/auto/price-list/catalog/cars.xml";
//        final String avUrl = "https://autoload.avito.ru/format/Autocatalog.xml";
//        final String drUrl = "https://www.drom.ru/cached_files/autoload/files/ref.xml";
//        executorService = Executors.newFixedThreadPool(3);
        Downloader task1 = new Downloader(new URL(auUrl), auFileName);
        Downloader task2 = new Downloader(new URL(avUrl), avFileName);
        Downloader task3 = new Downloader(new URL(drUrl), drFileName);
        executorService.execute(task1);
        executorService.execute(task2);
        executorService.execute(task3);

    }

    public boolean isAllFinished() {
        return progressBars.values().stream().filter(FileProgressStatus::isFinished).count() == numberOfThreads;
    }

    public boolean isConnectedStarted() {
        return progressBars.values().stream().filter(FileProgressStatus::hasBytesTotal).count() == numberOfThreads;
    }
    public boolean isDownloadStarted() {
        return progressBars.values().stream().filter(FileProgressStatus::hasBytesDownloaded).count() == numberOfThreads;
    }

    public Map<String, FileProgressStatus> getProgress(){
        return new HashMap<>(progressBars);
    }

    public boolean hasErrors(){
        return progressBars.values().stream().filter(FileProgressStatus::isHasNoError).count() != numberOfThreads;
    }

    public String getErrorMessage(){
        return progressBars.values().stream()
                .filter(fileProgressStatus -> !fileProgressStatus.isHasNoError())
                .toList()
                .stream()
                .map(FileProgressStatus::getErrorMessage)
                .findFirst()
                .orElse("Unknown error");
    }

    public void cancelDownload(){
        executorService.shutdownNow();
    }

}
