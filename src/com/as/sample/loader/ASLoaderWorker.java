package com.as.sample.loader;
import java.util.ArrayList;
import java.util.Collection;

import com.tibco.as.space.ASException;
import com.tibco.as.space.LockOptions;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.PutOptions;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASLoaderWorker implements Runnable {

	Metaspace ms = null;
	Space space = null;
	int unitCnt = 0;
	int threadNum = 0;
	
	public ASLoaderWorker(int idx, int totalCnt, int threadCnt, Metaspace ms_arg ) {
		System.out.println("Tread Num :"+idx );
		unitCnt = totalCnt / threadCnt;
		threadNum = idx;
		ms = ms_arg;
	}	
	
	public void run(){
		try
		{
			String spaceName = "TEST2";
			
			SpaceDef spd = SpaceDef.create(spaceName);
			spd.setLockWait(300000);
			//spd.setTTL(30000);
			
	        space = ms.getSpace(spaceName, DistributionRole.LEECH);
		
	        PutOptions putOpts = PutOptions.create();
	        putOpts.setUnlock(true);
	        putOpts.setForget(true);
	        
	        LockOptions lockOpts = LockOptions.create();
	        lockOpts.setLockWait(300*1000);
	        
	        Collection<Tuple> tuples = new ArrayList<Tuple>(); 
	        
	        long start = System.currentTimeMillis();
	        int count = unitCnt * threadNum;
	        int init = (unitCnt* threadNum) - unitCnt;
	        
			System.out.println("===<START>========");
	        
			for(int i=init;i<count;i++) {
				
				Tuple tuple = Tuple.create();
				tuple.put("key","KEY:"+ String.valueOf(i));
				tuple.put("idx1","IDX1:"+ String.valueOf(i));
				tuple.put("idx2","IDX2:"+ String.valueOf(i));
				tuple.put("data","DATA:"+String.valueOf(i+10000));
				
				tuples.add((Tuple)tuple);
			}	
			space.putAll(tuples,putOpts);
			
			System.out.println("===<END>========");
			
	        long end = System.currentTimeMillis();
	        
	        System.out.println("Thread ["+threadNum + "]: Elapsed time:" + unitCnt/((end-start)/1000));
	        
		}catch (ASException e){
			e.printStackTrace();
			try {
				ms.rollbackTransaction();
			} catch (ASException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				ms.rollbackTransaction();
			} catch (ASException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}finally{
			try{
				if (space != null)
					space.close();
			}catch (ASException ex){
				ex.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
