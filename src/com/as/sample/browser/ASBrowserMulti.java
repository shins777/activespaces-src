package com.as.sample.browser;
import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;
import com.tibco.as.space.browser.Browser;
import com.tibco.as.space.browser.BrowserDef;


public class ASBrowserMulti {

	public static void main(String[] args) throws InterruptedException
	{
		Metaspace ms = null;
		Space space = null;
		try
		{
			MemberDef memberDef = MemberDef.create();
			memberDef.setDiscovery("tcp://localhost:50001");
			memberDef.setListen("tcp://localhost:50010-50100");
			//memberDef.setRemoteDiscovery("tcp://localhost:6621?remote=true");
			//memberDef.setMemberName("ASClient");
			//memberDef.setConnectTimeout(10000);

			String metaspaceName = "ms";
			ms = Metaspace.connect(metaspaceName, memberDef);
			
			String spaceName = "TEST";
	        space = ms.getSpace(spaceName, DistributionRole.LEECH);
			
	        Tuple tuple = Tuple.create();
	        String filter = null; 
	        Browser browser = null;
	        
	        int count =10; 
	        long start = System.currentTimeMillis();  
	        
	        for(int i=0;i<count;i++){

	        	 filter = "key =\"KEY:"+i +"\"";
	             browser = space.browse(BrowserDef.BrowserType.GET,
	            				BrowserDef.create().setDistributionScope(BrowserDef.DistributionScope.ALL).
	                            setTimeScope(BrowserDef.TimeScope.SNAPSHOT).
	                            setPrefetch(BrowserDef.PREFETCH_ALL), filter);
	             
	             while ((tuple = browser.next()) != null) {
	            	 //System.out.println(tuple);
	             }
	             browser.stop();
			}   
	        long end = System.currentTimeMillis();
	        System.out.println("Browser TPS:" + count/((end-start)/1000.0));
	        
           
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
