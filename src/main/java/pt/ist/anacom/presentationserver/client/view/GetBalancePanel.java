package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
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
public class GetBalancePanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    /* Elementos */
    private final HorizontalPanel horizontalMainPanel;
    private Button updateButton;
    private Label balanceLabel;

    /** Titulo do Menu */
    private Label title;

    private Anacom anacom;

    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public GetBalancePanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;


        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Get Balance");
        this.horizontalMainPanel = new HorizontalPanel();

        updateButton = new Button("Update");
        balanceLabel = new Label("Dynamic Value");

        // Set label style
        this.title.setStyleName("h1");
        this.balanceLabel.setStyleName("h2");

        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(horizontalMainPanel);


        horizontalMainPanel.add(updateButton);
        horizontalMainPanel.add(balanceLabel);

        // Add the container panel to super painel
        this.add(this.mainPanel);

        getUpdateButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                refreshBalance();
            }
        });

    }


    public void refreshBalance() {
        String number = anacom.getCellPhoneBeingManaged();
        final PhoneNumberDTO dto = new PhoneNumberDTO(number);
        GWT.log("presentationserver.client.Anacom::rpcService.getBalance of: " + number);
        rpcService.getBalance(dto, new AsyncCallback<PhoneNumValueDTO>() {
            public void onSuccess(PhoneNumValueDTO response) {
                setBalanceLabel(response.getBalance() + " cents");
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.getBalance");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not get the balance (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not get the balance(CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not get the balance (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not get the balance: " + e.getMessage());
                }
            }
        });


    }

    public Button getUpdateButton() {
        return updateButton;
    }


    public void setUpdateButton(Button updateButton) {
        this.updateButton = updateButton;
    }


    public Label getBalanceLabel() {
        return balanceLabel;
    }


    public void setBalanceLabel(String text) {
        this.balanceLabel.setText(text);
    }
}
