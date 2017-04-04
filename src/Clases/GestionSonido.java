package Clases;

import java.util.HashMap; 
import java.applet.AudioClip; 
import java.net.*; 
import java.applet.*;
import javax.swing.JOptionPane;

public class GestionSonido {
    
//Almacena los sonidos en una lista 
HashMap ListaSonidos = new HashMap();

//Almacena las imágenes en una lista 
HashMap ListaImagenes = new HashMap();

//Carga el sonido de disco
public AudioClip CargaSonido(String Direccion){
    try{
    URL Rutasonido = getClass().getResource(Direccion);
    AudioClip Sonido = Applet.newAudioClip(Rutasonido);
    return Sonido;
    }catch(Exception e){
    JOptionPane.showMessageDialog(null, "Problemas al cargar archivo de sonido");
    return null;
    }
}
//Si el sonido no esta en memoria(verificando la lista) lo carga de disco
public AudioClip RetornaSonido(String NombreSonido) {
//Busca el sonido en la lista
AudioClip Sonido = (AudioClip) ListaSonidos.get(NombreSonido);

//Si no encuentra el sonido en la lista, lo carga del disco
    if(Sonido == null){
    Sonido = CargaSonido(NombreSonido);
    ListaSonidos.put(NombreSonido, Sonido);
        }
    return Sonido;
    }

}
