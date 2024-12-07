package com.recinf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class App 
{
    public static void main( String[] args )
    {
        //Crawler.buscador("https://raw.githubusercontent.com/PdedP/RECINF-Project/refs/heads/main/index.html");
        filtros.main(args);
        HashMap<String, HashMap<String, Integer>> termsFrecuencia = calcularTF1.funcionTF1();
        HashMap<String, Tupla> indiceInvertido = calcularTF2.funcionTF2(termsFrecuencia);
        indiceInvertido = calcularTF2.IDF(indiceInvertido, contarArchivosEnCarpeta());
        try {
            escribirIndiceInvertidoEnArchivo(indiceInvertido, "indiceInvertido.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        
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
}
