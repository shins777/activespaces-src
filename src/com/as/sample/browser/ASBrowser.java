package com.as.sample.browser;
import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;
import com.tibco.as.space.browser.Browser;
import com.tibco.as.space.browser.BrowserDef;


public class ASBrowser {

	public static void main(String[] args) throws InterruptedException
	{
		Metaspace ms = null;
		Space space = null;
		try
		{
			MemberDef memberDef = MemberDef.create();
			memberDef.setDiscovery("tcp://localhost:50001");
			memberDef.setListen("tcp://localhost:50100-50150");
			memberDef.setClusterSuspendThreshold(-1);
			memberDef.setMemberTimeout(0);
//			memberDef.setRemoteDiscovery("tcp://localhost:60001;localhost:60002;localhost:60003?remote=true");
			//memberDef.setMemberName("ASClient");
			memberDef.setConnectTimeout(100000);
			memberDef.setClientTimeout(1);
			String metaspaceName = "testMS";
			ms = Metaspace.connect(metaspaceName, memberDef);
			
			String spaceName = "TEST2";
			
			SpaceDef spd = SpaceDef.create(spaceName);
			spd.setLockWait(30000);
			spd.setTTL(30000);
			
	        space = ms.getSpace(spaceName, DistributionRole.LEECH);
			
	        Tuple tuple = Tuple.create();
	        String filter = null; 
	        Browser browser = null;
	        
	        filter = "key like \"KEY:"+ 10 +"*\"";
	        
	        System.out.println("\nData Search ["+spaceName+"]["+filter +"]---------------\n");
	        
	        long start = System.currentTimeMillis();  
	        
	        browser = space.browse(BrowserDef.BrowserType.GET,
	           				BrowserDef.create().setDistributionScope(BrowserDef.DistributionScope.ALL).
	                        setTimeScope(BrowserDef.TimeScope.SNAPSHOT).
	                        setPrefetch(BrowserDef.PREFETCH_ALL), filter);
	             
	        while ((tuple = browser.next()) != null) {
	           	 System.out.println(tuple);
	        }
	        browser.stop();
	             
	        long end = System.currentTimeMillis();
	        System.out.println("\nLatency -------------------------------------[" + (end-start) +" ms]");
	        
           
		}catch (ASException e)	{
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (space != null)
				try {
					space.close();
				} catch (ASException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    if (ms != null)
				try {
					ms.close();
				} catch (ASException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}	
	
}
