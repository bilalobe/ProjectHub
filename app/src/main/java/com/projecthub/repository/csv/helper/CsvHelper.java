package com.projecthub.repository.csv.helper;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.List;

public class CsvHelper {

    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

    public static <T> void writeBeansToCsv(String filepath, Class<T> type, List<T> beans, String[] columns) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(type);
            strategy.setColumnMapping(columns);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(beans);
            logger.info("{} records written to CSV file: {}", beans.size(), filepath);
        }
    }
}
