package com.recinf;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Crawler{
    public static void buscador(String s)
    {
        Queue<String> cola = new LinkedList<String>();
        HashSet<String> URLVistadas = new HashSet<String>();

        String ruta = "C:\\Users\\danie\\Desktop\\HTML\\";
        //Pattern pat = Pattern.compile("/wiki/[a-zA-Z]+[\\:]"); //Descartamos las referencias internas.

        Pattern corpus = Pattern.compile("^corpus\\/"); //Buscamos los documentos con href /corpus/"Referencia"

        cola.add(s);
        do{
            String p = cola.poll();
            if (!URLVistadas.contains(p))
            {
                try
                {
                    Document doc = Jsoup.connect(p).get();
                    System.out.println(p);
                    
                    // Guarda el contenido HTML en un archivo.
                    String[] sp = p.split("/");
                    String fileName = ruta + sp[sp.length-1]; // Nombre que tendrá el archivo.
                    try (FileWriter writer = new FileWriter(fileName)) {
                        writer.write(doc.outerHtml()); // Guarda el contenido HTML en el archivo.
                        URLVistadas.add(p);
                        System.out.println("Guardado en: " + fileName);
                    }

                    Elements a = doc.select("a"); //Selecciona todas las etiquetas <a>
                    
                    for (Element link : a) { // Búsqueda en Anchura
                        String url = link.attr("href"); // Obtener el valor del atributo href.
                        
                        Matcher corps = corpus.matcher(url);
                        if (corps.find()) {
                            String stringAux = "https://github.com/PdedP/RECINF-Project/blob/main/" + url;
                            System.out.println(stringAux);
                            cola.add(stringAux);
                        }
                        
                    }
                    

                } catch (org.jsoup.HttpStatusException e) {
                    if (e.getStatusCode() == 404){
                        System.out.println("Error 404: La página no fue encontrada para la URL: " + p);
                    }                    
                }catch(Exception e)
                {
                    //e.printStackTrace();
                }
            }
        }while(cola.isEmpty()); //El programa parará si la cola está vacia o ha llegado al nº máximo de descargas.
    }
}