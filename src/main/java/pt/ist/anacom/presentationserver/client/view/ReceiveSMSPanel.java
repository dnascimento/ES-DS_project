package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.SMSListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
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
public class ReceiveSMSPanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    private final HorizontalPanel getReceivedSMSPanel;

    private Button getReceivedSMSButton;
    private InboxTable inbox = new InboxTable();

    /** Titulo do Menu */
    private Label title;

    private Anacom anacom;


    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public ReceiveSMSPanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();

        this.title = new Label("Receive SMS");

        getReceivedSMSPanel = new HorizontalPanel();
        getReceivedSMSButton = new Button("SMS Inbox");

        // Set label style
        this.title.setStyleName("h1");

        // Add to panel
        getReceivedSMSPanel.add(getReceivedSMSButton);
        getReceivedSMSPanel.add(inbox);
        this.mainPanel.add(title);
        this.mainPanel.add(getReceivedSMSPanel);

        // Add the container panel to super painel
        this.add(this.mainPanel);

        getGetReceivedSMSButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                listSMS();
            }
        });

    }


    public Button getGetReceivedSMSButton() {
        return getReceivedSMSButton;
    }


    public void listSMS() {
        String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();

        final PhoneNumberDTO dto = new PhoneNumberDTO(cellPhoneBeingManaged);
        rpcService.getReceivedSMS(dto, new AsyncCallback<SMSListDTO>() {
            public void onSuccess(SMSListDTO response) {
                getReceivedSMSPanel.remove(inbox);
                inbox = new InboxTable();
                inbox.setInbox(response);
                getReceivedSMSPanel.add(inbox);
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.getReceivedSMS");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Cannot get the sms(CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Cannot get the sms (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Cannot get the balance: " + e.getMessage());
                }
            }
        });

    }

}
