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
public class CodeData {
	private int id;
	private String code;

	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append(id)//
				.append(",")//
				.append(code);
		return sb.toString();

	}
}
