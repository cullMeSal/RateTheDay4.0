/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author ADMIN
 */

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.NumberFormatter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import java.time.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme;


public class AppUI extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AppUI.class.getName());
    
    /**
     * Creates new form AppUI
     */
public AppUI() throws Exception {
        initComponents();
        LocalDate today = LocalDate.now();
//        List<Day> dayList = DayReader.readDayFile("dayData.json");
        dayEntries = DayReader.readDayFile("dayData.json");
//        ArrayList<String> entryStringList = new ArrayList<String>();
//        for (Day day : dayEntries){
//            entryStringList.add(day.getDateISO());
//        }
//        Collections.sort(entryStringList);
//        
//        DefaultListModel<String> dayListModel = new DefaultListModel<>();
//        entryList.setModel(dayListModel);
//        
//        for (String str : entryStringList) {
//            dayListModel.addElement(str);
//        }
        DefaultListModel<String> dayListModel = new DefaultListModel<>();
        entryList.setModel(dayListModel);

        dayEntries.stream()
            .map(Day::getDateISO)
            .sorted()
            .forEach(dayListModel::addElement);

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter intFormatter = new NumberFormatter(format);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(1);
        intFormatter.setAllowsInvalid(false);

        
               
        // Set up month calendar
        
//        String[] months = Arrays.stream(Month.values())
//                .map(m -> m.getDisplayName(TextStyle.FULL, Locale.getDefault()))
//                .toArray(String[]::new);
//        monthBox = new JComboBox<>(months); 
        
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 100; i <= currentYear + 50; i++) {
            yearBox.addItem(i);
        }
        monthBox.setSelectedIndex(today.getMonthValue() - 1);
        yearBox.setSelectedItem(currentYear);

        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        tableModel = new DefaultTableModel(null, headers){
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        
        calendarTable.setModel(tableModel);
        calendarTable.setCellSelectionEnabled(true);
        
        monthBox.addActionListener(e -> updateCalendar());
        yearBox.addActionListener(e -> updateCalendar());
        calendarTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = calendarTable.getSelectedRow();
                int col = calendarTable.getSelectedColumn();
                Object val = calendarTable.getValueAt(row, col);
                                if (val != null) {
                    System.out.println("You selected: " + val + " " + monthBox.getSelectedItem() + " " + yearBox.getSelectedItem());
                }
                String date = String.format("%s-%s-%s",yearBox.getSelectedItem(),formatDateNumber(monthBox.getSelectedItem().toString()),formatDateNumber(String.valueOf(val)));
                System.out.println("ISO format: " + date);
                
//                Day found = null;
//                for (Day day : dayEntries){
//                    if (day.getDateISO().equals(date)){
//                        found = day;
//                        break;
//                    }
//                }
                Day found = dayEntries.stream()
                        .filter(day -> day.getDateISO().equals(date))
                        .findFirst()
                        .orElse(null);
                
                if (found != null){
                    ratingComboBox.setSelectedItem(String.valueOf(found.getRating()));
                    noteTextArea.setText(found.getNote());
                    
                } else {
                    ratingComboBox.setSelectedItem("1");
                    noteTextArea.setText("");
                }
                String chosenDate = formatDateNumber(String.valueOf(val))+ " / " +
                        formatDateNumber(monthBox.getSelectedItem().toString())+ " / " + 
                        yearBox.getSelectedItem();
//                System.out.println(chosenDate);
                chosenDateLabel.setText(chosenDate);
                dayBox.setSelectedItem(val);
                
//                Day chosenDay = new Day(date);
//                System.out.println(chosenDay);

            }
        });
//        jPanel1.setVisible(false);
        updateCalendar();
        
        entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = String.format("%s-%s-%s",
                        yearBox.getSelectedItem(),
                        formatDateNumber(monthBox.getSelectedItem().toString()),
                        formatDateNumber(dayBox.getSelectedItem().toString()));
//                System.out.println(date);

                try {
                    DayRecorder.saveData(new Day(date, Integer.parseInt(ratingComboBox.getSelectedItem().toString()), noteTextArea.getText()));
                    if (!dayListModel.contains(date)){
                        dayListModel.addElement(date);
                    }
                    updateDayEntries();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }            
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = String.format("%s-%s-%s",
                        yearBox.getSelectedItem(),
                        formatDateNumber(monthBox.getSelectedItem().toString()),
                        formatDateNumber(dayBox.getSelectedItem().toString()));
                System.out.println("Date to be removed: "+ date);

                try {
                    DayRemover.removeData(date);
                    dayListModel.removeElement(date);
                    updateDayEntries();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });   
}   
    private void updateCalendar() {
        System.out.println("Heloo Update Calendar");
        int year = (int) yearBox.getSelectedItem();
        int month = monthBox.getSelectedIndex() + 1;
        tableModel.setRowCount(0);
        dayBox.removeAllItems();

        LocalDate firstOfMonth = LocalDate.of(year, month, 1);
        int startDay = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday=0
        int daysInMonth = firstOfMonth.lengthOfMonth();

        int dayCounter = 1;

        for (int i = 0; i < 6; i++) {
            String[] week = new String[7];
            for (int j = 0; j < 7; j++) {
                if (i == 0 && j < startDay) {
                    week[j] = null;
                } else if (dayCounter <= daysInMonth) {
                    week[j] =  String.valueOf(dayCounter);
//                    for (String str : week){
//                        System.out.println(str);
//                    }
                    dayBox.addItem(String.valueOf(dayCounter));
                    dayCounter++;
                } else {
                    week[j] = null;

                }
            }
            tableModel.addRow(week);
        }
        dayBox.setSelectedItem(String.valueOf(LocalDate.now().getDayOfMonth()));
        System.out.println("End of update ------");
    }


    private List<Day> dayEntries;
    private DefaultTableModel tableModel;
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        ratingComboBox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        chosenDateLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        noteTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        calendarTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dayBox = new javax.swing.JComboBox<>();
        monthBox = new javax.swing.JComboBox<>();
        yearBox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        entryList = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rate the Day");

        mainPanel.setLayout(new java.awt.GridBagLayout());

        ratingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        ratingComboBox.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 11, 0, 0);
        mainPanel.add(ratingComboBox, gridBagConstraints);

        jLabel6.setText("List of entry");
        jLabel6.setAutoscrolls(true);
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        mainPanel.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Rating:");
        jLabel8.setAutoscrolls(true);
        jLabel8.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 5, 0);
        mainPanel.add(jLabel8, gridBagConstraints);

        chosenDateLabel.setText("...");
        chosenDateLabel.setAutoscrolls(true);
        chosenDateLabel.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        mainPanel.add(chosenDateLabel, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setAlignmentX(1.0F);
        jPanel1.setAlignmentY(1.0F);
        jPanel1.setAutoscrolls(true);
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        noteTextArea.setColumns(20);
        noteTextArea.setLineWrap(true);
        noteTextArea.setRows(5);
        noteTextArea.setWrapStyleWord(true);
        noteTextArea.setBorder(null);
        noteTextArea.setLineWrap(true);
        jScrollPane3.setViewportView(noteTextArea);

        jPanel1.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(jPanel1, gridBagConstraints);

        calendarTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
            }
        ));
        calendarTable.setColumnSelectionAllowed(true);
        calendarTable.setPreferredSize(new java.awt.Dimension(400, 150));
        calendarTable.setRowHeight(30);
        calendarTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(calendarTable);
        calendarTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 400.0;
        gridBagConstraints.weighty = 300.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        mainPanel.add(jScrollPane2, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridLayout(2, 3, 5, 2));

        jLabel7.setText("Day");
        jPanel4.add(jLabel7);

        jLabel4.setText("Month");
        jPanel4.add(jLabel4);

        jLabel5.setText("Year");
        jPanel4.add(jLabel5);

        jPanel4.add(dayBox);

        monthBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        jPanel4.add(monthBox);

        yearBox.setModel(new DefaultComboBoxModel<>(new Integer[] {}));
        jPanel4.add(yearBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 14, 0);
        mainPanel.add(jPanel4, gridBagConstraints);

        saveButton.setText("Save");
        saveButton.setPreferredSize(new java.awt.Dimension(90, 25));

        deleteButton.setText("Delete");
        deleteButton.setPreferredSize(new java.awt.Dimension(90, 25));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        mainPanel.add(jPanel3, gridBagConstraints);

        entryList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        entryList.setPreferredSize(new java.awt.Dimension(1, 90));
        entryList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                entryListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(entryList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 400.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 9);
        mainPanel.add(jScrollPane1, gridBagConstraints);

        jLabel9.setText("Chosen Date:");
        jLabel9.setAutoscrolls(true);
        jLabel9.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        mainPanel.add(jLabel9, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 750, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    private void entryListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_entryListValueChanged
        if (!evt.getValueIsAdjusting()) {
        String selectedDate = entryList.getSelectedValue();
        System.out.println("Selected: " + selectedDate);
        
        Day found = null;
        for (Day day : dayEntries){
            if (day.getDateISO().equals(selectedDate)){
                found = day;
            }
        }
        if (found != null){
            ratingComboBox.setSelectedItem(String.valueOf(found.getRating()));
            noteTextArea.setText(found.getNote());
  
            monthBox.setSelectedItem(String.valueOf(found.getMonth()-1));
            yearBox.setSelectedItem(found.getYear());

            System.out.println("Number of item in dayBox: "+ dayBox.getItemCount());
//            SwingUtilities.invokeLater(() -> {
                dayBox.setSelectedItem(String.valueOf(found.getDayOfMonth()));
//            });            
        }
    }

    }//GEN-LAST:event_entryListValueChanged

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String args[]) throws Exception {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
                /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
                 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
                 */
//                try {
//                    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                        System.out.println(info.getName());
//                        if ("Nimbus".equals(info.getName())) {
//                            javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                            System.out.println(info.getClassName());
//                        
//                        }
//                    }
//                } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//                    logger.log(java.util.logging.Level.SEVERE, null, ex);
//                }
                //</editor-fold>
                
        try {
        // Set FlatLaf Cupertino Light theme
        UIManager.setLookAndFeel(new FlatAtomOneLightIJTheme());
        
        // OR you can use the built-in FlatLightLaf (plain light theme)
        // UIManager.setLookAndFeel(new FlatLightLaf());
    } catch (Exception ex) {
        ex.printStackTrace();
    }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new AppUI().setVisible(true);
            } catch (Exception ex) {
                System.getLogger(AppUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
    

    public JPanel getPanel() {
        return mainPanel;
    }

    public String formatDateNumber(String n){
        return Integer.parseInt(n) <10 ? "0"+ n : n;
    }
    public void updateDayEntries() throws Exception {
        dayEntries = DayReader.readDayFile("dayData.json");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable calendarTable;
    private javax.swing.JLabel chosenDateLabel;
    private javax.swing.JComboBox<String> dayBox;
    private javax.swing.JButton deleteButton;
    private javax.swing.JList<String> entryList;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> monthBox;
    private javax.swing.JTextArea noteTextArea;
    private javax.swing.JComboBox<String> ratingComboBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<Integer> yearBox;
    // End of variables declaration//GEN-END:variables
}
