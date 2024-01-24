package com.sample.trade.worker;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.batch.process.common.utils.MapSplitterUtil;
import com.batch.process.common.utils.storage.client.JsonFileClient;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;
import com.sample.trade.model.CodeData;
import com.sample.trade.model.TradeData;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DeliveryMarkingProcessorOptimized {

	@Autowired
	@Qualifier("fileClient")
	private JsonFileClient jsonFileClient;

	@Autowired
	private TradeProcessDataMaker tradeProcessDataMaker;

	private final ExecutorService executorService = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

	public void process(Map<String, Object> metaInfoMap, Map<String, Object> paramMap, Map<String, String> contextMap) {
		@SuppressWarnings("unused")
		List<CodeData> codeData = tradeProcessDataMaker.getCodeData(metaInfoMap);
		List<TradeData> trades = tradeProcessDataMaker.getTrades(metaInfoMap);
		Map<GroupingKey, List<TradeData>> codeAndScripGroupedList = trades.stream()
				.collect(Collectors.groupingBy(e -> GroupingKey.builder()//
						.code(e.getCode())//
						.sripName(e.getScripName())//
						.build(), Collectors.toList()));

		List<SortedMap<GroupingKey, List<TradeData>>> splitMapsData = MapSplitterUtil
				.optimiseAndAssignThreads(codeAndScripGroupedList);
		final CountDownLatch countDownLatch = new CountDownLatch(splitMapsData.size());
		
		for (SortedMap<GroupingKey, List<TradeData>> splitMap : splitMapsData) {
			for (Entry<GroupingKey, List<TradeData>> mapEntry : splitMap.entrySet()) {
				executorService.submit(new ComputationRunner(mapEntry.getValue(), countDownLatch));
			}
		}
		try {
			log.info("Awaiting threads to work on report generation");
			countDownLatch.await();
			log.info("All threads have completed report generation");
		} catch (InterruptedException e) {
			String errorMessage = "Interruption occurred while waiting for threads to generate reports.";
			throw new IllegalStateException(errorMessage, e);
		}

		String jobId = metaInfoMap.get(Constant.JOB_ID).toString() + Constant.SLASH
				+ TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA_PROCESSED;
		String partition = metaInfoMap.get(Constant.PARTITION).toString();
		jsonFileClient.put(Constant.STORAGE_0, jobId, partition, trades);
	}

	private static class ComputationRunner implements Runnable {
		private final List<TradeData> trades;
		private final CountDownLatch countDownLatch;

		public ComputationRunner(List<TradeData> trades, CountDownLatch countDownLatch) {
			this.trades = trades;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			computeAndAssignDeliveryQuantity(trades);
			countDownLatch.countDown();
		}

		private void computeAndAssignDeliveryQuantity(List<TradeData> trades) {
			int deliveryQuantity = findDeliveryQuantity(trades);

			if (deliveryQuantity > 0) {
				reduce(trades, deliveryQuantity, "B");
			} else if (deliveryQuantity < 0) {
				reduce(trades, deliveryQuantity, "S");
			} else {
				// do nothing.
			}
		}

		private void reduce(List<TradeData> tradeDataList, int deliveryQuantity, String side) {
			List<TradeData> filteredTrades = tradeDataList.stream().filter(t -> t.getBs().equals(side))
					.collect(Collectors.toList());
			deliveryQuantity = Math.abs(deliveryQuantity);
			tradeDataList.sort(Comparator.comparing(TradeData::getId));

			for (TradeData trade : filteredTrades) {

				int qty = trade.getQty();
				if (deliveryQuantity >= qty) {
					trade.setDelQty(qty);
				} else {
					trade.setDelQty(deliveryQuantity);
					break;
				}
				deliveryQuantity -= qty;
			}
		}

		private int findDeliveryQuantity(List<TradeData> trades) {
			int buyQuantity = 0;
			int sellQuantity = 0;

			for (TradeData t : trades) {

				if (t.getBs().equals("B")) {
					buyQuantity = buyQuantity + t.getQty();
				} else if (t.getBs().equals("S")) {
					sellQuantity = sellQuantity + t.getQty();
				}
			}
			return buyQuantity - sellQuantity;
		}

	}

	@Builder
	@Getter
	private static class GroupingKey implements Comparable<GroupingKey> {
		private String code;
		private String sripName;

		@Override
		public int hashCode() {
			return Objects.hash(code, sripName);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GroupingKey other = (GroupingKey) obj;
			return Objects.equals(code, other.code) && Objects.equals(sripName, other.sripName);
		}

		@Override
		public int compareTo(GroupingKey o) {
			return this.code.compareTo(o.code);
		}

	}

}
