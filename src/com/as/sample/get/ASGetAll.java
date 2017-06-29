package com.as.sample.get;
import java.util.ArrayList;
import java.util.Collection;

import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceResultList;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASGetAll {
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

	        Collection<Tuple> tuples = new ArrayList<Tuple>();
	        
	        int init = 0;
	        int count = 10;

	        for(int i=init;i<count;i++) {
	        	Tuple tuple = Tuple.create();
				tuple.put("key", "KEY:"+String.valueOf(i));
				tuples.add(tuple);
			}     
	        System.out.println("=======" + tuples);

	        long start = System.currentTimeMillis();
	        
			SpaceResultList list= space.getAll(tuples);
			System.out.println("=======" + list.size());
			 
			for(int i =0 ;i<list.size();i++) {
				 
				Tuple returnT = list.getTuple(i);
				System.out.println("=======" + returnT);
			}	 	        

	        long end = System.currentTimeMillis();
	       
	        System.out.println("GETALL TPS:" + count/((end-start)/1000.0));
	        
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
