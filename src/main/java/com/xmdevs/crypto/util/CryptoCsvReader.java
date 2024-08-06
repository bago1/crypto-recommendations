package com.xmdevs.crypto.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import com.xmdevs.crypto.data.CryptoData;
import com.xmdevs.crypto.model.Crypto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CryptoCsvReader {
    public static final String CSV = ".csv";
    private final CryptoData data;
    private static final String DIRECTORY_PATH = "src/main/resources/prices/";
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("^[A-Z]+_values\\.csv$");

    @Scheduled(cron = "0 0 0 * * ?")
    @PostConstruct
    public void loadData() {
        try (Stream<Path> paths = Files.list(Paths.get(DIRECTORY_PATH))) {
            List<String> cryptoFiles = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(string -> string.endsWith(CSV))
                    .toList();

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

    public void processUploadedFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();

        if (fileName == null || !FILE_NAME_PATTERN.matcher(fileName).matches()) {
            throw new IllegalArgumentException("Invalid file name format. Expected format: CURNAME_values.csv");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
            String[] line;
            List<Crypto> cryptoValues = new ArrayList<>();
            while ((line = csvReader.readNext()) != null) {
                Crypto crypto = new Crypto();
                crypto.setTimestamp(Long.parseLong(line[0].trim()));
                crypto.setSymbol(line[1].trim());
                crypto.setPrice(Double.parseDouble(line[2].trim()));

                cryptoValues.add(crypto);
            }
            String symbol = getSymbolFromFileName(file.getOriginalFilename());
            persistData(symbol, cryptoValues);
        } catch (Exception e) {
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
