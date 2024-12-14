package com.recinf;

import java.util.HashMap;
import java.util.Map;

public class calcularTF2 {
    public static HashMap<String, Tupla> funcionTF2(HashMap<String, HashMap<String, Integer>> termsFrecuencia)
    {
        HashMap<String, Tupla> indiceInvertido = new HashMap<>();
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

                    HashMap<String, Double> DOC_Longitud = new HashMap<>();
                    DOC_Longitud.put(archivo, Math.pow(tf, 2)); 

                    Tupla t = new Tupla(0, DOC_Peso, DOC_Longitud); 
                    
                    indiceInvertido.put(j.getKey(), t);
                }
            }
            for (Map.Entry<String, Tupla> entry : indiceInvertido.entrySet()) {
                Tupla t = entry.getValue();
                t.docIDLongitud.put(archivo, Math.sqrt(longitud)); 
            }
        }
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
    public HashMap<String, Double> docIDLongitud;

    public Tupla(double idf, HashMap<String, Double> docIDPeso, HashMap<String, Double> docIDLongitud) {
        this.idf = idf;
        this.docIDPeso = docIDPeso;
        this.docIDLongitud = docIDLongitud;
    }
}