package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.VoiceDTO;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MakeCallPannel extends
        DecoratorPanel {


    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    private final HorizontalPanel callPanel;

    private Button startCallButton;
    private Button endCallButton;
    private TextBox toNumberTextBox;

    /** Titulo do Menu */
    private Label title;

    private Anacom anacom;

    /**
     * Instantiates a new call painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */

    public MakeCallPannel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Make Call");
        this.callPanel = new HorizontalPanel();

        this.toNumberTextBox = new TextBox();
        this.startCallButton = new Button("Start Call");
        this.endCallButton = new Button("End Call");

        // Set label style
        this.title.setStyleName("h1");
        toNumberTextBox.setText("Enter the destination cellphone number");

        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(callPanel);

        callPanel.add(toNumberTextBox);
        callPanel.add(startCallButton);
        callPanel.add(endCallButton);


        // Add the container panel to super painel
        this.add(this.mainPanel);

        this.toNumberTextBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setToNumberTextBox("");

            }
        });


        getStartCallButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                startCall();
            }
        });

        getEndCallButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                endCall();
            }
        });
    }



    public TextBox getToNumberTextBox() {
        return toNumberTextBox;
    }



    public void setToNumberTextBox(String toNumberTextBox) {
        this.toNumberTextBox.setText(toNumberTextBox);
    }


    public Button getStartCallButton() {
        return startCallButton;
    }


    public Button getEndCallButton() {
        return endCallButton;
    }



    public Anacom getAnacom() {
        return anacom;
    }



    public void startCall() {
        final String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();

        final String destNumber = this.toNumberTextBox.getText().trim();

        if (destNumber.equals("")) {
            Window.alert("ERROR: Can not start voice call: empty destination number");
            return;
        }

        final VoiceDTO dto = new VoiceDTO(cellPhoneBeingManaged, destNumber);
        GWT.log("presentationserver.client.Anacom::rpcService.startCall: " + cellPhoneBeingManaged + " to: " + destNumber);
        rpcService.startVoice(dto, new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                Window.alert("Started voice Call sucessfully!");
                // actualizar o estado
                getAnacom().getGetModePanel().refreshCellPhoneMode();
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.startCall");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + caught.getMessage() + "'");
                try {
                    throw caught;
                } catch (NotEnoughBalanceException e) {
                    Window.alert("ERROR: Can not start call (NotEnoughBalanceException): " + e.getMsg());
                } catch (IncompatibleModeException e) {
                    Window.alert("ERROR: Can not start call (IncompatibleModeException): " + e.getMsg());
                } catch (CellPhoneNotAvailableException e) {
                    Window.alert("ERROR: Can not start call (CellPhoneNotAvailableException): " + e.getMsg());
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not start call (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not start call (CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not start call (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not start call: " + e.getMessage());
                }
            }
        });

    }

    public void endCall() {
        final String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();
        final String destNumber = this.toNumberTextBox.getText().trim();

        if (destNumber.equals("")) {
            Window.alert("ERROR: Can not start voice call: empty destination number");
            return;
        }
        final VoiceDTO dto = new VoiceDTO(cellPhoneBeingManaged, destNumber);
        GWT.log("presentationserver.client.Anacom::rpcService.endCall: " + cellPhoneBeingManaged);
        rpcService.endVoice(dto, new AsyncCallback<Void>() {
            public void onSuccess(Void result) {
                Window.alert("Click! Call Ended");
                getAnacom().getGetBalancePanel().refreshBalance(); // actualizar o saldo
                getAnacom().getGetModePanel().refreshCellPhoneMode(); // actualizar o
                                                                      // estado
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.endCall");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + caught.getMessage() + "'");
                try {
                    throw caught;
                } catch (IncompatibleModeException e) {
                    Window.alert("ERROR: Can not end call (IncompatibleModeException): " + e.getMsg());
                } catch (CellPhoneNotAvailableException e) {
                    Window.alert("ERROR: Can not end call (CellPhoneNotAvailableException): " + e.getMsg());
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not end call (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not end call (CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not end call (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not end call: " + e.getClass() + e.getMessage());
                }
            }
        });

    }

}
