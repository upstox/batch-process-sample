package com.sample.trade.worker;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.batch.process.common.utils.storage.client.JsonFileClient;
import com.sample.common.Constant;
import com.sample.common.TradeConstant;
import com.sample.trade.model.CodeData;
import com.sample.trade.model.TradeData;

import lombok.Builder;
import lombok.Getter;

@Component
public class DeliveryMarkingProcessor {

	@Autowired
	@Qualifier("fileClient")
	private JsonFileClient jsonFileClient;

	@Autowired
	private TradeProcessDataMaker tradeProcessDataMaker;

	public void process(Map<String, Object> metaInfoMap, Map<String, Object> paramMap, Map<String, String> contextMap) {
		@SuppressWarnings("unused")
		List<CodeData> codeData = tradeProcessDataMaker.getCodeData(metaInfoMap);
		List<TradeData> trades = tradeProcessDataMaker.getTrades(metaInfoMap);
		Map<GroupingKey, List<TradeData>> codeAndScripGroupedList = trades.stream()
				.collect(Collectors.groupingBy(e -> GroupingKey.builder()//
						.code(e.getCode())//
						.sripName(e.getScripName())//
						.build(), Collectors.toList()));
		for (Entry<GroupingKey, List<TradeData>> entry : codeAndScripGroupedList.entrySet()) {
			List<TradeData> tradeDataList = entry.getValue();
			int deliveryQuantity = findDeliveryQuantity(tradeDataList);

			if (deliveryQuantity > 0) {
				reduce(tradeDataList, deliveryQuantity, "B");
			} else if (deliveryQuantity < 0) {
				reduce(tradeDataList, deliveryQuantity, "S");
			} else {
				// do nothing.
			}
		}
		String jobId = metaInfoMap.get(Constant.JOB_ID).toString() + Constant.SLASH
				+ TradeConstant.CONTEXT_KEY_FOLDER_TRADE_DATA_PROCESSED;
		String partition = metaInfoMap.get(Constant.PARTITION).toString();
		jsonFileClient.put(Constant.STORAGE_0, jobId, partition, trades);
	}

	private static void reduce(List<TradeData> tradeDataList, int deliveryQuantity, String side) {
		List<TradeData> trades = tradeDataList.stream().filter(t -> t.getBs().equals(side))
				.collect(Collectors.toList());
		deliveryQuantity = Math.abs(deliveryQuantity);
		tradeDataList.sort(Comparator.comparing(TradeData::getId));

		for (TradeData trade : trades) {

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

	private static int findDeliveryQuantity(List<TradeData> trades) {
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

	@Builder
	@Getter
	private static class GroupingKey {
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

	}

}
