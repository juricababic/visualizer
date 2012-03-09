package org.powertac.visualizer.wholesale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.powertac.common.ClearedTrade;
import org.powertac.common.Order;
import org.powertac.common.Orderbook;
import org.powertac.common.Timeslot;

/**
 * 
 * WholesaleMarket contains information about the wholesale market for one timeslot.
 * 
 * @author Jurica Babic
 * 
 */
public class WholesaleMarket {

	private int timeslotSerialNumber;
	private Map<Integer,WholesaleSnapshot> snapshots = new TreeMap<Integer, WholesaleSnapshot>(); 
	private ClearedTrade lastClearedTrade;
	private double totalTradedQuantityMWh;
	private boolean closed;
	

		
	public WholesaleMarket(Integer timeslotSerialNumber) {
		this.timeslotSerialNumber = timeslotSerialNumber;
		
	}
	
	public int getTimeslotSerialNumber() {
		return timeslotSerialNumber;
	}
	
	/**
	 * Returns WholesaleSnapshot by given relative timeslot index. If there is no snapshot available, the new WholsaleSnapshot object with specified relative timeslot index is created.
	 * @param relativeTimeslotIndex
	 * @return
	 */
	public WholesaleSnapshot findSnapshot(int relativeTimeslotIndex){
		
		return snapshots.get(relativeTimeslotIndex);
	}
	
	public Map<Integer, WholesaleSnapshot> getSnapshots() {
		return snapshots;
	}
	
	public ClearedTrade getLastClearedTrade() {
		return lastClearedTrade;
	}
	
	public double getTotalTradedQuantityMWh() {
		return totalTradedQuantityMWh;
	}
		
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Wholesale Market should be closed when all of its snapshots have been closed and the current relative index is equal to market's relative index.
	 */
	public void close() {
		finish();
		closed = true;
		
	}

	private void finish() {
		// TODO 
		
	}
	

}