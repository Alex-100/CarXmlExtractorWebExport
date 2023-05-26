package pro.dralex.CarXmlExtractorWeb.front.fileLoad;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import pro.dralex.CarXmlExtractorWeb.back.url.UrlContainer;
import pro.dralex.CarXmlExtractorWeb.back.url.UrlService;

@Route(value = "files")
@PageTitle("File load")
@Slf4j
public class FileLoaderView extends VerticalLayout {

    UrlService urlService;

    public FileLoaderView(UrlService urlService) {

        this.urlService = urlService;

        PasswordField textFieldAv = new PasswordField("av-style url");
        textFieldAv.setMinWidth(80, Unit.PERCENTAGE);
        PasswordField textFieldAu = new PasswordField("au-style url");
        textFieldAu.setMinWidth(80, Unit.PERCENTAGE);
        PasswordField textFieldDr = new PasswordField("dr-style url");
        textFieldDr.setMinWidth(80, Unit.PERCENTAGE );

        Button buttonUpdate = new Button("Save");
        Button buttonLoad = new Button("Load");
        Button hideButton = new Button("", new Icon("lumo", "eye"));
        hideButton.addClickListener(event -> {
            textFieldAv.getElement().callJsFunction("_setPasswordVisible", true);
            textFieldAu.getElement().callJsFunction("_setPasswordVisible", true);
            textFieldDr.getElement().callJsFunction("_setPasswordVisible", true);
        });
        HorizontalLayout horizontalLayout = new HorizontalLayout(buttonUpdate, buttonLoad, hideButton);



        UrlContainer urlContainer = urlService.getContainer();
        textFieldAv.setValue(urlContainer.getAvStyleUrl());
        textFieldAu.setValue(urlContainer.getAuStyleUrl());
        textFieldDr.setValue(urlContainer.getDrStyleUrl());

        buttonLoad.addClickListener(buttonClickEvent -> {
            UI ui = this.getUI().orElseThrow();
            FileLoaderProgressDialog dialog =
                    new FileLoaderProgressDialog(ui, textFieldAv, textFieldAu, textFieldDr);
            dialog.open();
        });

        buttonUpdate.addClickListener(buttonClickEvent -> {
            UrlContainer urlContainerNew = new UrlContainer();
            urlContainerNew.setAuStyleUrl(textFieldAu.getValue());
            urlContainerNew.setAvStyleUrl(textFieldAv.getValue());
            urlContainerNew.setDrStyleUrl(textFieldDr.getValue());
            urlService.updateContainer(urlContainerNew);
        });

        add(horizontalLayout, textFieldAv, textFieldAu, textFieldDr);
//        add(pbAv, pbAu, pbDr);
    }

}
