package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.OperatorNotFoundException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class GetModePanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    /* Elementos */
    private final HorizontalPanel horizontalMainPanel;
    private Button showModeButton;
    private Label modeLabel;
    /** Titulo do Menu */
    private Label title;

    private Anacom anacom;

    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public GetModePanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Cellphone Mode");
        this.showModeButton = new Button("Update");
        this.horizontalMainPanel = new HorizontalPanel();
        this.modeLabel = new Label("Dynamic Mode");

        // Set label style
        this.title.setStyleName("h1");
        this.modeLabel.setStyleName("h2");
        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(horizontalMainPanel);

        horizontalMainPanel.add(showModeButton);
        horizontalMainPanel.add(modeLabel);

        // Add the container panel to super painel
        this.add(this.mainPanel);

        getShowModeButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                refreshCellPhoneMode();
            }
        });
    }


    public Button getShowModeButton() {
        return showModeButton;
    }

    public void setModeLabel(String text) {
        this.modeLabel.setText(text);
    }

    public void refreshCellPhoneMode() {
        String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();

        final PhoneNumberDTO dto = new PhoneNumberDTO(cellPhoneBeingManaged);
        rpcService.getMode(dto, new AsyncCallback<PhoneNumModeDTO>() {
            public void onSuccess(PhoneNumModeDTO response) {
                setModeLabel(response.getMode().toString());
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.showCellPhoneMode");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not get cellphone mode (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not get cellphone mode (CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not get cellphone mode (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not get cellphone mode: " + e.getMessage());
                }

            }
        });
    }


}
