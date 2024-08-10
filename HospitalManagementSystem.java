import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HospitalManagementSystem extends JFrame {
    
    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/HospitalDB";
    private static final String USER = "root"; // Replace with your DB username
    private static final String PASSWORD = ""; // Replace with your DB password

    // Constructor to set up the GUI
    public HospitalManagementSystem() {
        setTitle("Hospital Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Using JTabbedPane to create tabs for each module
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patient Registration", new PatientRegistrationPanel());
        tabbedPane.addTab("Appointment Scheduling", new AppointmentSchedulingPanel());
        tabbedPane.addTab("Electronic Health Records", new EHRPanel());
        tabbedPane.addTab("Billing", new BillingPanel());
        tabbedPane.addTab("Inventory Management", new InventoryManagementPanel());
        tabbedPane.addTab("Staff Management", new StaffManagementPanel());

        // Adding the tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);
    }

    // Database connection method
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC driver not found.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    

    // Main method to launch the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HospitalManagementSystem().setVisible(true));
    }

    // Panel for Patient Registration
    class PatientRegistrationPanel extends JPanel {
        private JTextField firstNameField, lastNameField, ageField, contactNumberField;
        private JTextArea addressArea, medicalHistoryArea;
        private JComboBox<String> genderComboBox;

        public PatientRegistrationPanel() {
            setLayout(new GridLayout(9, 2));

            add(new JLabel("First Name:"));
            firstNameField = new JTextField();
            add(firstNameField);

            add(new JLabel("Last Name:"));
            lastNameField = new JTextField();
            add(lastNameField);

            add(new JLabel("Age:"));
            ageField = new JTextField();
            add(ageField);

            add(new JLabel("Gender:"));
            String[] genders = {"Male", "Female", "Other"};
            genderComboBox = new JComboBox<>(genders);
            add(genderComboBox);

            add(new JLabel("Address:"));
            addressArea = new JTextArea();
            add(addressArea);

            add(new JLabel("Contact Number:"));
            contactNumberField = new JTextField();
            add(contactNumberField);

            add(new JLabel("Medical History:"));
            medicalHistoryArea = new JTextArea();
            add(medicalHistoryArea);

            JButton registerButton = new JButton("Register");
            add(registerButton);
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    registerPatient();
                }
            });
        }

        private void registerPatient() {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = (String) genderComboBox.getSelectedItem();
            String address = addressArea.getText();
            String contactNumber = contactNumberField.getText();
            String medicalHistory = medicalHistoryArea.getText();

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Patients (FirstName, LastName, Age, Gender, Address, ContactNumber, MedicalHistory) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setInt(3, age);
                stmt.setString(4, gender);
                stmt.setString(5, address);
                stmt.setString(6, contactNumber);
                stmt.setString(7, medicalHistory);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Patient registered successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error registering patient: " + ex.getMessage());
            }
        }
    }

    // Panel for Appointment Scheduling
    class AppointmentSchedulingPanel extends JPanel {
        private JTextField patientIdField, doctorField, dateField, timeField;

        public AppointmentSchedulingPanel() {
            setLayout(new GridLayout(5, 2));

            add(new JLabel("Patient ID:"));
            patientIdField = new JTextField();
            add(patientIdField);

            add(new JLabel("Doctor:"));
            doctorField = new JTextField();
            add(doctorField);

            add(new JLabel("Date (YYYY-MM-DD):"));
            dateField = new JTextField();
            add(dateField);

            add(new JLabel("Time (HH:MM):"));
            timeField = new JTextField();
            add(timeField);

            JButton scheduleButton = new JButton("Schedule Appointment");
            add(scheduleButton);
            scheduleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    scheduleAppointment();
                }
            });
        }

        private void scheduleAppointment() {
            int patientId = Integer.parseInt(patientIdField.getText());
            String doctor = doctorField.getText();
            String date = dateField.getText();
            String time = timeField.getText();

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Appointments (PatientID, Doctor, Date, Time) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, patientId);
                stmt.setString(2, doctor);
                stmt.setString(3, date);
                stmt.setString(4, time);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error scheduling appointment: " + ex.getMessage());
            }
        }
    }

    // Panel for Electronic Health Records (EHR)
    class EHRPanel extends JPanel {
        private JTextField patientIdField;
        private JTextArea recordsArea;

        public EHRPanel() {
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new GridLayout(1, 2));
            add(topPanel, BorderLayout.NORTH);

            topPanel.add(new JLabel("Patient ID:"));
            patientIdField = new JTextField();
            topPanel.add(patientIdField);

            JButton fetchButton = new JButton("Fetch Records");
            topPanel.add(fetchButton);
            fetchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fetchRecords();
                }
            });

            recordsArea = new JTextArea();
            add(new JScrollPane(recordsArea), BorderLayout.CENTER);
        }

        private void fetchRecords() {
            int patientId = Integer.parseInt(patientIdField.getText());
            recordsArea.setText("");

            try (Connection conn = getConnection()) {
                String sql = "SELECT * FROM MedicalRecords WHERE PatientID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, patientId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    recordsArea.append("Record ID: " + rs.getInt("RecordID") + "\n");
                    recordsArea.append("Date: " + rs.getString("Date") + "\n");
                    recordsArea.append("Doctor: " + rs.getString("Doctor") + "\n");
                    recordsArea.append("Diagnosis: " + rs.getString("Diagnosis") + "\n");
                    recordsArea.append("Treatment: " + rs.getString("Treatment") + "\n");
                    recordsArea.append("Notes: " + rs.getString("Notes") + "\n\n");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching records: " + ex.getMessage());
            }
        }
    }

    // Panel for Billing
    class BillingPanel extends JPanel {
        private JTextField patientIdField, amountField;

        public BillingPanel() {
            setLayout(new GridLayout(3, 2));

            add(new JLabel("Patient ID:"));
            patientIdField = new JTextField();
            add(patientIdField);

            add(new JLabel("Amount:"));
            amountField = new JTextField();
            add(amountField);

            JButton billButton = new JButton("Generate Bill");
            add(billButton);
            billButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generateBill();
                }
            });
        }

        private void generateBill() {
            int patientId = Integer.parseInt(patientIdField.getText());
            double amount = Double.parseDouble(amountField.getText());

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Billing (PatientID, Amount) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, patientId);
                stmt.setDouble(2, amount);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Bill generated successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error generating bill: " + ex.getMessage());
            }
        }
    }

    // Panel for Inventory Management
    class InventoryManagementPanel extends JPanel {
        private JTextField itemNameField, quantityField;

        public InventoryManagementPanel() {
            setLayout(new GridLayout(4, 2));

            add(new JLabel("Item Name:"));
            itemNameField = new JTextField();
            add(itemNameField);

            add(new JLabel("Quantity:"));
            quantityField = new JTextField();
            add(quantityField);

            JButton addButton = new JButton("Add Item");
            add(addButton);
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addItem();
                }
            });

            JButton checkButton = new JButton("Check Inventory");
            add(checkButton);
            checkButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkInventory();
                }
            });
        }

        private void addItem() {
            String itemName = itemNameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Inventory (ItemName, Quantity) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, itemName);
                stmt.setInt(2, quantity);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Item added successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage());
            }
        }

        private void checkInventory() {
            StringBuilder inventoryList = new StringBuilder();

            try (Connection conn = getConnection()) {
                String sql = "SELECT * FROM Inventory";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    inventoryList.append("Item ID: " + rs.getInt("ItemID") + "\n");
                    inventoryList.append("Item Name: " + rs.getString("ItemName") + "\n");
                    inventoryList.append("Quantity: " + rs.getInt("Quantity") + "\n\n");
                }

                JOptionPane.showMessageDialog(this, inventoryList.toString());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error checking inventory: " + ex.getMessage());
            }
        }
    }

    // Panel for Staff Management
    class StaffManagementPanel extends JPanel {
        private JTextField nameField, roleField, scheduleField;

        public StaffManagementPanel() {
            setLayout(new GridLayout(4, 2));

            add(new JLabel("Name:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Role:"));
            roleField = new JTextField();
            add(roleField);

            add(new JLabel("Schedule:"));
            scheduleField = new JTextField();
            add(scheduleField);

            JButton addButton = new JButton("Add Staff");
            add(addButton);
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addStaff();
                }
            });
        }

        private void addStaff() {
            String name = nameField.getText();
            String role = roleField.getText();
            String schedule = scheduleField.getText();

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Staff (Name, Role, Schedule) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, role);
                stmt.setString(3, schedule);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Staff added successfully!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding staff: " + ex.getMessage());
            }
        }
    }
}
