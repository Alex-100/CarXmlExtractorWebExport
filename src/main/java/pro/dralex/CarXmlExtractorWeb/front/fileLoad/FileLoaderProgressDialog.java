package pro.dralex.CarXmlExtractorWeb.front.fileLoad;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.extern.slf4j.Slf4j;
import pro.dralex.CarXmlExtractorWeb.back.fileLoader.FileLoader;
import pro.dralex.CarXmlExtractorWeb.back.fileLoader.FileProgressStatus;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileLoaderProgressDialog extends Dialog {

    private final FileLoader fileLoader;
    private Thread thread = null;
    private String avLabelText = "";
    private String auLabelText = "";
    private String drLabelText = "";

    private TextField messageFiled;
    private HorizontalLayout messageLayout;

    private Label pbAuLabel;
    private Label pbAvLabel;
    private Label pbDrLabel;

    private final UI ui;

    private class ProgressThread extends Thread {
        private final UI ui;
        ProgressBar pbAvProgress;
        ProgressBar pbAuProgress;
        ProgressBar pbDrProgress;
        Dialog dialog;
        public ProgressThread(Dialog dialog,
                UI ui,
                ProgressBar pbAvProgress,
                ProgressBar pbAuProgress,
                ProgressBar pbDrProgress
        ) {
            this.ui = ui;
            this.pbAvProgress = pbAvProgress;
            this.pbAuProgress = pbAuProgress;
            this.pbDrProgress = pbDrProgress;
            this.dialog = dialog;
        }

        public void run(){
            while (!Thread.currentThread().isInterrupted()) {
                if(fileLoader.isAllFinished()) {
                    showMessage("Download finished", false);
                    break;
                }
                if(fileLoader.hasErrors()) {
                    showMessage(fileLoader.getErrorMessage(), true);
                    break;
                }
                if(fileLoader.isConnectedStarted() &&
                        pbAvProgress.isIndeterminate() &&
                        pbAuProgress.isIndeterminate() &&
                        pbDrProgress.isIndeterminate()) {
                    ui.access(() -> {
                        pbDrProgress.setIndeterminate(false);
                        pbAvProgress.setIndeterminate(false);
                        pbAuProgress.setIndeterminate(false);
                    });
                }
                if (fileLoader.isDownloadStarted()) {
                    log.info("file download started");
                    Map<String, FileProgressStatus> statusMap = fileLoader.getProgress();
                    ui.access(() -> {
                        double pbAvDownloaded = statusMap.get(FileLoader.avFileName).getBytesDownloadedPercent();
                        double pbAuDownloaded = statusMap.get(FileLoader.auFileName).getBytesDownloadedPercent();
                        double pbDrDownloaded = statusMap.get(FileLoader.drFileName).getBytesDownloadedPercent();
                        log.info("% {} - {}", pbAvDownloaded, FileLoader.avFileName);
                        log.info("% {} - {}", pbAuDownloaded, FileLoader.auFileName);
                        log.info("% {} - {}", pbDrDownloaded, FileLoader.drFileName);
                        pbAvProgress.setValue(pbAvDownloaded);
                        pbAuProgress.setValue(pbAuDownloaded);
                        pbDrProgress.setValue(pbDrDownloaded);

                        pbAuLabel.setText(getKb(statusMap.get(FileLoader.auFileName), auLabelText));
                        pbAvLabel.setText(getKb(statusMap.get(FileLoader.avFileName), avLabelText));
                        pbDrLabel.setText(getKb(statusMap.get(FileLoader.drFileName), drLabelText));
                    });
                } else {
                    log.info("Await download...");
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    log.info("Thread stopped");
                    break;
                }
            }
        }
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        pbAvLabel = new Label();
        pbAvLabel.setText(avLabelText);
        ProgressBar pbAvProgress = new ProgressBar(0, 1);
        pbAvProgress.setWidth(100, Unit.PERCENTAGE);
        pbAvProgress.setIndeterminate(true);
        VerticalLayout pbAvLayout = new VerticalLayout(pbAvLabel, pbAvProgress);

        pbAuLabel = new Label();
        pbAuLabel.setText(auLabelText);
        ProgressBar pbAuProgress = new ProgressBar(0, 1);
        pbAuProgress.setWidth(100, Unit.PERCENTAGE);
        pbAuProgress.setIndeterminate(true);
        VerticalLayout pbAuLayout = new VerticalLayout(pbAuLabel, pbAuProgress);

        pbDrLabel = new Label();
        pbDrLabel.setText(drLabelText);
        ProgressBar pbDrProgress = new ProgressBar(0, 1);
        pbDrProgress.setWidth(100, Unit.PERCENTAGE);
        pbDrProgress.setIndeterminate(true);
        VerticalLayout pbDrLayout = new VerticalLayout(pbDrLabel, pbDrProgress);

        pbAvLayout.setSizeFull();
        pbAuLayout.setSizeFull();
        pbDrLayout.setSizeFull();

        messageFiled = new TextField();
        messageFiled.setEnabled(false);
        messageFiled.setWidth(100, Unit.PERCENTAGE);
        messageFiled.setReadOnly(true);
        messageLayout = new HorizontalLayout(messageFiled);
        messageLayout.setWidth(100, Unit.PERCENTAGE);
        messageLayout.setVisible(false);


        VerticalLayout verticalLayout = new VerticalLayout(messageLayout, pbAvLayout, pbAuLayout, pbDrLayout);
        verticalLayout.setSizeFull();
        add(verticalLayout);

        thread = new ProgressThread(this, ui, pbAvProgress, pbAuProgress, pbDrProgress);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if(thread != null ) {
            thread.interrupt();
            thread = null;
        }
    }

    public FileLoaderProgressDialog(UI ui,
                                    PasswordField textFieldAv,
                                    PasswordField textFieldAu,
                                    PasswordField textFieldDr) {
        this.ui = ui;
        fileLoader = new FileLoader(3);
        auLabelText = textFieldAu.getValue();
        avLabelText = textFieldAv.getValue();
        drLabelText = textFieldDr.getValue();

        Button hideButton = new Button("", new Icon("lumo", "eye-disabled"));
        hideButton.addClickListener(event -> {
            auLabelText = "https://path/to/one.xml";
            avLabelText = "https://path/to/two.xml";
            drLabelText = "https://path/to/three.xml";
        });

        Button button = new Button("Close");
        button.addClickListener(buttonClickEvent -> {
            this.close();
        });
        this.setCloseOnOutsideClick(false);
        this.setCloseOnEsc(false);

        try {
            fileLoader.loadFiles(auLabelText, avLabelText, drLabelText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getFooter().add(hideButton, button);
    }

    private void showMessage(String message, boolean error){
        ui.access(() -> {
            if(messageFiled != null && messageLayout != null) {
                messageLayout.setVisible(true);
                if(error) {
                    messageFiled.setValue("ERROR:" + message);
                } else {
                    messageFiled.setValue(message);
                }
            }
        });
    }

    private String getKb(FileProgressStatus status, String url) {
        int totalKb = (int) (status.getBytesTotal() / 1024);
        long downloadedKb =  (int) (status.getBytesDownloaded() / 1024);
        String result = String.format("[ %05d Kb / %05d Kb ] - ", downloadedKb, totalKb);
        return result + " " + url;
    }

}
