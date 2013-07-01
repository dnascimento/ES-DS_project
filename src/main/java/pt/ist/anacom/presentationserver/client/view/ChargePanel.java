package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.InvalidBalanceException;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class ChargePanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;
    private final HorizontalPanel horizontalMainPanel;


    /** Titulo do Menu */
    private Label title;

    private TextBox addBalanceTextBox;
    private Button addBalanceButton;

    private Anacom anacom;


    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public ChargePanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Add Balance");
        this.horizontalMainPanel = new HorizontalPanel();
        this.addBalanceTextBox = new TextBox();
        addBalanceButton = new Button("Add Balance");

        // Set label style
        this.title.setStyleName("h1");

        addBalanceTextBox.setText("Enter the value to be added");

        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(horizontalMainPanel);

        this.horizontalMainPanel.add(addBalanceTextBox);
        this.horizontalMainPanel.add(addBalanceButton);


        // Add the container panel to super painel
        this.add(this.mainPanel);

        this.addBalanceTextBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setAddBalanceTextBox("");
            }
        });

        getAddBalanceButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                chargeCellPhoneBalance();
            }
        });

    }


    public TextBox getAddBalanceTextBox() {
        return addBalanceTextBox;
    }



    public void setAddBalanceTextBox(String text) {
        this.addBalanceTextBox.setText(text);
    }



    public Button getAddBalanceButton() {
        return addBalanceButton;
    }



    public void setAddBalanceButton(Button addBalanceButton) {
        this.addBalanceButton = addBalanceButton;
    }



    public Anacom getAnacom() {
        return anacom;
    }


    public void chargeCellPhoneBalance() {

        try {
            int value = Integer.parseInt(addBalanceTextBox.getText().trim());
            final PhoneNumValueDTO dto = new PhoneNumValueDTO(anacom.getCellPhoneBeingManaged(), value);

            rpcService.addBalance(dto, new AsyncCallback<Void>() {
                public void onSuccess(Void result) {
                    getAnacom().getGetBalancePanel().refreshBalance();
                }

                public void onFailure(Throwable caught) {
                    try {
                        throw caught;
                    } catch (InvalidBalanceException e) {
                        Window.alert("ERROR: Cannot charge CellPhone (InvalidBalanceException): " + e.getMsg());
                    } catch (InvalidOperatorException e) {
                        Window.alert("ERROR: Cannot charge CellPhone (InvalidOperatorException): " + e.getMsg());
                    } catch (CellPhoneNotExistsException e) {
                        Window.alert("ERROR: Cannot charge CellPhone (CellPhoneNotExistsException): " + e.getMsg());
                    } catch (OperatorNotFoundException e) {
                        Window.alert("ERROR: Cannot charge CellPhone (OperatorNotFoundException): " + e.getMsg());
                    } catch (Throwable e) {
                        // Inesperada
                        Window.alert("ERROR: Cannot charge CellPhone: " + e.getMessage());
                    }
                    GWT.log("presentationserver.client.Anacom::rpcService.addBalance");
                    GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                }
            });

        } catch (NumberFormatException e) {
            Window.alert("ERROR: Cannot charge CellPhone: value isn't numeric");
        }
    }
}
