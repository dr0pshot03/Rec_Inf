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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import java.io.File;

class Crawler {
    public static void buscador(String s) {
        Queue<String> cola = new LinkedList<String>();
        HashSet<String> URLVistadas = new HashSet<String>();

        String ruta = System.getProperty("user.dir") + File.separator + "corpus" + File.separator;

        Pattern corpus = Pattern.compile("^corpus\\/"); // Buscamos los documentos con href /corpus/"Referencia"

        // Ya almacenados en el directorio corpus.
        File folder = new File(ruta);
        File[] files = folder.listFiles();

        for(File i : files)
        {
            URLVistadas.add(i.getName());
        }


        cola.add(s);
        do {
            String p = cola.poll();
            String[] sp = p.split("/");
            String fileName = ruta + sp[sp.length - 1]; // Nombre que tendrá el archivo.
            if (!URLVistadas.contains(p)) { // Comprueba que el archivo no esté almacenado.
                if (sp[sp.length - 2].equals("corpus")) { //Comprueba que sea corpus
                    try{
                        URL url = new URL(p);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");

                        // Leer el contenido
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        StringBuilder content = new StringBuilder();

                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        reader.close();

                        try (FileWriter writer = new FileWriter(fileName)) {
                            writer.write(content.toString());
                            URLVistadas.add(p);
                            System.out.println("Guardado en: " + fileName);
                        }
                    
                    }catch(Exception e){}
                } else {
                    try {
                        Document doc = Jsoup.connect(p).get();
                        Elements a = doc.select("a"); // Selecciona todas las etiquetas <a>
                        String aux = new String();

                        for(int i = 0 ; i<sp.length ; i++)
                        {
                            if (i!=sp.length-1)
                            {
                                aux = aux + sp[i] + "/";
                            }

                        }

                        for (Element link : a) {
                            String url = link.attr("href"); // Obtener el valor del atributo href.

                            Matcher corps = corpus.matcher(url);
                            if (corps.find()) {
                                cola.add(aux + url);
                            }
                        }

                    } catch (org.jsoup.HttpStatusException e) {
                        if (e.getStatusCode() == 404) {
                            System.out.println("Error 404: La página no fue encontrada para la URL: " + p);
                        }
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                }
            }
        } while (!cola.isEmpty()); // El programa parará si la cola está vacía.
    }
}