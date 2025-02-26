package gov.nasa.ksc.itacl.mpc.models;

import gov.nasa.ksc.itacl.Utilities.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Telemetry {
	private TreeMap<Double, Map<String, MPCObject>> updates = new TreeMap<Double, Map<String, MPCObject>>();
	private List<String> objects = new ArrayList<String>();
	private Map<String, String> metaData = new HashMap<>();

	public Telemetry() {}
	
	public void registerObject(String name) {
		objects.add(name);
	}
	
	public void add(double time, MPCObject mpcObject) {
		synchronized (updates) {
			if(updates.containsKey(time)) {
				Map<String, MPCObject> update = updates.get(time);
				update.put(mpcObject.getName(), mpcObject);
			}
			else {
				Map<String, MPCObject> update = new HashMap<String,MPCObject>();
				update.put(mpcObject.getName(), mpcObject);
				updates.put(time, update);
			}
		}
//		Map<String, String> metadata = mpcObject.getMetaData();
//		if (metaData.isEmpty()) {
//		    Utils.warn("[ERROR] Metadata file appears empty or failed to parse!");
//		} else 
//		Utils.info("[Telemetry Debug] Metadata for " + mpcObject.getName() + ": " + metadata.size());
	}
	
	/*
	public void add(double time, MPCObject mpcObject) {
	    synchronized (updates) {
	        if (updates.containsKey(time)) {
	            Map<String, MPCObject> update = updates.get(time);
	            update.put(mpcObject.getName(), mpcObject);
	        } else {
	            Map<String, MPCObject> update = new HashMap<String, MPCObject>();
	            update.put(mpcObject.getName(), mpcObject);
	            updates.put(time, update);
	        }
	    }

	    // ✅ Extract metadata from mpcObject
	    Map<String, String> metadata = mpcObject.getMetaData();
	    Utils.info("[Telemetry Debug] Metadata for " + mpcObject.getName() + ": " + metadata);
	    StringBuilder metadataInfo = new StringBuilder();
	    for (Map.Entry<String, String> entry : metadata.entrySet()) {
	        metadataInfo.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
	    }

	    // ✅ Convert position and orientation arrays to readable text
	    String positionStr = Arrays.toString(mpcObject.getPosition());
	    String orientationStr = Arrays.toString(mpcObject.getRotation());

	    // ✅ Print detailed telemetry update including metadata
	    Utils.info("[Telemetry Update] Time: " + time +
	               ", Object: " + mpcObject.getName() + 
	               ", Position: " + positionStr +
	               ", Orientation: " + orientationStr +
	               ", Metadata: {" + metadataInfo.toString() + "}");
	}
    */
	
	public Map<String, MPCObject> get(double time, boolean through) {
		Map<String, MPCObject> update = null;
		synchronized (updates) {
			if(updates.containsKey(time)) {
				update = updates.get(time);
				if(through) {
					injectObjects(time, update);
				}
			}
			else {
				if(through) {
					update = getLastValidUpdate(time);
					if(update != null) {
						updates.put(time, update);
					}
				}
			}
		}
		return update;
	}
	
	public Map<Double, Map<String, MPCObject>> getRange( double start, boolean startInclusive, double end, boolean endInclusive) {
		Map<Double, Map<String, MPCObject>> updates = null;
		synchronized (this.updates) {
			updates =  new TreeMap<Double, Map<String,MPCObject>>(this.updates.subMap(start, startInclusive, end, endInclusive));
		}
		return updates;
	}

	public boolean addMissionMetadata (String key, String value)
	{
		if(metaData.containsKey(key))
		{
			Utils.warn("Could not add mission meta data. Key already exists");
			return false;
		}
		metaData.put(key, value);
	    
		return true;
	}
	
	public Map<String, String> getMissionMetadata()
	{
		return metaData;
	}
		
	private void injectObjects(double time, Map<String, MPCObject> update) {
		Iterator<String> it = objects.iterator();
		while(it.hasNext()) {
			String name = it.next();
			if(!update.containsKey(name)) {
				MPCObject obj = getLastValidUpdate(time, name);
				update.put(obj.getName(), obj);
			}
		}
	}

	private MPCObject getLastValidUpdate(double time, String name) {
		Entry<Double, Map<String, MPCObject>> entry = updates.floorEntry(time);
		if(entry == null) return null;
		
		Map<String, MPCObject> update = entry.getValue();
		if( update.containsKey(name)) {
			return update.get(name);
		}
		else {
			return getLastValidUpdate(entry.getKey(), name);
		}
	}
	
	private Map<String, MPCObject> getLastValidUpdate(double time) {
		Entry<Double, Map<String, MPCObject>> entry = updates.floorEntry(time);
		if(entry == null) return null;
		
		Map<String, MPCObject> update = entry.getValue();
		return update;
	}
	
}
