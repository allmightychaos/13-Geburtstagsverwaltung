import com.toedter.calendar.JDateChooser;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BirthdayManager extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private final List<Birthday> birthdayList = new ArrayList<>();
    private final String xmlFilePath = "birthdays.xml";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM. yyyy", Locale.getDefault());

    public BirthdayManager() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        createUI();
        loadBirthdays();
    }

    private void createUI() {
        model = new DefaultTableModel(new String[]{"Geburtstag", "Name"}, 0);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshTable();
        add(new JScrollPane(table));
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menü");
        JMenuItem addItem = createMenuItem("Geburtstag hinzufügen", KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        JMenuItem deleteItem = createMenuItem("Geburtstag löschen", KeyEvent.VK_BACK_SPACE, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
        JMenuItem changeLanguageItem = createMenuItem("Sprache ändern", KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());

        JMenu dateMenu = getDateMenu();

        menu.add(addItem);
        menu.add(deleteItem);
        menu.add(changeLanguageItem);
        menu.add(dateMenu);
        menuBar.add(menu);

        addItem.addActionListener(e -> addBirthday());
        deleteItem.addActionListener(e -> deleteBirthday());
        changeLanguageItem.addActionListener(e -> changeLanguage());

        return menuBar;
    }

    private JMenu getDateMenu() {
        JMenu dateMenu = new JMenu("Datum");
        String[] dateFormats = {"dd MMM yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "dd-MMM-yy"};

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < dateFormats.length; i++) {
            JCheckBoxMenuItem formatItem = new JCheckBoxMenuItem(dateFormats[i]);
            if (i == 0) {
                formatItem.setSelected(true);
            }
            group.add(formatItem);
            dateMenu.add(formatItem);

            int finalI = i;

            formatItem.addActionListener(e -> {
                changeDateFormat(dateFormats[finalI]);
            });
        }
        return dateMenu;
    }

    private void changeDateFormat(String newFormat) {
        dateFormat = new SimpleDateFormat(newFormat, Locale.getDefault());
        refreshTable();
    }

    private JMenuItem createMenuItem(String title, int keyEvent, int modifier) {
        JMenuItem item = new JMenuItem(title);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, modifier);
        item.setAccelerator(keyStroke);
        return item;
    }

    private void addBirthday() {
        JTextField nameField = new JTextField(10);
        JDateChooser dateChooser = new JDateChooser();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Geburtstag:"));
        panel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(this, panel, "Geburtstag hinzufügen", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && dateChooser.getDate() != null && !nameField.getText().isEmpty()) {
            birthdayList.add(new Birthday(nameField.getText(), dateChooser.getDate()));
            refreshTable();
            saveBirthdays();
        }
    }

    private void deleteBirthday() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            birthdayList.remove(selectedRow);
            model.removeRow(selectedRow);
            saveBirthdays();
        }
    }

    private void changeLanguage() {
        Locale.setDefault(Locale.getDefault().equals(Locale.GERMANY) ? Locale.ENGLISH : Locale.GERMANY);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        birthdayList.forEach(b -> model.addRow(new Object[]{dateFormat.format(b.getDate()), b.getName()}));
    }

    private void loadBirthdays() {
        File file = new File(xmlFilePath);
        if (file.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(BirthdayList.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                BirthdayList birthdayListWrapper = (BirthdayList) unmarshaller.unmarshal(file);
                birthdayList.addAll(birthdayListWrapper.getBirthdays());
                refreshTable();
            } catch (JAXBException e) {
                Logger.getLogger(BirthdayManager.class.getName()).log(Level.SEVERE, "Error processing XML", e);
            }
        }
    }

    private void saveBirthdays() {
        try {
            JAXBContext context = JAXBContext.newInstance(BirthdayList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new BirthdayList(birthdayList), new File(xmlFilePath));
        } catch (JAXBException e) {
            Logger.getLogger(BirthdayManager.class.getName()).log(Level.SEVERE, "Error saving XML", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BirthdayManager().setVisible(true));
    }
}
@XmlRootElement(name = "birthday")
@XmlAccessorType(XmlAccessType.FIELD)
class Birthday {
    private String name;
    private Date date;

    public Birthday() {}
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
}

@XmlRootElement(name = "birthdays")
@XmlAccessorType(XmlAccessType.FIELD)
class BirthdayList {
    private List<Birthday> birthdays;

    public BirthdayList() {}
    public BirthdayList(List<Birthday> birthdays) {
        this.birthdays = birthdays;
    }

    public List<Birthday> getBirthdays() {
        return birthdays == null ? new ArrayList<>() : birthdays;
    }
}