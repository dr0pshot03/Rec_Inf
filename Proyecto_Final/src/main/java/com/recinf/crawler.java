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
import java.net.URI;

class Crawler {
    public static void buscador(String s) {
        Queue<String> cola = new LinkedList<String>();
        HashSet<String> URLVistadas = new HashSet<String>();

        String ruta = "C:\\Users\\danie\\Desktop\\HTML\\";

        Pattern corpus = Pattern.compile("^corpus\\/"); // Buscamos los documentos con href /corpus/"Referencia"

        cola.add(s);
        do {
            String p = cola.poll();
            String[] sp = p.split("/");
            String fileName = ruta + sp[sp.length - 1]; // Nombre que tendrá el archivo.
            if (!URLVistadas.contains(p)) {
                if (sp[sp.length - 2].equals("corpus")) {
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
                            writer.write(content.toString()); // Guarda el contenido HTML en el archivo.
                            URLVistadas.add(p);
                            System.out.println("Guardado en: " + fileName);
                        }
                    
                    }catch(Exception e){}
                } else {
                    try {
                        Document doc = Jsoup.connect(p).get();
                        System.out.println(p);

                        try (FileWriter writer = new FileWriter(fileName)) {
                            writer.write(doc.outerHtml()); // Guarda el contenido HTML en el archivo.
                            URLVistadas.add(p);
                            System.out.println("Guardado en: " + fileName);
                        }

                        Elements a = doc.select("a"); // Selecciona todas las etiquetas <a>

                        for (Element link : a) {
                            String url = link.attr("href"); // Obtener el valor del atributo href.

                            Matcher corps = corpus.matcher(url);
                            if (corps.find()) {
                                String stringAux = "https://raw.githubusercontent.com/PdedP/RECINF-Project/refs/heads/main/" + url;
                                // System.out.println(stringAux);
                                cola.add(stringAux);
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
        } while (!cola.isEmpty()); // El programa parará si la cola está vacía o ha llegado al nº máximo de descargas.
    }
}