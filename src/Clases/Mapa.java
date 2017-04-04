package Clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Mapa implements Cloneable {
    
    public static final int LIBRE = 0;
    public static final int OBSTACULO = 1;
    public static final int ROBOT = 2;
    public static final int ROBOT_ENEMIGO = 3;
    public static final int OBJETIVO = 4;
    public static final int CAMINO = 7;

    public static final int COSTO_NORMAL = 1;
    public static final int COSTO_ENEMIGO = 4;
    public static ArrayList<String> LIST_OBJETIVOS;

    int[][] mapa;
    int[][] solucionMapa;
    int balas;
    int[] estadoInicial = new int[2];
    int[] estadoFinal = new int[2];
    int columnas, filas;
    String strPosicion;
    private int intCountObjetivo;
    private ArrayList<String> listPosObjetivo;

    @Override
    public Mapa clone() throws CloneNotSupportedException {
        try {
            final Mapa result = (Mapa) super.clone();
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
        
public Mapa(File flMapa) {
        this.listPosObjetivo = new ArrayList<>();
        LIST_OBJETIVOS = new ArrayList<>();
        String linea;
        String delimitador = " ";
        int numlinea = 0;
        FileReader fr = null;
        BufferedReader br;
        int intFila = 0, intColumna = 0;

        try {

            fr = new FileReader(flMapa);
            br = new BufferedReader(fr);

            while ((linea = br.readLine()) != null) {
                intFila += 1;
                intColumna = linea.split(delimitador).length;
            }

            fr = new FileReader(flMapa);
            br = new BufferedReader(fr);

            this.filas = intFila-1;
            this.columnas = intColumna;

            this.mapa = new int[intFila-1][intColumna];
            this.solucionMapa = new int[intFila-1][intColumna];
            
            intFila = 0;
            while ((linea = br.readLine()) != null) {
                String[] strColumn = linea.split(delimitador);
                if(numlinea > 0){
                    for (int i = 0; i < strColumn.length; i++) {
                        int posInicial = Integer.parseInt(strColumn[i]);
                        if (posInicial == ROBOT) {
                            this.strPosicion = intFila + "," + i;
                            this.estadoInicial[0] = intFila;
                            this.estadoInicial[1] = i;
                        }
                        if (posInicial == OBJETIVO) {
                            this.estadoFinal[0] = intFila;
                            this.estadoFinal[1] = i;
                        }
                        if (strColumn[i].equals(String.valueOf(OBJETIVO))) {
                            this.listPosObjetivo.add(intFila + "," + i);
                            LIST_OBJETIVOS.add(intFila + "," + i);
                        }
                        this.intCountObjetivo = strColumn[i].equals(String.valueOf(OBJETIVO)) ? this.intCountObjetivo + 1 : this.intCountObjetivo;
                        this.mapa[intFila][i] = Integer.parseInt(strColumn[i]);
                        this.solucionMapa[intFila][i] = Integer.parseInt(strColumn[i]);
                    }
                intFila += 1;
                }else{
                this.balas = Integer.parseInt(linea);
                }
                numlinea+=1;
            }

        } catch (IOException | NumberFormatException e) {
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (IOException e2) {
            }
        }
    }
    
    public int[][] getSolucionMapa() {
        return solucionMapa;
    }

    public void setSolucionMapa(int[][] solucionMapa) {
        this.solucionMapa = solucionMapa;
    }

    public int getBalas() {
        return balas;
    }

    public void setBalas(int balas) {
        this.balas = balas;
    }

    public int[] getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(int[] estadoInicial) {
        this.estadoInicial = estadoInicial;
    }
    
    public int[] getEstadoFinal() {
        return estadoFinal;
    }

    public void setEstadoFinal(int[] estadoFinal) {
        this.estadoFinal = estadoFinal;
    }

    public int[][] getMapa() {
        return mapa;
    }

    public void setMapa(int[][] mapa) {
        this.mapa = mapa;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int valuePosition(int intFila, int intColumna) {
        return this.mapa[intFila][intColumna];
    }

    public int valuePosition(int[] estadoInicial) {
        int intFila = estadoInicial[0];
        int intColumna = estadoInicial[1];
        return this.mapa[intFila][intColumna];
    }

    public void setValuePosition(int intFila, int intColumna, int intValor) {
        this.mapa[intFila][intColumna] = intValor;
    }
}
