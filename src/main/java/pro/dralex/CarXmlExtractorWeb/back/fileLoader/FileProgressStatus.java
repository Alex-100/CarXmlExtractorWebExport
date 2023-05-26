package pro.dralex.CarXmlExtractorWeb.back.fileLoader;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.util.Precision;

@Data
@AllArgsConstructor
public class FileProgressStatus {
    private Long bytesDownloaded;
    private Long bytesTotal;

    private boolean hasFinished = false;
    private boolean hasNoError;
    private String errorMessage;

    public FileProgressStatus(Long bytesDownloaded, Long bytesTotal, boolean hasFinished) {
        if(bytesDownloaded >= bytesTotal) {
            this.bytesDownloaded = bytesTotal;
        } else {
            this.bytesDownloaded = bytesDownloaded;
        }
        this.bytesTotal = bytesTotal;
        this.hasNoError = true;
        this.errorMessage = "";
        this.hasFinished = hasFinished;
    }
    public FileProgressStatus(String errorMessage) {
        this.bytesDownloaded = 0L;
        this.bytesTotal = 1L;
        this.hasNoError = false;
        this.errorMessage = errorMessage;
        this.hasFinished = false;
    }

    public boolean hasBytesTotal(){
        return bytesTotal != null;
    }
    public boolean hasBytesDownloaded(){
        return bytesDownloaded != null;
    }
    public boolean isFinished(){
        return hasFinished;
    }

    public double getBytesDownloadedPercent(){
        double percentTmp = (double)bytesDownloaded/bytesTotal;
        double percent = Precision.round(percentTmp, 2);
        return Math.min(percent, 1.0);
    }

}
