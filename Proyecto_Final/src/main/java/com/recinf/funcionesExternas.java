package com.recinf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;

public class funcionesExternas {
    public static HashMap<String, Double> leerLongitudesDeArchivo(String rutaArchivo) {
        HashMap<String, Double> longitudes = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            String documento = null;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Documento: ")) {
                    documento = linea.substring(11).trim();
                } else if (linea.startsWith("Longitud: ")) {
                    if (documento != null) {
                        double longitud = Double.parseDouble(linea.substring(10).trim());
                        longitudes.put(documento, longitud);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return longitudes;
    }

    public static void escribirIndiceInvertidoEnArchivo(HashMap<String, Tupla> indiceInvertido, String nombreArchivo) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo));
        
        for (Map.Entry<String, Tupla> entry : indiceInvertido.entrySet()) {
            String termino = entry.getKey();
            Tupla tupla = entry.getValue();
            writer.write("Término: " + termino + "\n");
            writer.write("IDF: " + tupla.idf + "\n");
            for (Map.Entry<String, Double> docEntry : tupla.docIDPeso.entrySet()) {
                writer.write("Documento: " + docEntry.getKey() + ", Peso: " + docEntry.getValue() + "\n");
            }
            writer.write("\n");
            }
            
            writer.close();
    }

    public static int contarArchivosEnCarpeta() {
        int contador = 0;
        Path carpeta = Paths.get(System.getProperty("user.dir") + File.separator + "corpus_procesado");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(carpeta)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    contador++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contador;
    }

    public static HashMap<String, Tupla> leerIndiceInvertidoDeArchivo(String nombreArchivo) throws IOException {
        HashMap<String, Tupla> indiceInvertido = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo));
        String linea;
        String termino = null;
        double idf = 0.0;
        HashMap<String, Double> docIDPeso = null;

        while ((linea = reader.readLine()) != null) {
            if (linea.startsWith("Término: ")) {
                if (termino != null) {
                    indiceInvertido.put(termino, new Tupla(idf, docIDPeso));
                }
                termino = linea.substring(9);
                docIDPeso = new HashMap<>();
            } else if (linea.startsWith("IDF: ")) {
                idf = Double.parseDouble(linea.substring(5));
            } else if (linea.startsWith("Documento: ")) {
                String[] partes = linea.substring(11).split(", Peso: ");
                String docID = partes[0];
                double peso = Double.parseDouble(partes[1]);
                docIDPeso.put(docID, peso);
            }
        }
        if (termino != null) {
            indiceInvertido.put(termino, new Tupla(idf, docIDPeso));
        }
        reader.close();
        return indiceInvertido;
    }

    public static HashSet<String> documentos(String palabra, HashMap<String, Tupla> mapa)
    {
        HashSet<String> documentos = new HashSet<String>();
        Tupla t = mapa.get(palabra);
        for(Map.Entry<String, Double> entry : t.docIDPeso.entrySet()){
            documentos.add(entry.getKey());
        }
        return documentos;
    }

    public static HashMap<String, Double> ranking(String palabra, HashMap<String, Tupla> indice, HashMap<String, Double> longitud)
    {
        
        HashMap<String, Double> ranking = new HashMap<>();
        try{
            Tupla p = indice.get(palabra);
            for(Map.Entry<String, Double> doc : p.docIDPeso.entrySet())
            {
                double valor = (p.idf*doc.getValue())/(longitud.get(doc.getKey())*p.idf);//No se eleva al cuadrado al ser anulado por la raiz ya que solo lo calculamos para ubna palabra
                ranking.put(doc.getKey(), valor);
            }
        }catch(Exception e)
        {
            String ANSI_RESET = "\u001B[0m";
            String ANSI_RED = "\u001B[31m";
            System.out.println(ANSI_RED+"La palabra no existe"+ANSI_RESET);
        }
        return ranking;
    }

    public static List<Map.Entry<String, Double>> ordenarHashMapPorValor(HashMap<String, Double> map) {
        // Convertir el HashMap en una lista de entradas
        List<Map.Entry<String, Double>> lista = new ArrayList<>(map.entrySet());

        // Ordenar la lista por el valor (Double) en orden descendente
        lista.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        return lista;
    }
}


