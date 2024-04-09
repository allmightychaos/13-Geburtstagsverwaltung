import com.toedter.calendar.JDateChooser;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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

        addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

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
        DateFormat df = new SimpleDateFormat("dd. MMMM", Locale.GERMAN);
        model.addRow(new Object[]{df.format(new Date()), "Alexander Mustermann"});
        model.addRow(new Object[]{df.format(new Date()), "Marina Trump"});
        model.addRow(new Object[]{"30. Jänner", "Max Moritz"});
        model.addRow(new Object[]{"3. Februar", "Maria Smith"});

        refreshTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField nameField = new JTextField(10);
        JDateChooser dateChooser = new JDateChooser();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date:"));
        panel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Birthday",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Date date = dateChooser.getDate();
            String name = nameField.getText();
            if (date != null && !name.isEmpty()) {
                birthdayList.add(new Birthday(name, date));
                refreshTable();
                saveBirthdays();
            }
        }
    }

    private void deleteBirthday() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            birthdayList.remove(selectedRow);
            refreshTable();
            saveBirthdays();
        }
    }

    private void changeLanguage() {
        // [USED CHATGPT FOR THIS METHODE]
        // Simple implementation to change date format, to be replaced by full i18n approach
        Locale newLocale = Locale.getDefault().equals(Locale.GERMANY) ? Locale.ENGLISH : Locale.GERMANY;
        Locale.setDefault(newLocale);
        refreshTable();
    }

    private void refreshTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM", Locale.getDefault());
        model.setRowCount(0); // Clears the table
        for (Birthday b : birthdayList) {
            model.addRow(new Object[]{dateFormat.format(b.getDate()), b.getName()});
        }
    }

    private void loadBirthdays() {
        try {
            File file = new File(xmlFilePath);
            if (file.exists()) {
                JAXBContext context = JAXBContext.newInstance(BirthdayList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                BirthdayList birthdayListWrapper = (BirthdayList) unmarshaller.unmarshal(file);
                birthdayList.clear();
                birthdayList.addAll(birthdayListWrapper.getBirthdays());
                refreshTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBirthdays() {
        try {
            JAXBContext context = JAXBContext.newInstance(BirthdayList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            BirthdayList birthdayListWrapper = new BirthdayList();
            birthdayListWrapper.setBirthdays(birthdayList);
            marshaller.marshal(birthdayListWrapper, new File(xmlFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // No-argument constructor for JAXB
    public Birthday() {
    }

    // Constructor with arguments for the application
    public Birthday(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

// BirthdayList class to store the list of birthdays
@XmlRootElement(name = "birthdays")
@XmlAccessorType(XmlAccessType.FIELD)
class BirthdayList {
    private List<Birthday> birthdays;

    public BirthdayList() {
        birthdays = new ArrayList<>();
    }

    public List<Birthday> getBirthdays() {
        if (birthdays == null) {
            birthdays = new ArrayList<>();
        }
        return birthdays;
    }

    public void setBirthdays(List<Birthday> birthdays) {
        this.birthdays = birthdays;
    }
}