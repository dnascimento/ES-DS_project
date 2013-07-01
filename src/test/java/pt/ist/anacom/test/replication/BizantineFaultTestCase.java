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

public class BizantineFaultTestCase extends MockObjectTestCase {
	
//	public BizantineFaultTestCase(){
//		
//	}
//	
//	/**
//	 * Testa o Caso de Uma falha bizantina, uma mensagem fora de ordem e 
//	 * duas respostas certas
//	 */
//	public void testOneBizantineandOneEqualOutOfDate() {
//			ReplicateLogicV3 client = new ReplicateLogicV3(); 
//			
//			PhoneNumValueDTO dto = new PhoneNumValueDTO();
//			dto.set_number("917236942");
//			dto.setBalance(0);
//			
//			final ArrayList<AnacomApplicationServerPortType> mockList = new ArrayList<AnacomApplicationServerPortType>();
//			
//			final AnacomApplicationServerPortType mockNormal1 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server1");
//			final AnacomApplicationServerPortType mockNormal2 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server2");
//			final AnacomApplicationServerPortType mockNormal3 = mock(AnacomApplicationServerPortType.class, "Normal Mock Server3");
//			final AnacomApplicationServerPortType mockNormalBizantine = mock(AnacomApplicationServerPortType.class, "Bizantine Mock Server");
//
//			final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//			msg.setNumber("917236942");
//			msg.setSequenceNumber(10);
//			
//			mockList.add(mockNormalBizantine);
//			mockList.add(mockNormal1);
//			mockList.add(mockNormal2);
//			mockList.add(mockNormal3);
//			
//			
//		
//			final Response<PhoneNumValueDTOType> BizantineOrOldAsyncMock = new Response<PhoneNumValueDTOType>(){
//
//				@Override
//				public boolean isDone() {
//					return true;
//				}
//				
//				@Override
//				public boolean isCancelled() {
//					return false;
//				}
//				
//				@Override
//				public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//						throws InterruptedException, ExecutionException, TimeoutException {
//					return null;
//				}
//				
//				@Override
//				public PhoneNumValueDTOType get() throws InterruptedException,
//						ExecutionException {
//					PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//					
//					ret.setBalance(112);
//					ret.setNumber("917121212");
//					ret.setSequenceNumber(1);
//					
//					return ret;
//				}
//				
//				@Override
//				public boolean cancel(boolean mayInterruptIfRunning) {
//					return false;
//				}
//				
//				@Override
//				public Map<String, Object> getContext() {
//					return null;
//				}
//			};
//			
//			final Response<PhoneNumValueDTOType> loudAsyncMock = new Response<PhoneNumValueDTOType>() {
//				
//				@Override
//				public boolean isDone() {
//					return true;
//				}
//				
//				@Override
//				public boolean isCancelled() {
//					return false;
//				}
//				
//				@Override
//				public PhoneNumValueDTOType get(long timeout, TimeUnit unit)
//						throws InterruptedException, ExecutionException, TimeoutException {
//					return null;
//				}
//				
//				@Override
//				public PhoneNumValueDTOType get() throws InterruptedException,
//						ExecutionException {
//					PhoneNumValueDTOType ret = new PhoneNumValueDTOType();
//					
//					ret.setBalance(69);
//					ret.setNumber("917236942");
//					ret.setSequenceNumber(10);
//					
//					return ret;
//				}
//				
//				@Override
//				public boolean cancel(boolean mayInterruptIfRunning) {
//					return false;
//				}
//				
//				@Override
//				public Map<String, Object> getContext() {
//					return null;
//				}
//			};
//			
//			checking(new Expectations(){{
//				// Fazer com que haja um a nao responder e o resto a responder
//				oneOf (mockNormalBizantine).getBalanceAsync(msg); will(returnValue(BizantineOrOldAsyncMock));
//				oneOf (mockNormal1).getBalanceAsync(msg); will(returnValue(BizantineOrOldAsyncMock));
//				oneOf (mockNormal2).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//				oneOf (mockNormal3).getBalanceAsync(msg); will(returnValue(loudAsyncMock));
//			}});
//
//			//execute
//			PhoneNumValueDTOType resp = null;
//			try {
//				resp = client.getBalance(mockList, msg);
//			} catch (InvalidOperatorException_Exception e) {
//				fail("Mandou InvalidOperatorException_Exception");	
//			} catch (CellPhoneNotExistsException e) {
//				fail("Mandou CellPhoneNotExistsException");
//			} catch (OperatorNotFoundException e) {
//				fail("Mandou OperatorNotFoundException");
//			} catch (InvalidBalanceException e) {
//				fail("Mandou InvalidBalanceException");
//			} catch (InterruptedException e) {
//				fail("Mandou InterruptedException");
//			} catch (Throwable e) {
//				fail("Mandou Throwable");
//			}
//			assertEquals(69, resp.getBalance());
//			assertEquals(10, resp.getSequenceNumber());
//			
//		}
//	
//	/**
//	 * Testa o Caso de Uma falha bizantina, uma mensagem fora de ordem igual á bizantina
//	 * (ambas com timestamp inferior a certa)  e 
//	 * duas respostas certas
//	 */
//	public void testOneBizantineandOneOutOfDate() {
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
//		final AnacomApplicationServerPortType mockNormalBizantine = mock(AnacomApplicationServerPortType.class, "Bizantine Mock Server");
//
//		final PhoneNumberDTOType msg = new PhoneNumberDTOType();
//		msg.setNumber("917236942");
//		msg.setSequenceNumber(10);
//		
//		mockList.add(mockNormalBizantine);
//		mockList.add(mockNormal1);
//		mockList.add(mockNormal2);
//		mockList.add(mockNormal3);
//		
//		
//	
//		final Response<PhoneNumValueDTOType> BizantineAsyncMock = new Response<PhoneNumValueDTOType>(){
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
//				ret.setBalance(1337);
//				ret.setNumber("917121212");
//				ret.setSequenceNumber(101231);
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
//				ret.setSequenceNumber(10);
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
//				ret.setBalance(10);
//				ret.setNumber("910000000");
//				ret.setSequenceNumber(3);
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
//			oneOf (mockNormalBizantine).getBalanceAsync(msg); will(returnValue(BizantineAsyncMock));
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
//		assertEquals(10, resp.getSequenceNumber());
//		
//	}
//		
//	
}
		