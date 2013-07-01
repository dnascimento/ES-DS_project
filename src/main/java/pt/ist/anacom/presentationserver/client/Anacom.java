package pt.ist.anacom.presentationserver.client;

import pt.ist.anacom.presentationserver.client.view.ChargePanel;
import pt.ist.anacom.presentationserver.client.view.GetBalancePanel;
import pt.ist.anacom.presentationserver.client.view.GetModePanel;
import pt.ist.anacom.presentationserver.client.view.LastCommunicationPannel;
import pt.ist.anacom.presentationserver.client.view.MakeCallPannel;
import pt.ist.anacom.presentationserver.client.view.MobileSelectPanel;
import pt.ist.anacom.presentationserver.client.view.ReceiveSMSPanel;
import pt.ist.anacom.presentationserver.client.view.SendSMSPanel;
import pt.ist.anacom.presentationserver.client.view.SetModePanel;
import pt.ist.anacom.shared.dto.phone.PhoneDetailedDTO;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.CellPhoneAlreadyExistsException;
import pt.ist.anacom.shared.exceptions.InvalidNumberException;
import pt.ist.anacom.shared.exceptions.InvalidOperatorException;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Anacom
        implements EntryPoint {

    /** String when Local. */
    private static final String localServerType = "ES-only";
    /** String when Remove. */
    private static final String remoteServerType = "ES+SD";
    /** String when Remove. */
    private static final String remoteReplicatedServerType = "SD-RepliKate";

    /** AnacomServiceAsync Service. */
    private final AnacomServiceAsync rpcService = GWT.create(AnacomService.class);

    /** Qual o tipo de deploy actual */
    private static String serverType;


    /** Title. */
    private final Label serverTypeLabel = new Label("Anacom");



    /* Elementos da interface */
    /** Panel para seleccionar o telefone a usar. */
    private final MobileSelectPanel selectedMobilePanel = new MobileSelectPanel(this.rpcService, this);

    /** Painel para obter o saldo actual do telefone. */
    private final GetBalancePanel getBalancePanel = new GetBalancePanel(this.rpcService, this);

    /** Painel para carregar o telefone. */
    private final ChargePanel chargePanel = new ChargePanel(this.rpcService, this);

    /** Painel para obter o modo o telefone. */
    private final GetModePanel getModePanel = new GetModePanel(this.rpcService, this);

    /** Painel para alterar o modo o telefone. */
    private final SetModePanel setModePanel = new SetModePanel(this.rpcService, this);

    /** Painel para enviar sms do telefone. */
    private final SendSMSPanel sendSMSPanel = new SendSMSPanel(this.rpcService, this);


    /** Painel para receber sms do telefone. */
    private final ReceiveSMSPanel receiveSMSPanel = new ReceiveSMSPanel(this.rpcService, this);

    /** Painel para receber sms do telefone. */
    private final MakeCallPannel makeCallPanel = new MakeCallPannel(this.rpcService, this);

    /** Painel para obter a ultima chamada efectuada. */
    private final LastCommunicationPannel lastCallPanel = new LastCommunicationPannel(this.rpcService, this);



    /**
     * Método de inicialização do GWT. É aqui que é decidido qual o tipo de distribuição
     * utilizada. Consulta o header da html e consoante o que lá está escrito, inicia o
     * servidor. Se não encontrar nada escrito, inicia a remote senão utiliza a Local.
     **/
    public void onModuleLoad() {
        // create label with mode type
        serverTypeLabel.setStyleName("h1");
        if (RootPanel.get(remoteServerType) != null) {
            GWT.log("presentationserver.client.Anacom::onModuleLoad() running on remote mode");
            serverTypeLabel.setText("Anacom - Remote mode");
            serverType = remoteServerType;
        } else if (RootPanel.get(remoteReplicatedServerType) != null) {
            GWT.log("presentationserver.client.Anacom::onModuleLoad() running on Replicated remote mode");
            serverTypeLabel.setText("Anacom - Replicated Remote mode");
            serverType = remoteReplicatedServerType;
        } else { // default: local - even if it is misspelled
            GWT.log("presentationserver.client.Anacom::onModuleLoad() running on local mode");
            serverTypeLabel.setText("Anacom - Local mode");
            serverType = localServerType;
        }




        // Initializes the correspondent Bridge
        this.rpcService.initBridge(serverType, new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Carregar os Telefone para efeitos de demonstração
                loadTestPhones();


                // Load widgets Panels -> agora que carregou os dados, já pode inicializar
                // os paneis
                selectMobilePanelInit();
                panelGetBalanceInit();
                panelChargeInit();
                getModePanelInit();
                panelSetModeInit();
                panelSendSMSInit();
                panelReceiveSMSInit();
                panelLastCallInit();
                panelCallInit();
            }

            @Override
            public void onFailure(Throwable caught) {
                GWT.log("presentationserver.client.Anacom::onModuleLoad()::rpcService.initBridge");
                GWT.log("-- Throwable: '" + caught.getClass().getName() + "'");
                Window.alert("Not able to init aplication server bridge: " + caught.getMessage());
            }
        });

        GWT.log("presentationserver.client.Anacom::onModuleLoad() - done!");
    }


    /** Registar telemoveis para teste do cliente */
    protected void loadTestPhones() {
    	 if (serverType.equals(remoteReplicatedServerType)) {
	        final PhoneDetailedDTO phone1 = new PhoneDetailedDTO("911111111", 0, CellPhoneType.PHONE2G, "Red");
	        final PhoneDetailedDTO phone2 = new PhoneDetailedDTO("911111112", 0, CellPhoneType.PHONE3G, "Red");
	        final PhoneDetailedDTO phone3 = new PhoneDetailedDTO("931111111", 0, CellPhoneType.PHONE2G, "Orange");

	        registerCellPhone(phone1);
	        registerCellPhone(phone2);
	        registerCellPhone(phone3);
    	 }else{
    		 final PhoneDetailedDTO phone4 = new PhoneDetailedDTO("911111111", 0, CellPhoneType.PHONE2G, "Red");
    		 final PhoneDetailedDTO phone5 = new PhoneDetailedDTO("911111112", 0, CellPhoneType.PHONE3G, "Red");
    		 final PhoneDetailedDTO phone6 = new PhoneDetailedDTO("931111111", 0, CellPhoneType.PHONE2G, "Orange");
    		 registerCellPhone(phone4);
    		 registerCellPhone(phone5);
    		 registerCellPhone(phone6);
        // se não estivermos em replicate, podemos carregar outras operadoras
       
           
        }
    }

    protected void registerCellPhone(PhoneDetailedDTO dto) {
        GWT.log("presentationserver.client.Anacom::registerCellPhone: " + dto.get_number() + " : " + dto.get_operator());
        rpcService.addCellPhone(dto, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                try {
                    throw caught;
                } catch (InvalidOperatorException e) {
                    GWT.log("presentationserver.client.Anacom::RegisterCellPhone:InvalidOperatorException");
                } catch (CellPhoneAlreadyExistsException e) {
                    GWT.log("presentationserver.client.Anacom::RegisterCellPhone:InvalidOperatorException");
                } catch (InvalidNumberException e) {
                    GWT.log("presentationserver.client.Anacom::RegisterCellPhone:InvalidNumberException");
                } catch (Throwable e) {
                    GWT.log("presentationserver.client.Anacom::RegisterCellPhone: " + e.getClass().toString());
                }
            }

            @Override
            public void onSuccess(Void result) {
                GWT.log("presentationserver.client.Anacom::RegisterCellPhone:sucessfull");
            }
        });
    }


    /** Inicializar o painel de selecção de nº de telemovel. */
    public void selectMobilePanelInit() {
        final RootPanel containerPanel = RootPanel.get("selectMobileContainer");
        containerPanel.add(selectedMobilePanel);
        selectedMobilePanel.setWidth("100%");

        // Update list on load
        selectedMobilePanel.refreshMobileList(serverType);
    }

    /** Get CellPhone Balance Panels. */
    private void panelGetBalanceInit() {
        final RootPanel containerPanel = RootPanel.get("getBalancePanelContainer");
        containerPanel.add(getBalancePanel);
        getBalancePanel.setWidth("100%");
    }


    /** Painel de envio de carregar o telemóvel. */
    public void panelChargeInit() {
        final RootPanel containerPanel = RootPanel.get("chargePanelContainer");
        containerPanel.add(chargePanel);
        chargePanel.setWidth("100%");
    }


    /** Obter o modo actual do telemovel */
    public void getModePanelInit() {
        final RootPanel containerPanel = RootPanel.get("modeContainer");
        containerPanel.add(getModePanel);
        getModePanel.setWidth("100%");
    }





    private void panelSetModeInit() {
        final RootPanel containerPanel = RootPanel.get("changeModeContainer");
        containerPanel.add(setModePanel);
        setModePanel.setWidth("100%");
    }





    /** Painel de envio de SMS. */
    private void panelSendSMSInit() {
        final RootPanel containerPanel = RootPanel.get("sendSMSContainer");
        containerPanel.add(sendSMSPanel);
        sendSMSPanel.setWidth("100%");
    }





    /** Painel de receber de SMS. */
    public void panelReceiveSMSInit() {
        final RootPanel containerPanel = RootPanel.get("receiveSMSContainer");
        containerPanel.add(receiveSMSPanel);
        receiveSMSPanel.setWidth("100%");
    }


    /** Painel para efectuar uma chamada */
    public void panelCallInit() {
        final RootPanel containerPanel = RootPanel.get("makeCallContainer");
        containerPanel.add(makeCallPanel);
        lastCallPanel.setWidth("100%");
    }

    /** Painel da ultima chamada */
    public void panelLastCallInit() {
        final RootPanel containerPanel = RootPanel.get("lastCallContainer");
        containerPanel.add(lastCallPanel);
        lastCallPanel.setWidth("100%");
    }





    public String getCellPhoneBeingManaged() {
        return selectedMobilePanel.getSelectedNumber();
    }


    public Label getServerTypeLabel() {
        return serverTypeLabel;
    }


    public MobileSelectPanel getSelectedMobilePanel() {
        return selectedMobilePanel;
    }


    public GetBalancePanel getGetBalancePanel() {
        return getBalancePanel;
    }


    public ChargePanel getChargePanel() {
        return chargePanel;
    }


    public GetModePanel getGetModePanel() {
        return getModePanel;
    }


    public SetModePanel getSetModePanel() {
        return setModePanel;
    }


    public SendSMSPanel getSendSMSPanel() {
        return sendSMSPanel;
    }


    public ReceiveSMSPanel getReceiveSMSPanel() {
        return receiveSMSPanel;
    }

    public MakeCallPannel getMakeCallPanelPanel() {
        return makeCallPanel;
    }

    public LastCommunicationPannel getLastCommunicationPannel() {
        return lastCallPanel;
    }
}
