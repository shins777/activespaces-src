package com.as.sample.listener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.tibco.as.space.ASException;
import com.tibco.as.space.MemberDef;
import com.tibco.as.space.Metaspace;
import com.tibco.as.space.Space;
import com.tibco.as.space.SpaceDef;
import com.tibco.as.space.Member.DistributionRole;
import com.tibco.as.space.event.EvictEvent;
import com.tibco.as.space.event.ExpireEvent;
import com.tibco.as.space.event.PutEvent;
import com.tibco.as.space.event.SeedEvent;
import com.tibco.as.space.event.TakeEvent;
import com.tibco.as.space.event.UnseedEvent;
import com.tibco.as.space.listener.EvictListener;
import com.tibco.as.space.listener.ExpireListener;
import com.tibco.as.space.listener.Listener;
import com.tibco.as.space.listener.ListenerDef;
import com.tibco.as.space.listener.PutListener;
import com.tibco.as.space.listener.SeedListener;
import com.tibco.as.space.listener.TakeListener;
import com.tibco.as.space.listener.UnseedListener;

class tibasListener implements PutListener, TakeListener, ExpireListener, SeedListener, UnseedListener, EvictListener {
		
	
	    public void onPut (PutEvent event)
	    {
	        System.out.print("[" + event.getSpaceName() + "]");
	        System.out.println(" Put:" + event.getTuple());
	        if (event.hasOldTuple()) {
	            System.out.println("Old: " + event.getOldTuple());
	        }
	    }

	    public void onTake (TakeEvent event) {
	        System.out.println("[" + event.getSpaceName() + "]" + " Take:" + event.getTuple());
	    }
	    public void onExpire (ExpireEvent event) {
	        System.out.println("[" + event.getSpaceName() + "]" + " Expire:" + event.getTuple());
	    }
	    // applies only if distribution scope is SEEDED
	    public void onSeed (SeedEvent event) {
	        System.out.println("[" + event.getSpaceName() + "]" + " Seed:" + event.getTuple());
	    }
	    // applies only if distribution scope is SEEDED
	    public void onUnseed (UnseedEvent event) {
	        System.out.println("[" + event.getSpaceName() + "]" + " Unseed:" + event.getTuple());
	    }
	    public void onEvict (EvictEvent event) {
	        System.out.println("[" + event.getSpaceName() + "]" + " Evict:" + event.getTuple());
	    }
	}

public class ASListener {
	
    Metaspace ms;
    Space space;
    Listener listener;
    
    public static void main (String[] args) {
        ASListener asListener = null;
        try {
            asListener = new ASListener(args);
            asListener.init();
            asListener.run();
        } catch (ASException as) {
            as.printStackTrace();
        } finally {
            if (asListener != null)
                asListener.cleanup();
        }
    }

    public ASListener (String[] args) {
    }

    public void init() throws ASException
    {
		MemberDef memberDef = MemberDef.create();
		memberDef.setDiscovery("tcp://localhost:50001");
		memberDef.setListen("tcp://localhost:50100-50150");
		memberDef.setClusterSuspendThreshold(-1);
		memberDef.setMemberTimeout(0);
//		memberDef.setRemoteDiscovery("tcp://localhost:60001;localhost:60002;localhost:60003?remote=true");
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

        ListenerDef listenerDef = ListenerDef.create();
        //listenerDef.setTimeScope(ListenerDef.TimeScope.SNAPSHOT);
        listenerDef.setTimeScope(ListenerDef.TimeScope.ALL);
        //listenerDef.setTimeScope(ListenerDef.TimeScope.NEW);
        //listenerDef.setTimeScope(ListenerDef.TimeScope.NEW_EVENTS);
        
        listenerDef.setDistributionScope(ListenerDef.DistributionScope.ALL);
        //listenerDef.setDistributionScope(ListenerDef.DistributionScope.SEEDED);
        
        String filter = "key = 'KEY:10'";
        
        listener = space.listen(new tibasListener(), listenerDef, filter);
    }

    public void run ()
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Type 'quit' to quit");
            while (true)
            {
                String input = br.readLine();
                if (input.equals("quit"))
                {
                    break;
                }
            }
        }
        catch (Exception ex)
        {
        }
    }

    public void cleanup()
    {
        try
        {
            if (space != null && listener != null) space.stopListener(listener);
            if (space != null) space.close();
            if (ms != null) ms.closeAll();
        }
        catch (ASException ex)
        {
            ex.printStackTrace();
        }
    }


    public ListenerDef getListenerDef ()
    {
        ListenerDef listenerDef = ListenerDef.create();

        //listenerDef.setTimeScope(ListenerDef.TimeScope.SNAPSHOT);
        listenerDef.setTimeScope(ListenerDef.TimeScope.ALL);
        //listenerDef.setTimeScope(ListenerDef.TimeScope.NEW);
        //listenerDef.setTimeScope(ListenerDef.TimeScope.NEW_EVENTS);

        listenerDef.setDistributionScope(ListenerDef.DistributionScope.ALL);
        //listenerDef.setDistributionScope(ListenerDef.DistributionScope.SEEDED);
        
        return listenerDef;

    }

}
