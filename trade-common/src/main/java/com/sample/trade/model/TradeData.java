package com.sample.trade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeData {
	private int id;
	private String code;
	private String scripName;
	private String bs;
	private int qty;
	private int delQty;

	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(id)//
				.append(",")//
				.append(code)//
				.append(",")//
				.append(scripName)//
				.append(",")//
				.append(bs)//
				.append(",")//
				.append(qty).append(",")//
				.append(delQty);
		return sb.toString();

	}

	@Override
	public String toString() {
		return "TradeData [id=" + id + ", code=" + code + ", scripName=" + scripName + ", bs=" + bs + ", qty=" + qty
				+ ", delQty=" + delQty + "]\n";
	}
}