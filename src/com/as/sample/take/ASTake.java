package com.as.sample.take;
import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASTake {
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
//			memberDef.setMemberName("ASClient");
			memberDef.setConnectTimeout(100000);
			memberDef.setClientTimeout(1);
			String metaspaceName = "testMS";
			ms = Metaspace.connect(metaspaceName, memberDef);
			
			
			String spaceName = "TEST2";
	        space = ms.getSpace(spaceName, DistributionRole.LEECH);
	        
	        long start = System.currentTimeMillis();
	        
	        Tuple tuple = Tuple.create();
			tuple.put("key","KEY:40");
	        	
	        Tuple result = space.take(tuple);
	        System.out.println("key[" + result.getString("key") +"]-value["+result.getString("data") +"]");
	        
	        long end = System.currentTimeMillis();
	       
	        System.out.println("TAKE Latency:" + (end-start) + " ms");
	        
		}catch (ASException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if (space != null)
					space.close();
			    if (ms != null)
				    ms.close();
			}catch (ASException ex){
				ex.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}	
}
