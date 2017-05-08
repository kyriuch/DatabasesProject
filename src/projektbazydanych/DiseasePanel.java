/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class DiseasePanel extends JPanel {

    public DiseasePanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(670, 410, 140, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(470, 410, 140, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        // Labels
        JLabel lNumber = new JLabel("Numer: ");
        Font labelFont = new Font(lNumber.getFont().getName(), Font.PLAIN, 20);
        lNumber.setBounds(20, 20, 140, 20);
        lNumber.setFont(labelFont);
        add(lNumber);

        JLabel lName = new JLabel("Nazwa: ");
        lName.setBounds(20, 70, 140, 20);
        lName.setFont(labelFont);
        add(lName);

        JLabel lDescription = new JLabel("Opis: ");
        lDescription.setBounds(20, 120, 140, 20);
        lDescription.setFont(labelFont);
        add(lDescription);

        JLabel lSymptoms = new JLabel("Objawy: ");
        lSymptoms.setBounds(20, 290, 140, 20);
        lSymptoms.setFont(labelFont);
        add(lSymptoms);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextField tNumber = new JTextField();
        Font textFont = new Font(tNumber.getFont().getName(), Font.PLAIN, 20);
        tNumber.setBounds(110, 15, 240, 40);
        tNumber.setBorder(border);
        tNumber.setFont(textFont);
        add(tNumber);

        final JTextField tName = new JTextField();
        tName.setBounds(110, 65, 240, 40);
        tName.setBorder(border);
        tName.setFont(textFont);
        add(tName);

        final JTextArea tDescription = new JTextArea();
        tDescription.setBounds(110, 115, 700, 140);
        tDescription.setBorder(border);
        tDescription.setLineWrap(true);
        tDescription.setWrapStyleWord(true);
        tDescription.setFont(textFont);
        add(tDescription);

        final JTextArea tSymptoms = new JTextArea();
        tSymptoms.setBounds(110, 285, 700, 100);
        tSymptoms.setBorder(border);
        tSymptoms.setLineWrap(true);
        tSymptoms.setWrapStyleWord(true);
        tSymptoms.setFont(textFont);
        add(tSymptoms);

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int number;
                String name, description, symptoms;

                try {
                    number = Integer.parseInt(tNumber.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy numer", "Błąd", 0);
                    return;
                }

                name = tName.getText().trim().replaceAll(" +", " ");
                description = tDescription.getText().trim().replaceAll(" +", " ");
                symptoms = tSymptoms.getText().trim().replaceAll(" +", " ");

                if (name.trim().isEmpty() || description.trim().isEmpty()
                        || symptoms.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij puste pole", "Błąd", 0);
                } else {
                    Statement st;
                    
                    try {
                        st = con.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }
                    
                    String myQuery = "INSERT INTO baza.Choroba(numer, nazwa, opis, objawy) "
                            + "VALUES(" + number + ", \'" + name + "\', \'" + description
                            + "\', \'" + symptoms + "\')";

                    try {
                        st.executeUpdate(myQuery);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się dodać choroby", "Informacja", 1);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Dodano chorobę", "Informacja", 1);
                    cLayout.show(pContainer, "pEntry");
                    tNumber.setText("");
                    tName.setText("");
                    tDescription.setText("");
                    tSymptoms.setText("");
                }
            }
        });

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                tNumber.setText("");
                tName.setText("");
                tDescription.setText("");
                tSymptoms.setText("");
            }
        });
    }
}
