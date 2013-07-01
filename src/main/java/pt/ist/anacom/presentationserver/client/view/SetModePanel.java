package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.phone.PhoneNumModeDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.exceptions.CellPhoneNotExistsException;
import pt.ist.anacom.shared.exceptions.IncompatibleModeException;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class SetModePanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    private final HorizontalPanel horizontalMainPanel;


    /** Titulo do Menu */
    private Label title;


    private ListBox changeModeListBox;
    private Button changeModeButton;




    private final String ON = "ON";
    private final String OFF = "OFF";
    private final String SILENCE = "Silence";
    private final String BUSY = "Busy";


    private Anacom anacom;

    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public SetModePanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.title = new Label("Change Mode");
        this.horizontalMainPanel = new HorizontalPanel();

        this.changeModeListBox = new ListBox();
        changeModeButton = new Button("Change Mode");

        // Set label style
        this.title.setStyleName("h1");


        // Add to panel
        this.mainPanel.add(title);
        this.mainPanel.add(horizontalMainPanel);

        this.horizontalMainPanel.add(changeModeListBox);
        this.horizontalMainPanel.add(changeModeButton);


        // Add the container panel to super painel
        this.add(this.mainPanel);


        changeModeListBox.addItem(ON);
        changeModeListBox.addItem(OFF);
        changeModeListBox.addItem(SILENCE);
        changeModeListBox.setVisibleItemCount(1);



        getChangeModeButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                changeMobileMode();
            }
        });

    }


    public Button getChangeModeButton() {
        return changeModeButton;
    }


    public Anacom getAnacom() {
        return anacom;
    }


    public void changeMobileMode() {
        String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();
        CellPhoneMode mode = null;
        final String selectedMode = changeModeListBox.getValue(changeModeListBox.getSelectedIndex());

        if (selectedMode.equals(ON)) {
            mode = CellPhoneMode.ON;
        } else if (selectedMode.equals(OFF)) {
            mode = CellPhoneMode.OFF;
        } else if (selectedMode.equals(SILENCE)) {
            mode = CellPhoneMode.SILENCE;
        } else if (selectedMode.equals(BUSY)) {
            mode = CellPhoneMode.BUSY;
        } else {
            Window.alert("ERROR: Undefined mode!");
            return;
        }

        GWT.log(cellPhoneBeingManaged + mode);
        final PhoneNumModeDTO dto = new PhoneNumModeDTO(cellPhoneBeingManaged, mode);

        rpcService.changeCellPhoneMode(dto, new AsyncCallback<Void>() {
            public void onSuccess(Void response) {
                getAnacom().getGetModePanel().refreshCellPhoneMode();
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.changeMode");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (IncompatibleModeException e) {
                    Window.alert("ERROR: Can not change mode   (IncompatibleModeException): " + e.getMsg());
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not change mode   (InvalidOperatorException): " + e.getMsg());
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Can not change mode  (CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Can not change mode   (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not change mode   " + e.getClass().toString());
                }
            }
        });

    }





}
