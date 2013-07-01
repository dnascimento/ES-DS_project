package pt.ist.anacom.test.services;

import java.util.Set;

import junit.framework.TestCase;
import pt.ist.anacom.domain.CellPhone;
import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.domain.Operator;
import pt.ist.anacom.domain.Plan;
import pt.ist.anacom.domain.network.Network2G;
import pt.ist.anacom.domain.network.Network3G;
import pt.ist.anacom.shared.enumerados.CellPhoneMode;
import pt.ist.anacom.shared.enumerados.CellPhoneType;
import pt.ist.anacom.shared.exceptions.AnaComException;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.Config.RepositoryType;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.Transaction;

public abstract class FFServiceTestCase extends
TestCase {
	static {
		if (FenixFramework.getConfig() == null) {
			FenixFramework.initialize(new Config() {
				{
					dbAlias = "/tmp/test-db";
					domainModelPath = "src/main/dml/anacom.dml";
					repositoryType = RepositoryType.BERKELEYDB;
					rootClass = NetworkManager.class;
				}
			});
		}
	}

	protected FFServiceTestCase(String msg) {
		super(msg);
	}

	protected FFServiceTestCase() {
		super();
	}

	protected void setUp() {
		cleanNetworkManager();
	}

	protected void tearDown() {
		cleanNetworkManager();
	}


	protected void cleanNetworkManager() {
		boolean commited = false;
		try {
			Transaction.begin();

			NetworkManager nm = FenixFramework.getRoot();
			Set<Operator> allOperators = nm.getOperatorSet();
			allOperators.clear();

			Transaction.commit();
			commited = true;
		} finally {
			if (!commited) {
				Transaction.abort();
				fail("Network manager não foi reiniciado");
			}
		}
	}




	/*
	 * Abstracção addOperator JUnit
	 */
	 protected void addOperator(String name, String prefix, int smsTariff, int voiceTariff, int videoTariff, double extraChargeTariff, double bonusTariff) {
		 boolean commited = false;
		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();

			 Plan pl = new Plan(smsTariff, voiceTariff, videoTariff, extraChargeTariff, bonusTariff);
			 Operator op = new Operator(name, prefix, pl);

			 nm.addOperator(op);

			 Transaction.commit();
			 commited = true;

		 } finally {
			 if (!commited) {
				 Transaction.abort();
				 fail("Erro a criar operador");
			 }
		 }

	 }

	 protected void addOperatorSimple(String name, String prefix) {
		 boolean committed = false;
		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();
			 try {
				 Plan staticPlan = new Plan(5, 10, 15, 100, 0);
				 nm.addOperator(new Operator(name, prefix, staticPlan));
			 } catch (Exception e) {
			 }
			 Transaction.commit();
			 committed = true;
		 } finally {
			 if (!committed) {
				 Transaction.abort();
				 fail("Could not add new operator: " + name);
			 }
		 }
	 }

	 /**
	  * True se operador faz parte da rede. False otherwise
	  * 
	  * @param name
	  * @return
	  */
	 protected boolean checkOperatorByName(String name) {
		 boolean committed = false;
		 boolean res = false;
		 try {
			 Transaction.begin();

			 NetworkManager nm = FenixFramework.getRoot();
			 try {
				 res = nm.hasOperator(nm.getOperatorByName(name));
			 } catch (AnaComException e) {
				 res = false;
			 }

			 Transaction.commit();
			 committed = true;

			 return res;

		 } finally {
			 if (!committed) {
				 Transaction.abort();
				 fail("Could not get operator: " + name);
			 }
		 }
	 }



	 protected void addCellPhone(String operatorName, String number, CellPhoneType cellPhoneType, int balance) {
		 boolean commited = false;

		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();

			 Operator operator = nm.getOperatorByName(operatorName);

			 switch (cellPhoneType) {
			 case PHONE2G:
				 operator.addCellPhone(new Network2G(number, balance));
				 break;
			 case PHONE3G:
				 operator.addCellPhone(new Network3G(number, balance));
				 break;
			 default:
				 throw new IllegalArgumentException("FFServiceTestCase: Tipo de telefone inválido");
			 }

			 Transaction.commit();
			 commited = true;

		 } catch (Exception e) {
			 System.out.println("FFServiceTestCase: " + e.getMessage());
		 } finally {
			 if (!commited) {
				 Transaction.abort();
				 fail("FFServiceTestCase: Erro ao criar telemóvel");
			 }
		 }
	 }

	 protected int getPhoneBalance(String number) {
		 CellPhone phone;
		 boolean committed = false;
		 int balance;

		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();
			 phone = nm.getOperatorByPhoneNumber(number).getCellPhoneByNumber(number);
			 balance = phone.getBalance();
			 Transaction.commit();
			 committed = true;
			 return balance;
		 } finally {
			 if (!committed) {
				 Transaction.abort();
				 fail("Could not get balance from cellphone number " + number);
			 }
		 }
	 }


	 /*
	  * checkReceivedCommunication - Verifica quantas chamadas recebidas o telemovel tem
	  * Utilizado para: SendSMSService,
	  * 
	  * @author gpestana
	  * 
	  * @param Nome operador e nr de telemóvel a verificar comunicação
	  */

	 protected int checkReceivedCommunication(String operatorName, String cellPhoneNumber) {
		 boolean commited = false;
		 int nrReceivedCom = 0;

		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();

			 Operator operator = nm.getOperatorByName(operatorName);
			 CellPhone cellPhone = operator.getCellPhoneByNumber(cellPhoneNumber);

			 nrReceivedCom = cellPhone.getReceivedSMSCount();

			 Transaction.commit();
			 commited = true;

		 } catch (AnaComException e) {
			 System.out.println(e.getMessage());
		 } finally {
			 if (!commited) {
				 Transaction.abort();
				 fail("Erro a fazer verificação de Comunicação recebida");
			 }
		 }

		 return nrReceivedCom;

	 }

	 /*
	  * checkMadeCommunication - Verifica quantas chamadas feitas o telemovel tem Utilizado
	  * para: SendSMSService,
	  * 
	  * @author gpestana
	  * 
	  * @param Nome operador e nr de telemóvel a verificar comunicação
	  */

	 protected int checkMadeCommunication(String operatorName, String cellPhoneNumber) {
		 boolean commited = false;
		 int nrMadeCom = 0;

		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();

			 Operator operator = nm.getOperatorByName(operatorName);
			 CellPhone cellPhone = operator.getCellPhoneByNumber(cellPhoneNumber);

			 nrMadeCom = cellPhone.getMadeSMSCount();

			 Transaction.commit();
			 commited = true;

		 } catch (AnaComException e) {
			 System.out.println(e.getMessage());
		 } finally {
			 if (!commited) {
				 Transaction.abort();
				 fail("Erro a fazer verificação de Comunicação feita");
			 }
		 }

		 return nrMadeCom;

	 }


	 /**
	  * Muda o estado de um dado telemóvel
	  * 
	  * @param cellPhone
	  * @param busy
	  */

	 public void changeCellPhoneMode(String operatorName, String cellPhoneNr, CellPhoneMode mode) {

		 boolean committed = false;

		 try {
			 Transaction.begin();
			 NetworkManager nm = FenixFramework.getRoot();
			 Operator operator = nm.getOperatorByName(operatorName);
			 CellPhone cellPhone = operator.getCellPhoneByNumber(cellPhoneNr);

			 switch (mode) {
			 case ON:
				 cellPhone.changeMode(CellPhoneMode.ON);
				 break;
			 case OFF:
				 cellPhone.changeMode(CellPhoneMode.OFF);
				 break;
			 case SILENCE:
				 cellPhone.changeMode(CellPhoneMode.SILENCE);
				 break;
			 case BUSY:
				 cellPhone.changeMode(CellPhoneMode.BUSY);
				 break;
			 }

			 Transaction.commit();
			 committed = true;

		 } catch (AnaComException e) {
			 System.out.println(e.getMessage());
		 } finally {
			 if (!committed) {
				 Transaction.abort();
				 fail("Could not change cellphone mode");
			 }
		 }

	 }


}
