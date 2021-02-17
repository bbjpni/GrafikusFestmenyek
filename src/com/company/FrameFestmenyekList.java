package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class FrameFestmenyekList extends JFrame {
    private FestmenyAB ab;

    private JList<Festmeny> festmenyList;
    private DefaultListModel<Festmeny> festmenyListModel;
    private JScrollPane festmenyScrollPane;

    private JPanel mainPanel;
    private JButton btnHozzaad, btnTorol, btnModosit;

    public FrameFestmenyekList(){
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        festmenyListModel = new DefaultListModel<>();

        try {
            ab = new FestmenyAB();
            loadFestmenyek();
        } catch (SQLException throwables) {
            JOptionPane.showMessageDialog(null, "Hiba az adatbázishoz való kapcsolódáskor.");
            dispose();
            return;
        }

        this.setTitle("Festmények");
        this.setSize(800,400);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        mainPanel = (JPanel)(this.getContentPane());

        festmenyList = new JList<>(festmenyListModel);
        festmenyList.setFont(new Font("Courier New",Font.PLAIN,14));
        festmenyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        festmenyScrollPane = new JScrollPane(festmenyList);
        festmenyScrollPane.setBounds(20,20,740,300);
        mainPanel.add(festmenyScrollPane);

        btnHozzaad = new JButton("Hozzáad");
        btnHozzaad.setBounds(20,330,100,20);
        btnHozzaad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormFestmeny insertForm = new FormFestmeny(ab);
                setVisible(false);
                insertForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                        try {
                            loadFestmenyek();
                        } catch (SQLException throwables) {
                            JOptionPane.showMessageDialog(null,
                                    "Hiba a lista frissítésekor"+throwables.getMessage());
                        }
                        super.windowClosed(e);
                    }
                });
            }
        });
        mainPanel.add(btnHozzaad);

        btnTorol = new JButton("Töröl");
        btnTorol.setBounds(140,330,100,20);
        btnTorol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (festmenyList.getSelectedIndex() < 0){
                    JOptionPane.showMessageDialog(null, "Törlés előtt válasszon ki egy festményt");
                    return;
                }
                Festmeny torlendo = festmenyList.getSelectedValue();
                int biztos = JOptionPane.showConfirmDialog(null,
                        "Biztos kívánja törölni az alábbi festményt: "+torlendo.getCim());
                if (biztos == JOptionPane.YES_OPTION){
                    try {
                        int sikeres = ab.deleteFestmeny(torlendo.getId());
                        loadFestmenyek();

                        String uzenet = String.format("%s törlés", sikeres > 0 ? "Sikeres" : "Sikertelen");
                        JOptionPane.showMessageDialog(null,
                                uzenet);
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null,
                                "Sikertelen törlés\n"+throwables.getMessage());
                    }
                }
            }
        });
        mainPanel.add(btnTorol);

        btnModosit = new JButton("Módosít");
        btnModosit.setBounds(260,330,100,20);
        btnModosit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (festmenyList.getSelectedIndex() < 0){
                    JOptionPane.showMessageDialog(null, "Módosítás előtt válasszon ki egy festményt");
                    return;
                }
                Festmeny modositando = festmenyList.getSelectedValue();

                FormFestmeny modositForm = new FormFestmeny(ab,modositando);
                setVisible(false);
                modositForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                        try {
                            loadFestmenyek();
                        } catch (SQLException throwables) {
                            JOptionPane.showMessageDialog(null,
                                    "Hiba a lista frissítésekor"+throwables.getMessage());
                        }
                        super.windowClosed(e);
                    }
                });
            }
        });
        mainPanel.add(btnModosit);



        this.setVisible(true);
    }

    private void loadFestmenyek() throws SQLException {
        ArrayList<Festmeny> festmenyArrayList = ab.getFestmenyek();
        festmenyListModel.clear();
        for (Festmeny f : festmenyArrayList) {
            festmenyListModel.addElement(f);
        }
    }
}
