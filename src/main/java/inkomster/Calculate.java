package inkomster;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class Calculate {


	public void go(String pathToFile, int TILL_OCH_MED_MÅNAD) throws IOException, CsvException {
		if (pathToFile == null)
			throw new IllegalArgumentException();
		if (pathToFile.length() < 5)
			throw new IllegalArgumentException();
		
		int ANTAL_MÅNADER_RESTEN_AV_ÅRET = 12 - TILL_OCH_MED_MÅNAD;
		
		

		// read the csv file
		// for every line check source
		// "Bokföringsdatum";"Transaktionsdatum";"Transaktionstyp";"Meddelande";"Belopp"

		List<String[]> records = null;
		CSVParser csvParser = new CSVParserBuilder().withSeparator(';').build(); // custom separator
		try (CSVReader reader = new CSVReaderBuilder(new FileReader(pathToFile)).withCSVParser(csvParser) // custom CSV
																											// parser
				.build()) {
			records = reader.readAll();
//			r.forEach(x -> System.out.println(Arrays.toString(x)));
		}

		Map<String, Double> summaPerBolag = new HashMap<>();
		Double pension = 0.0D;
		Double tjanstePension = 0.0D;

		boolean isHeaderLine = true;
		for (String record[] : records) {
			if (isHeaderLine) {
				isHeaderLine = false;
				continue;
			}

			String bokforingsDatum = record[0];
			String transaktionsDatumStr = record[1];
			
			LocalDate transaktionsDatum = LocalDate.parse(transaktionsDatumStr);
			int transaktionsMonth = transaktionsDatum.getMonth().getValue();
			if (transaktionsMonth > TILL_OCH_MED_MÅNAD) {
				continue;
			}
			
			String meddelande = record[3].toUpperCase().trim();
			String beloppStr = record[4].strip();
			beloppStr = beloppStr.replace(" ", "");
			beloppStr = beloppStr.replace(',', '.');

			Double belopp = Double.parseDouble(beloppStr);
			if (belopp < 0) {
				continue;
			}

			if (meddelande.equals("PENSION") || meddelande.equals("PENSION SPP") || meddelande.contains("SKANDIA")
					|| meddelande.contains("AVANZA") || meddelande.contains("LÄNSFÖRSÄKR")
					|| meddelande.contains("ALECTA") || meddelande.contains("SHB")

			) {
				if (summaPerBolag.containsKey(meddelande)) {
					Double wrk = summaPerBolag.get(meddelande);
					wrk = wrk + belopp;
					summaPerBolag.put(meddelande, wrk);
				} else {
					summaPerBolag.put(meddelande, belopp);
				}

				continue;
			}
		}

		Set<String> keys = summaPerBolag.keySet();
		for (String key : keys) {
			Double beloppWrk = summaPerBolag.get(key);
			if (key.equals("PENSION")) {
				pension = pension + beloppWrk;
			} else {
				tjanstePension = tjanstePension + beloppWrk;
			}

		}

		Double pensionPerManad = pension / TILL_OCH_MED_MÅNAD;
		Double tjanstepensionPerManad = tjanstePension / TILL_OCH_MED_MÅNAD;
		Double tjanstepensionBrutto = calcBrutto(tjanstePension);

		String report ; 
		report = String.format("=============================================== Till och med månad %s\n",TILL_OCH_MED_MÅNAD);
		report += String.format("Summa per bolag: %s\n", summaPerBolag);
		report += String.format("===============================================\n");
		report += String.format("Statlig pension hittills (netto)  :%10.2f\n", pension);
		report += String.format("Tjänstepension hittills  (netto)  :%10.2f\n", tjanstePension);
		report += String.format("Tjänstepension hittills  (brutto) :%10.2f\n", tjanstepensionBrutto);
		report += String.format("Dragen skatt tjänste hittills     :%10.2f\n", tjanstepensionBrutto - tjanstePension);
		report += String.format("===============================================\n");
		report += String.format("Pension per månad                 :%10.2f\n", pensionPerManad);
		report += String.format("Tjänstepension per månad          :%10.2f\n", tjanstepensionPerManad);
		report += String.format("Summa                             :%10.2f\n", tjanstepensionPerManad + pensionPerManad);
		report += String.format("===============================================\n");

		report += String.format("Pension kvar att få i år          :%10.2f\n",  (pensionPerManad * ANTAL_MÅNADER_RESTEN_AV_ÅRET));
		report += String.format("Tjänstepension kvar att få i år   :%10.2f\n" , (tjanstepensionPerManad * ANTAL_MÅNADER_RESTEN_AV_ÅRET));
		report += String.format("Summa                             :%10.2f\n", (pensionPerManad * ANTAL_MÅNADER_RESTEN_AV_ÅRET) + (tjanstepensionPerManad * ANTAL_MÅNADER_RESTEN_AV_ÅRET));
		report += String.format("===============================================\n");
		
		System.out.println(report);
		printToFile(report);

	}
	
	private void printToFile(String content) throws IOException {
		
		String path = System.getProperty("user.home") + "/Documents/rapport.txt";
		// TODO Auto-generated method stub
		Files.write(Paths.get(path), content.getBytes(), 
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		
	}

	Double calcBrutto(Double peng) {
		peng = peng * 100d;
		peng = peng / 70d;
		//System.out.println(String.format("%-7.2f", 100d / 70d));
		return peng;
	}

}
