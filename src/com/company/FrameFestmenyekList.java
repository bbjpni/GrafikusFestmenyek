package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrameFestmenyekList extends JFrame {
    private FestmenyAB ab;

    private JList<Festmeny> festmenyList;
    private DefaultListModel<Festmeny> festmenyListModel;
    private JScrollPane festmenyScrollPane;

    private JPanel mainPanel;
    private JButton btnHozzaad, btnTorol, btnModosit, btnProg1, btnProg2, btnVisszatoltes, btnStatisztika;

    public FrameFestmenyekList() {
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
        this.setSize(800, 420);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        mainPanel = (JPanel) (this.getContentPane());

        festmenyList = new JList<>(festmenyListModel);
        festmenyList.setFont(new Font("Courier New", Font.PLAIN, 14));
        festmenyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        festmenyScrollPane = new JScrollPane(festmenyList);
        festmenyScrollPane.setBounds(20, 40, 750, 300);
        mainPanel.add(festmenyScrollPane);

        btnStatisztika = new JButton("Csoportosítás festők szerint");
        btnStatisztika.setBounds(20, 10, 750, 20);
        btnStatisztika.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    CsoportFestokSzerint();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,
                            "Hiba a művelet végrehajtásakor" + throwables.getMessage());
                }
            }
        });
        mainPanel.add(btnStatisztika);

        btnHozzaad = new JButton("Hozzáad");
        btnHozzaad.setBounds(20, 350, 100, 20);
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
                                    "Hiba a lista frissítésekor" + throwables.getMessage());
                        }
                        super.windowClosed(e);
                    }
                });
            }
        });
        mainPanel.add(btnHozzaad);

        btnTorol = new JButton("Töröl");
        btnTorol.setBounds(130, 350, 100, 20);
        btnTorol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (festmenyList.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(null, "Törlés előtt válasszon ki egy festményt");
                    return;
                }
                Festmeny torlendo = festmenyList.getSelectedValue();
                int biztos = JOptionPane.showConfirmDialog(null,
                        "Biztos kívánja törölni az alábbi festményt: " + torlendo.getCim());
                if (biztos == JOptionPane.YES_OPTION) {
                    try {
                        int sikeres = ab.deleteFestmeny(torlendo.getId());
                        loadFestmenyek();

                        String uzenet = String.format("%s törlés", sikeres > 0 ? "Sikeres" : "Sikertelen");
                        JOptionPane.showMessageDialog(null,
                                uzenet);
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null,
                                "Sikertelen törlés\n" + throwables.getMessage());
                    }
                }
            }
        });
        mainPanel.add(btnTorol);

        btnModosit = new JButton("Módosít");
        btnModosit.setBounds(240, 350, 100, 20);
        btnModosit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (festmenyList.getSelectedIndex() < 0) {
                    JOptionPane.showMessageDialog(null, "Módosítás előtt válasszon ki egy festményt");
                    return;
                }
                Festmeny modositando = festmenyList.getSelectedValue();

                FormFestmeny modositForm = new FormFestmeny(ab, modositando);
                setVisible(false);
                modositForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                        try {
                            loadFestmenyek();
                        } catch (SQLException throwables) {
                            JOptionPane.showMessageDialog(null,
                                    "Hiba a lista frissítésekor" + throwables.getMessage());
                        }
                        super.windowClosed(e);
                    }
                });
            }
        });
        mainPanel.add(btnModosit);

        btnProg1 = new JButton("Legrégebbi");
        btnProg1.setBounds(350, 350, 100, 20);
        btnProg1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    legregebbiFestmeny();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,
                            "Hiba a művelet végrhajtásakor" + throwables.getMessage());
                }
            }
        });
        mainPanel.add(btnProg1);

        btnProg2 = new JButton("Elmúlt 100 év festményei");
        btnProg2.setBounds(460, 350, 200, 20);
        btnProg2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    festményekAzElmult100Evben();
                    int biztos = JOptionPane.showConfirmDialog(null, "Újratölti a listát?");
                    if (biztos == JOptionPane.YES_OPTION) {
                        loadFestmenyek();
                    }
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,
                            "Hiba a művelet végrhajtásakor" + throwables.getMessage());
                }
            }
        });
        mainPanel.add(btnProg2);

        btnVisszatoltes = new JButton("Feltölt");
        btnVisszatoltes.setBounds(670, 350, 100, 20);
        btnVisszatoltes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadFestmenyek();
                } catch (SQLException throwables) {
                    JOptionPane.showMessageDialog(null,
                            "Hiba a művelet végrhajtásakor" + throwables.getMessage());
                }
            }
        });
        mainPanel.add(btnVisszatoltes);

        this.setVisible(true);
    }

    private void loadFestmenyek() throws SQLException {
        ArrayList<Festmeny> festmenyArrayList = ab.getFestmenyek();
        festmenyListModel.clear();
        for (Festmeny f : festmenyArrayList) {
            festmenyListModel.addElement(f);
        }
    }

    private void legregebbiFestmeny() throws SQLException {
        int index = 0;
        ArrayList<Festmeny> festmenyArrayList = ab.getFestmenyek();
        int min = festmenyArrayList.get(index).getEv();
        for (int i = 1; i < festmenyArrayList.size(); i++) {
            if (festmenyArrayList.get(index).getEv() > festmenyArrayList.get(i).getEv()) {
                index = i;
                min = festmenyArrayList.get(index).getEv();
            }
        }
        JOptionPane.showMessageDialog(null,
                festmenyArrayList.get(index).getSzerzo() + " - " + festmenyArrayList.get(index).getCim() + " (" + min + ")");
    }

    private void festményekAzElmult100Evben() throws SQLException {
        int step = 0;
        int now = LocalDate.now().getYear();
        ArrayList<Festmeny> festmenyArrayList = ab.getFestmenyek();
        festmenyListModel.clear();
        for (Festmeny f : festmenyArrayList) {
            if (now - f.getEv() <= 100) {
                festmenyListModel.addElement(f);
                step++;
            }
        }
        JOptionPane.showMessageDialog(null,
                step + " darab találat!");
    }

    private void CsoportFestokSzerint() throws SQLException {
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        HashMap<String, Boolean> see = new HashMap<String, Boolean>();
        int now = LocalDate.now().getYear();
        ArrayList<Festmeny> festmenyArrayList = ab.getFestmenyek();
        festmenyListModel.clear();
        for (Festmeny f : festmenyArrayList) {
            if (!hm.containsKey(f.getSzerzo())) {
                hm.put(f.getSzerzo(), 1);
                see.put(f.getSzerzo(), f.isDisplay());
            } else {
                hm.put(f.getSzerzo(), hm.get(f.getSzerzo()) + 1);
                boolean temp = see.get(f.getSzerzo()) && f.isDisplay();
                see.put(f.getSzerzo(), temp);
            }
        }
        ArrayList<String> darab = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            darab.add(entry.getKey()+";"+entry.getValue());
        }
        ArrayList<String> kiall = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : see.entrySet()) {
            kiall.add(entry.getKey()+";"+entry.getValue());
        }
        ArrayList<String> sorok = new ArrayList<>();
        for (int i = 0; i < darab.size(); i++) {
            sorok.add(darab.get(i)+";"+kiall.get(i).split(";")[1]);
            String[] tomb = sorok.get(i).split(";");
            Festmeny f = new Festmeny(0,tomb[0],tomb[2].equals("true"),0, tomb[1]+" darab");
            festmenyListModel.addElement(f);
        }
    }
}