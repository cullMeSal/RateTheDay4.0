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
import java.util.Arrays;
import java.util.Locale;



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

        DefaultListModel<String> dayListModel = new DefaultListModel<>();
        entryList.setModel(dayListModel);
        
        for (Day day : dayEntries) {
            dayListModel.addElement(day.getDateISO());
        }

        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter intFormatter = new NumberFormatter(format);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(1);
        intFormatter.setAllowsInvalid(false);

        // Set up date input fields
        dayField.setText(String.valueOf(today.getDayOfMonth()));
        monthField.setText(String.valueOf(today.getMonthValue()));
        yearField.setText(String.valueOf(today.getYear()));
        
               
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
                
                Day found = null;
                for (Day day : dayEntries){
                    if (day.getDateISO().equals(date)){
                        found = day;
                        break;
                    }
                }
                if (found != null){
                    ratingComboBox.setSelectedItem(String.valueOf(found.getRating()));
                    noteTextArea.setText(found.getNote());
                    dayField.setText(String.valueOf(found.getDayOfMonth()));
                    monthField.setText(String.valueOf(found.getMonth()));
                    yearField.setText(String.valueOf(found.getYear()));
                    
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
                
                Day chosenDay = new Day(date);
//                System.out.println(chosenDay);

            }
        });

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

        mainPanel = new javax.swing.JPanel();
        crudPanel = new javax.swing.JPanel();
        noteTextArea = new java.awt.TextArea();
        ratingComboBox = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dayField = new javax.swing.JTextField();
        monthField = new javax.swing.JTextField();
        yearField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        chosenDateLabel = new javax.swing.JLabel();
        secondPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        entryList = new javax.swing.JList<>();
        thirdPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        calendarTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        monthBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        yearBox = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        dayBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("uullala");

        mainPanel.setPreferredSize(new java.awt.Dimension(600, 300));

        crudPanel.setPreferredSize(new java.awt.Dimension(300, 310));

        ratingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        ratingComboBox.setBorder(null);

        jLabel1.setText("Day");
        jLabel1.setAutoscrolls(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel2.setText("Year");
        jLabel2.setAutoscrolls(true);
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 20));

        jLabel3.setText("Month");
        jLabel3.setAutoscrolls(true);
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 20));

        dayField.setPreferredSize(new java.awt.Dimension(90, 22));

        monthField.setPreferredSize(new java.awt.Dimension(90, 22));

        yearField.setPreferredSize(new java.awt.Dimension(90, 22));

        saveButton.setText("Save");
        saveButton.setPreferredSize(new java.awt.Dimension(90, 20));

        deleteButton.setText("Delete");
        deleteButton.setPreferredSize(new java.awt.Dimension(90, 20));

        jLabel6.setText("Chosen Date:");
        jLabel6.setAutoscrolls(true);
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 20));

        chosenDateLabel.setText("...");
        chosenDateLabel.setAutoscrolls(true);
        chosenDateLabel.setPreferredSize(new java.awt.Dimension(100, 20));

        javax.swing.GroupLayout crudPanelLayout = new javax.swing.GroupLayout(crudPanel);
        crudPanel.setLayout(crudPanelLayout);
        crudPanelLayout.setHorizontalGroup(
            crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(crudPanelLayout.createSequentialGroup()
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(crudPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(crudPanelLayout.createSequentialGroup()
                        .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(dayField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(crudPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(monthField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, crudPanelLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(chosenDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(yearField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ratingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(crudPanelLayout.createSequentialGroup()
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(crudPanelLayout.createSequentialGroup()
                .addComponent(noteTextArea, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addContainerGap())
        );
        crudPanelLayout.setVerticalGroup(
            crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(crudPanelLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dayField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ratingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chosenDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(crudPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout secondPanelLayout = new javax.swing.GroupLayout(secondPanel);
        secondPanel.setLayout(secondPanelLayout);
        secondPanelLayout.setHorizontalGroup(
            secondPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        secondPanelLayout.setVerticalGroup(
            secondPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(secondPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                .addContainerGap())
        );

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
        calendarTable.setRowHeight(30);
        calendarTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(calendarTable);
        calendarTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jLabel4.setText("Month");

        monthBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));

        jLabel5.setText("Year");

        yearBox.setModel(new DefaultComboBoxModel<>(new Integer[] {}));

        jLabel7.setText("Day");

        javax.swing.GroupLayout thirdPanelLayout = new javax.swing.GroupLayout(thirdPanel);
        thirdPanel.setLayout(thirdPanelLayout);
        thirdPanelLayout.setHorizontalGroup(
            thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thirdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(thirdPanelLayout.createSequentialGroup()
                        .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(monthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(yearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        thirdPanelLayout.setVerticalGroup(
            thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(thirdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(thirdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(yearBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(secondPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(thirdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crudPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(thirdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(secondPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
            .addComponent(crudPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
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
            dayField.setText(String.valueOf(found.getDayOfMonth()));
            monthField.setText(String.valueOf(found.getMonth()));
            yearField.setText(String.valueOf(found.getYear()));
            
            
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
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new AppUI().setVisible(true);
            } catch (Exception ex) {
                System.getLogger(AppUI.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
//        JFrame frame = new JFrame("Rate the Day prototype");
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setSize(800,300);
//        frame.setContentPane(new NewAppUI().getPanel());
//        frame.setVisible(true);
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
    private javax.swing.JPanel crudPanel;
    private javax.swing.JComboBox<String> dayBox;
    private javax.swing.JTextField dayField;
    private javax.swing.JButton deleteButton;
    private javax.swing.JList<String> entryList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> monthBox;
    private javax.swing.JTextField monthField;
    private java.awt.TextArea noteTextArea;
    private javax.swing.JComboBox<String> ratingComboBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel secondPanel;
    private javax.swing.JPanel thirdPanel;
    private javax.swing.JComboBox<Integer> yearBox;
    private javax.swing.JTextField yearField;
    // End of variables declaration//GEN-END:variables
}
