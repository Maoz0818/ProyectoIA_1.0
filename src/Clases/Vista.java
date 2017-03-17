package Clases;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import net.miginfocom.swing.MigLayout;

public class Vista implements ActionListener, MouseListener{
        
    public static final int LIBRE = 0;
    public static final int OBSTACULO = 1;
    public static final int ROBOT = 2;
    public static final int ROBOT_ENEMIGO = 3;
    public static final int OBJETIVO = 4;
    
    JFrame jfVentana;
    JPanel panelCentro, panelOpciones, panelSolucion;
    ImagePanel panelMapa, panelInicio;
    JComboBox cbxBusqueda, cbxAlgoritmo;
    JButton btnBuscar, btnEjecutar, btnSimulacion, btnResetMapa;
    Mapa mp;
    JTextField txtNameFile;
    JLabel lblNodosExpandidos, lblProfundidad, lblTiempo, lblCosto, lblNodosCreados, lblBalas;
    long time;
    Nodo nodo;
    HashMap<String, JLabel> hmLabels;
        
    public void init() {
        jfVentana = new JFrame("Metodos de Busqueda");
        jfVentana.setLayout(new MigLayout());
        
        JLabel lblTitle = new JLabel("<html><span style='font-size:3em'>Proyecto IA</span></html>");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setVerticalAlignment(JLabel.CENTER);
        
        JLabel lblIco = new JLabel();
        ImageIcon imgIcoUV = new ImageIcon(this.getClass().getResource("/images/logounivalle.png"));
        Image image = imgIcoUV.getImage();
        Image newimg = image.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
        imgIcoUV = new ImageIcon(newimg);
        lblIco.setBackground(Color.WHITE);
        lblIco.setIcon(imgIcoUV);
        
        JLabel lblIco2 = new JLabel();
        ImageIcon imgFondo = new ImageIcon(this.getClass().getResource("/images/fondoMapa.png"));
        Image imageFondo = imgFondo.getImage();
        Image newimg2 = imageFondo.getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH);
        imgFondo = new ImageIcon(newimg2);
        lblIco2.setBackground(Color.WHITE);
        lblIco2.setIcon(imgFondo);
        
        jfVentana.add(lblIco);
        jfVentana.add(lblTitle, "wrap 5, growy, width max(100%)");

        /*Panel Opciones*/
        panelOpciones = new JPanel(new MigLayout());
        panelOpciones.setBorder(BorderFactory.createTitledBorder("Opciones"));
        
        JLabel lbl = new JLabel("<html><span style='font-size:1em'>Cargar mapa:</span></html>");
        txtNameFile = new JTextField();
        btnBuscar = new JButton("...");
        btnBuscar.addActionListener(this);
        
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(txtNameFile, "width max(40%, 40%), growx, growy");
        panelOpciones.add(btnBuscar, "wrap 10");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Tipo de búsqueda:</span></html>");
        String[] strBusqueda = {"Seleccione una opción", "Búsqueda no informada", "Búsqueda informada"};
        cbxBusqueda = new JComboBox(strBusqueda);
        cbxBusqueda.addActionListener(this);
        
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(cbxBusqueda, "width max(100%, 100%), growx, growy, wrap 10");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Algoritmo:</span></html>");
        cbxAlgoritmo = new JComboBox(strBusqueda);
        cbxAlgoritmo.addActionListener(this);
        cbxAlgoritmo.setEnabled(false);
        
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(cbxAlgoritmo, "width max(100%, 100%), growx, growy, wrap 10");
        
        btnEjecutar = new JButton("Ejecutar");
        btnEjecutar.addActionListener(this);
        
        panelOpciones.add(btnEjecutar, "span, right, wrap 50");
        /*Panel Opciones*/

        /*Panel Mapa*/
        ImageIcon background = new ImageIcon(this.getClass().getResource("/images/fondoMapa.png"));
        image = background.getImage();
        newimg = image.getScaledInstance(600, 600, java.awt.Image.SCALE_SMOOTH);
        background = new ImageIcon(newimg);
        panelMapa = new ImagePanel(background.getImage());
        panelMapa.setLayout(new GridLayout());
        panelMapa.setPreferredSize(new Dimension(600, 600));
        panelMapa.setVisible(true);
        /*Panel Mapa*/

        /*Panel Solucion*/
        panelSolucion = new JPanel(new MigLayout());
        panelSolucion.setBorder(BorderFactory.createTitledBorder("Reporte"));
        panelSolucion.setVisible(true);
        
        lbl = new JLabel("<html><span style='font-size:1em'>Total nodos creados: </span></html>");
        lblNodosCreados = new JLabel("<html><span style='font-size:1em'>0</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblNodosCreados, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Total nodos expandidos: </span></html>");
        lblNodosExpandidos = new JLabel("<html><span style='font-size:1em'>0</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblNodosExpandidos, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Profundidad: </span></html>");
        lblProfundidad = new JLabel("<html><span style='font-size:1em'>0</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblProfundidad, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Costo: </span></html>");
        lblCosto = new JLabel("<html><span style='font-size:1em'>0</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblCosto, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Tiempo: </span></html>");
        lblTiempo = new JLabel("<html><span style='font-size:1em'>0 ms</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblTiempo, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Balas restantes: </span></html>");
        lblBalas = new JLabel("<html><span style='font-size:1em'>0</span></html>");
        
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblBalas, "growx, growy, wrap 15");
        
        lbl = new JLabel("<html><span style='font-size:1em'>Simulación: </span></html>");
        btnSimulacion = new JButton("Automática");
        btnSimulacion.setHorizontalAlignment(SwingConstants.LEFT);
        btnSimulacion.addActionListener(this);
        panelSolucion.add(lbl, "growx, growy, wrap");
        panelSolucion.add(btnSimulacion, "wrap 10");
        /*Panel Solucion*/
        
        /*Panel EstadoInicial*/
        ImageIcon background2 = new ImageIcon(this.getClass().getResource("/images/fondoMapa.png"));
        imageFondo = background2.getImage();
        newimg2 = imageFondo.getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH);
        background2 = new ImageIcon(newimg2);
        panelInicio = new ImagePanel(background2.getImage());
        panelInicio.setLayout(new GridLayout());
        panelInicio.setPreferredSize(new Dimension(250, 250));
        panelInicio.setVisible(true);
        /*Panel EstadoInicial*/
              
        panelCentro = new JPanel(new MigLayout());
        panelCentro.add(panelOpciones, "width max(25%, 25%), growx, growy");
        panelCentro.add(panelMapa, "span 2 2, growx, growy");
        panelCentro.add(panelSolucion, "span 2 2, growx, growy, width max(25%, 25%)");
        panelCentro.add(panelInicio, "growx, growy, width max(25%, 25%), cell 0 1");
        
        jfVentana.add(panelCentro, "span, width max(100%, 100%) ");
        jfVentana.setVisible(true);
        jfVentana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfVentana.setSize(new Dimension(1100, 650));
        jfVentana.setPreferredSize(new Dimension(1100, 650));
        jfVentana.setResizable(false);
        
    }
    
    public void construirMapa() {
        panelMapa.removeAll();
        panelMapa.updateUI();
        panelMapa.setLayout(new GridLayout(mp.filas, mp.columnas));
        JLabel lbl;
        ImageIcon imgIcoUV = new ImageIcon();
        hmLabels = new HashMap<>();
        for (int i = 0; i < mp.getMapa().length; i++) {
            for (int j = 0; j < mp.getMapa()[i].length; j++) {
                lbl = new JLabel();
                lbl.setToolTipText(i + "," + j);
                switch (mp.mapa[i][j]) {
                    case Mapa.OBSTACULO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/muro.png"));
                        break;
                    case Mapa.ROBOT:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
                        break;
                    case Mapa.ROBOT_ENEMIGO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot_enemigo.png"));
                        break;
                    case Mapa.OBJETIVO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/item.png"));
                        break;
                    case 0:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/libre.png"));
                        break;
                }
                Image image = imgIcoUV.getImage();
                Image newimg = image.getScaledInstance(45, 45, java.awt.Image.SCALE_SMOOTH);
                imgIcoUV = new ImageIcon(newimg);
                lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                lbl.setIcon(imgIcoUV);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                hmLabels.put(i + "," + j, lbl);
                panelMapa.add(lbl);
            }
        }
        panelMapa.updateUI();
    }
    
    public void construirMapaInicial() {
        panelInicio.removeAll();
        panelInicio.updateUI();
        panelInicio.setLayout(new GridLayout(mp.filas, mp.columnas));
        JLabel lbl;
        ImageIcon imgIcoUV = new ImageIcon();
        hmLabels = new HashMap<>();
        for (int i = 0; i < mp.getMapa().length; i++) {
            for (int j = 0; j < mp.getMapa()[i].length; j++) {
                lbl = new JLabel();
                lbl.setToolTipText(i + "," + j);
                switch (mp.mapa[i][j]) {
                    case Mapa.OBSTACULO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/muro.png"));
                        break;
                    case Mapa.ROBOT:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
                        break;
                    case Mapa.ROBOT_ENEMIGO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot_enemigo.png"));
                        break;
                    case Mapa.OBJETIVO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/item.png"));
                        break;
                    case 0:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/libre.png"));
                        break;
                }
                Image image = imgIcoUV.getImage();
                Image newimg = image.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
                imgIcoUV = new ImageIcon(newimg);
                lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                lbl.setIcon(imgIcoUV);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                hmLabels.put(i + "," + j, lbl);
                panelInicio.add(lbl);
            }
        }
        panelInicio.updateUI();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cbxBusqueda) {
            switch (cbxBusqueda.getSelectedIndex()) {
                case 1:
                    cbxAlgoritmo.setEnabled(true);
                    cbxAlgoritmo.removeAllItems();
                    cbxAlgoritmo.addItem(new Item(1, "Preferente por amplitud"));
                    cbxAlgoritmo.addItem(new Item(2, "De Costo uniforme"));
                    cbxAlgoritmo.addItem(new Item(3, "Profundidad e.c"));
                    break;
                case 2:
                    cbxAlgoritmo.setEnabled(true);
                    cbxAlgoritmo.removeAllItems();
                    cbxAlgoritmo.addItem(new Item(4, "Avara"));
                    cbxAlgoritmo.addItem(new Item(5, "A*"));
                    break;
                default:
                    String[] strOpciones3 = {"Seleccione"};
                    cbxAlgoritmo.setEnabled(false);
                    cbxAlgoritmo.setModel(new DefaultComboBoxModel(strOpciones3));
                    break;
            }
        } else if (e.getSource() == btnBuscar) {
            JFileChooser selectorArchivos = new JFileChooser();
            selectorArchivos.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            int seleccion = selectorArchivos.showOpenDialog(jfVentana);
            
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File fichero = selectorArchivos.getSelectedFile();
                txtNameFile.setText(fichero.getAbsolutePath());
                mp = new Mapa(fichero);
                construirMapaInicial();
            }
        } else if (e.getSource() == btnEjecutar) {
            if (mp == null) {
                JOptionPane.showMessageDialog(jfVentana, "No se ha cargado un mapa");
            } else {
                    construirMapaSolucion();
                    //reporte();
            }
        } else if (e.getSource() == btnSimulacion) {
            //construirMapa();
            //simulacion();
        }
    }
    
    public void reporte() {
        AlgoritmosDeBusqueda objAlgoritmos = new AlgoritmosDeBusqueda(mp);
        int intAlgoritmo = ((Item) cbxAlgoritmo.getSelectedItem()).getId();
        long time_start = System.currentTimeMillis();
        nodo = objAlgoritmos.init(intAlgoritmo);
        long time_end = System.currentTimeMillis();
        time = time_end - time_start;
        lblNodosCreados.setText("" + nodo.getNodosCreados());
        lblNodosExpandidos.setText("" + nodo.getNodosExpandidos());
        lblProfundidad.setText("" + nodo.getProfundidad());
        lblCosto.setText("" + nodo.getCosto());
        lblTiempo.setText("" + time + "ms");
        lblBalas.setText("" + nodo.getBalas());
    }
    
    public void simulacion() {
        
        ArrayList<Nodo> strCamino = nodo.getCamino();
                
        int delay = 300;
        new Timer(delay, new ActionListener() {
            private int i = 0;
            int[] strAnt = mp.getEstadoInicial();
            boolean disparo = false;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (i < strCamino.size()) {
                    if (mp.valuePosition(strCamino.get(i).getEstado()) == Mapa.ROBOT_ENEMIGO) {
                        disparo = true;
                    }
                    JLabel lblAnteriror = hmLabels.get(strAnt[0]+","+strAnt[1]);
                    ImageIcon imgIcoUV;
                    Image image;
                    Image newimg;
                    switch (mp.valuePosition(strAnt)) {
                        case Mapa.ROBOT_ENEMIGO:
                            imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot_enemigo.png"));
                            image = imgIcoUV.getImage();
                            newimg = image.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
                            imgIcoUV = new ImageIcon(newimg);
                            lblAnteriror.setIcon(imgIcoUV);
                            break;
//                        case Mapa.ELECTROMAGNETICO_2:
//                            imgIcoUV = new ImageIcon(this.getClass().getResource("/img/toxic.png"));
//                            image = imgIcoUV.getImage();
//                            newimg = image.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
//                            imgIcoUV = new ImageIcon(newimg);
//                            lblAnteriror.setIcon(imgIcoUV);
//                            break;
                        default:
                            lblAnteriror.setIcon(null);
                            break;
                        
                    }
                    
                    JLabel lbl = hmLabels.get(strCamino.get(i).getEstado()[0]+","+strCamino.get(i).getEstado()[1]);
                    
                    if (disparo) {
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
                    } else {
                        switch (mp.valuePosition(strCamino.get(i).getEstado())) {
                            case Mapa.ROBOT_ENEMIGO:
                                imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
                                break;
//                            case Mapa.ELECTROMAGNETICO_2:
//                                imgIcoUV = new ImageIcon(this.getClass().getResource("/img/explotox.png"));
//                                break;
                            default:
                                imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
                                break;
                        }
                        
                    }
                    image = imgIcoUV.getImage();
                    newimg = image.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
                    imgIcoUV = new ImageIcon(newimg);
                    lbl.setIcon(imgIcoUV);
                    
                    strAnt = strCamino.get(i).getEstado();
                    
                } else {
                    ((Timer) e.getSource()).stop();
                }
                i++;
            }
        }).start();
    }
    
    public void construirMapaSolucion() {
        AlgoritmosDeBusqueda objAlgoritmos = new AlgoritmosDeBusqueda(mp);
        int intAlgoritmo = ((Item) cbxAlgoritmo.getSelectedItem()).getId();
        Nodo nodo2 = objAlgoritmos.init(intAlgoritmo);
        ArrayList<Nodo> strCamino = nodo2.getCamino();
        Arrays.asList(strCamino).forEach(n -> System.out.print(n + " "));

//        panelMapa.removeAll();
//        panelMapa.updateUI();
//        panelMapa.setLayout(new GridLayout(mp.filas, mp.columnas));
//        JLabel lbl;
//        ImageIcon imgIcoUV = new ImageIcon();
//        hmLabels = new HashMap<>();
//        for (int i = 0; i < mp.getMapa().length; i++) {
//            for (int j = 0; j < mp.getMapa()[i].length; j++) {
//                lbl = new JLabel();
//                lbl.setToolTipText(i + "," + j);
//                switch (mp.mapa[i][j]) {
//                    case Mapa.OBSTACULO:
//                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/muro.png"));
//                        break;
//                    case Mapa.ROBOT:
//                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot.png"));
//                        break;
//                    case Mapa.ROBOT_ENEMIGO:
//                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/robot_enemigo.png"));
//                        break;
//                    case Mapa.OBJETIVO:
//                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/item.png"));
//                        break;
//                    case 0:
//                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/libre.png"));
//                        break;
//                }
//                Image image = imgIcoUV.getImage();
//                Image newimg = image.getScaledInstance(45, 45, java.awt.Image.SCALE_SMOOTH);
//                imgIcoUV = new ImageIcon(newimg);
//                lbl.setBorder(BorderFactory.createLineBorder(Color.WHITE));
//                lbl.setIcon(imgIcoUV);
//                lbl.setHorizontalAlignment(JLabel.CENTER);
//                hmLabels.put(i + "," + j, lbl);
//                panelMapa.add(lbl);
//            }
//        }
//        panelMapa.updateUI();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet 111."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    /* Para key value de JComboBox*/
    class ItemRenderer extends BasicComboBoxRenderer {       
        @Override
        @SuppressWarnings("null")
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected,
                    cellHasFocus);
            if (value != null) {
                Item item = (Item) value;
                setText(item.getDescription().toUpperCase());
            }
            if (index == -1) {
                Item item = (Item) value;
                setText("" + item.getId());
            }
            return this;
        }
    }
    
    class Item {
        
        private int id;
        private String description;
        
        public Item(int id, String description) {
            this.id = id;
            this.description = description;
        }
        
        public int getId() {
            return id;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }

    /* Para key value de JComboBox*/
    /* clase para poner imagen de fondo a JPanel*/
    class ImagePanel extends JPanel {
        
        private Image img;
        
        public ImagePanel(String img) {
            this(new ImageIcon(img).getImage());
        }
        
        public ImagePanel(Image img) {
            
            this.img = img;
        }
        
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
        
    }
    
}
