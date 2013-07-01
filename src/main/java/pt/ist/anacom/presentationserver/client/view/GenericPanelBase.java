package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.presentationserver.client.AnacomServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * The Class MobileSelectPainel.
 */
public class GenericPanelBase extends
        DecoratorPanel {

    /** RPC Service do painel. */
    private final AnacomServiceAsync rpcService;

    /** The manage cell phone panel. */
    private VerticalPanel mainPanel;

    /** Titulo do Menu */
    private Label title;


    /**
     * Instantiates a new mobile select painel.
     * 
     * @param rpcServiceArg the rpc service that this panel will use
     */
    public GenericPanelBase(AnacomServiceAsync rpcServiceArg) {
        GWT.log("presentationserver.client.view.SelectMobilePanel::constructor()");
        this.rpcService = rpcServiceArg;


        this.mainPanel = new VerticalPanel();
        this.title = new Label("Manage Cellphone");


        // Set label style
        this.title.setStyleName("h1");

        // Add to panel
        this.mainPanel.add(title);


        // Add the container panel to super painel
        this.add(this.mainPanel);


    }


}
