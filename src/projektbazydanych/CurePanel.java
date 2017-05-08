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
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class CurePanel extends JPanel {

    File file;
    String base64;
    RenderedImage img;

    private String imgToBase64String(final RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
            return os.toString(StandardCharsets.ISO_8859_1.name());
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public CurePanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        // Buttons
        JButton bAdd = new JButton("Dodaj");
        Font buttonFont = new Font(bAdd.getFont().getName(), Font.PLAIN, 20);
        bAdd.setBounds(220, 165, 140, 40);
        bAdd.setFont(buttonFont);
        add(bAdd);

        JButton bBack = new JButton("Wróć");
        bBack.setBounds(70, 165, 140, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        // Labels
        JLabel lCode = new JLabel("Kod: ");
        Font labelFont = new Font(lCode.getFont().getName(), Font.PLAIN, 20);
        lCode.setBounds(20, 20, 140, 20);
        lCode.setFont(labelFont);
        add(lCode);

        JLabel lName = new JLabel("Nazwa: ");
        lName.setBounds(20, 70, 140, 20);
        lName.setFont(labelFont);
        add(lName);

        JLabel lLeaflet = new JLabel("Ulotka: ");
        lLeaflet.setBounds(20, 120, 140, 20);
        lLeaflet.setFont(labelFont);
        add(lLeaflet);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Text Fields
        final JTextField tCode = new JTextField();
        Font textFont = new Font(tCode.getFont().getName(), Font.PLAIN, 20);
        tCode.setBounds(120, 15, 240, 40);
        tCode.setBorder(border);
        tCode.setFont(textFont);
        add(tCode);

        final JTextField tName = new JTextField();
        tName.setBounds(120, 65, 240, 40);
        tName.setBorder(border);
        tName.setFont(textFont);
        add(tName);

        final JButton bImg = new JButton("Wybierz");
        bImg.setBounds(120, 115, 240, 40);
        bImg.setFont(buttonFont);
        add(bImg);

        bImg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Wybierz ulotkę");
                int returnVal = fc.showOpenDialog(CurePanel.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    img = null;

                    if (file.isFile()) {
                        try {
                            img = ImageIO.read(file);
                        } catch (IOException ex) {
                            Logger.getLogger(CurePanel.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                    }

                    base64 = imgToBase64String(img, getFileExtension(file));
                }
            }

        });

        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int code;
                String name;

                try {
                    code = Integer.parseInt(tCode.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowy kod lekarstwa", "Błąd", 0);
                    return;
                }

                name = tName.getText().trim().replaceAll(" +", " ");

                if (name.trim().isEmpty() && img != null) {
                    JOptionPane.showMessageDialog(null, "Uzupełnij puste pole", "Błąd", 0);
                } else {
                    PreparedStatement st;

                    try {
                        String myQuery = "INSERT INTO baza.Lekarstwo(kod, nazwa, ulotka) "
                                + "VALUES(?,?,?)";

                        st = con.prepareStatement(myQuery);
                        st.setInt(1, code);
                        st.setString(2, name);
                        st.setString(3, base64);
                        st.executeUpdate();
                    } catch (SQLException ex) {
                        Logger.getLogger(CurePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(null, "Dodano lekarstwo", "Informacja", 1);
                    cLayout.show(pContainer, "pEntry");
                    tCode.setText("");
                    tName.setText("");
                }
            }

        });

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
                tCode.setText("");
                tName.setText("");
            }
        });
    }
}
