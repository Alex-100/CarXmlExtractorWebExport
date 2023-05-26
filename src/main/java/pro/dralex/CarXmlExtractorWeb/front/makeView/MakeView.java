package pro.dralex.CarXmlExtractorWeb.front.makeView;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import pro.dralex.CarXmlExtractorWeb.back.fileLoader.FileLoader;
import pro.dralex.CarXmlExtractorWeb.back.repo.CarMakeRepository;
import pro.dralex.CarXmlExtractorWeb.back.xml.*;
import pro.dralex.CarXmlExtractorWeb.front.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route(value = "xml-makes")
@Slf4j
public class MakeView extends VerticalLayout {
    private final CarMakeTmpRepository carMakeTmpRepository;
    private final CarMakeRepository carMakeRepository;
    private Grid<CarMakeConnectorTmp> gridManual;
    private Grid<CarMakeConnector> gridSupported;
    private final MakesResultFromXml makesResultFromXml;

    public MakeView(CarMakeTmpRepository carMakeTmpRepository, CarMakeRepository carMakeRepository) {
        this.carMakeRepository = carMakeRepository;
        this.carMakeTmpRepository = carMakeTmpRepository;
        XmlExtractor xmlExtractor = new XmlExtractor();
        makesResultFromXml = getData(xmlExtractor);

        setSizeFull();

        Component supported = getSupportedTab();
        Component unSupported = getUnsupportedTab(makesResultFromXml);

        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("Supported models", supported);
        tabSheet.add("Unsupported models", unSupported);

        add(tabSheet);

        gridSupported.setItems(carMakeRepository.findAll());

    }

    public Component getSupportedTab(){
        final TextField avStyleText = new TextField("Av Style");
        final TextField auStyleText = new TextField("Au Style");
        final TextField drStyleText = new TextField("Dr Style");
        final TextField idIndex = new TextField("");
        idIndex.setVisible(false);

        Button saveButton = new Button("Update");
        saveButton.addClickListener(event -> {
            if(avStyleText.isEmpty() || auStyleText.isEmpty() || drStyleText.isEmpty() || idIndex.isEmpty()) {
                Message.show("An field is empty. You should full up all fields", true);
            } else {
                Long id = Long.parseLong(idIndex.getValue());
//                ServiceTmp serviceTmp = new ServiceTmp(carMakeRepository);
//                serviceTmp.updateSupportGridItem(id, avStyleText.getValue(), auStyleText.getValue(), drStyleText.getValue());
                updateSupportGridItem(id, avStyleText.getValue(), auStyleText.getValue(), drStyleText.getValue());
                gridSupported.setItems(carMakeRepository.findAll());
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(event -> {
            if(!idIndex.getValue().isEmpty()) {
                showSuggestDialog(event1 -> {
                    Long id = Long.parseLong(idIndex.getValue());
                    carMakeRepository.deleteById(id);
                    gridSupported.setItems(carMakeRepository.findAll());
                });
            } else {
                Message.show("Cannot delete an empty row", true);
            }

        });

        Button cancelButton = new Button("Clear");
        cancelButton.addClickListener(event -> {
           avStyleText.setValue("");
           auStyleText.setValue("");
           drStyleText.setValue("");
           idIndex.setValue("");
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, deleteButton, cancelButton);
        FormLayout formLayout = new FormLayout(
                avStyleText,
                auStyleText,
                drStyleText,
                idIndex,
                buttonLayout
        );

        gridSupported = new Grid<>(CarMakeConnector.class);
        gridSupported.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridSupported.setColumns("avStyle", "auStyle", "drStyle");
        gridSupported.setSizeFull();
        gridSupported.getColumns().forEach(col -> col.setAutoWidth(true));
        gridSupported.addItemClickListener(event -> {
            CarMakeConnector connector = event.getItem();
            idIndex.setValue(connector.getId().toString());
            avStyleText.setValue(connector.getAvStyle());
            auStyleText.setValue(connector.getAuStyle());
            drStyleText.setValue(connector.getDrStyle());
        });


        Button saveToDbButton = new Button(new Icon("lumo","reload"));
        saveToDbButton.addClickListener(event -> {
            gridSupported.setItems(carMakeRepository.findAll());
        });

        Button addNewButton = new Button("Add new");

        HorizontalLayout supportedLayout = new HorizontalLayout(gridSupported, formLayout);
        supportedLayout.setFlexGrow(3, gridSupported);
        supportedLayout.setFlexGrow(1, formLayout);
        supportedLayout.setSizeFull();

        HorizontalLayout toolBarSupported = new HorizontalLayout(saveToDbButton, addNewButton);
        VerticalLayout supported = new VerticalLayout(toolBarSupported, supportedLayout);
        supported.setSizeFull();
        return supported;
    }

    private Component getUnsupportedTab(MakesResultFromXml makesResultFromXml){


        ////... form layout

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.setMinHeight(100, Unit.PIXELS);

        TextField avLabel = new TextField("Av label");
        avLabel.setReadOnly(true);
        TextField auLabel = new TextField("Au label");
        auLabel.setReadOnly(true);
        TextField drLabel = new TextField("Dr label");
        drLabel.setReadOnly(true);
        Button button = new Button(new Icon("lumo", "arrow-right"));
        button.addClickListener(event -> {
            String auText = auLabel.getValue();
            String avText = avLabel.getValue();
            String drText = drLabel.getValue();
            if(!auText.isEmpty() && !avText.isEmpty() && !drText.isEmpty()) {
                if (carMakeTmpRepository.getByFields(auText, avText, drText).isPresent()){
                    String msg = "Make [" + auText + "] [" + avText + "] " + "[" + drText + "] is exist";
                    Message.show(msg, true);
                    return;
                }
                CarMakeConnectorTmp carMakeConnectorTmp = new CarMakeConnectorTmp(auText, avText, drText, ConnectorSource.MANUAL);
                carMakeTmpRepository.save(carMakeConnectorTmp);
                gridManual.setItems(getManualGridData());
            } else {
                Message.show("Impossible to add, because one/many fields are empty","EMPTY_FIELD", true);
            }
        });

        formLayout.add(avLabel, auLabel, drLabel, button);
        formLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);


        ////... content layout

        ListBox<String> avListBox = new ListBox<>();
        avListBox.setItems(makesResultFromXml.getAvStyleUnsupported());
        avListBox.addValueChangeListener(event -> {
            avLabel.setValue(event.getValue()!=null ? event.getValue():"");
        });
        VerticalLayout avLayout = new VerticalLayout(new Text("Av Style"), avListBox);

        ListBox<String> auListBox = new ListBox<>();
        auListBox.setItems(makesResultFromXml.getAuStyleUnsupported());
        auListBox.addValueChangeListener(event -> {
            auLabel.setValue(event.getValue()!=null ? event.getValue():"");
        });
        VerticalLayout auLayout = new VerticalLayout(new Text("Au Style"), auListBox);

        ListBox<String> drListBox = new ListBox<>();
        drListBox.setItems(makesResultFromXml.getDrStyleUnsupported());
        drListBox.addValueChangeListener(event -> {
            drLabel.setValue(event.getValue()!=null ? event.getValue():"");
        });
        VerticalLayout drLayout = new VerticalLayout(new Text("Dr Style"), drListBox);

        HorizontalLayout contentLayout = new HorizontalLayout(avLayout, auLayout, drLayout);

        VerticalLayout makeSelectLayout = new VerticalLayout(formLayout, contentLayout);


        //// ... manual layout

        TextField idTextField = new TextField();
        idTextField.setVisible(false);
        Button buttonSend = new Button("Send to supported");
        buttonSend.addClickListener(event -> {
            List<CarMakeConnectorTmp> listXml = this.makesResultFromXml.getSupportedMakes();
            List<CarMakeConnectorTmp> listRepo = carMakeTmpRepository.findAll();
            List<CarMakeConnector> carMakeResult = new ArrayList<>();
            carMakeResult.addAll(
                    listXml.stream()
                    .map(item -> {
                        CarMakeConnector connector = new CarMakeConnector();
                        BeanUtils.copyProperties(item, connector, "id", "source");
                        return connector;
                    })
                    .toList()
            );
            carMakeResult.addAll(
                    listRepo.stream()
                    .map(item -> {
                        CarMakeConnector connector = new CarMakeConnector();
                        BeanUtils.copyProperties(item, connector, "id", "source");
                        return connector;
                    })
                    .toList()
            );
            log.info("saved items:{}", carMakeResult.size());
            carMakeRepository.deleteAll();
            carMakeRepository.saveAll(carMakeResult);
            gridSupported.setItems(carMakeRepository.findAll());
            Message.show("Sent " + carMakeResult.size() + " items",false);
        });

        Button buttonDelete = new Button("Remove");
        buttonDelete.setEnabled(false);
        buttonDelete.addClickListener(eventDelete -> {
            if(!idTextField.isEmpty() && idTextField.getValue() != null) {
                Long id = Long.valueOf(idTextField.getValue());
                Optional<CarMakeConnectorTmp> item = carMakeTmpRepository.findById(id);
                if(item.isPresent() && item.get().getSource() == ConnectorSource.MANUAL) {
                    carMakeTmpRepository.deleteById(id);
                    gridManual.setItems(getManualGridData());
                } else {
                    Message.show("Impossible to delete auto fields", true);
                }
            }
        });
        HorizontalLayout sendButtonLayout = new HorizontalLayout(buttonSend, buttonDelete, idTextField);
        sendButtonLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        sendButtonLayout.setMinHeight(100, Unit.PIXELS);
        sendButtonLayout.setMinWidth(100, Unit.PERCENTAGE);

        gridManual = new Grid<>(CarMakeConnectorTmp.class);
        gridManual.addClassNames(LumoUtility.Border.NONE);
        gridManual.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridManual.setColumns("avStyle", "auStyle", "drStyle", "source");
        gridManual.setItems(getManualGridData());
        gridManual.addItemClickListener(event -> {
            if(event.getItem().getId() != null) {
                buttonDelete.setEnabled(true);
                idTextField.setValue(event.getItem().getId().toString());
            } else {
                buttonDelete.setEnabled(false);
            }
        });


        VerticalLayout manualLayout = new VerticalLayout(sendButtonLayout, gridManual);



        ////...split layout
        SplitLayout splitLayout = new SplitLayout(makeSelectLayout, manualLayout);
        splitLayout.setSplitterPosition(55.0);
        return  splitLayout;
    }

    private List<CarMakeConnectorTmp> getManualGridData(){
        List<CarMakeConnectorTmp> list1 = makesResultFromXml.getSupportedMakes();
        List<CarMakeConnectorTmp> list2 = carMakeTmpRepository.findAll();
        list2.addAll(list1);
        return list2;
    }
//    private void updateList(MakesResult makesResult) {
//        try {
//            final List<CarMakeConnectorTmp> supportedMakes = makesResult.getSupportedMakes();
//            gridSupported.setItems(supportedMakes);
//            gridSupported.getDataProvider().refreshAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Message.show(e.getMessage(), e.getClass().getCanonicalName(), true);
//        }
//    }

    private MakesResultFromXml getData(XmlExtractor xmlExtractor){
        try {
            return xmlExtractor.getSupportedMakes(FileLoader.avFileName, FileLoader.auFileName, FileLoader.drFileName);
        } catch (Exception e) {
            e.printStackTrace();
            Message.show(e.getMessage(), e.getClass().getCanonicalName(), true);
        }
        return new MakesResultFromXml();
    }

    private void showSuggestDialog(ComponentEventListener<ClickEvent<Button>> lambda){
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setHeaderTitle("Delete an item");
        dialog.add("You are going to delete a row of makes. Are you sure ?");
        Button deleteButton = new Button("Delete", lambda);
        deleteButton.addClickListener(event -> dialog.close());
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
//        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button("Cancel", (e) -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);
        dialog.open();
    }

    public void updateSupportGridItem(Long id, String avStyle, String auStyle, String drStyle) {
        Optional<CarMakeConnector> connectorOptional = carMakeRepository.findById(id);
        if(connectorOptional.isPresent()) {
            connectorOptional.get().setAuStyle(avStyle);
            connectorOptional.get().setAvStyle(auStyle);
            connectorOptional.get().setDrStyle(drStyle);
            carMakeRepository.save(connectorOptional.get());
        } else {
            Message.show("Cannot find fiend with id:" + id, true);
        }
    }
}
