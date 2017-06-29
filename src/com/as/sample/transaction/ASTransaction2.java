package com.as.sample.transaction;
import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Tuple;
import com.tibco.as.space.Member.DistributionRole;


public class ASTransaction2 {
	
	public static void main(String[] args) throws InterruptedException, ASException {
		Metaspace ms = null;
		Space space1 = null;
		Space space2 = null;
		Space space3 = null;
		
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
			
	        String spaceName1 = "TEST1";
			String spaceName2 = "TEST2";
	        space1 = ms.getSpace(spaceName1, DistributionRole.LEECH);
	        space2 = ms.getSpace(spaceName2, DistributionRole.LEECH);
	        
	       
	        long beginTime=0, commitTime=0, operTime=0;
	        
        	long t1 = System.currentTimeMillis ( );
        	ms.beginTransaction();
        	long t2 = System.currentTimeMillis ( );
        	
        	Tuple tuple1 = Tuple.create();
        	tuple1.put("key","KEY:"+ 9990);
        	tuple1.put("data","DATA:9990");
        	
        	Tuple tuple2 = Tuple.create();
			tuple2.put("key","KEY:"+ 9990);
			tuple2.put("idx1","IDX1:"+ 9990);
			tuple2.put("idx2","IDX2:"+ 9990);
			tuple2.put("data","DATA:"+9990);
        	
        	long t3 = System.currentTimeMillis ( );
        	
    		space1.put(tuple1);
    		space2.put(tuple2);
        		
        	long t4 = System.currentTimeMillis ( );
        	ms.commitTransaction();
        	long t5 = System.currentTimeMillis ( );
        	
			beginTime =  beginTime + (t2-t1);
			operTime =  operTime + (t4-t3);
			commitTime =  commitTime + (t5-t4);
			
	        
			System.out.println("### Total Elapsed Time : " + ( beginTime + commitTime + operTime) + " msec");
			System.out.println("	Begin Time : " + beginTime + " msec");
			System.out.println("	Oper Time : " + operTime + " msec");
			System.out.println("	Commit Time : " + commitTime + " msec"); 	
			System.out.println("	Transaction Time : " + (beginTime + commitTime) + " msec"); 	
	        
		}catch (ASException e){
			e.printStackTrace();
			ms.rollbackTransaction();
		}catch(Exception e){
			e.printStackTrace();
			ms.rollbackTransaction();
		}finally{
			try{
				if (space1 != null) space1.close();
				if (space2 != null) space2.close();
				if (space3 != null) space3.close();
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
