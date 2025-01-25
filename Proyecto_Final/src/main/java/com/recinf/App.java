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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        long tiempoInicial = System.nanoTime();
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        String ANSI_GREEN = "\u001B[32m";

        String ruta = System.getProperty("user.dir") + File.separator + "corpus_procesado";
        File directory = new File(ruta);

        System.out.println(ANSI_YELLOW + "¿Deseas indexar los archivos?" + ANSI_RESET);
        String ind = new String();
        Scanner sc = new Scanner(System.in);
        while (!ind.equals("S") && !ind.equals("N")) {
            System.out.println("Introduce S para SI y N para NO");
            if(!directory.isDirectory()){
                System.out.println(ANSI_GREEN+"Al ser la primera vez que se ejecuta se va a proceder a indexar."+ANSI_RESET);
                ind = "S";
            }
            else ind = sc.nextLine().trim().toUpperCase();
        }
        if (ind.equals("S"))
        {
            System.out.println(ANSI_YELLOW + "¿Deseas crawlear?" + ANSI_RESET);
            String resp = new String();
            Scanner scan = new Scanner(System.in);
            while (!resp.equals("S") && !resp.equals("N")) {
                System.out.println("Introduce S para SI y N para NO");
                resp = scan.nextLine().trim().toUpperCase();
            }
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
            System.out.println("Se está calculando el indice invertido y la longitud.");
            HashMap<String, Tupla> indiceInvertido = calcularTF2.funcionTF2(termsFrecuencia);
            indiceInvertido = calcularTF2.IDF(indiceInvertido, funcionesExternas.contarArchivosEnCarpeta());
            calcularTF2.Longitud2(indiceInvertido, termsFrecuencia);
            System.out.println("Se está almacenando el indice invertido.");
            funcionesExternas.escribirIndiceInvertidoEnArchivo(indiceInvertido, "indiceInvertido.txt");
        }

        System.out.println("Se está cargando en memoria el indice invertido.");
        HashMap<String, Tupla> indice = funcionesExternas.leerIndiceInvertidoDeArchivo("indiceInvertido.txt");
        HashMap<String, Double> longitud = funcionesExternas.leerLongitudesDeArchivo("Longitud.txt");
        System.out.println(ANSI_YELLOW + "Introduce la palabra que quieres buscar." + ANSI_RESET);
        String buscar = sc.nextLine();
        String buscar_proc = filtros.prepocesar(buscar);
        System.out.println("Se ha procesado la palabra introducida.");
        //HashSet<String> documentos = funcionesExternas.documentos(buscar, indice);

        HashMap<String, Double> ranking = funcionesExternas.ranking(buscar_proc, indice, longitud);

        if (!ranking.isEmpty())
        {
            List<Map.Entry<String, Double>> lista = funcionesExternas.ordenarHashMapPorValor(ranking);

            System.out.println(ANSI_YELLOW + "¿Cuantos documentos quieres que se muestren?" + ANSI_RESET);
            int cantidad = Integer.parseInt(sc.nextLine());

            for (Map.Entry<String, Double> entry : lista) {
                if(cantidad>0)
                {
                    System.out.println("Documento: " + entry.getKey() + ", Ranking: " + entry.getValue());
                    funcionesExternas.muestraFrase(buscar, entry.getKey());
                    cantidad--;
                }
                else break;
            }
        }

        long tiempoFinal = System.nanoTime();
        sc.close();
        System.out.println("El programa ha tardado un total de: "+ (double)(tiempoFinal-tiempoInicial)/1e9 + " segundos.");
    }
}
