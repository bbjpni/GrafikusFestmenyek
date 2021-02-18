package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class FormFestmeny extends JFrame {
    FestmenyAB ab;
    private JPanel mainPanel;

    private JLabel lblNev, lblSee, lblEv, lblFizetes;

    private JTextField textFieldSzerzo, textFieldCim;
    private JRadioButton radioSeen, radioUnseen;
    private JSpinner spinnerKor;

    private JButton btnHozzaad;

    private Festmeny festmeny;
    private boolean modosit;

    public FormFestmeny(FestmenyAB ab){
        this.ab = ab;

        modosit = false;

        this.setTitle("Festmény hozzáadása");
        this.setSize(300,260);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mainPanel = (JPanel)(this.getContentPane());

        lblNev = new JLabel("Festő:");
        lblNev.setBounds(20,20,100,20);
        mainPanel.add(lblNev);

        lblSee = new JLabel("Kiállítva:");
        lblSee.setBounds(20,140,100,20);
        mainPanel.add(lblSee);

        lblEv = new JLabel("Keletkezés (év):");
        lblEv.setBounds(20,100,100,20);
        mainPanel.add(lblEv);

        lblFizetes = new JLabel("Cím:");
        lblFizetes.setBounds(20,60,100,20);
        mainPanel.add(lblFizetes);

        textFieldSzerzo = new JTextField();
        textFieldSzerzo.setBounds(120, 20, 140,20);
        mainPanel.add(textFieldSzerzo);

        radioSeen = new JRadioButton("Igen");
        radioSeen.setBounds(120,140,50,20);
        radioSeen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radioSeen.setSelected(true);
                radioUnseen.setSelected(false);
            }
        });
        mainPanel.add(radioSeen);

        radioUnseen = new JRadioButton("Nem");
        radioUnseen.setBounds(180,140,100,20);
        radioUnseen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radioSeen.setSelected(false);
                radioUnseen.setSelected(true);
            }
        });
        mainPanel.add(radioUnseen);
        int max = LocalDate.now().getYear();
        SpinnerModel korSpinnerModel = new SpinnerNumberModel(max,0,max,1);
        spinnerKor = new JSpinner(korSpinnerModel);
        spinnerKor.setBounds(120,100,140,20);
        mainPanel.add(spinnerKor);

        textFieldCim = new JTextField();
        textFieldCim.setBounds(120,60,140,20);
        mainPanel.add(textFieldCim);

        btnHozzaad = new JButton("Hozzáad");
        btnHozzaad.setBounds(160,180,100,20);
        btnHozzaad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cim = textFieldCim.getText().trim();
                String festo = textFieldSzerzo.getText().trim();
                if (festo.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Festő megadása kötelező");
                    return;
                }
                if (cim.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Cím megadása kötelező");
                    return;
                }
                if (!radioSeen.isSelected() && !radioUnseen.isSelected()){
                    JOptionPane.showMessageDialog(null, "státusz megadása kötelező");
                    return;
                }

                boolean display = radioSeen.isSelected();
                int ev = (int)spinnerKor.getValue();

                if (modosit){
                    try {
                        int id = festmeny.getId();
                        int sikeres = ab.updateFestmeny(id,festo,display,ev,cim);
                        String uzenet = String.format("%s módosítás", sikeres > 0 ? "Sikeres" : "Sikertelen");
                        JOptionPane.showMessageDialog(null,uzenet);
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null,
                                "Adatbázis hiba\n"+throwables.getMessage());
                    }
                    dispose();
                } else{
                    try {
                        int sikeres = ab.insertFestmeny(festo,display,ev,cim);
                        String uzenet = String.format("%s hozzáadás", sikeres > 0 ? "Sikeres" : "Sikertelen");
                        JOptionPane.showMessageDialog(null,uzenet);
                        if (sikeres > 0)
                        {
                            textFieldSzerzo.setText("");
                            textFieldCim.setText("");
                            radioUnseen.setSelected(false);
                            radioSeen.setSelected(false);
                            spinnerKor.setValue(LocalDate.now().getYear());
                        }
                    } catch (SQLException throwables) {
                        JOptionPane.showMessageDialog(null,
                                "Adatbázis hiba\n"+throwables.getMessage());
                    }
                }
            }
        });
        mainPanel.add(btnHozzaad);

        this.setVisible(true);
    }

    public FormFestmeny(FestmenyAB ab, Festmeny f){
        this(ab);
        this.festmeny = f;

        this.setTitle(festmeny.getCim()+" adatainak módosítása");
        this.btnHozzaad.setText("Módosít");
        this.modosit = true;
        this.textFieldCim.setText(festmeny.getCim());
        if ((festmeny.isDisplay() ? "Kiállítva" : "Nem megtekínthető").equals("Kiállítva")){
            radioSeen.setSelected(true);
            radioUnseen.setSelected(false);
        }else{
            radioSeen.setSelected(false);
            radioUnseen.setSelected(true);
        }
        spinnerKor.setValue(festmeny.getEv());
        textFieldSzerzo.setText(festmeny.getSzerzo());
    }
}
