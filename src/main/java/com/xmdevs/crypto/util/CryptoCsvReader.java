package com.xmdevs.crypto.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.xmdevs.crypto.data.CyrptoData;
import com.xmdevs.crypto.model.Crypto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CryptoCsvReader {
    private final CyrptoData data;
    private static final String DIRECTORY_PATH = "src/main/resources/prices/";

    @PostConstruct
    public void loadData() {
        try (Stream<Path> paths = Files.list(Paths.get(DIRECTORY_PATH))) {
            List<String> cryptoFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".csv"))
                    .map(Path::toString)
                    .collect(Collectors.toList());

            for (String cryptoFile : cryptoFiles) {
                try (CSVReader reader = new CSVReaderBuilder(new FileReader(cryptoFile)).withSkipLines(1).build()) {
                    String[] line;
                    List<Crypto> cryptoValues = new ArrayList<>();
                    while ((line = reader.readNext()) != null) {
                        Crypto crypto = new Crypto();
                        crypto.setTimestamp(Long.parseLong(line[0].trim()));
                        crypto.setSymbol(line[1].trim());
                        crypto.setPrice(Double.parseDouble(line[2].trim()));

                        cryptoValues.add(crypto);
                    }
                    String symbol = getSymbolFromFileName(cryptoFile);
                    persistData(symbol, cryptoValues);
                } catch (IOException | CsvValidationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getSymbolFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        if (parts.length > 0) {
            return parts[0].replace(DIRECTORY_PATH, "");
        }
        return "UNKNOWN";
    }


    private void persistData(String file, List<Crypto> cryptoList) {
        data.setCryptoData(file.split("_")[0], cryptoList);
    }
}
