package com.as.sample.transaction;
import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASTransaction {
	public static void main(String[] args) throws InterruptedException, ASException
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
			
	        ms.beginTransaction();
	        
	        // data get
	        Tuple input = Tuple.create();
			input.put("key","KEY:10");
	        Tuple ret = space.get(input);
	        
	        if(ret !=null) {
	        	System.out.println("DATA:" + ret.get("data"));
	        }	
	       
	        // data put
	        Tuple tuple = Tuple.create();
			tuple.put("key","KEY:"+ 120);
			tuple.put("idx1","KEY:"+ 120);
			tuple.put("idx2","KEY:"+ 120);
			tuple.put("data","DATA:"+120);
			
			Tuple ret2 = space.put(tuple);
			
			if(ret2 !=null) {
				System.out.println("=DATA:" + ret2.get("data"));
			}	
			
			ms.commitTransaction();
	        
		}catch (ASException e){
			e.printStackTrace();
			ms.rollbackTransaction();
		}catch(Exception e){
			e.printStackTrace();
			ms.rollbackTransaction();
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
