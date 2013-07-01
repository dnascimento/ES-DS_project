package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;

import com.google.gwt.user.client.ui.FlexTable;

public class InboxTable extends
        FlexTable {

    public InboxTable() {
        addStyleName("inboxTable");
        // add header row:
        setText(0, 0, "Source Number");
        setText(0, 1, "Message");
        // add style to row:
        getRowFormatter().addStyleName(0, "h2");
    }

    public void setInbox(SMSListDTO dto) {

        // get the number of the next row:
        int row = getRowCount();

        for (SMSDTO sms : dto.getSMSList()) {
            // add number and message (and set style from CSS)
            setText(row, 0, sms.getSrcNumber());
            getCellFormatter().addStyleName(row, 0, "h2");

            setText(row, 1, sms.getText());
            getCellFormatter().addStyleName(row, 0, "h2");

            row++;
        }
    }


}
