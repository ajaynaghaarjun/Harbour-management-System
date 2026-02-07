package com.harbormgmt.ui.panels;

import com.harbormgmt.dao.*;
import com.harbormgmt.model.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dashboard panel that displays overview information
 */
public class DashboardPanel extends JPanel {
    
    private final ShipDAO shipDAO;
    private final DockDAO dockDAO;
    private final HarborDAO harborDAO;
    private final EmployeeDAO employeeDAO;
    
    private JPanel chartsPanel;
    private JPanel statsPanel;
    
    /**
     * Constructor to initialize the dashboard panel
     */
    public DashboardPanel() {
        this.shipDAO = new ShipDAO();
        this.dockDAO = new DockDAO();
        this.harborDAO = new HarborDAO();
        this.employeeDAO = new EmployeeDAO();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        refreshData();
    }
    
    /**
     * Initialize dashboard components
     */
    private void initComponents() {
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Harbor Management Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 153));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        // Create stats panel
        statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        
        // Create charts panel
        chartsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(chartsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh all dashboard data
     */
    public void refreshData() {
        // Clear existing components
        statsPanel.removeAll();
        chartsPanel.removeAll();
        
        // Load new data
        List<Ship> ships = shipDAO.getAllShips();
        List<Dock> docks = dockDAO.getAllDocks();
        List<Harbor> harbors = harborDAO.getAllHarbors();
        List<Employee> employees = employeeDAO.getAllEmployees();
        
        // Create stat cards
        statsPanel.add(createStatCard("Total Ships", ships.size(), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Total Docks", docks.size(), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Total Harbors", harbors.size(), new Color(155, 89, 182)));
        statsPanel.add(createStatCard("Total Employees", employees.size(), new Color(231, 76, 60)));
        
        // Create charts
        chartsPanel.add(createDockStatusChart(docks));
        chartsPanel.add(createShipStatusChart(ships));
        
        // Refresh UI
        statsPanel.revalidate();
        statsPanel.repaint();
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
    
    /**
     * Create a stat card with title, value and color
     * @param title the title of the stat
     * @param value the value to display
     * @param color the background color
     * @return a panel containing the stat card
     */
    private JPanel createStatCard(String title, int value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setBackground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a pie chart showing dock statuses
     * @param docks list of docks
     * @return a chart panel containing the dock status chart
     */
    private ChartPanel createDockStatusChart(List<Dock> docks) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Count dock statuses
        int available = 0;
        int occupied = 0;
        int maintenance = 0;
        
        for (Dock dock : docks) {
            switch (dock.getStatus()) {
                case "Available":
                    available++;
                    break;
                case "Occupied":
                    occupied++;
                    break;
                case "Under Maintenance":
                    maintenance++;
                    break;
            }
        }
        
        dataset.setValue("Available", available);
        dataset.setValue("Occupied", occupied);
        dataset.setValue("Under Maintenance", maintenance);
        
        JFreeChart chart = ChartFactory.createPieChart(
                "Dock Status Distribution",
                dataset,
                true,
                true,
                false);
        
        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Available", new Color(46, 204, 113));
        plot.setSectionPaint("Occupied", new Color(52, 152, 219));
        plot.setSectionPaint("Under Maintenance", new Color(231, 76, 60));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
    
    /**
     * Create a bar chart showing ship statuses
     * @param ships list of ships
     * @return a chart panel containing the ship status chart
     */
    private ChartPanel createShipStatusChart(List<Ship> ships) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Count ship statuses
        int docked = 0;
        int atSea = 0;
        int maintenance = 0;
        int other = 0;
        
        for (Ship ship : ships) {
            String status = ship.getStatus();
            if (status == null) continue;
            
            if (status.equals("Docked")) {
                docked++;
            } else if (status.equals("At Sea") || status.equals("Sailing")) {
                atSea++;
            } else if (status.contains("Maintenance")) {
                maintenance++;
            } else {
                other++;
            }
        }
        
        dataset.addValue(docked, "Ships", "Docked");
        dataset.addValue(atSea, "Ships", "At Sea");
        dataset.addValue(maintenance, "Ships", "Maintenance");
        dataset.addValue(other, "Ships", "Other");
        
        JFreeChart chart = ChartFactory.createBarChart(
                "Ship Status Distribution",
                "Status",
                "Number of Ships",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 300));
        
        return chartPanel;
    }
}