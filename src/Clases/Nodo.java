package Clases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Nodo implements Comparable<Nodo> {
    private int estado[]=new int[2];
    private Nodo padre;
    private String operador;
    private int profundidad = 0;
    private int costo = 0;
    private int heuristica = 0;
    private int f = 0;
    private int balas = 0;
    private int compare = 0;
    private int nodosCreados = 0;
    private int nodosExpandidos = 0;
    
    public Nodo(){}

    public int getNodosCreados() {
        return nodosCreados;
    }

    public void setNodosCreados(int nodosCreados) {
        this.nodosCreados = nodosCreados;
    }

    public int getNodosExpandidos() {
        return nodosExpandidos;
    }

    public void setNodosExpandidos(int nodosExpandidos) {
        this.nodosExpandidos = nodosExpandidos;
    }
    
    public int[] getEstado() {
        return estado;
    }

    public void setEstado(int[] estado) {
        this.estado = estado;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public int getProfundidad() {
        return profundidad;
    }

    public void setProfundidad(int profundidad) {
        this.profundidad = profundidad;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public int getHeuristica() {
        return heuristica;
    }

    public void setHeuristica(int heuristica) {
        this.heuristica = heuristica;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getBalas() {
        return balas;
    }

    public void setBalas(int balas) {
        this.balas = balas;
    }

    public int getCompare() {
        return compare;
    }

    public Nodo getPadre() {
        return padre;
    }

    public void setPadre(Nodo padre) {
        this.padre = padre;
    }

    public void setCompare(int compare) {
        this.compare = compare;
    }
    
    public boolean eviteDevolverse(Nodo nodo, int posX, int posY){
        if(nodo.padre!=null){
            if(nodo.padre.estado[0] == posX && nodo.padre.estado[1] == posY){
                return false;
            }
        }
        return true;
    }
    
    public int asignarHeuristica(Nodo nodo, int[] estadoFinal){
        int h;
        int x1 = nodo.estado[0];
        int y1 = nodo.estado[1];
        int x2 = estadoFinal[0];
        int y2 = estadoFinal[1];
        h = Math.abs(x2-x1) + Math.abs(y2-y1);
        return h;
    }
    
    public boolean evitarCiclos(Nodo nodo, int posX, int posY){  
        ArrayList<int[]> rama = new ArrayList<>();
        Queue<Nodo> aux;
        aux=new LinkedList();
        aux.add(nodo);
        
        while(!aux.isEmpty()){  
            Nodo actual;
            actual = aux.remove();
            rama.add(actual.estado);
            if(actual.padre != null){
                aux.add(actual.padre);
            }
        }
        for(int i=0; i<rama.size(); i++){
            if(rama.get(i)[0] == posX && rama.get(i)[1] == posY){
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Nodo> getCamino() {
        Nodo nHijo;
        nHijo = this;
        ArrayList<Nodo> lsCamino = new ArrayList<>();
        for (int i = 0; i < this.getProfundidad(); i++) {  //recorro hasta la profundidad exacta para no generar error
            lsCamino.add(nHijo);
            nHijo = nHijo.getPadre(); // se llama sucesivamente al padre.
        }
        Collections.reverse(lsCamino);
        return lsCamino;
    }
    
    @Override
    public int compareTo(Nodo o) {
        switch(o.compare){
            case 0:
                //System.out.println("compareTo con costo");
                if (costo < o.costo) {
                return -1;
            }
            if (costo > o.costo) {
                return 1;
            }
                break;
            
            case 1:
                //System.out.println("compareTo con heuristica");
                if (heuristica < o.heuristica) {
                return -1;
            }
            if (heuristica > o.heuristica) {
                return 1;
            }
                break;
            
            case 2:
                //System.out.println("compareTo con g");
                if (f < o.f) {
                return -1;
            }
            if (f > o.f) {
                return 1;
            }
                break;
            
            default:
                //System.out.println("Fallo compareTo");
                break;
        }
        return 0;
    }  
}
