package pt.ist.anacom.test.replication;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.Response;

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

public class SilentFaultTestCase extends MockObjectTestCase {
	
	public SilentFaultTestCase(){
		
	}
//	
//	public void testOneSilentFaultAndOneOutOfDate() {
//		
//		
//		ReplicateLogicV3 client = new ReplicateLogicV3(); 
//		
//		PhoneNumValueDTO dto = new PhoneNumValueDTO();
//		dto.set_number("917236942");
//		dto.setBalance(0);
//		
//		final ArrayList<AnacomApplicationServerPortType> mockList = new ArrayList<AnacomApplicationServerPortType>();
//		
//		final AnacomApplicationServerPortType mockNormal1 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server1");
//		final AnacomApplicationServerPortType mockNormal2 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server2");
//		final AnacomApplicationServerPortType mockNormal3 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server3");
//		final AnacomApplicationServerPortType mockNormalSilent = mock(AnacomApplicationServerPortType.class, "Silent Mock Server");
//
//		final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//		
//		mockList.add(mockNormal1);
//		mockList.add(mockNormal2);
//		mockList.add(mockNormal3);
//		mockList.add(mockNormalSilent);
//		
//	
//		final Response<PhoneNumValueDTOType> silentAsyncMock = new Response<PhoneNumValueDTOType>(){
//
//			@Override
//			public boolean isDone() {
//				return false;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				return null;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> loudAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//				
//				ret.setBalance(69);
//				ret.setNumber("917236942");
//				ret.setSequenceNumber(1);
//				
//				return ret;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> delayedAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//				
//				ret.setBalance(69);
//				ret.setNumber("910000000");
//				ret.setSequenceNumber(0);
//				return ret;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		
//		checking(new Expectations(){{
//			// Fazer com que haja um a nao responder e o resto a responder
//			oneOf (mockNormalSilent).getBalanceAsync(msg); will(returnValue(silentAsyncMock));
//			oneOf (mockNormal1).getBalanceAsync(msg); will(returnValue(delayedAsyncMock));
//			oneOf (mockNormal2).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//			oneOf (mockNormal3).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//		}});
//
//		//execute
//		PhoneNumValueDTOType resp = null;
//		try {
//			resp = client.getBalance(mockList, msg);
//		} catch (InvalidOperatorException_Exception e) {
//			fail("Mandou InvalidOperatorException_Exception");	
//		} catch (CellPhoneNotExistsException e) {
//			fail("Mandou CellPhoneNotExistsException");
//		} catch (OperatorNotFoundException e) {
//			fail("Mandou OperatorNotFoundException");
//		} catch (InvalidBalanceException e) {
//			fail("Mandou InvalidBalanceException");
//		} catch (InterruptedException e) {
//			fail("Mandou InterruptedException");
//		} catch (Throwable e) {
//			fail("Mandou Throwable");
//		}
//		assertEquals(69, resp.getBalance());
//		assertEquals(1, resp.getSequenceNumber());
//		
//	}
//	
//	public void testOneSilentFaultAndOneOutOfDateAndTwoException(){
//
//		
//		ReplicateLogicV3 client = new ReplicateLogicV3(); 
//		
//		PhoneNumValueDTO dto = new PhoneNumValueDTO();
//		dto.set_number("917236942");
//		dto.setBalance(0);
//		
//		final ArrayList<AnacomApplicationServerPortType> mockList = new ArrayList<AnacomApplicationServerPortType>();
//		
//		final AnacomApplicationServerPortType mockNormal1 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server1");
//		final AnacomApplicationServerPortType mockNormal2 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server2");
//		final AnacomApplicationServerPortType mockNormal3 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server3");
//		final AnacomApplicationServerPortType mockNormalSilent = mock(AnacomApplicationServerPortType.class, "Silent Mock Server");
//
//		final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//		
//		mockList.add(mockNormal1);
//		mockList.add(mockNormal2);
//		mockList.add(mockNormal3);
//		mockList.add(mockNormalSilent);
//		
//	
//		final Response<PhoneNumValueDTOType> silentAsyncMock = new Response<PhoneNumValueDTOType>(){
//
//			@Override
//			public boolean isDone() {
//				return false;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				return null;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//			
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> loudExeptionAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//			
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				
//				SimpleExceptionType simpl = new SimpleExceptionType();
//				simpl.setSequenceNumber((long) 2);
//				simpl.setError("Cellphone Error");
//				pt.ist.anacom.shared.stubs.CellPhoneNotExistsException cause 
//				= new pt.ist.anacom.shared.stubs.CellPhoneNotExistsException("Test Exception!", simpl);
//				
//				ExecutionException exe = new ExecutionException(cause);
//				throw exe;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> delayedAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//				
//				ret.setBalance(69);
//				ret.setNumber("910000000");
//				ret.setSequenceNumber(0);
//				return ret;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		
//		checking(new Expectations(){{
//			// Fazer com que haja um a nao responder e o resto a responder
//			oneOf (mockNormalSilent).getBalanceAsync(msg); will(returnValue(silentAsyncMock));
//			oneOf (mockNormal1).getBalanceAsync(msg); will(returnValue(delayedAsyncMock));
//			oneOf (mockNormal2).getBalanceAsync(msg); will(returnValue(loudExeptionAsyncMock));
//			oneOf (mockNormal3).getBalanceAsync(msg); will(returnValue(loudExeptionAsyncMock));
//		}});
//
//		try {
//			client.getBalance(mockList, msg);
//			fail("Não mandou excepção!");
//		} catch (InvalidOperatorException_Exception e) {
//			fail("Mandou InvalidOperatorException_Exception");	
//		} catch (CellPhoneNotExistsException e) {
//			long sNResposta = (long) (((CellPhoneNotExistsException)e).getFaultInfo().getSequenceNumber());
//			assertEquals(sNResposta, 2);
//		} catch (OperatorNotFoundException e) {
//			fail("Mandou OperatorNotFoundException");
//		} catch (InvalidBalanceException e) {
//			fail("Mandou InvalidBalanceException");
//		} catch (InterruptedException e) {
//			fail("Mandou InterruptedException");
//		} catch (Throwable e) {
//			fail("Mandou Throwable");
//		}
//		
//		
//	}
//
//	public void testOneSilentFaultAndOneOutOfDateAndOneException(){
//
//		
//		ReplicateLogicV3 client = new ReplicateLogicV3(); 
//		
//		PhoneNumValueDTO dto = new PhoneNumValueDTO();
//		dto.set_number("917236942");
//		dto.setBalance(0);
//		
//		final ArrayList<AnacomApplicationServerPortType> mockList = new ArrayList<AnacomApplicationServerPortType>();
//		
//		final AnacomApplicationServerPortType mockNormal1 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server1");
//		final AnacomApplicationServerPortType mockNormal2 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server2");
//		final AnacomApplicationServerPortType mockNormal3 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server3");
//		final AnacomApplicationServerPortType mockNormalSilent = mock(AnacomApplicationServerPortType.class, "Silent Mock Server");
//
//		final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//		
//		mockList.add(mockNormal1);
//		mockList.add(mockNormal2);
//		mockList.add(mockNormal3);
//		mockList.add(mockNormalSilent);
//		
//	
//		final Response<PhoneNumValueDTOType> silentAsyncMock = new Response<PhoneNumValueDTOType>(){
//
//			@Override
//			public boolean isDone() {
//				return false;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				return null;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> loudAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//
//				PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//				ret.setBalance(69);
//				ret.setNumber("910000000");
//				ret.setSequenceNumber(3);
//				return ret;
//				
//				
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> delayedExceptionAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				
//				SimpleExceptionType simpl = new SimpleExceptionType();
//				simpl.setSequenceNumber((long) 2);
//				simpl.setError("Cellphone Error");
//				pt.ist.anacom.shared.stubs.CellPhoneNotExistsException cause 
//				= new pt.ist.anacom.shared.stubs.CellPhoneNotExistsException("Test Exception!", simpl);
//				
//				ExecutionException exe = new ExecutionException(cause);
//				throw exe;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		
//		checking(new Expectations(){{
//			// Fazer com que haja um a nao responder e o resto a responder
//			oneOf (mockNormalSilent).getBalanceAsync(msg); will(returnValue(silentAsyncMock));
//			oneOf (mockNormal1).getBalanceAsync(msg); will(returnValue(delayedExceptionAsyncMock));
//			oneOf (mockNormal2).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//			oneOf (mockNormal3).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//		}});
//
//		//execute
//		PhoneNumValueDTOType resp = null;
//		try {
//			resp = client.getBalance(mockList, msg);
//		} catch (InvalidOperatorException_Exception e) {
//			fail("Mandou InvalidOperatorException_Exception");	
//		} catch (CellPhoneNotExistsException e) {
//			fail("Mandou CellPhoneNotExistsException");
//		} catch (OperatorNotFoundException e) {
//			fail("Mandou OperatorNotFoundException");
//		} catch (InvalidBalanceException e) {
//			fail("Mandou InvalidBalanceException");
//		} catch (InterruptedException e) {
//			fail("Mandou InterruptedException");
//		} catch (Throwable e) {
//			fail("Mandou Throwable");
//		}
//		
//		assertEquals(69, resp.getBalance());
//		assertEquals(3, resp.getSequenceNumber());
//		
//		
//		
//	}
//
//	public void testOneSilentFaultAndOneOutOfDateExceptionAndOneException(){
//
//		
//		ReplicateLogicV3 client = new ReplicateLogicV3(); 
//		
//		PhoneNumValueDTO dto = new PhoneNumValueDTO();
//		dto.set_number("917236942");
//		dto.setBalance(0);
//		
//		final ArrayList<AnacomApplicationServerPortType> mockList = new ArrayList<AnacomApplicationServerPortType>();
//		
//		final AnacomApplicationServerPortType mockNormal1 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server1");
//		final AnacomApplicationServerPortType mockNormal2 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server2");
//		final AnacomApplicationServerPortType mockNormal3 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server3");
//		final AnacomApplicationServerPortType mockNormalSilent = mock(AnacomApplicationServerPortType.class, "Silent Mock Server");
//
//		final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//		
//		mockList.add(mockNormal1);
//		mockList.add(mockNormal2);
//		mockList.add(mockNormal3);
//		mockList.add(mockNormalSilent);
//		
//	
//		final Response<PhoneNumValueDTOType> silentAsyncMock = new Response<PhoneNumValueDTOType>(){
//
//			@Override
//			public boolean isDone() {
//				return false;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				return null;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> loudExceptionAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//
//				SimpleExceptionType simpl = new SimpleExceptionType();
//				simpl.setSequenceNumber((long) 3);
//				simpl.setError("Cellphone Error");
//				pt.ist.anacom.shared.stubs.CellPhoneNotExistsException cause 
//				= new pt.ist.anacom.shared.stubs.CellPhoneNotExistsException("Test Exception!", simpl);
//				
//				ExecutionException exe = new ExecutionException(cause);
//				throw exe;
//				
//				
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		final Response<PhoneNumValueDTOType> delayedExceptionAsyncMock = new Response<PhoneNumValueDTOType>() {
//			
//			@Override
//			public boolean isDone() {
//				return true;
//			}
//			
//			@Override
//			public boolean isCancelled() {
//				return false;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//					throws InterruptedException, ExecutionException, TimeoutException {
//				return null;
//			}
//			
//			@Override
//			public PhoneNumValueDTOType get() throws InterruptedException,
//					ExecutionException {
//				
//				SimpleExceptionType simpl = new SimpleExceptionType();
//				simpl.setSequenceNumber((long) 2);
//				simpl.setError("Cellphone Error");
//				pt.ist.anacom.shared.stubs.CellPhoneNotExistsException cause 
//				= new pt.ist.anacom.shared.stubs.CellPhoneNotExistsException("Test Exception!", simpl);
//				
//				ExecutionException exe = new ExecutionException(cause);
//				throw exe;
//			}
//			
//			@Override
//			public boolean cancel(boolean mayInterruptIfRunning) {
//				return false;
//			}
//			
//			@Override
//			public Map<String, Object> getContext() {
//				return null;
//			}
//		};
//		
//		checking(new Expectations(){{
//			// Fazer com que haja um a nao responder e o resto a responder
//			oneOf (mockNormalSilent).getBalanceAsync(msg); will(returnValue(silentAsyncMock));
//			oneOf (mockNormal1).getBalanceAsync(msg); will(returnValue(delayedExceptionAsyncMock));
//			oneOf (mockNormal2).getBalanceAsync(msg); will(returnValue(loudExceptionAsyncMock));
//			oneOf (mockNormal3).getBalanceAsync(msg); will(returnValue(loudExceptionAsyncMock));
//		}});
//
//		try {
//			client.getBalance(mockList, msg);
//			fail("Não Apanhou Excepção");
//		} catch (InvalidOperatorException_Exception e) {
//			fail("Mandou InvalidOperatorException_Exception");	
//		} catch (CellPhoneNotExistsException e) {
//			long sNResposta = (long) (((CellPhoneNotExistsException)e).getFaultInfo().getSequenceNumber());
//			assertEquals(sNResposta, 3);
//		} catch (OperatorNotFoundException e) {
//			fail("Mandou OperatorNotFoundException");
//		} catch (InvalidBalanceException e) {
//			fail("Mandou InvalidBalanceException");
//		} catch (InterruptedException e) {
//			fail("Mandou InterruptedException");
//		} catch (Throwable e) {
//			fail("Mandou Throwable");
//		}
//	}


}