/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projektbazydanych;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author Tomek
 */
public class CuresPanel extends JPanel {

    private final JScrollPane scrollPane;
    private final Connection connection;
    private JTable jTable;
    ArrayList<Integer> aCodes = new ArrayList<>();
    ArrayList<String> aNames = new ArrayList<>();
    String orderBy = "l.kod";
    String[] ascDesc = {" ASC", " DESC"};
    int pointer = 0;

    private BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            return null;
        }
    }

    private void fillTable() {

        aCodes.clear();
        aNames.clear();

        Statement st;

        try {
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
            return;
        }

        String myQuery = "SELECT l.kod, l.nazwa "
                + "FROM baza.Lekarstwo l ORDER BY " + orderBy + ascDesc[pointer];

        try {
            ResultSet rs = st.executeQuery(myQuery);

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(null, "Nie znaleziono żadnego lekarstwa", "Informacja", 1);
                return;
            } else {
                while (rs.next()) {
                    aCodes.add(rs.getInt(1));
                    aNames.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Nie udało się pobrać lekarstw", "Informacja", 1);
            return;
        }

        if (!aCodes.isEmpty() && !aNames.isEmpty()) {
            Integer[] numbers = aCodes.toArray(new Integer[aCodes.size()]);
            String[] names = aNames.toArray(new String[aNames.size()]);

            String[] columnNames = {"Kod", "Nazwa"};
            Object[][] data = new Object[aCodes.size()][2];

            int i = 0;

            for (Integer numb : numbers) {
                data[i][0] = numb;
                i++;
            }

            i = 0;

            for (String name : names) {
                data[i][1] = name;
                i++;
            }

            jTable = new JTable(data, columnNames);
            Font tableFont = new Font(jTable.getFont().getName(), Font.PLAIN, 20);
            jTable.setFillsViewportHeight(true);
            jTable.setFont(tableFont);
            jTable.setRowHeight(40);
            jTable.setShowGrid(false);
            DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            jTable.setModel(tableModel);
            scrollPane.getViewport().add(jTable);

            jTable.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int col = jTable.columnAtPoint(e.getPoint());
                    if (col == 0) {
                        if (orderBy.equals("l.kod")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "l.kod";
                        }
                        fillTable();
                    } else if (col == 1) {
                        if (orderBy.equals("l.nazwa")) {
                            pointer = pointer == 0 ? 1 : 0;
                        } else {
                            orderBy = "l.nazwa";
                        }
                        fillTable();
                    }
                }
            });

        }
    }

    public CuresPanel(final CardLayout cLayout, final JPanel pContainer, final Connection con) {
        this.setLayout(null);

        connection = con;

        // Buttons
        JButton bBack = new JButton("Wróć");
        Font buttonFont = new Font(bBack.getFont().getName(), Font.PLAIN, 20);
        bBack.setBounds(20, 660, 150, 40);
        bBack.setFont(buttonFont);
        add(bBack);

        JButton bDel = new JButton("Usuń");
        bDel.setBounds(180, 660, 150, 40);
        bDel.setFont(buttonFont);
        add(bDel);

        JButton bLeaflet = new JButton("Drukuj ulotkę");
        bLeaflet.setBounds(340, 660, 240, 40);
        bLeaflet.setFont(buttonFont);
        add(bLeaflet);

        // Border
        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(0, 5, 0, 0);
        CompoundBorder border = new CompoundBorder(line, empty);

        // Scroll Pane
        scrollPane = new JScrollPane();
        scrollPane.setBounds(20, 20, 970, 600);
        scrollPane.setBorder(border);
        add(scrollPane);

        bBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                cLayout.show(pContainer, "pEntry");
            }

        });

        bDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jTable.getSelectedRow() >= 0) {
                    Statement st;

                    try {
                        st = connection.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "DELETE FROM baza.Lekarstwo WHERE baza.Lekarstwo.kod = " + aCodes.get(jTable.getSelectedRow());

                    try {
                        st.executeUpdate(myQuery);
                        fillTable();
                        JOptionPane.showMessageDialog(null, "Pomyślnie usunięto lekarstwo", "Informacja", 1);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się usunąć lekarstwa", "Informacja", 1);
                    }
                }
            }
        });

        bLeaflet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (jTable.getSelectedRow() >= 0) {
                    Statement st;

                    try {
                        st = connection.createStatement();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się utworzyć połączenia", "Informacja", 1);
                        return;
                    }

                    String myQuery = "SELECT l.ulotka FROM baza.Lekarstwo l WHERE l.kod = " + aCodes.get(jTable.getSelectedRow());

                    ResultSet rs;

                    String sImg;

                    try {
                        rs = st.executeQuery(myQuery);
                        rs.next();

                        sImg = rs.getString(1);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Nie udało się pobrać ulotki", "Informacja", 1);
                        return;
                    }
                    
                    if(sImg == null) return;
                    
                    BufferedImage bImg = base64StringToImg(sImg);
                    
                    if(bImg == null) return;

                    PDDocument document = new PDDocument();
                    PDPage blankPage = new PDPage();
                    document.addPage(blankPage);

                    try {
                        PDRectangle mediabox = blankPage.getMediaBox();

                        float width = mediabox.getWidth();
                        float height = bImg.getHeight() / (bImg.getWidth() / (width - 70)) - 70;

                        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, bImg);
                        try (PDPageContentStream contentStream = new PDPageContentStream(document, blankPage)) {
                            contentStream.drawImage(pdImageXObject, 10, mediabox.getHeight() - height - 10, width - 70, height);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }

                    PDDocumentInformation pdd = document.getDocumentInformation();
                    pdd.setAuthor("Przychodnia");
                    pdd.setTitle("Ulotka " + aNames.get(jTable.getSelectedRow()));
                    pdd.setCreator("Przychodnia");
                    pdd.setSubject("Ulotka");

                    Calendar currDate = Calendar.getInstance();
                    pdd.setCreationDate(currDate);
                    pdd.setModificationDate(currDate);

                    SimpleDateFormat fFullDate = new SimpleDateFormat("dd_MM_yyyy__HH_mm");
                    String sFullDate = fFullDate.format(currDate.getTime());

                    try {
                        document.save("C:/Pdfy/Ulotki/" + sFullDate + "_" + aNames.get(jTable.getSelectedRow()) + ".pdf");
                        document.close();
                        JOptionPane.showMessageDialog(null, "Pomyślnie wydrukowano ulotkę do pliku pdf", "Informacja", 1);
                        if (Desktop.isDesktopSupported()) {
                            try {
                                File myFile = new File("C:/Pdfy/Ulotki/" + sFullDate + "_" + aNames.get(jTable.getSelectedRow()) + ".pdf");
                                Desktop.getDesktop().open(myFile);
                            } catch (IOException ex) {
                                // no application registered for PDFs
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(AppointmentDetailsPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                fillTable();
            }
        });
    }
}
