package inkomster;

import java.io.IOException;

import com.opencsv.exceptions.CsvException;

public class Main {

	public static void main(String[] args) {
		Calculate calc = new Calculate();
		try {
			calc.go("C:/Users/tomas/Documents/transaktions_8.csv",Cst.AUGUSTI);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
