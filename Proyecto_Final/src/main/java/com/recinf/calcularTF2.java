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
            //System.out.println("Archivo: "+archivo);
            for(Map.Entry<String, Integer> j : i.getValue().entrySet())
            {
                double tf = 1 + Math.log(j.getValue()) / Math.log(2);

                if (indiceInvertido.containsKey(j.getKey()))
                {
                    Tupla t = indiceInvertido.get(j.getKey());
                    t.docIDPeso.put(archivo, tf);
                    indiceInvertido.put(j.getKey(), t); // Vuelve a insertar la palabra
                }
                else{
                    HashMap<String, Double> DOC_Peso = new HashMap<>();
                    DOC_Peso.put(archivo, tf);
                    Tupla t = new Tupla(0, DOC_Peso);
                    indiceInvertido.put(j.getKey(), t);
                }
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

    public Tupla(double idf, HashMap<String, Double> docIDPeso) {
        this.idf = idf;
        this.docIDPeso = docIDPeso;
    }
}