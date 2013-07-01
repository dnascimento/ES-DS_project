package pt.ist.anacom.test.replication;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

import org.hamcrest.core.AllOf;
import org.hamcrest.core.AnyOf;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import pt.ist.anacom.presentationserver.server.ReplicateLogicV3;
import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;
import pt.ist.anacom.shared.stubs.CellPhoneNotExistsException;
import pt.ist.anacom.shared.stubs.InvalidBalanceException;
import pt.ist.anacom.shared.stubs.InvalidOperatorException_Exception;
import pt.ist.anacom.shared.stubs.OperatorNotFoundException;
import pt.ist.anacom.shared.stubs.PhoneNumValueDTOType;
import pt.ist.anacom.shared.stubs.PhoneNumberDTOType;
import pt.ist.anacom.shared.stubs.SimpleExceptionType;

import org.jmock.integration.junit3.MockObjectTestCase;

import com.sleepycat.je.SequenceNotFoundException;

import pt.ist.anacom.shared.dto.phone.PhoneNumValueDTO;
import pt.ist.anacom.shared.stubs.AnacomApplicationServerPortType;

public class JmockTestCase extends MockObjectTestCase {

	private static boolean DEBUG = false;
	private static String correctNumber;
	private static String wrongNumber;

	private static long correctsequenceNumber;
	private static long OldSequenceNumber;
	private static long BizantineSequenceNumber;

	private static int correctBalance;
	private static int wrongBalance;

	private int newBalance;

	private static pt.ist.anacom.shared.stubs.InvalidBalanceException correctException;
	private static pt.ist.anacom.shared.stubs.CellPhoneNotExistsException wrongException;
	private static pt.ist.anacom.shared.stubs.CellPhoneNotExistsException outOfDateException;

	private static ArrayList<AnacomApplicationServerPortType> portListNormal;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantine;
	private static ArrayList<AnacomApplicationServerPortType> portListOneSilent;
	private static ArrayList<AnacomApplicationServerPortType> portListOneSilentAndOutOfDate;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantineAndOutOfDate;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantineAndEqualOutOfDate;

	private static ArrayList<AnacomApplicationServerPortType> portListNormalException;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantineException;
	private static ArrayList<AnacomApplicationServerPortType> portListOneSilentException;
	private static ArrayList<AnacomApplicationServerPortType> portListOneSilentAndOutOfDateException;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantineAndOutOfDateException;
	private static ArrayList<AnacomApplicationServerPortType> portListOneBizantineAndEqualOutOfDateException;

	private static ArrayList<AnacomApplicationServerPortType> portListAckNormal;
	private static ArrayList<AnacomApplicationServerPortType> portListAckExceptions;
	private static ArrayList<AnacomApplicationServerPortType> portListAckExceptionsAndOneAck;

	private static PhoneNumberDTOType leituraDto;
	private static PhoneNumValueDTOType escritaDto;

	private static AnacomApplicationServerPortType mockCorrect;
	private static AnacomApplicationServerPortType mockBizantine;
	private static AnacomApplicationServerPortType mockOutOfDate;
	private static AnacomApplicationServerPortType mockSilent;
	private static AnacomApplicationServerPortType mockCorrectException;
	private static AnacomApplicationServerPortType mockBizantineException;
	private static AnacomApplicationServerPortType mockOutOfDateException;
	private static AnacomApplicationServerPortType mockAck;

	private static Response<PhoneNumValueDTOType> correctAnswear;
	private static Response<PhoneNumValueDTOType> bizantineAnswear;
	private static Response<PhoneNumValueDTOType> outOfDateAnswear;
	private static Response<PhoneNumValueDTOType> silentAnswear;

	private static Response<PhoneNumValueDTOType> correctExceptionAnswear;
	private static Response<PhoneNumValueDTOType> bizantineExceptionAnswear;
	private static Response<PhoneNumValueDTOType> outOfDateExceptionAnswear;
	private static Response<?> ackAnswear;
	private static Response <PhoneNumberDTOType> correctSequenceAnswear;

	@Override
	protected void setUp() throws Exception {

		
		correctNumber = "917236942";
		correctsequenceNumber = 1337;

		wrongNumber = "666666666";
		BizantineSequenceNumber = 6666;

		OldSequenceNumber = 123;

		newBalance = 69;


		correctBalance = 1000;
		wrongBalance = 300;

		escritaDto = new PhoneNumValueDTOType();
		escritaDto.setNumber(correctNumber);
		escritaDto.setBalance(newBalance);

		leituraDto = new PhoneNumberDTOType();
		leituraDto.setNumber(correctNumber);

		mockCorrect = mock(AnacomApplicationServerPortType.class, "Correct");
		mockBizantine = mock(AnacomApplicationServerPortType.class, "Bizantine");
		mockOutOfDate = mock(AnacomApplicationServerPortType.class, "Out-of-Date");
		mockSilent = mock(AnacomApplicationServerPortType.class, "Silent");
		mockCorrectException = mock(AnacomApplicationServerPortType.class,
				"Correct Exception");
		mockBizantineException = mock(AnacomApplicationServerPortType.class,
				"Bizantine Exception");
		mockOutOfDateException = mock(AnacomApplicationServerPortType.class,
				"Out-of-Date Exception");
		mockAck = mock(AnacomApplicationServerPortType.class, "ACK");

		SimpleExceptionType iE = new SimpleExceptionType();
		iE.setSequenceNumber(correctsequenceNumber);
		iE.setError("Correct Exception");
		correctException = new InvalidBalanceException("Correct InvalidBalance", iE);

		SimpleExceptionType simpleOOD = new SimpleExceptionType();
		simpleOOD.setSequenceNumber(OldSequenceNumber);
		simpleOOD.setError("Out-of-Date");
		outOfDateException = new CellPhoneNotExistsException("CellPhoneExists Old", simpleOOD);

		SimpleExceptionType iE1 = new SimpleExceptionType();
		iE1.setSequenceNumber(BizantineSequenceNumber);
		iE1.setError("Bizantine");
		wrongException = new CellPhoneNotExistsException("CellPhone Not Exists", iE1);

	

		//Async Response Simulator's
		correctAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();

				ret.setBalance(correctBalance);
				ret.setNumber(correctNumber);
				ret.setSequenceNumber(correctsequenceNumber);

				return ret;
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		bizantineAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {

				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
				ret.setBalance(wrongBalance);
				ret.setNumber(wrongNumber);
				ret.setSequenceNumber(BizantineSequenceNumber);

				return ret;
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		outOfDateAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();

				ret.setBalance(wrongBalance);
				ret.setNumber(wrongNumber);
				ret.setSequenceNumber(OldSequenceNumber);

				return ret;
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		silentAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return false;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				throw new InterruptedException();
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		correctExceptionAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {

				throw new ExecutionException(correctException);
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		bizantineExceptionAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				throw new ExecutionException(wrongException);
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		outOfDateExceptionAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				throw new ExecutionException(outOfDateException);
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};

		ackAnswear = new Response<PhoneNumValueDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumValueDTOType get() throws InterruptedException,
			ExecutionException {
				throw new NullPointerException("ACK");
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};
		
		correctSequenceAnswear = new Response<PhoneNumberDTOType>() {

			@Override
			public boolean isDone() {
				return true;
			}
			@Override
			public boolean isCancelled() {
				return false;
			}
			@Override
			public PhoneNumberDTOType get(long timeout, TimeUnit unit)
					throws InterruptedException, ExecutionException, TimeoutException {
				return null;
			}
			@Override
			public PhoneNumberDTOType get() throws InterruptedException,
			ExecutionException {
				return leituraDto;
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				return false;
			}

			@Override
			public Map<String, Object> getContext() {
				return null;
			}
		};
		/***********************************************************************************************************/
		//Port Simulator's (jMock Object's expectations)
		checking(new Expectations(){{
			allowing(mockCorrect).getBalanceAsync(leituraDto); will(returnValue(correctAnswear));
			allowing (mockBizantine).getBalanceAsync(leituraDto); will(returnValue(bizantineAnswear));
			allowing (mockOutOfDate).getBalanceAsync(leituraDto); will(returnValue(outOfDateAnswear));
			allowing (mockSilent).getBalanceAsync(leituraDto); will(returnValue(silentAnswear));
			allowing (mockCorrectException).getBalanceAsync(leituraDto); will(returnValue(correctExceptionAnswear));
			allowing (mockOutOfDateException).getBalanceAsync(leituraDto); will(returnValue(outOfDateExceptionAnswear));
			allowing (mockBizantineException).getBalanceAsync(leituraDto); will(returnValue(bizantineExceptionAnswear));
			allowing (mockAck).addBalanceAsync(with(any(PhoneNumValueDTOType.class))); will(returnValue(ackAnswear));
			allowing (mockCorrectException).addBalanceAsync(with(any(PhoneNumValueDTOType.class))); will(returnValue(correctExceptionAnswear));
			allowing (mockAck).getSequenceNumberAsync(with(any(PhoneNumberDTOType.class))); will(returnValue(correctSequenceAnswear));
			allowing (mockCorrectException).getSequenceNumberAsync(with(any(PhoneNumberDTOType.class))); will(returnValue(correctSequenceAnswear));
			
		}});

		portListNormal = new ArrayList<AnacomApplicationServerPortType>();
		portListNormal.ensureCapacity(4);
		portListNormal.add(mockCorrect);
		portListNormal.add(mockCorrect);
		portListNormal.add(mockCorrect);
		portListNormal.add(mockCorrect);

		portListNormalException = new ArrayList<AnacomApplicationServerPortType>();
		portListNormalException.ensureCapacity(4);
		portListNormalException.add(mockCorrectException);
		portListNormalException.add(mockCorrectException);
		portListNormalException.add(mockCorrectException);
		portListNormalException.add(mockCorrectException);

		portListOneBizantine = new ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantine.ensureCapacity(4);
		portListOneBizantine.add(mockBizantine);
		portListOneBizantine.add(mockCorrect);
		portListOneBizantine.add(mockCorrect);
		portListOneBizantine.add(mockCorrect);

		portListOneBizantineException = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantineException.ensureCapacity(4);
		portListOneBizantineException.add(mockBizantineException);
		portListOneBizantineException.add(mockCorrectException);
		portListOneBizantineException.add(mockCorrectException);
		portListOneBizantineException.add(mockCorrectException);

		portListOneSilent = new ArrayList<AnacomApplicationServerPortType>();
		portListOneSilent.ensureCapacity(4);
		portListOneSilent.add(mockSilent);
		portListOneSilent.add(mockCorrect);
		portListOneSilent.add(mockCorrect);
		portListOneSilent.add(mockCorrect);

		portListOneSilentAndOutOfDate = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneSilentAndOutOfDate.ensureCapacity(4);
		portListOneSilentAndOutOfDate.add(mockSilent);
		portListOneSilentAndOutOfDate.add(mockOutOfDate);
		portListOneSilentAndOutOfDate.add(mockCorrect);
		portListOneSilentAndOutOfDate.add(mockCorrect);

		portListOneSilentException = new ArrayList<AnacomApplicationServerPortType>();
		portListOneSilentException.ensureCapacity(4);
		portListOneSilentException.add(mockSilent);
		portListOneSilentException.add(mockCorrectException);
		portListOneSilentException.add(mockCorrectException);
		portListOneSilentException.add(mockCorrectException);

		portListOneBizantineAndOutOfDate = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantineAndOutOfDate.ensureCapacity(4);
		portListOneBizantineAndOutOfDate.add( mockOutOfDate);
		portListOneBizantineAndOutOfDate.add(mockBizantine);
		portListOneBizantineAndOutOfDate.add(mockCorrect);
		portListOneBizantineAndOutOfDate.add(mockCorrect);

		portListOneBizantineAndEqualOutOfDate = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantineAndEqualOutOfDate.ensureCapacity(4);
		portListOneBizantineAndEqualOutOfDate.add(mockOutOfDate);
		portListOneBizantineAndEqualOutOfDate.add(mockOutOfDate);
		portListOneBizantineAndEqualOutOfDate.add(mockCorrect);
		portListOneBizantineAndEqualOutOfDate.add( mockCorrect);

		portListOneSilentAndOutOfDateException = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneSilentAndOutOfDateException.ensureCapacity(4);
		portListOneSilentAndOutOfDateException.add(mockSilent);
		portListOneSilentAndOutOfDateException.add( mockOutOfDateException);
		portListOneSilentAndOutOfDateException.add( mockCorrectException);
		portListOneSilentAndOutOfDateException.add( mockCorrectException);

		portListOneBizantineAndOutOfDateException = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantineAndOutOfDateException.ensureCapacity(4);
		portListOneBizantineAndOutOfDateException.add(mockBizantineException);
		portListOneBizantineAndOutOfDateException.add(mockOutOfDateException);
		portListOneBizantineAndOutOfDateException.add(mockCorrectException);
		portListOneBizantineAndOutOfDateException.add(mockCorrectException);

		portListOneBizantineAndEqualOutOfDateException = new
				ArrayList<AnacomApplicationServerPortType>();
		portListOneBizantineAndEqualOutOfDateException.ensureCapacity(4);
		portListOneBizantineAndEqualOutOfDateException.add(mockOutOfDateException);
		portListOneBizantineAndEqualOutOfDateException.add(mockOutOfDateException);
		portListOneBizantineAndEqualOutOfDateException.add(mockCorrectException);
		portListOneBizantineAndEqualOutOfDateException.add(mockCorrectException);

		portListAckNormal = new ArrayList<AnacomApplicationServerPortType>();
		portListAckNormal.ensureCapacity(4);
		portListAckNormal.add(mockAck);
		portListAckNormal.add(mockAck);
		portListAckNormal.add(mockAck);
		portListAckNormal.add(mockAck);

		portListAckExceptions = new ArrayList<AnacomApplicationServerPortType>();
		portListAckExceptions.ensureCapacity(4);
		portListAckExceptions.add(mockCorrectException);
		portListAckExceptions.add(mockCorrectException);
		portListAckExceptions.add(mockCorrectException);
		portListAckExceptions.add(mockCorrectException);

		portListAckExceptionsAndOneAck = new
				ArrayList<AnacomApplicationServerPortType>();
		portListAckExceptionsAndOneAck.ensureCapacity(4);
		portListAckExceptionsAndOneAck.add(mockAck);
		portListAckExceptionsAndOneAck.add(mockCorrectException);
		portListAckExceptionsAndOneAck.add(mockCorrectException);
		portListAckExceptionsAndOneAck.add(mockCorrectException);


	}

	//Todas as respostas iguais.
	public void testNormal() {

		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;
		if(DEBUG) System.out.println("\n--------\nTest Read Normal Case:");
		try {
			resp = client.getBalance(portListNormal, leituraDto);
		} catch (InvalidOperatorException_Exception e) {
			fail("Mandou InvalidOperatorException_Exception");
		} catch (CellPhoneNotExistsException e) {
			fail("Mandou CellPhoneNotExistsException");
		} catch (OperatorNotFoundException e) {
			fail("Mandou OperatorNotFoundException");
		} catch (InvalidBalanceException e) {
			fail("Mandou InvalidBalanceException");
		} catch (InterruptedException e) {
			fail("Mandou InterruptedException");
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());

	}
	//Uma resposta bizantina com seq. numb. superior
	public void testOneBizantine() {

		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;
		if(DEBUG) System.out.println("\n--------\nTest Read Bizantine Case:");
		try {
			resp = client.getBalance(portListOneBizantine, leituraDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());

	}
	//Todas as respostas iguais com excepções
	public void testNormalException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();

		if(DEBUG) System.out.println("\n--------\nTest Read Normal Exception Case:");
		try {
			client.getBalance(portListNormalException, leituraDto);
			fail("Não mandou excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Correct Message:" + "Exc:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}

	}
	//Uma resposta silenciosa
	public void testSilent() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;

		if(DEBUG) System.out.println("\n--------\nTest Read Silent Case:");

		try {

			resp = client.getBalance(portListOneSilent, leituraDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());
	}
	//Uma resposta silenciosa e outra errada com seq. numb. inferior
	public void testOneSilentAndOutOfDate() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;

		if(DEBUG) System.out.println("\n--------\nTest Read Silent & OOD Case:");

		try {

			resp = client.getBalance(portListOneSilentAndOutOfDate, leituraDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());
	}
	//Uma resposta bizantina e outra antiga.
	public void testOneBizantineAndOutOfDate() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;

		if(DEBUG) System.out.println("\n--------\nTest Read Bizantine & OOD Case:");

		try {

			resp = client.getBalance(portListOneBizantineAndOutOfDate, leituraDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());
	}
	//Uma resposta bizantina e outra antiga ambos com o mesmo valor
	public void testOneBizantineAndEqualOutOfDate() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		PhoneNumValueDTOType resp = null;

		if(DEBUG) System.out.println("\n--------\nTest Read Bizantine && OOD Equal Case:");

		try {

			resp = client.getBalance(portListOneBizantineAndEqualOutOfDate, leituraDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Correct Message: Balance:" + resp.getBalance() + "SN:" + resp.getSequenceNumber());
		
		assertEquals(correctBalance, resp.getBalance());
		assertEquals(correctsequenceNumber, resp.getSequenceNumber());
	}
	//Uma bizantina a mandar excepção e as outras correctas excepções
	public void testOneBizantineException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Read Bizantine Exception Case:");
		try {
			client.getBalance(portListOneBizantineException, leituraDto);
			fail("não Mandou Excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			
			if(DEBUG) System.out.println("Correct Message:" + e.getClass());
			
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
	}
	//uma Silenciosa e o resto com excepções
	public void testOneSilentException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Read Silent Exception Case:");

		try {
			client.getBalance(portListOneSilentException, leituraDto);
			fail("não Mandou Excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Correct Message:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
	}
	//Uma Silenciosa e outra antiga com excepções
	public void testOneSilentAndOutOfDateException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Read One Silent And Out Of Date Exception Case:");

		try {
			client.getBalance(portListOneSilentAndOutOfDateException, leituraDto);
			fail("não Mandou Excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Correct Message:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
	}
	//Uma Bizantina com excepção e uma outofdate com excepção
	public void testOneBizantineAndOutOfDateException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Read One Silent And Out Of Date Exception Case:");

		try {
			client.getBalance(portListOneBizantineAndOutOfDateException, leituraDto);
			fail("não Mandou Excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Correct Message:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
	}
	//Duas respostas antigas com excepção (bizantina e antiga) e as certas (excp)
	public void testOneBizantineAndEqualOutOfDateException() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Read One Silent And Out Of Date Exception Case:");

		try {
			client.getBalance(portListOneBizantineAndEqualOutOfDateException,
					leituraDto);
			fail("não Mandou Excepção");
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Correct Message:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
	}

	public void testPortListAckNormal() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		
		if(DEBUG) System.out.println("\n--------\nTest Write Ack Normal Case:");

		try {
			client.addBalance(portListAckNormal, escritaDto);
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			fail("Mandou Throwable");
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}
		if(DEBUG) System.out.println("Succesfull Write!");

	}

	public void testAckExceptionsAndOneAck() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Write Ack Exceptions And One Normal Ack:");

		try {
			client.addBalance(portListAckExceptionsAndOneAck, escritaDto);
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Failed Write:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}

	}

	public void testAckExceptions() {
		ReplicateLogicV3 client = new ReplicateLogicV3();
		if(DEBUG) System.out.println("\n--------\nTest Write Ack Exceptions Case:");

		try {
			client.addBalance(portListAckExceptions, escritaDto);
		} catch (pt.ist.anacom.shared.stubs.InvalidBalanceException e) {
			if(DEBUG) System.out.println("Failed Write:" + e.getClass());
			assertTrue(e.equals(correctException));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Mandou Throwable");
		}

	}

}