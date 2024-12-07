package com.recinf;

import java.util.HashMap;

public class App 
{
    public static void main( String[] args )
    {
        //Crawler.buscador("https://raw.githubusercontent.com/PdedP/RECINF-Project/refs/heads/main/index.html");
        filtros.main(args);
        HashMap<String, HashMap<String, Integer>> termsFrecuencia = calcularTF1();
        calcularTF2()
    }
}
