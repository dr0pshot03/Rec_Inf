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
import java.util.HashMap;
import java.util.Map;

public class funcionesExternas {
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
}
