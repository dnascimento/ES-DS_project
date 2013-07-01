package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.CommunicationDTO;
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

public class LastCommunicationPannel extends DecoratorPanel{

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    private final HorizontalPanel getLastCallPanel = new HorizontalPanel();;

    private Button getLastCallButton = new Button("Get Last Communication");;
    private LastCommunicationTable result = new LastCommunicationTable();

    /** Titulo do Menu */
    private Label title = new Label("Last Made Communication");;

    private Anacom anacom;


    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public LastCommunicationPannel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();

        // Set label style
        this.title.setStyleName("h1");

        // Add to panel
        getLastCallPanel.add(getLastCallButton);
        getLastCallPanel.add(result);
        this.mainPanel.add(title);
        this.mainPanel.add(getLastCallPanel);

        // Add the container panel to super painel
        this.add(this.mainPanel);

        getLastCallButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                getLastCall();
            }
        });

    }


    /**
	 * @return the result
	 */
	private LastCommunicationTable getResult() {
		return result;
	}


	public void refreshMadeCommunication(){
    	this.getLastCall();
    	
    }
    
    public Button getLastCallButton() {
        return getLastCallButton;
    }


    public void getLastCall() {
        String cellPhoneBeingManaged = anacom.getCellPhoneBeingManaged();

        final PhoneNumberDTO dto = new PhoneNumberDTO(cellPhoneBeingManaged);
        rpcService.getLastMadeCall(dto, new AsyncCallback<CommunicationDTO>() {
            public void onSuccess(CommunicationDTO response) {
                getLastCallPanel.remove(result);
                result = new LastCommunicationTable();
                result.setTable(response);
                getLastCallPanel.add(result);

            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.getLastCall");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (CellPhoneNotExistsException e) {
                    Window.alert("ERROR: Cannot get the last call(CellPhoneNotExistsException): " + e.getMsg());
                } catch (OperatorNotFoundException e) {
                    Window.alert("ERROR: Cannot get the last call (OperatorNotFoundException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Cannot get the last call: " + e.getMessage());
                }
            }
        });

    }

}
