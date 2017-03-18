package Clases;

import java.util.*;

public class AlgoritmosDeBusqueda {
    
    public static final int AMPLITUD = 1;
    public static final int COSTO_UNIFORME = 2;
    public static final int PROFUNDIDA_EVITANDO_CICLOS = 3;
    public static final int AVARA = 4;
    public static final int AESTRELLA = 5;
    public Mapa mapa;
    public int nodosCreados = 0;
        
    public AlgoritmosDeBusqueda(Mapa mp) {
        this.mapa = mp;
    }
    
    public Nodo init(int algoritmo) {
        Nodo nPadre = new Nodo();
        nPadre.setEstado(mapa.getEstadoInicial());
        nPadre.setProfundidad(0); 
        Nodo nodo = new Nodo();
        switch (algoritmo) {
            case AMPLITUD:
                nodo = PreferentePorAmplitud(nPadre);
                break;
            case COSTO_UNIFORME:
                nodo = DeCostoUniforme(nPadre);
                break;
            case PROFUNDIDA_EVITANDO_CICLOS:
                nodo = PreferentePorProfundidad(nPadre);
                break;
            case AVARA:
                nodo = Avara(nPadre);
                break;
            case AESTRELLA:
                nodo = AEstrella(nPadre);
                break;
        }
        return nodo;
    }
    
    public Nodo PreferentePorAmplitud(Nodo raiz){
        int nodosExpandidos = 0;
        raiz.setBalas(mapa.getBalas());   
        Queue<Nodo> frontera;
        frontera=new LinkedList();
        frontera.add(raiz);
        
        while(!frontera.isEmpty()){ 
            Nodo actual;
            actual = frontera.remove();          
            if(mapa.getMapa()[actual.getEstado()[0]][actual.getEstado()[1]] == 4){
                actual.setNodosCreados(nodosCreados);
                actual.setNodosExpandidos(nodosExpandidos);
                return actual;
            }
            Queue<Nodo> sucesores;
            sucesores = Expandir(actual, 0);
            nodosExpandidos+=1;
            
            while(!sucesores.isEmpty()){
                frontera.add(sucesores.remove());    
            } 
        }
        return null;
    }
    
    public Nodo DeCostoUniforme(Nodo raiz){
        int nodosExpandidos = 0;
        raiz.setBalas(mapa.getBalas());
        raiz.setCompare(0);
        ArrayList<Nodo> frontera = new ArrayList();
        frontera.add(raiz);
        
        while(!frontera.isEmpty()){
            Nodo actual;
            Collections.sort(frontera);
            actual = frontera.remove(0);
            if(mapa.getMapa()[actual.getEstado()[0]][actual.getEstado()[1]] == 4){
                actual.setNodosCreados(nodosCreados);
                actual.setNodosExpandidos(nodosExpandidos);
                return actual;
            }           
            Queue<Nodo> sucesores;
            sucesores = Expandir(actual, 0);
            nodosExpandidos+=1;
            
            while(!sucesores.isEmpty()){
                frontera.add(sucesores.remove());    
            } 
        }
        return null;
    }  

    public Nodo PreferentePorProfundidad(Nodo raiz){
        int nodosExpandidos = 0;
        raiz.setBalas(mapa.getBalas());
        Stack<Nodo> frontera = new Stack<>();
        frontera.push(raiz);
        
        while(!frontera.isEmpty()){
            Nodo actual;
            actual = frontera.pop();         
            if(mapa.getMapa()[actual.getEstado()[0]][actual.getEstado()[1]] == 4){
                actual.setNodosCreados(nodosCreados);
                actual.setNodosExpandidos(nodosExpandidos);
                return actual;
            }          
            Queue<Nodo> sucesores;
            sucesores = ExpandirEvitandoCiclos(actual);
            nodosExpandidos+=1;
            
            while(!sucesores.isEmpty()){
                frontera.push(sucesores.remove());    
            } 
        }
        return null;
    }
    
    public Nodo Avara(Nodo raiz){
        int nodosExpandidos = 0;
        raiz.setBalas(mapa.getBalas());
        raiz.setHeuristica(raiz.asignarHeuristica(raiz, mapa.getEstadoFinal()));
        raiz.setCompare(1);
        ArrayList<Nodo> frontera = new ArrayList();
        frontera.add(raiz);
        
        while(!frontera.isEmpty()){
            Nodo actual;
            Collections.sort(frontera);
            actual = frontera.remove(0);
            if(mapa.getMapa()[actual.getEstado()[0]][actual.getEstado()[1]] == 4){
                actual.setNodosCreados(nodosCreados);
                actual.setNodosExpandidos(nodosExpandidos);
                return actual;
            }
            
            Queue<Nodo> sucesores;
            sucesores = Expandir(actual, 1);
            nodosExpandidos+=1;

            while(!sucesores.isEmpty()){
                frontera.add(sucesores.remove());    
            } 
        }
        return null;
    }
    
    public Nodo AEstrella (Nodo raiz){
        int nodosExpandidos = 0;
        raiz.setBalas(mapa.getBalas());
        raiz.setHeuristica(raiz.asignarHeuristica(raiz, mapa.getEstadoFinal()));
        raiz.setCompare(2);
        raiz.setF(raiz.getCosto() + raiz.getHeuristica());
        ArrayList<Nodo> frontera = new ArrayList();
        frontera.add(raiz);
        
        while(!frontera.isEmpty()){
            
            Nodo actual;
            Collections.sort(frontera);
            actual = frontera.remove(0);
                        
            if(mapa.getMapa()[actual.getEstado()[0]][actual.getEstado()[1]] == 4){
                actual.setNodosCreados(nodosCreados);
                actual.setNodosExpandidos(nodosExpandidos);
                return actual;
            }  
                        
            Queue<Nodo> sucesores;
            sucesores = Expandir(actual, 2);
            nodosExpandidos+=1;
            
            while(!sucesores.isEmpty()){
                frontera.add(sucesores.remove());    
            } 
        }
        return null;
    }
    
    public Queue<Nodo> Expandir(Nodo nodo, int compare){
        int posX = nodo.getEstado()[0];
        int posY = nodo.getEstado()[1];
        Queue<Nodo> hijos;
        hijos=new LinkedList();
        
        //Acción arriba
        if(posX-1 >= 0 && posX-1 < mapa.getMapa().length && posY >= 0 && posY < mapa.getMapa()[0].length && mapa.getMapa()[posX-1][posY] != 1 && nodo.eviteDevolverse(nodo, posX-1, posY)){
            int[] estado = new int[2];
            estado[0] = posX-1;
            estado[1] = posY;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("arriba");
            if(mapa.getMapa()[posX-1][posY] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX-1][posY] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijo.setHeuristica(hijo.asignarHeuristica(hijo, mapa.getEstadoFinal()));
            hijo.setF(hijo.getCosto() + hijo.getHeuristica());
            hijo.setCompare(compare);
            hijos.add(hijo);
        }
        
        //Accion derecha
        if(posX >= 0 && posX < mapa.getMapa().length && posY+1 >= 0 && posY+1 < mapa.getMapa()[0].length && mapa.getMapa()[posX][posY+1] != 1 && nodo.eviteDevolverse(nodo, posX, posY+1)){
            int[] estado = new int[2];
            estado[0] = posX;
            estado[1] = posY+1;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("derecha");
            if(mapa.getMapa()[posX][posY+1] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX][posY+1] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijo.setHeuristica(hijo.asignarHeuristica(hijo, mapa.getEstadoFinal()));
            hijo.setF(hijo.getCosto() + hijo.getHeuristica());
            hijo.setCompare(compare);
            hijos.add(hijo);
        }
        
        //Accion abajo
        if(posX+1 >= 0 && posX+1 < mapa.getMapa().length && posY >= 0 && posY < mapa.getMapa()[0].length && mapa.getMapa()[posX+1][posY] != 1 && nodo.eviteDevolverse(nodo, posX+1, posY)){
            int[] estado = new int[2];
            estado[0] = posX+1;
            estado[1] = posY;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("abajo");
            if(mapa.getMapa()[posX+1][posY] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX+1][posY] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijo.setHeuristica(hijo.asignarHeuristica(hijo, mapa.getEstadoFinal()));
            hijo.setF(hijo.getCosto() + hijo.getHeuristica());
            hijo.setCompare(compare);
            hijos.add(hijo);
        }
        
        //Accion izquierda
        if(posX >= 0 && posX < mapa.getMapa().length && posY-1 >= 0 && posY-1 < mapa.getMapa()[0].length && mapa.getMapa()[posX][posY-1] != 1 && nodo.eviteDevolverse(nodo, posX, posY-1)){
            int[] estado = new int[2];
            estado[0] = posX;
            estado[1] = posY-1;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("izquierda");
            if(mapa.getMapa()[posX][posY-1] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX][posY-1] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijo.setHeuristica(hijo.asignarHeuristica(hijo, mapa.getEstadoFinal()));
            hijo.setF(hijo.getCosto() + hijo.getHeuristica());
            hijo.setCompare(compare);
            hijos.add(hijo);
        }
        return hijos;
    }
    
    public Queue<Nodo> ExpandirEvitandoCiclos(Nodo nodo){
        
        int posX = nodo.getEstado()[0];
        int posY = nodo.getEstado()[1];
        Queue<Nodo> hijos;
        hijos=new LinkedList();
        
        //Acción arriba
        if(posX-1 >= 0 && posX-1 < mapa.getMapa().length && posY >= 0 && posY < mapa.getMapa()[0].length && mapa.getMapa()[posX-1][posY] != 1 && !nodo.evitarCiclos(nodo, posX-1, posY)){
            int[] estado = new int[2];
            estado[0] = posX-1;
            estado[1] = posY;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("arriba");
            if(mapa.getMapa()[posX-1][posY] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX-1][posY] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijos.add(hijo);
        }
        
        //Accion derecha
        if(posX >= 0 && posX < mapa.getMapa().length && posY+1 >= 0 && posY+1 < mapa.getMapa()[0].length && mapa.getMapa()[posX][posY+1] != 1 && !nodo.evitarCiclos(nodo, posX, posY+1)){
            int[] estado = new int[2];
            estado[0] = posX;
            estado[1] = posY+1;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("derecha");
            if(mapa.getMapa()[posX][posY+1] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX][posY+1] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijos.add(hijo);
        }
        
        //Accion abajo
        if(posX+1 >= 0 && posX+1 < mapa.getMapa().length && posY >= 0 && posY < mapa.getMapa()[0].length && mapa.getMapa()[posX+1][posY] != 1 && !nodo.evitarCiclos(nodo, posX+1, posY)){
            int[] estado = new int[2];
            estado[0] = posX+1;
            estado[1] = posY;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("abajo");
            if(mapa.getMapa()[posX+1][posY] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX+1][posY] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijos.add(hijo);
        }
        
        //Accion izquierda
        if(posX >= 0 && posX < mapa.getMapa().length && posY-1 >= 0 && posY-1 < mapa.getMapa()[0].length && mapa.getMapa()[posX][posY-1] != 1 && !nodo.evitarCiclos(nodo, posX, posY-1)){
            int[] estado = new int[2];
            estado[0] = posX;
            estado[1] = posY-1;
            Nodo hijo = new Nodo();
            nodosCreados+=1;
            hijo.setEstado(estado);
            hijo.setPadre(nodo);
            hijo.setOperador("izquierda");
            if(mapa.getMapa()[posX][posY-1] == 3 && nodo.getBalas() != 0){
                hijo.setCosto(nodo.getCosto()+1);
                hijo.setBalas(nodo.getBalas());
                hijo.setBalas(hijo.getBalas()-1);
            }else{
                if(mapa.getMapa()[posX][posY-1] == 3 && nodo.getBalas() == 0){
                    hijo.setCosto(nodo.getCosto()+1+4);
                    hijo.setBalas(nodo.getBalas());
                }else{
                    hijo.setCosto(nodo.getCosto()+1);
                    hijo.setBalas(nodo.getBalas());
                }
            }
            hijo.setProfundidad(nodo.getProfundidad()+1);
            hijos.add(hijo);
        }
        return hijos;
    }
}
