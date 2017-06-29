package com.as.sample.put;
import java.util.ArrayList;
import java.util.Collection;

import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASPut {
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
	        
	        long start = System.currentTimeMillis();
	        
	        int init = 0;
	        int count = 100;
	        
			for(int i=init;i<count;i++) {
				Tuple tuple = Tuple.create();
				
				tuple.put("key","KEY:"+ String.valueOf(i));
				tuple.put("idx1","IDX1:"+ String.valueOf(i));
				tuple.put("idx2","IDX2:"+ String.valueOf(i));
				tuple.put("data","DATA:"+String.valueOf(i+10000));
				
				space.put(tuple);
			}	
			
	        long end = System.currentTimeMillis();
	        
	        System.out.println("Elapsed time:" + (end-start) + "ms");
	        
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
