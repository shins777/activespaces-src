package com.as.sample.loader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.PutOptions;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASLoaderManager {
	public static void main(String[] args) throws InterruptedException, ASException
	{
	    int threadCount = 10;	
	    int totalCount = 600000;
	    
	    Metaspace ms = null;
	    try {
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
			
	    	ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		
	    	long start = System.currentTimeMillis();
	        
	    	for(int i=1;i<=threadCount;i++){
	    		Runnable worker = new ASLoaderWorker(i,totalCount, threadCount,ms);
	    		executor.execute(worker);
	    	}   

	    	executor.shutdown();
	    	while (!executor.isTerminated()) {
	    	}
	
	    	long end = System.currentTimeMillis();
		
	        System.out.println("Total Elapsed time:" + totalCount/((end-start)/1000));
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}	
}
