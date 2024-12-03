package Proyecto_Final.src.main.java.com.recinf;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

public class filtros{
    public static void main(String[] args)
    {
        String directorPath = "../../../../../../corpus";
        File directory = new File(directorPath);
        if (directory.isDirectory()){
            File[] archivos = directory.listFiles();
            for (File archivo : archivos) {
                
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
                    String linea;
                    System.out.println("archivo:" + archivo + "\n");
                    while ((linea = br.readLine()) != null) {
                        linea=linea.toLowerCase();
                        linea=caracteresRaros(linea);
                        linea=numeros(linea);
                        linea=palabrasvacias(linea);
                        linea.replaceAll(" +", " ");
                        System.out.println(linea + "\n");
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private static String caracteresRaros(String input)
    {  
        return input.replaceAll("([.,¿?¡!=]|(?<=\\s)-|-(?=\\s))", "");
    }

    private static String numeros(String input)
    {  
        return input.replaceAll("\\b\\d+\\b", "");
    }

    private static String palabrasvacias(String input)
    {
        File palabras_vacias = new File("palabrasvacias.txt");
        String terminos[]=input.split("\\s+");
        StringBuilder resultado = new StringBuilder();
        List<String> palabrasvacias = new ArrayList<>();
        boolean eliminar=false;
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(palabras_vacias), "UTF-8"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                palabrasvacias.add(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String termino : terminos)
        {
            for (String palabra : palabrasvacias) {
                if(termino.contentEquals(palabra))
                    eliminar=true;
            }
            if(eliminar==false)
                resultado.append(termino + " ");
            eliminar=false;
        }
        return resultado.toString();
    }
}