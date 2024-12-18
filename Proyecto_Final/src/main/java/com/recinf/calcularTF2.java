package com.recinf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class calcularTF2 {

    public static void Longitud(HashMap<String, Double> docIdLongitud)
    {
        System.out.println("Se está almacenando la longitud de los documentos.");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Longitud.txt"));
            for(Map.Entry<String, Double> entry : docIdLongitud.entrySet()){
                writer.write("Documento: " + entry.getKey() + "\n");
                writer.write("Longitud: " + entry.getValue() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
        

    public static HashMap<String, Tupla> funcionTF2(HashMap<String, HashMap<String, Integer>> termsFrecuencia)
    {
        HashMap<String, Tupla> indiceInvertido = new HashMap<>();
        HashMap<String, Double> docIdLongitud = new HashMap<>();
        for(Map.Entry<String, HashMap<String, Integer>> i : termsFrecuencia.entrySet())
        {
            String archivo = i.getKey();
            double longitud = 0;
            //System.out.println("Archivo: "+archivo);
            for(Map.Entry<String, Integer> j : i.getValue().entrySet())
            {
                double tf = 1 + Math.log(j.getValue()) / Math.log(2);

                if (indiceInvertido.containsKey(j.getKey()))
                {
                    Tupla t = indiceInvertido.get(j.getKey());
                    t.docIDPeso.put(archivo, tf);
                    indiceInvertido.put(j.getKey(), t); // Vuelve a insertar la palabra
                    longitud += Math.pow(tf, 2);
                }
                else{
                    HashMap<String, Double> DOC_Peso = new HashMap<>();
                    DOC_Peso.put(archivo, tf);
                    Tupla t = new Tupla(0, DOC_Peso);
                    indiceInvertido.put(j.getKey(), t);
                }
            }
                docIdLongitud.put(archivo, Math.sqrt(longitud));
        }
        Longitud(docIdLongitud);
        return indiceInvertido;
    }

    public static HashMap<String, Tupla> IDF(HashMap<String, Tupla> indiceInvertido, int totalDocumentos)
    {
        for(Map.Entry<String, Tupla> j : indiceInvertido.entrySet())
        {
            j.getValue().idf = Math.log((double)totalDocumentos / j.getValue().docIDPeso.size()) / Math.log(2);
        }
        return indiceInvertido;
    }
}

class Tupla {
    public double idf;
    public HashMap<String, Double> docIDPeso;

    public Tupla(double idf, HashMap<String, Double> docIDPeso) {
        this.idf = idf;
        this.docIDPeso = docIDPeso;
    }
}