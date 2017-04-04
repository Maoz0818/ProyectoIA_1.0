package Clases;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import net.miginfocom.swing.MigLayout;


public class Vista implements ActionListener, MouseListener{
    
    // variables enteras que representan los objetos del mapa
    public static final int LIBRE = 0;
    public static final int OBSTACULO = 1;
    public static final int ROBOT = 2;
    public static final int ROBOT_ENEMIGO = 3;
    public static final int OBJETIVO = 4;
    
    // Todo lo necesario para armar la ventana
    JFrame jfVentana;
    JPanel panelCentro, panelOpciones, panelSolucion;
    ImagePanel panelMapa, panelInicio;
    JComboBox cbxBusqueda, cbxAlgoritmo;
    JButton btnBuscar, btnEjecutar, btnSimulacion, btnResetMapa;
    Mapa mp;
    JTextField txtNameFile;
    JLabel lblNodosExpandidos, lblProfundidad, lblTiempo, lblCosto, lblNodosCreados, lblBalas, lblBalasIniciales;
    long time;
    Nodo nodo;
    HashMap<String, JLabel> hmLabels;
    GestionSonido Recurso = new GestionSonido(); 
    AudioClip disparo,intro,meta,ladrar;
    
    // Metodo que da inicio al JFrame y carga todo lo necesario en el mismo
    public void init() {     
        try {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
	}
        jfVentana = new JFrame("Métodos de Busqueda");
        jfVentana.setLayout(new MigLayout());
        jfVentana.getContentPane().setBackground(Color.decode("#000000"));
                
        JLabel lblTitle = new JLabel("<html><span style='font-size:3em'><font color='white'>Proyecto 1: Dog sapiens</font></span></html>");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setVerticalAlignment(JLabel.CENTER);
        
        JLabel lblIco = new JLabel();
        ImageIcon imgIcoUV = new ImageIcon(this.getClass().getResource("/images/logounivalle.gif"));
        Image image = imgIcoUV.getImage();
        Image newimg = image.getScaledInstance(60, 80, java.awt.Image.SCALE_REPLICATE);
        imgIcoUV = new ImageIcon(newimg);
        lblIco.setBackground(Color.WHITE);
        lblIco.setIcon(imgIcoUV);
                       
        jfVentana.add(lblIco);
        jfVentana.add(lblTitle, "wrap 5, growy, width max(100%)");

        //Panel que contiene las opciones   
        panelOpciones = new JPanel(new MigLayout());
        panelOpciones.setBackground(Color.decode("#000000"));
         
        panelOpciones.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Opciones de Busqueda",0,0,new Font("arial",Font.BOLD,12),Color.WHITE));
                
        JLabel lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Cargar mapa:</font></span></html>");
        txtNameFile = new JTextField();
        btnBuscar = new JButton("...");
        btnBuscar.addActionListener(this);
        
                
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(txtNameFile, "width max(40%, 40%), growx, growy");
        panelOpciones.add(btnBuscar, "wrap 10");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Tipo de búsqueda:</font></span></html>");
        String[] strBusqueda = {"Seleccione una opción", "Búsqueda no informada", "Búsqueda informada"};
        cbxBusqueda = new JComboBox(strBusqueda);
        cbxBusqueda.addActionListener(this);
        
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(cbxBusqueda, "width max(100%, 100%), growx, growy, wrap 10");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Algoritmo:</font></span></html>");
        cbxAlgoritmo = new JComboBox(strBusqueda);
        cbxAlgoritmo.addActionListener(this);
        cbxAlgoritmo.setEnabled(false);
        
        panelOpciones.add(lbl, "wrap");
        panelOpciones.add(cbxAlgoritmo, "width max(100%, 100%), growx, growy, wrap 10");
        
        btnEjecutar = new JButton("Ejecutar busqueda");
        btnEjecutar.addActionListener(this);
          
        panelOpciones.add(btnEjecutar, "span, center, wrap 50");

        // Panel que contiene el mapa principal
        ImageIcon background = new ImageIcon(this.getClass().getResource("/images/fondoMapa.png"));
        panelMapa = new ImagePanel(background.getImage());
        panelMapa.setLayout(new GridLayout());
        panelMapa.setPreferredSize(new Dimension(600, 600));
        panelMapa.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panelMapa.setVisible(true);

        // Panel que contiene el reporte
        panelSolucion = new JPanel(new MigLayout());
        panelSolucion.setBackground(Color.decode("#000000"));
        panelSolucion.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE),"Reporte de Busqueda",0,0,new Font("arial",Font.BOLD,12),Color.WHITE));
        panelSolucion.setVisible(true);
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Total nodos creados:</font> </span></html>");
        lblNodosCreados = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblNodosCreados.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblNodosCreados, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Total nodos expandidos:</font> </span></html>");
        lblNodosExpandidos = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblNodosExpandidos.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblNodosExpandidos, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Profundidad:</font> </span></html>");
        lblProfundidad = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblProfundidad.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblProfundidad, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Costo:</font> </span></html>");
        lblCosto = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblCosto.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblCosto, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Tiempo:</font> </span></html>");
        lblTiempo = new JLabel("<html><span style='font-size:1em'><font color='white'>0 ms </font></span></html>");
        lblTiempo.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblTiempo, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Balas iniciales: </font></span></html>");
        lblBalasIniciales = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblBalasIniciales.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblBalasIniciales, "growx, growy, wrap");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Balas restantes: </font></span></html>");
        lblBalas = new JLabel("<html><span style='font-size:1em'><font color='white'>0</font></span></html>");
        lblBalas.setForeground(Color.WHITE);
        panelSolucion.add(lbl, "growx, growy");
        panelSolucion.add(lblBalas, "growx, growy, wrap 15");
        
        lbl = new JLabel("<html><span style='font-size:1em'><font color='white'>Simulación: </font></span></html>");
        btnSimulacion = new JButton("Recorrer camino");
        btnSimulacion.setHorizontalAlignment(SwingConstants.CENTER);
        btnSimulacion.addActionListener(this);
        panelSolucion.add(lbl, "growx, growy, wrap");
        panelSolucion.add(btnSimulacion, "left,wrap 10");
        
        // Panel que contiene el estado inicial del mapa
        ImageIcon background2 = new ImageIcon(this.getClass().getResource("/images/fondoMapa.png"));
        panelInicio = new ImagePanel(background2.getImage());
        panelInicio.setLayout(new GridLayout());
        panelInicio.setPreferredSize(new Dimension(250, 250));
        panelInicio.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        panelInicio.setVisible(true);
              
        panelCentro = new JPanel(new MigLayout());
        panelCentro.setBackground(Color.decode("#000000"));
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
        jfVentana.setLocationRelativeTo(null);
        
        disparo = Recurso.RetornaSonido("/sound/disparo.wav");
        meta = Recurso.RetornaSonido("/sound/meta.wav");
        intro = Recurso.RetornaSonido("/sound/intro.wav");
        ladrar = Recurso.RetornaSonido("/sound/ladrar.wav");
    }
    
    // Metodo que construye el mapa inicial en el panelMapa
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
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/pasto.png"));
                        break;
                    case Mapa.ROBOT:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/dog.gif"));
                        break;
                    case Mapa.ROBOT_ENEMIGO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck_malo.gif"));
                        break;
                    case Mapa.OBJETIVO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck.gif"));
                        break;
                    case 0:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/libre.png"));
                        break;
                }
                Image image = imgIcoUV.getImage();
                Image newimg = image.getScaledInstance(52, 46, java.awt.Image.SCALE_REPLICATE);
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
    
    // Metodo que construye el mapa inicial 
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
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/pasto.png"));
                        break;
                    case Mapa.ROBOT:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/dog.png"));
                        break;
                    case Mapa.ROBOT_ENEMIGO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck_malo.png"));
                        break;
                    case Mapa.OBJETIVO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck.png"));
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
    
    // Metodo que construye el mapa con el camino encontrado en el panelMapa
    public void construirMapaSolucion() {
        intro.play();
        ArrayList<Nodo> strCamino = nodo.getCamino();
        int[][] matriz_solucion = new int[mp.solucionMapa.length][mp.solucionMapa[0].length];
        
        for (int i=0; i < mp.solucionMapa.length; i++) {
            System.arraycopy(mp.solucionMapa[i], 0, matriz_solucion[i], 0, mp.solucionMapa[0].length);
        }
                
        for (int i = 0; i < strCamino.size()-1; i++) {
            matriz_solucion[strCamino.get(i).getEstado()[0]][strCamino.get(i).getEstado()[1]] = 7;
        }

        panelMapa.removeAll();
        panelMapa.updateUI();
        panelMapa.setLayout(new GridLayout(matriz_solucion.length, matriz_solucion[0].length));
        JLabel lbl;
        ImageIcon imgIcoUV = new ImageIcon();
        hmLabels = new HashMap<>();
        for (int i = 0; i < matriz_solucion.length; i++) {
            for (int j = 0; j < matriz_solucion[0].length; j++) {
                lbl = new JLabel();
                lbl.setToolTipText(i + "," + j);
                switch (matriz_solucion[i][j]) {
                    case Mapa.OBSTACULO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/pasto.png"));
                        break;
                    case Mapa.ROBOT:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/dog.gif"));
                        break;
                    case Mapa.ROBOT_ENEMIGO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck_malo.gif"));
                        break;
                    case Mapa.OBJETIVO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/meta.png"));
                        break;
                    case Mapa.CAMINO:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/camino.gif"));
                        break;
                    case 0:
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/libre.png"));
                        break;
                }
                Image image = imgIcoUV.getImage();
                Image newimg = image.getScaledInstance(52, 46, java.awt.Image.SCALE_REPLICATE);
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
    
    // Metodo que asigna las acciones a los distintos botones
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
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT", "txt");//filtro de txt
            selectorArchivos.setFileFilter(filtro);//agrega eel filtro
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
                    reporte();
                    construirMapaSolucion();
            }
        } else if (e.getSource() == btnSimulacion) {
            construirMapa();
            simulacion();
        }
    }
    
    //Metodo que crea el reporte
    public void reporte() {
        AlgoritmosDeBusqueda objAlgoritmos = new AlgoritmosDeBusqueda(mp);
        int intAlgoritmo = ((Item) cbxAlgoritmo.getSelectedItem()).getId();
        long time_start = System.currentTimeMillis();
        nodo = objAlgoritmos.init(intAlgoritmo);
        long time_end = System.currentTimeMillis();
        time = time_end - time_start;
        
        
        lblNodosCreados.setText("" + nodo.getNodosCreados());
        lblNodosExpandidos.setText("" + nodo.getNodosExpandidos());
        lblProfundidad.setText(""+ nodo.getProfundidad());
        lblCosto.setText("" + nodo.getCosto());
        lblTiempo.setText("" + time + "ms");
        lblBalasIniciales.setText("" + mp.getBalas());
        lblBalas.setText("" + nodo.getBalas());
    }
    
    // Metodo que genera la simulación para el recorrido del camino encontrado
    public void simulacion() {
        ladrar.play();
        ArrayList<Nodo> strCamino = nodo.getCamino();     
        int delay = 500;
        new Timer(delay, new ActionListener() {
            private int i = 0;
            int[] strAnt = mp.getEstadoInicial();
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if (i < strCamino.size()) {
                    JLabel lblAnteriror = hmLabels.get(strAnt[0]+","+strAnt[1]);
                    ImageIcon imgIcoUV;
                    Image image;
                    Image newimg;
                    switch (mp.valuePosition(strAnt)) {
                        case Mapa.ROBOT_ENEMIGO:
                            imgIcoUV = new ImageIcon(this.getClass().getResource("/images/duck_malo.gif"));
                            image = imgIcoUV.getImage();
                            newimg = image.getScaledInstance(52, 46, java.awt.Image.SCALE_REPLICATE);
                            imgIcoUV = new ImageIcon(newimg);
                            lblAnteriror.setIcon(imgIcoUV);
                            break;
                        default:
                            lblAnteriror.setIcon(null);
                            break;
                        
                    }
                    
                    JLabel lbl = hmLabels.get(strCamino.get(i).getEstado()[0]+","+strCamino.get(i).getEstado()[1]);
                    
                    if (strCamino.get(i).isDisparo()) {
                        disparo.play();
                        imgIcoUV = new ImageIcon(this.getClass().getResource("/images/disparo.png"));
                    } else {
                        switch (mp.valuePosition(strCamino.get(i).getEstado())) {
                            case Mapa.ROBOT_ENEMIGO:
                                imgIcoUV = new ImageIcon(this.getClass().getResource("/images/dog.gif"));
                                break;
                            case Mapa.OBJETIVO:
                                meta.play();
                                imgIcoUV = new ImageIcon(this.getClass().getResource("/images/meta.png"));
                                break;
                            default:
                                imgIcoUV = new ImageIcon(this.getClass().getResource("/images/dog.gif"));
                                break;
                        }
                        
                    }
                    image = imgIcoUV.getImage();
                    newimg = image.getScaledInstance(52, 46, java.awt.Image.SCALE_REPLICATE);
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

    // Para key value de JComboBox
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

    // Para key value de JComboBox
    // clase para poner imagen de fondo a JPanel
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
