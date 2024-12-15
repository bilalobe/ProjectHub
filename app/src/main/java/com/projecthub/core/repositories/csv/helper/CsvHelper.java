package com.projecthub.core.repositories.csv.helper;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.util.List;

/**
 * Writes a list of beans to a CSV file at the specified filepath.
 *
 * @param filepath the path to the CSV file to write to
 * @param type     the class type of the beans
 * @param beans    the list of beans to write to the CSV
 * @param columns  the column headers for the CSV
 * @param <T>      the type of the beans
 */
public class CsvHelper {

    private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);

    public static <T> void writeBeansToCsv(String filepath, Class<T> type, List<T> beans, String[] columns)
            throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {
            ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(type);
            strategy.setColumnMapping(columns);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).withMappingStrategy(strategy)
                    .build();
            beanToCsv.write(beans);
            logger.info("{} records written to CSV file: {}", beans.size(), filepath);
        }
    }
}
