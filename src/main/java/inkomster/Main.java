package inkomster;

import java.io.IOException;

import com.opencsv.exceptions.CsvException;

public class Main {

	public static void main(String[] args) {
		Calculate calc = new Calculate();
		Calculate2 calc2 = new Calculate2();
		try {
			calc.go("C:/Users/tomas/Documents/Lön 2022-09-30 - 2022-01-01.csv",Cst.SEPTEMBER);
			calc2.go("C:/Users/tomas/Documents/Lön 2022-09-30 - 2022-01-01.csv",Cst.SEPTEMBER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
