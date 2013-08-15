package de.newsarea.homecockpit.fsuipc2net;

public class FSUIPCServerHandlerTest {

    /*

	private static Logger log = LoggerFactory.getLogger(FSUIPCServerHandlerTest.class);
	
	private FSUIPCInterface fsuipcInterface;
	private NetServer netServer;
	
	private ServerEventListener netServerServerEventListener;
	private String netServerWriteValue;
	private String netServerClientId;
	private OffsetEventListener fsuipcOffsetEventListener;	
	private List<OffsetItem[]> fsuipcOffsetItemsList;
	private List<OffsetIdent[]> fsuipcOffsetIdentsList;

	@BeforeMethod
	public void before() throws IOException {
		log.debug("test start");
		//
		this.fsuipcInterface = mock(FSUIPCInterface.class);
		// 
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();				
				fsuipcOffsetEventListener = (OffsetEventListener) args[0];
				return null;
			}
		}).when(this.fsuipcInterface).addEventListener(any(OffsetEventListener.class));
		//
		this.fsuipcOffsetItemsList = new ArrayList<OffsetItem[]>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();			
				fsuipcOffsetItemsList.add((OffsetItem[])args[0]);
				return null;
			}
		}).when(this.fsuipcInterface).write(any(OffsetItem[].class));
		//
		this.fsuipcOffsetIdentsList = new ArrayList<OffsetIdent[]>();
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();			
				fsuipcOffsetIdentsList.add((OffsetIdent[])args[0]);
				return null;
			}
		}).when(this.fsuipcInterface).monitor(any(OffsetIdent[].class));
		//
		this.netServer = mock(NetServer.class);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();				
				netServerServerEventListener = (ServerEventListener) args[0];
				return null;
			}
		}).when(this.netServer).addEventListener(any(ServerEventListener.class));
		//
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();			
				netServerClientId = (String)args[0];
				netServerWriteValue = (String)args[1]; 
				return null;
			}
		}).when(this.netServer).write(any(String.class), anyString());
		//
		this.fsuipcServerHandler = new FSUIPCServerHandler(netServer, fsuipcInterface);
		this.fsuipcServerHandler.doStart();
	}
	
	@Test
	public void testWrite() {
		this.netServerServerEventListener.valueReceived(null, "write [ [ 10,  2, 30 ] ]");
		//
		OffsetItem[] offsetItems = fsuipcOffsetItemsList.get(0);
		//
		assertEquals(10, offsetItems[0].getOffset());
		assertEquals(2, offsetItems[0].getSize());
		assertEquals(30, offsetItems[0].getValue().toShort());
	}
	
	@Test
	public void testWriteMulti() {
		this.netServerServerEventListener.valueReceived(null, "write [ [ 10,  2, 30 ], [ 40,  1, 50 ] ]");
		//
		OffsetItem[] offsetItems = fsuipcOffsetItemsList.get(0);
		//
		assertEquals(10, offsetItems[0].getOffset());
		assertEquals(2, offsetItems[0].getSize());
		assertEquals(30, offsetItems[0].getValue().toShort());
		//
		assertEquals(40, offsetItems[1].getOffset());
		assertEquals(1, offsetItems[1].getSize());
		assertEquals(50, offsetItems[1].getValue().toShort());
	}
	
	@Test
	public void testMonitor() {
		this.netServerServerEventListener.valueReceived(null, "monitor [[10,2]]");
		//
		OffsetIdent[] offsetIdents = fsuipcOffsetIdentsList.get(0);
		//
		assertEquals(10, offsetIdents[0].getOffset());
		assertEquals(2, offsetIdents[0].getSize());
	}
	
	@Test
	public void testMonitorMulti() {
		this.netServerServerEventListener.valueReceived(null, "monitor [[10,2],[20,1]]");
		//
		OffsetIdent[] offsetIdents = fsuipcOffsetIdentsList.get(0);
		//
		assertEquals(10, offsetIdents[0].getOffset());
		assertEquals(2, offsetIdents[0].getSize());
		//
		assertEquals(20, offsetIdents[1].getOffset());
		assertEquals(1, offsetIdents[1].getSize());
	}
	
	@Test
	public void testReadClientId() {
		String clientId = UUID.randomUUID().toString();
		when(this.fsuipcInterface.read(any(OffsetIdent.class))).thenReturn(new OffsetItem(0, 0, ByteArray.create(new byte[] { 20 })));
		this.netServerServerEventListener.valueReceived(clientId, "read [[1,8]]");		
		assertEquals("value[[1,8,20]]", this.netServerWriteValue);
		assertEquals(clientId, this.netServerClientId);
	}

	@Test
	public void testEvent() {
		this.netServerServerEventListener.valueReceived(UUID.randomUUID().toString(), "monitor [[10, 4]]");
		this.fsuipcOffsetEventListener.offsetValueChanged(new OffsetItem(10, 4, new byte[] { 100 }));	
		assertEquals("changed[[10,4,100]]", this.netServerWriteValue);
	}

	*/
	
}
