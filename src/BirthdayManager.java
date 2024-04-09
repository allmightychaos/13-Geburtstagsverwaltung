import jakarta.xml.bind.annotation.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BirthdayManager extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private final List<Birthday> birthdayList = new ArrayList<>();
    private final String xmlFilePath = "birthdays.xml";

    public BirthdayManager() {
        loadBirthdays();
        createUI();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void createUI() {
        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem addItem = new JMenuItem("Add Birthday");
        JMenuItem deleteItem = new JMenuItem("Delete Birthday");
        JMenuItem changeLanguageItem = new JMenuItem("Change Language");
        menu.add(addItem);
        menu.add(deleteItem);
        menu.add(changeLanguageItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Table
        String[] columnNames = {"Date", "Birthdays"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Custom data
        DateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN); // Use appropriate locale
        model.addRow(new Object[]{df.format(new Date()), "Alexander Mustermann"});
        model.addRow(new Object[]{df.format(new Date()), "Marina Trump"});
        model.addRow(new Object[]{"30. Jänner", "Max Moritz"});
        model.addRow(new Object[]{"3. Februar", "Maria Smith"});

        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        // Listeners
        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBirthday();
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBirthday();
            }
        });

        changeLanguageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLanguage();
            }
        });
    }

    private void addBirthday() {
        // TODO: Implement add birthday logic using JDatePicker
    }

    private void deleteBirthday() {
        // TODO: Implement delete birthday logic
    }

    private void changeLanguage() {
        // TODO: Implement language change logic
    }

    private void refreshTable() {
        // TODO: Implement refresh logic for the table
    }

    private void loadBirthdays() {
        // TODO: Implement load logic from XML
    }

    private void saveBirthdays() {
        // TODO: Implement save logic to XML
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BirthdayManager().setVisible(true);
            }
        });
    }
}

// Birthday class to store birthday data
@XmlRootElement(name = "birthday")
@XmlAccessorType(XmlAccessType.FIELD)
class Birthday {
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "date")
    @XmlSchemaType(name = "date")
    private Date date;

    // Getters and setters
}

// BirthdayList class to store the list of birthdays
@XmlRootElement(name = "birthdays")
@XmlAccessorType(XmlAccessType.FIELD)
class BirthdayList {
    @XmlElement(name = "birthday")
    private List<Birthday> birthdays = null;

    // Getters and setters
}
