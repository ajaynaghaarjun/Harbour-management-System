package com.harbormgmt.ui;

import com.formdev.flatlaf.FlatLightLaf;
import com.harbormgmt.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window that contains all panels
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private HarborPanel harborPanel;
    private ShipPanel shipPanel;
    private DockPanel dockPanel;
    private CargoPanel cargoPanel;
    private EmployeePanel employeePanel;
    
    /**
     * Constructor to initialize the main application frame
     */
    public MainFrame() {
        setTitle("Harbor Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set icon if available
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/harbor_icon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // If icon not found, continue without setting icon
        }
        
        // Initialize components
        initComponents();
        
        // Add window listener to handle application close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }
    
    /**
     * Initialize all UI components
     */
    private void initComponents() {
        // Set up the tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Initialize panels
        dashboardPanel = new DashboardPanel();
        harborPanel = new HarborPanel();
        shipPanel = new ShipPanel();
        dockPanel = new DockPanel();
        cargoPanel = new CargoPanel();
        employeePanel = new EmployeePanel();
        
        // Add tabs with icons if available
        tabbedPane.addTab("Dashboard", createIcon("dashboard.png"), dashboardPanel);
        tabbedPane.addTab("Harbors", createIcon("harbor.png"), harborPanel);
        tabbedPane.addTab("Ships", createIcon("ship.png"), shipPanel);
        tabbedPane.addTab("Docks", createIcon("dock.png"), dockPanel);
        tabbedPane.addTab("Cargo", createIcon("cargo.png"), cargoPanel);
        tabbedPane.addTab("Employees", createIcon("employee.png"), employeePanel);
        
        // Add toolbar with common actions
        JToolBar toolBar = createToolBar();
        
        // Set up status bar
        JPanel statusBar = createStatusBar();
        
        // Add components to frame
        add(toolBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        // Set initial tab
        tabbedPane.setSelectedIndex(0);
    }
    
    /**
     * Create toolbar with common actions
     * @return the toolbar
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Add buttons with icons
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setIcon(createIcon("refresh.png"));
        refreshButton.setToolTipText("Refresh current view");
        refreshButton.addActionListener(e -> refreshCurrentPanel());
        
        JButton addButton = new JButton("Add New");
        addButton.setIcon(createIcon("add.png"));
        addButton.setToolTipText("Add new item");
        addButton.addActionListener(e -> addNewItem());
        
        JButton helpButton = new JButton("Help");
        helpButton.setIcon(createIcon("help.png"));
        helpButton.setToolTipText("Show help");
        helpButton.addActionListener(e -> showHelp());
        
        toolBar.add(refreshButton);
        toolBar.add(addButton);
        toolBar.addSeparator();
        toolBar.add(helpButton);
        
        return toolBar;
    }
    
    /**
     * Create status bar for application
     * @return the status bar panel
     */
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel databaseLabel = new JLabel("Connected to Database ");
        databaseLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(databaseLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    /**
     * Create an icon from the resources folder
     * @param imageName the name of the image file
     * @return the icon, or null if not found
     */
    private ImageIcon createIcon(String imageName) {
        try {
            return new ImageIcon(getClass().getResource("/images/" + imageName));
        } catch (Exception e) {
            // If icon not found, return null
            return null;
        }
    }
    
    /**
     * Refresh the currently selected panel
     */
    private void refreshCurrentPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        
        switch (selectedIndex) {
            case 0: // Dashboard
                dashboardPanel.refreshData();
                break;
            case 1: // Harbors
                harborPanel.refreshData();
                break;
            case 2: // Ships
                shipPanel.refreshData();
                break;
            case 3: // Docks
                dockPanel.refreshData();
                break;
            case 4: // Cargo
                cargoPanel.refreshData();
                break;
            case 5: // Employees
                employeePanel.refreshData();
                break;
        }
    }
    
    /**
     * Add a new item in the current panel
     */
    private void addNewItem() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        
        switch (selectedIndex) {
            case 1: // Harbors
                harborPanel.showAddDialog();
                break;
            case 2: // Ships
                shipPanel.showAddDialog();
                break;
            case 3: // Docks
                dockPanel.showAddDialog();
                break;
            case 4: // Cargo
                cargoPanel.showAddDialog();
                break;
            case 5: // Employees
                employeePanel.showAddDialog();
                break;
            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Adding new items is not available in this tab.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Show help information
     */
    private void showHelp() {
        JOptionPane.showMessageDialog(
                this,
                "Harbor Management System\n\n" +
                "Use the tabs to navigate between different sections of the application.\n" +
                "Each section allows you to view, add, edit, and delete items.\n\n" +
                "For more information, please refer to the user manual.",
                "Help",
                JOptionPane.INFORMATION_MESSAGE);
    }
}