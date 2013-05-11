package org.powertac.visualizer.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.powertac.visualizer.display.BrokerSeriesTemplate;
import org.powertac.visualizer.display.CustomerStatisticsTemplate;
import org.powertac.visualizer.display.DrillDownTemplate;
import org.powertac.visualizer.domain.broker.BrokerModel;
import org.powertac.visualizer.domain.broker.TariffData;
import org.powertac.visualizer.domain.broker.TariffDynamicData;
import org.powertac.visualizer.services.BrokerService;
import org.powertac.visualizer.services.handlers.VisualizerHelperService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class TariffMarketBean implements Serializable {

	private String tariffDynData;
	private String tariffDynDataOneTimeslot;
	private ArrayList<TariffData> allTarifs = new ArrayList<TariffData>();//tom
	private String customerStatictics;
	private List<TariffData> filteredValue;
	private Logger log = Logger.getLogger(TariffMarketBean.class);
	

	@Autowired
	public TariffMarketBean(BrokerService brokerService,
			VisualizerHelperService helper) {

		Gson gson = new Gson();

		// Data Array
		ArrayList<Object> tariffData = new ArrayList<Object>();

		ArrayList<Object> tariffDataOneTimeslot = new ArrayList<Object>();
		ArrayList<Object> customerStaticticsArray = new ArrayList<Object>();//tom
		int safetyTsIndex = helper.getSafetyWholesaleTimeslotIndex();

		Collection<BrokerModel> brokers = brokerService.getBrokers();
		// brokers:
		for (Iterator iterator = brokers.iterator(); iterator.hasNext();) {
			BrokerModel brokerModel = (BrokerModel) iterator.next();
			Object customerStatisticsBroker; 
			ArrayList<Object> customerNumberData = new ArrayList<Object>();
			ArrayList<Object> profitData = new ArrayList<Object>();
			ArrayList<Object> netKWhData = new ArrayList<Object>();
			ArrayList<Object> drillDataBroker = new ArrayList<Object>();
			
			//calculating number of customers for each broker's tariff -- by tom
			Collection<TariffData> allTariffsData = brokerModel.getTariffCategory().getTariffData().values();
			for(TariffData td:allTariffsData){
				Object[] drillData = {Long.toString(td.getSpec().getId()), td.getCustomers()};
				drillDataBroker.add(drillData);
			}
			customerStatisticsBroker = new DrillDownTemplate(brokerModel.getName(), brokerModel.getAppearance().getColorCode(), drillDataBroker);
			

			// one timeslot
			ArrayList<Object> customerNumberDataOneTimeslot = new ArrayList<Object>();
			ArrayList<Object> profitDataOneTimeslot = new ArrayList<Object>();
			ArrayList<Object> kwhDataOneTimeslot = new ArrayList<Object>();

			ConcurrentHashMap<Integer, TariffDynamicData> tariffDynData = brokerModel
					.getTariffCategory().getTariffDynamicDataMap();

			Set<Integer> keysTariffDynData = new TreeSet<Integer>(brokerModel
					.getTariffCategory().getTariffDynamicDataMap().keySet())
					.headSet(safetyTsIndex, true);

			// dynamic tariff data:
			for (Iterator iterator2 = keysTariffDynData.iterator(); iterator2
					.hasNext();) {
				int key = (Integer) iterator2.next();
				TariffDynamicData dynData = tariffDynData.get(key);
				Object[] timeCustomerCount = { helper.getMillisForIndex(key),
						dynData.getCustomerCount() };
				Object[] profit = { helper.getMillisForIndex(key),
						dynData.getDynamicData().getProfit() };
				Object[] netKWh = { helper.getMillisForIndex(key),
						dynData.getDynamicData().getEnergy() };

				customerNumberData.add(timeCustomerCount);
				profitData.add(profit);
				netKWhData.add(netKWh);

				// one timeslot:
				Object[] customerCountOneTimeslot = {
						helper.getMillisForIndex(key),
						dynData.getCustomerCountDelta() };
				Object[] profitOneTimeslot = { helper.getMillisForIndex(key),
						dynData.getDynamicData().getProfitDelta() };
				Object[] kWhOneTimeslot = { helper.getMillisForIndex(key),
						dynData.getDynamicData().getEnergyDelta()};

				customerNumberDataOneTimeslot.add(customerCountOneTimeslot);
				profitDataOneTimeslot.add(profitOneTimeslot);
				kwhDataOneTimeslot.add(kWhOneTimeslot);

			}
			if (keysTariffDynData.size() == 0) {
				// dummy:
				double[] dummy = { helper.getMillisForIndex(0), 0 };
				customerNumberData.add(dummy);
				profitData.add(dummy);
				netKWhData.add(dummy);
				customerNumberDataOneTimeslot.add(dummy);
				profitDataOneTimeslot.add(dummy);
				kwhDataOneTimeslot.add(dummy);
			}
			tariffData.add(new BrokerSeriesTemplate(brokerModel.getName()
					+ " PRICE", brokerModel.getAppearance().getColorCode(), 0,
					profitData, true));
			tariffData.add(new BrokerSeriesTemplate(brokerModel.getName()
					+ " KWH", brokerModel.getAppearance().getColorCode(), 1,
					netKWhData, false));
			tariffData.add(new BrokerSeriesTemplate(brokerModel.getName()
					+ " CUST", brokerModel.getAppearance().getColorCode(), 2,
					true, customerNumberData, false));

			// one timeslot:
			tariffDataOneTimeslot.add(new BrokerSeriesTemplate(brokerModel
					.getName() + " PRICE", brokerModel.getAppearance()
					.getColorCode(), 0, profitDataOneTimeslot,true));
			tariffDataOneTimeslot.add(new BrokerSeriesTemplate(brokerModel
					.getName() + " KWH", brokerModel.getAppearance()
					.getColorCode(), 1, kwhDataOneTimeslot, false));
			tariffDataOneTimeslot.add(new BrokerSeriesTemplate(brokerModel
					.getName() + " CUST", brokerModel.getAppearance()
					.getColorCode(), 2, true, customerNumberDataOneTimeslot, false));

			customerStaticticsArray.add(new CustomerStatisticsTemplate(
					brokerModel.getName(), brokerModel.getAppearance()
							.getColorCode(), brokerModel.getTariffCategory().getCustomerCount(), customerStatisticsBroker));//
			//tom
			for (TariffData data : brokerModel.getTariffCategory()
					.getTariffData().values()) {
				allTarifs.add(data);
			}
			//-tom
		}//end BROKER for loop
		this.tariffDynData = gson.toJson(tariffData);
		this.tariffDynDataOneTimeslot = gson.toJson(tariffDataOneTimeslot);
		this.customerStatictics = gson.toJson(customerStaticticsArray);
		
		log.info("*******" + tariffDynData);

	}

	public String getTariffDynData() {
		return tariffDynData;
	}

	public String getTariffDynDataOneTimeslot() {
		return tariffDynDataOneTimeslot;
	}

	public ArrayList<TariffData> getAllTarifs() {
		return allTarifs;
	}
	
	public List<TariffData> getFilteredValue() {
		return filteredValue;
	}

	public void setFilteredValue(List<TariffData> filteredValue) {
		this.filteredValue = filteredValue;
	}

	public String getCustomerStatictics() {
		return customerStatictics;
	}

}
