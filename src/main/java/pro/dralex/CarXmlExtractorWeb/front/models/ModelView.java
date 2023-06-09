package pro.dralex.CarXmlExtractorWeb.front.models;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pro.dralex.CarXmlExtractorWeb.back.makes.CarMakeConnector;
import pro.dralex.CarXmlExtractorWeb.back.makes.ConnectorSource;
import pro.dralex.CarXmlExtractorWeb.back.makes.MakeService;
import pro.dralex.CarXmlExtractorWeb.back.model.CarModelConnector;
import pro.dralex.CarXmlExtractorWeb.back.model.CarModelConnectorManual;
import pro.dralex.CarXmlExtractorWeb.back.model.ModelService;
import pro.dralex.CarXmlExtractorWeb.back.xml.ModelsFromXml;
import pro.dralex.CarXmlExtractorWeb.front.MainLayout;
import pro.dralex.CarXmlExtractorWeb.front.Message;

import java.util.*;

@Route(value = "models", layout = MainLayout.class)
public class ModelView extends HorizontalLayout {
    private final MakeService makeService;
    private final ModelService modelService;
    private Grid<CarModelConnector> gridSupported;
    private Grid<CarModelConnectorManual> gridBlueprint;
    private Button updateButton;
    private CarModelConnectorManual selectedModelBlueprint = null;
    private String selectedMake = "";

    private Map<String, ModelsFromXml> dataUnsupported;
    private ListBox<String> avStyleListBox;
    private ListBox<String> auStyleListBox;
    private ListBox<String> drStyleListBox;

    public ModelView(MakeService makeService, ModelService modelService) {
        this.setSizeFull();
        this.makeService = makeService;
        this.modelService = modelService;

        reloadData(false);
        Component listOfMakes = createListOfMakes();
        Component blueprintTab = getBlueprintTab();
        Component dbTab = getDbTab();
        Component unsupportedTab = getUnsupportedTab();
        TabSheet tabSheet = createTabs(dbTab, blueprintTab, unsupportedTab);

        add(listOfMakes, tabSheet);
    }

    private Component getUnsupportedTab() {


        ///...edit panel
        TextField modelGroupStyleText = new TextField("Model Group");
        TextField avStyleText = new TextField("Av Style");
        TextField auStyleText = new TextField("Au Style");
        TextField drStyleText = new TextField("Dr Style");

        int width = 120;
        modelGroupStyleText.setWidth(width, Unit.PIXELS);
        avStyleText.setWidth(width, Unit.PIXELS);
        auStyleText.setWidth(width, Unit.PIXELS);
        drStyleText.setWidth(width, Unit.PIXELS);

        Button sendButton = new Button("To blueprint");
        sendButton.addClickListener(event -> {
            if(selectedMake.isEmpty() ||
                    avStyleText.isEmpty() ||
                    auStyleText.isEmpty() ||
                    drStyleText.isEmpty() ||
                    modelGroupStyleText.isEmpty()) {
                Message.show("Empty field(s)", true);
                return;
            }
            Optional<CarModelConnectorManual> optional = modelService.findByFields(
                    avStyleText.getValue(),
                    auStyleText.getValue(),
                    drStyleText.getValue());
            if(optional.isPresent()) {
                Message.show("The item exist", true);
                return;
            }

            CarModelConnectorManual newConnectorManual = new CarModelConnectorManual(
                    selectedMake,
                    modelGroupStyleText.getValue(),
                    avStyleText.getValue(),
                    auStyleText.getValue(),
                    drStyleText.getValue()
            );
            modelService.addManualModel(newConnectorManual);
            reloadData(true);
            gridBlueprint.setItems(dataUnsupported.get(selectedMake.toUpperCase()).getAll());
            modelGroupStyleText.setValue("");
            avStyleText.setValue("");
            auStyleText.setValue("");
            drStyleText.setValue("");

        });

        Button clearButton = new Button("Clear");
        clearButton.addClickListener(event -> {
            avStyleText.setValue("");
            auStyleText.setValue("");
            drStyleText.setValue("");
            modelGroupStyleText.setValue("");
        });

        HorizontalLayout editLayout = new HorizontalLayout(
                modelGroupStyleText,
                avStyleText,
                auStyleText,
                drStyleText,
                sendButton,
                clearButton
        );
        editLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        editLayout.setSizeFull();


        ///...list panel

        avStyleListBox = new ListBox<>();
        avStyleListBox.addValueChangeListener(event -> {
            modelGroupStyleText.setValue(event.getValue());
            avStyleText.setValue(event.getValue());
        });

        auStyleListBox = new ListBox<>();
        auStyleListBox.addValueChangeListener(event -> {
            auStyleText.setValue(event.getValue());
        });

        drStyleListBox = new ListBox<>();
        drStyleListBox.addValueChangeListener(event -> {
            drStyleText.setValue(event.getValue());
        });

        HorizontalLayout listLayout = new HorizontalLayout(avStyleListBox, auStyleListBox, drStyleListBox);
        listLayout.setWidthFull();

        VerticalLayout layout = new VerticalLayout(editLayout, listLayout);
        layout.setWidthFull();
        return layout;
    }

    private Component getBlueprintTab() {

        //.. edit panel
        int width = 120;
        TextField modelGroup = new TextField("modelGroup");
        modelGroup.setWidth(width, Unit.PIXELS);
        TextField avStyleText = new TextField("Av Style");
        avStyleText.setWidth(width, Unit.PIXELS);
        TextField auStyleText = new TextField("Au Style");
        auStyleText.setWidth(width, Unit.PIXELS);
        TextField drStyleText = new TextField("Dr Style");
        drStyleText.setWidth(width, Unit.PIXELS);

        Button sendButton = new Button("Update");
        sendButton.addClickListener(event -> {
            if(selectedMake.isEmpty()) {
                Message.show("Empty make", true);
                return;
            }
            CarModelConnectorManual newConnectorManual = new CarModelConnectorManual(
                    selectedMake,
                    modelGroup.getValue(),
                    avStyleText.getValue(),
                    auStyleText.getValue(),
                    drStyleText.getValue()
            );
            modelService.updateManualModel(newConnectorManual, selectedModelBlueprint.getId());
            reloadData(true);
            gridBlueprint.setItems(dataUnsupported.get(selectedMake.toUpperCase()).getAll());

        });

        Button removeButton = new Button("Remove");
        removeButton.addClickListener(event -> {
            dataUnsupported.get(selectedModelBlueprint.getMake().toUpperCase())
                    .getSupportedMakesXml()
                    .remove(selectedModelBlueprint);
            modelService.remove(selectedModelBlueprint);
            reloadData(true);
            gridBlueprint.setItems(
                    dataUnsupported.get(selectedMake.toUpperCase()).getAll()
            );
            modelGroup.setValue("");
            avStyleText.setValue("");
            auStyleText.setValue("");
            drStyleText.setValue("");
        });

        Button clearButton = new Button("Clear");
        clearButton.addClickListener(event -> {
            modelGroup.setValue("");
            avStyleText.setValue("");
            auStyleText.setValue("");
            drStyleText.setValue("");
        });
        Button addNewButton = new Button("As new");
        addNewButton.addClickListener(event -> {
            Optional<CarModelConnectorManual> optional = modelService.findByFields(
                    avStyleText.getValue(),
                    auStyleText.getValue(),
                    drStyleText.getValue());
            if(optional.isPresent()) {
                Message.show("The item exist", true);
                return;
            }

            CarModelConnectorManual newConnectorManual = new CarModelConnectorManual(
                    selectedMake,
                    modelGroup.getValue(),
                    avStyleText.getValue(),
                    auStyleText.getValue(),
                    drStyleText.getValue()
            );
            modelService.addManualModel(newConnectorManual);
            reloadData(true);
            gridBlueprint.setItems(dataUnsupported.get(selectedMake.toUpperCase()).getAll());
            modelGroup.setValue("");
            avStyleText.setValue("");
            auStyleText.setValue("");
            drStyleText.setValue("");
        });

        HorizontalLayout editPanel = new HorizontalLayout(
                modelGroup,
                avStyleText,
                auStyleText,
                drStyleText,
                sendButton,
                removeButton,
                addNewButton,
                clearButton
        );
        editPanel.setDefaultVerticalComponentAlignment(Alignment.BASELINE);

        //.. grid panel
        gridBlueprint = new Grid<>(CarModelConnectorManual.class);
        gridBlueprint.setSizeFull();
        gridBlueprint.addClassNames(LumoUtility.Border.NONE);
        gridBlueprint.setColumns("modelGroup", "avStyle", "auStyle", "drStyle", "source");
        gridBlueprint.addItemClickListener(event -> {
                selectedModelBlueprint = event.getItem();
                if(selectedModelBlueprint.getSource().equals(ConnectorSource.MANUAL)) {
                    sendButton.setEnabled(true);
                    removeButton.setEnabled(true);
                    modelGroup.setValue(event.getItem().getModelGroup());
                    avStyleText.setValue(event.getItem().getAvStyle());
                    auStyleText.setValue(event.getItem().getAuStyle());
                    drStyleText.setValue(event.getItem().getDrStyle());
                } else {
                    sendButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    modelGroup.setValue("");
                    avStyleText.setValue("");
                    auStyleText.setValue("");
                    drStyleText.setValue("");
                }
        });

        VerticalLayout verticalLayout = new VerticalLayout(editPanel, gridBlueprint);
        verticalLayout.setSizeFull();
        return verticalLayout;
    }

    private Component getDbTab() {

        //...grid
        gridSupported = new Grid<>(CarModelConnector.class);
        gridSupported.setHeightFull();
        gridSupported.setColumns("avStyle", "auStyle", "drStyle");
        gridSupported.addClassNames(LumoUtility.Border.NONE);
        return gridSupported;
    }

    private Component createListOfMakes(){

        ListBox<String> makesListBox = new ListBox<>();
        List<String> models = makeService.getSupportedMakes().stream().map(CarMakeConnector::getAvStyle).toList();
        makesListBox.setItems(models);
        makesListBox.addValueChangeListener(event -> {
            selectedMake = event.getValue();
            List<CarModelConnectorManual> selectedMakesXml = dataUnsupported.get(selectedMake.toUpperCase()).getSupportedMakesXml();
            List<CarModelConnectorManual> selectedMakesDb = modelService.getManualModelsFromDb(selectedMake);
            List<CarModelConnectorManual>  finalList = new ArrayList<>();
            finalList.addAll(selectedMakesXml);
            finalList.addAll(selectedMakesDb);
            gridBlueprint.setItems(finalList);
            List<String> avList = dataUnsupported.get(selectedMake.toUpperCase()).getAvStyleUnsupported();
            List<String> auList = dataUnsupported.get(selectedMake.toUpperCase()).getAuStyleUnsupported();
            List<String> drList = dataUnsupported.get(selectedMake.toUpperCase()).getDrStyleUnsupported();
            if(avList.size() == 0) {
                avStyleListBox.setItems(List.of());
            } else {
                avStyleListBox.setItems(avList);
            }
            if(auList.size() == 0) {
                auStyleListBox.setItems(List.of());
            } else {
                auStyleListBox.setItems(auList);
            }
            if(drList.size() == 0) {
                drStyleListBox.setItems(List.of());
            } else {
                drStyleListBox.setItems(drList);
            }

            gridSupported.setItems(modelService.getAllModelsFromDb(selectedMake));

        });

        updateButton = new Button("Update DB");
        updateButton.setEnabled(false);
        updateButton.setWidth(100, Unit.PERCENTAGE);
        updateButton.addThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName());
        updateButton.addClickListener(event -> {
            List<CarModelConnector> result = new ArrayList<>();
            dataUnsupported.keySet().forEach(make -> {
                List<CarModelConnector> list = dataUnsupported.get(make).getAll()
                        .stream()
                        .map(item -> new CarModelConnector(
                                item.getMake(),
                                item.getModelGroup(),
                                item.getAvStyle(),
                                item.getAuStyle(),
                                item.getDrStyle()))
                        .toList();
                result.addAll(list);
            });
            modelService.saveAllModelsToDb(result);

            gridSupported.setItems(modelService.getAllModelsFromDb(selectedMake));
        });
        final VerticalLayout verticalLayout = new VerticalLayout(updateButton , new Scroller(makesListBox));
        verticalLayout.setWidth(20, Unit.PERCENTAGE);
        return verticalLayout;
    }

    private TabSheet createTabs(Component supported, Component blueprint, Component unsupported){
        TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("Supported", supported);
        tabSheet.add("Blueprint", blueprint);
        tabSheet.add("Unsupported", unsupported);
        tabSheet.addSelectedChangeListener(event -> {
            updateButton.setEnabled(event.getSelectedTab().getLabel().equals("Blueprint"));
        });
        return tabSheet;
    }

    private void reloadData(boolean onlyManual){
        try {
            if(!onlyManual) {
                dataUnsupported = modelService.getModelsFromXml();
            }
            if(modelService.hasEmptyManualModels()) {
                modelService.saveManualModelsToDb();
            }
            Set<String> xmlData = dataUnsupported.keySet();
            xmlData.forEach(make -> {
                List<CarModelConnectorManual> dbModels = modelService.getManualModelsFromDb(make);
                if(dbModels.size() > 0)
                    dataUnsupported.get(make).setSupportedMakesManual(dbModels);
            });
        } catch (Exception e) {
            Message.show(e.getMessage(), true);
            e.printStackTrace();
        }
    }


}
