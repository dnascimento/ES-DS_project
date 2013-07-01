package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.exceptions.CellPhoneNotAvailableException;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;
import pt.ist.anacom.shared.exceptions.NotEnoughBalanceException;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class SendSMSPanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    private final HorizontalPanel sendSMSPanel;

    private Button sendSMSButton;
    private TextBox toNumberTextBox;
    private TextArea messageSMSTextBox;

    /** Titulo do Menu */
    private Label title;

    private Anacom anacom;

    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public SendSMSPanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Send SMS");
        this.sendSMSPanel = new HorizontalPanel();

        this.sendSMSButton = new Button("Send SMS");
        this.toNumberTextBox = new TextBox();
        this.messageSMSTextBox = new TextArea();

        // Set label style
        this.title.setStyleName("h1");
        toNumberTextBox.setText("Enter the destination cellphone number");
        messageSMSTextBox.setText("Enter the desired message");

        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(sendSMSPanel);

        sendSMSPanel.add(toNumberTextBox);
        sendSMSPanel.add(messageSMSTextBox);
        sendSMSPanel.add(sendSMSButton);


        // Add the container panel to super painel
        this.add(this.mainPanel);

        this.toNumberTextBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setToNumberTextBox("");

            }
        });

        this.messageSMSTextBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setMessageSMSTextBox("");
            }
        });



        getSendSMSButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                sendSMS();
            }
        });
    }



    public TextBox getToNumberTextBox() {
        return toNumberTextBox;
    }



    public void setToNumberTextBox(String toNumberTextBox) {
        this.toNumberTextBox.setText(toNumberTextBox);
    }



    public TextArea getMessageSMSTextBox() {
        return messageSMSTextBox;
    }



    public void setMessageSMSTextBox(String messageSMSTextBox) {
        this.messageSMSTextBox.setText(messageSMSTextBox);
    }



    public Button getSendSMSButton() {
        return sendSMSButton;
    }



    public Anacom getAnacom() {
        return anacom;
    }



    public void sendSMS() {
        final String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();

        final String number = this.toNumberTextBox.getText().trim();
        final String sms = this.messageSMSTextBox.getText().trim();

        if (number.equals("")) {
            Window.alert("ERROR: Can not send  sms: empty destination number");
            return;
        }

        final SMSDTO dto = new SMSDTO(cellPhoneBeingManaged, number, sms);
        GWT.log("presentationserver.client.Anacom::rpcService.sendSMS: " + cellPhoneBeingManaged + " to: " + number + " : " + sms);
        rpcService.processSMS(dto, new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                Window.alert("Sent SMS sucessfully!");
                getAnacom().getGetBalancePanel().refreshBalance(); // actualizar o saldo
                getAnacom().getReceiveSMSPanel().listSMS(); // actualizar sms
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.sendSMS");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + caught.getMessage() + "'");
                try {
                    throw caught;
                } catch (NotEnoughBalanceException e) {
                    Window.alert("ERROR: Can not send  sms (NotEnoughBalanceException): " + e.getMsg());
                } catch (IncompatibleModeException e) {
                    Window.alert("ERROR: Can not send  sms (IncompatibleModeException): " + e.getMsg());
                } catch (CellPhoneNotAvailableException e) {
                    Window.alert("ERROR: Can not send  sms (CellPhoneNotAvailableException): " + e.getMsg());
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not send  sms (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not send  sms(CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not send  sms (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not send  sms: " + e.getMessage());
                }
            }
        });

    }
}
