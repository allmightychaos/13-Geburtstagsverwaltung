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
    private DefaultTableModel model;
    private final List<Birthday> birthdayList = new ArrayList<>();
    private final String xmlFilePath = "birthdays.xml";

    public BirthdayManager() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        loadBirthdays();
        createUI();
    }

    private void createUI() {
        setJMenuBar(createMenuBar());
        JTable table = new JTable(model = new DefaultTableModel(new String[]{"Date", "Name"}, 0)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        refreshTable();
        add(new JScrollPane(table));
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem addItem = createMenuItem("Add Birthday", KeyEvent.VK_N);
        JMenuItem deleteItem = createMenuItem("Delete Birthday", KeyEvent.VK_DELETE);
        JMenuItem changeLanguageItem = new JMenuItem("Change Language");

        menu.add(addItem);
        menu.add(deleteItem);
        menu.add(changeLanguageItem);
        menuBar.add(menu);

        addItem.addActionListener(e -> addBirthday());
        deleteItem.addActionListener(e -> deleteBirthday(birthdayList, model));
        changeLanguageItem.addActionListener(e -> changeLanguage());

        return menuBar;
    }

    private JMenuItem createMenuItem(String title, int keyEvent) {
        JMenuItem item = new JMenuItem(title);
        item.setAccelerator(KeyStroke.getKeyStroke(keyEvent, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        return item;
    }

    private void addBirthday() {
        JTextField nameField = new JTextField(10);
        JDateChooser dateChooser = new JDateChooser();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Date:"));
        panel.add(dateChooser);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Birthday", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && dateChooser.getDate() != null && !nameField.getText().isEmpty()) {
            birthdayList.add(new Birthday(nameField.getText(), dateChooser.getDate()));
            refreshTable();
            saveBirthdays();
        }
    }

    private void deleteBirthday(List<Birthday> list, DefaultTableModel model) {
        int selectedRow = model.getRowCount() > 0 ? getSelectedRow() : -1;
        if (selectedRow != -1) {
            list.remove(selectedRow);
            refreshTable();
            saveBirthdays();
        }
    }

    private int getSelectedRow() {
        return new JTable(model).getSelectedRow();
    }

    private void changeLanguage() {
        Locale.setDefault(Locale.getDefault().equals(Locale.GERMANY) ? Locale.ENGLISH : Locale.GERMANY);
        refreshTable();
    }

    private void refreshTable() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MMMM", Locale.getDefault());
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
            Logger.getLogger(BirthdayManager.class.getName()).log(Level.SEVERE, "Error processing XML", e);
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
