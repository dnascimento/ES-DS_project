package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.Anacom;
import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;
import pt.ist.anacom.shared.dto.operator.OperatorSimpleDTO;
import pt.ist.anacom.shared.dto.phone.PhoneListDTO;
import pt.ist.anacom.shared.dto.phone.PhoneNumberDTO;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class MobileSelectPanel extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;


    /** Qual o tipo de deploy actual */
    private static String serverType;

    private static final String remoteReplicatedServerType = "SD-RepliKate";


    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    /** The cell phone list. */
    private ListBox cellPhoneList;

    /** The manage cell phone label. */
    private Label manageCellPhoneLabel;

    private Anacom anacom;

    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public MobileSelectPanel(AnacomServiceAsync rpcServiceArg, Anacom anacom) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;

        this.anacom = anacom;

        this.mainPanel = new VerticalPanel();
        this.cellPhoneList = new ListBox();
        this.manageCellPhoneLabel = new Label("Manage Cellphone");


        // Set label style
        this.manageCellPhoneLabel.setStyleName("h1");

        // Add to panel
        this.mainPanel.add(manageCellPhoneLabel);
        this.mainPanel.add(cellPhoneList);

        // Add the container panel to super painel
        this.add(this.mainPanel);


        getCellPhoneList().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                getAnacom().getGetBalancePanel().refreshBalance(); // actualizar o saldo
                getAnacom().getGetModePanel().refreshCellPhoneMode(); // actualizar o modo
                getAnacom().getReceiveSMSPanel().listSMS();
                // getAnacom().getLastCommunicationPannel().refreshMadeCommunication();
            }
        });

    }

    /**
     * Actualizar a lista de todos os telemoveis de todas as operadoras na listbox da
     * interface.
     */
    public void refreshMobileList(String serverType) {
    	   cellPhoneList.clear();
    	if (serverType.equals(remoteReplicatedServerType)){
            addToCellPhoneList(new OperatorSimpleDTO("Vodafone"));
    	}else{
    		addToCellPhoneList(new OperatorSimpleDTO("Red"));
            addToCellPhoneList(new OperatorSimpleDTO("Orange"));
    	}
    }

    /**
     * Método auxiliar da refreshMobileList, obtem a lista de telefones de uma operadora e
     * coloca-as na listbox.
     * 
     * @param dto the dto
     */
    private void addToCellPhoneList(OperatorSimpleDTO dto) {
        rpcService.getCellPhoneList(dto, new AsyncCallback<PhoneListDTO>() {
            public void onSuccess(PhoneListDTO result) {
                for (PhoneNumberDTO dto : result.getPhones()) {
                    cellPhoneList.addItem(dto.get_number());
                }
            }

            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::rpcService.getCellPhoneList");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                try {
                    throw caught;
                } catch (InvalidOperatorException e) {
                    Window.alert("ERROR: Can not get cellphone  (InvalidOperatorException): " + e.getMsg());
                } catch (Throwable e) {
                    // Inesperada
                    Window.alert("ERROR: Can not get cellphone : " + e.getMessage());
                }
            }
        });
    }



    /**
     * Obter o número de telefone seleccionado na list box.
     * 
     * @return numero telefone seleccionado na list box
     */
    public String getSelectedNumber() {
        final int selectIndex = getCellPhoneList().getSelectedIndex();
        return getCellPhoneList().getItemText(selectIndex);

    }

    /**
     * Gets the cell phone list.
     * 
     * @return the cell phone list
     */
    public ListBox getCellPhoneList() {
        return cellPhoneList;
    }



    public Anacom getAnacom() {
        return anacom;
    }

    /**
     * Sets the cell phone list.
     * 
     * @param cellPhoneList the new cell phone list
     */
    public void setCellPhoneList(ListBox cellPhoneList) {
        this.cellPhoneList = cellPhoneList;
    }

    /**
     * Gets the manage cell phone label.
     * 
     * @return the manage cell phone label
     */
    public Label getManageCellPhoneLabel() {
        return manageCellPhoneLabel;
    }

    /**
     * Sets the manage cell phone label.
     * 
     * @param manageCellPhoneLabel the new manage cell phone label
     */
    public void setManageCellPhoneLabel(Label manageCellPhoneLabel) {
        this.manageCellPhoneLabel = manageCellPhoneLabel;
    }


}
