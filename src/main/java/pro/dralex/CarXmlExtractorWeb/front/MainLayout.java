package pro.dralex.CarXmlExtractorWeb.front;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import pro.dralex.CarXmlExtractorWeb.front.fileLoad.FileLoaderView;
import pro.dralex.CarXmlExtractorWeb.front.makeView.MakeView;
import pro.dralex.CarXmlExtractorWeb.front.models.ModelView;

public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createContent();
    }

    private void createContent() {
        addToDrawer(new VerticalLayout(new RouterLink("Files", FileLoaderView.class)));
        addToDrawer(new VerticalLayout(new RouterLink("Makes", MakeView.class)));
        addToDrawer(new VerticalLayout(new RouterLink("Models", ModelView.class)));
    }

    private void createHeader() {
        H1 logo = new H1("CarXmlExtractor");
        logo.addClassName(LumoUtility.FontSize.LARGE);

        var header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);
        addToNavbar(header);


    }
}
