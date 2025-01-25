package com.recinf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class calcularTF1 {

    public static HashMap<String, HashMap<String, Integer>> funcionTF1()
    {
        HashMap<String, HashMap<String, Integer>> mapa = new HashMap<>();
        String ruta_procesada = System.getProperty("user.dir") + File.separator + "corpus_procesado" + File.separator;
        File directory = new File(ruta_procesada);
        if (directory.isDirectory()){
            File[] archivos = directory.listFiles();
            for (File archivo : archivos) {
                HashMap<String, Integer> frec_terminos = new HashMap<>();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
                    String linea;

                    while ((linea = br.readLine()) != null) {
                        
                        String terminos[]=linea.split("\\s+");

                        for (String termino : terminos)

                            if(frec_terminos.containsKey(termino))
                            {
                                int frecuenciaActual = frec_terminos.get(termino);
                                frec_terminos.put(termino, frecuenciaActual + 1);
                            }
                            else
                                frec_terminos.put(termino, 1);

                    }

                    mapa.put(archivo.getName(), frec_terminos); // nombre documento, [termino, frecuencia]
                    
                }catch (IOException e) {
                    e.printStackTrace(); // Manejo de excepciones si ocurre un error al leer el archivo
                }

            }
        }
        return mapa;
    }
}
