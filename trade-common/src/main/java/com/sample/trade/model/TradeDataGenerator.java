package com.sample.trade.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TradeDataGenerator {

	private static final String[] SCRIP_NAMES = { "RELIANCE", "TATASTEEL"
//			, "INDUSIND", "MARUTI", "INFOSYS", "UPL",
//			"TATAMOTOR", "CIPLA", "HCLTECK", "BAJAJ", "ASIANPAINTS", "WIPRO", "LT", "TITAN", "KOTAK", "SBILIFE",
//			"BAJAJFINSERV", "AXIS" 
	};

	private static final String[] CODES = { //
			"ABCD12"
//			, "BCDE22", "EFHG12", "HIGJ45", "LMNO34", "PQRS45", "QRST23", "RSTU34", "XYZA12", "IJKL22",
//			"OPQR12", "VWXY22", "KLMN12", "STUV45", "FGHI34", "GHIJ45", "HIJK23", "MNOP34", "XYAA12", "IJBL22",
//			"ABDD12", "BBDE22", "EFGG12", "HIGG45", "LMOO34", "PQRR45", "QRTT23", "RSTT34", "XYZZ12", "IJLL22",
//			"ABDDA2", "BBDED2", "EFGGE2", "AIGG45", "BMOO34", "FQRR45", "GRTT23", "LSTT34", "FYZZ12", "AJLL22",
//			"ABDAA2", "ABDED2", "AFGGE2", "AAGG45", "AMOO34", "AQRR45", "ARTT23", "ASTT34", "AYZZ12", "BJLL22",
//			"ABCC12", "BCDE2A", "EAHG12", "HIGP45", "LMNL34", "PQRS95", "QRST2W", "RZTU34", "XYZV12", "IJKY22",
//			"OPQC12", "VWXY2A", "KAMN12", "STUP45", "FGHL34", "GHIJ95", "HIJK2W", "MZOP34", "XYAV12", "IJBY22",
//			"ABDC12", "BBDE2A", "EAGG12", "HIAP45", "LMOL34", "PQRR95", "QRTT2W", "RZTT34", "GYZV12", "IJLY22",
//			"ABDCA2", "BBDEDA", "EAGGE2", "AIGP45", "BMOL34", "FQRR95", "GRTT2W", "LZTT34", "FYZV12", "AJLY22",
//			"ABQCA2", "ABDEDA", "AAGGE2", "AAGP45", "AMOL34", "AQRR95", "ARTT2W", "AZTT34", "AYZV12", "BJLY22"//
	};

	private static final Random random = new Random();

	public static void generateTradeData() throws IOException {
		try (FileWriter fw = new FileWriter(new File("trades.csv")); //
				BufferedWriter bw = new BufferedWriter(fw);) {
			int recordCount = 10;
			for (int i = 0; i < recordCount; i++) {
				int codeIndex = random.nextInt(CODES.length);
				int scripIndex = random.nextInt(SCRIP_NAMES.length);
				int randomQty = random.nextInt(10);
				String bs = randomQty % 2 == 1 ? "B" : "S";
				TradeData tradeData = TradeData.builder().id(i + 1).code(CODES[codeIndex])
						.scripName(SCRIP_NAMES[scripIndex]).qty(randomQty).bs(bs).build();
				bw.write(tradeData.toCsv());
				if (i < recordCount - 1)
					bw.newLine();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		generateTradeData();
	}

}
