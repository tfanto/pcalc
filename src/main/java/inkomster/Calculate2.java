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

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

public class Calculate2 {

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

		boolean isHeaderLine = true;
		for (String record[] : records) {
			if (isHeaderLine) {
				isHeaderLine = false;
				continue;
			}

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
			if (belopp >= 0) {
				continue;
			}

			if (summaPerBolag.containsKey(meddelande)) {
				Double wrk = summaPerBolag.get(meddelande);
				wrk = wrk + belopp;
				summaPerBolag.put(meddelande, wrk);
			} else {
				summaPerBolag.put(meddelande, belopp);
			}

		}

		String report;
		report = String.format("=============================================================== Till och med månad %s\n",
				TILL_OCH_MED_MÅNAD);

		for (Map.Entry<String, Double> entry : summaPerBolag.entrySet()) {
			report += String.format("%60s   %10.2f\n", entry.getKey(), entry.getValue());
		}

		report += String.format("===============================================================\n");
		printToFile(report);

	}

	private void printToFile(String content) throws IOException {

		String path = System.getProperty("user.home") + "/Documents/rapportUtgifter.txt";
		// TODO Auto-generated method stub
		Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);

	}

}
