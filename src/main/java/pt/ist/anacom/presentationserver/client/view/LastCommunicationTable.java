package pt.ist.anacom.presentationserver.client.view;

import pt.ist.anacom.shared.dto.CommunicationDTO;
import pt.ist.anacom.shared.dto.SMSDTO;
import pt.ist.anacom.shared.dto.SMSListDTO;

import com.google.gwt.user.client.ui.FlexTable;

public class LastCommunicationTable  extends FlexTable {

	public LastCommunicationTable() {
		addStyleName("lastCallTable");
		// add header row:
		setText(0, 0, "To");
		setText(0, 1, "Call Type");
		setText(0, 2, "Cost");
		setText(0, 3, "Lenght/Duration");
		// add style to row:
		getRowFormatter().addStyleName(0, "h2");
	}

	public void setTable(CommunicationDTO dto) {

		// get the number of the next row:
		int row = getRowCount();


		setText(row, 0, dto.get_destNumber());
		getCellFormatter().addStyleName(row, 0, "h2");

		setText(row, 1, dto.get_type());
		getCellFormatter().addStyleName(row, 0, "h2");
		
		setText(row, 2, dto.get_cost().toString());
		getCellFormatter().addStyleName(row, 0, "h2");
		
		setText(row, 3, dto.get_size().toString());
		getCellFormatter().addStyleName(row, 0, "h2");

	}

}
