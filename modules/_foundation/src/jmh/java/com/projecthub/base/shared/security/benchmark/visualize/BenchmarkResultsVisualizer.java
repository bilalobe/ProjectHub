package com.projecthub.base.shared.security.benchmark.visualize;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.chart.renderer.category.*;
import java.awt.*;

public class BenchmarkResultsVisualizer {
    
    private static final String RESULTS_FILE = "build/reports/jmh/results.csv";
    private static final String CHARTS_DIR = "build/reports/jmh/charts";
    
    public static void main(String[] args) throws Exception {
        List<BenchmarkResult> results = readResults();
        generateCharts(results);
        generateReport(results);
    }
    
    private static List<BenchmarkResult> readResults() throws Exception {
        List<BenchmarkResult> results = new ArrayList<>();
        Path path = Paths.get(RESULTS_FILE);
        
        List<String> lines = Files.readAllLines(path);
        // Skip header
        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            results.add(new BenchmarkResult(
                parts[0], // Benchmark name
                Double.parseDouble(parts[1]), // Score
                Double.parseDouble(parts[2]), // Error
                parts[3] // Units
            ));
        }
        return results;
    }
    
    private static void generateCharts(List<BenchmarkResult> results) throws Exception {
        // Performance chart
        createBarChart(
            filterResults(results, "PermissionCheckBenchmark"),
            "Permission Check Performance",
            "Operation",
            "Time (microseconds)",
            "performance.png"
        );
        
        // Memory usage chart
        createBarChart(
            filterResults(results, "MemoryUsageBenchmark"),
            "Memory Usage Analysis",
            "Scenario",
            "Memory (MB)",
            "memory.png"
        );
    }
    
    private static void createBarChart(
            List<BenchmarkResult> results,
            String title,
            String xLabel,
            String yLabel,
            String filename) throws Exception {
            
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (BenchmarkResult result : results) {
            dataset.addValue(result.score, "Score", simplifyBenchmarkName(result.benchmark));
            // Add error bars
            dataset.addValue(result.score + result.error, "Error+", simplifyBenchmarkName(result.benchmark));
            dataset.addValue(result.score - result.error, "Error-", simplifyBenchmarkName(result.benchmark));
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            title,
            xLabel,
            yLabel,
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize appearance
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185));
        renderer.setDrawBarOutline(true);
        renderer.setItemMargin(0.1);
        
        // Save chart
        Files.createDirectories(Paths.get(CHARTS_DIR));
        ChartUtils.saveChartAsPNG(
            new File(CHARTS_DIR + "/" + filename),
            chart,
            800,
            600
        );
    }
    
    private static void generateReport(List<BenchmarkResult> results) throws Exception {
        StringBuilder report = new StringBuilder();
        report.append("# RBAC Performance Benchmark Results\n\n");
        
        // Performance results
        report.append("## Performance Results\n\n");
        report.append("| Operation | Time (μs) | Error (μs) |\n");
        report.append("|-----------|-----------|------------|\n");
        
        for (BenchmarkResult result : filterResults(results, "PermissionCheckBenchmark")) {
            report.append(String.format("| %s | %.2f | ±%.2f |\n",
                simplifyBenchmarkName(result.benchmark),
                result.score,
                result.error));
        }
        
        // Memory usage results
        report.append("\n## Memory Usage Results\n\n");
        report.append("| Scenario | Memory (MB) | Error (MB) |\n");
        report.append("|----------|-------------|-------------|\n");
        
        for (BenchmarkResult result : filterResults(results, "MemoryUsageBenchmark")) {
            report.append(String.format("| %s | %.2f | ±%.2f |\n",
                simplifyBenchmarkName(result.benchmark),
                result.score / (1024 * 1024), // Convert to MB
                result.error / (1024 * 1024)));
        }
        
        // Write report
        Files.write(
            Paths.get(CHARTS_DIR + "/benchmark-report.md"),
            report.toString().getBytes()
        );
    }
    
    private static List<BenchmarkResult> filterResults(List<BenchmarkResult> results, String prefix) {
        return results.stream()
            .filter(r -> r.benchmark.startsWith(prefix))
            .collect(Collectors.toList());
    }
    
    private static String simplifyBenchmarkName(String fullName) {
        // Extract method name from full benchmark name
        String[] parts = fullName.split("\\.");
        return parts[parts.length - 1];
    }
    
    private static class BenchmarkResult {
        final String benchmark;
        final double score;
        final double error;
        final String units;
        
        BenchmarkResult(String benchmark, double score, double error, String units) {
            this.benchmark = benchmark;
            this.score = score;
            this.error = error;
            this.units = units;
        }
    }
}