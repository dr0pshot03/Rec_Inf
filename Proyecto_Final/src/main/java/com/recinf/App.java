package com.recinf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println("¿Deseas crawlear?");
        String resp = new String();
        Scanner scan = new Scanner(System.in);
        while (!resp.equals("S") && !resp.equals("N")) {
            System.out.println("Introduce S para SI y N para NO");
            resp = scan.nextLine().trim().toUpperCase();
        }
        scan.close();
        if (resp.equals("S")) 
        {
            System.out.println("Se está crawleando en busca de archivos nuevos.");
            Crawler.buscador("https://raw.githubusercontent.com/PdedP/RECINF-Project/refs/heads/main/index.html");
            System.out.println("Fin del crawleo.");
        }
        System.out.println("Se están aplicando los filtros.");
        filtros.main(args);
        System.out.println("Se está calculando el TF.");
        HashMap<String, HashMap<String, Integer>> termsFrecuencia = calcularTF1.funcionTF1();
        HashMap<String, Tupla> indiceInvertido = calcularTF2.funcionTF2(termsFrecuencia);
        System.out.println("Se está calculando el indice invertido.");
        indiceInvertido = calcularTF2.IDF(indiceInvertido, contarArchivosEnCarpeta());
        System.out.println("Se está almacenando el indice invertido.");
        escribirIndiceInvertidoEnArchivo(indiceInvertido, "indiceInvertido.txt");
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
