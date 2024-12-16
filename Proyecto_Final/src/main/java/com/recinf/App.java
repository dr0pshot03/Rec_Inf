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

        System.out.println("¿Deseas indexar los archivos?");
        String ind = new String();
        Scanner sc = new Scanner(System.in);
        while (!ind.equals("S") && !ind.equals("N")) {
            System.out.println("Introduce S para SI y N para NO");
            ind = sc.nextLine().trim().toUpperCase();
        }
        if (ind.equals("S"))
        {
            System.out.println("¿Deseas crawlear?");
            String resp = new String();
            Scanner scan = new Scanner(System.in);
            while (!resp.equals("S") && !resp.equals("N")) {
                System.out.println("Introduce S para SI y N para NO");
                resp = scan.nextLine().trim().toUpperCase();
            }
            //scan.close();
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
            indiceInvertido = calcularTF2.IDF(indiceInvertido, funcionesExternas.contarArchivosEnCarpeta());
            System.out.println("Se está almacenando el indice invertido.");
            funcionesExternas.escribirIndiceInvertidoEnArchivo(indiceInvertido, "indiceInvertido.txt");
        }

        System.out.println("Se está cargando en memoria el indice invertido.");
        HashMap<String, Tupla> indice = funcionesExternas.leerIndiceInvertidoDeArchivo("indiceInvertido.txt");
        HashMap<String, Double> longitud = funcionesExternas.leerLongitudesDeArchivo("Longitud.txt");
        System.out.println("Introduce la palabra que quieres buscar.");
        String buscar = sc.nextLine();
        buscar = filtros.prepocesar(buscar);
        System.out.println("Se ha procesado la palabra introducida.");
        HashSet<String> documentos = funcionesExternas.documentos(buscar, indice);

        HashMap<String, Double> ranking = funcionesExternas.ranking(buscar, indice, longitud);

        List<Map.Entry<String, Double>> lista = funcionesExternas.ordenarHashMapPorValor(ranking);

        System.out.println("¿Cuantos documentos quieres que se muestren?");
        int cantidad = Integer.parseInt(sc.nextLine());

        for (Map.Entry<String, Double> entry : lista) {
            if(cantidad>0)
            {
                System.out.println("Clave: " + entry.getKey() + ", Ranking: " + entry.getValue());
                cantidad--;
            }
        }

        

        System.out.println("El programa ha tardado un total de: "+ (double)(System.nanoTime()-tiempoInicial)/10e9 + " segundos.");
    }
}
