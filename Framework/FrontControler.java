package mg.itu.controleur;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrontController extends HttpServlet {
    private Map<String, Mapping> urlMappings;

    @Override
    public void init() throws ServletException {
        urlMappings = new HashMap<>();
        try {
            ServletContext context = getServletContext();
            String chemin = context.getInitParameter("chemin");
            List<String> controllers = scan(chemin); // Utilisation de la méthode scan mise à jour
            for (String controller : controllers) {
                Class<?> clazz = Class.forName(controller);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GET.class)) {
                        GET annotation = method.getAnnotation(GET.class);
                        String url = annotation.value();
                        urlMappings.put(url, new Mapping(clazz.getName(), method.getName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'initialisation du FrontController", e);
        }
    }
    private List<String> scan(String chemin) throws Exception {
        List<String> liste = new ArrayList<>();
        try {
            String cheminRepertoire = chemin.replace('.', '/');
            URL urPackage = Thread.currentThread().getContextClassLoader().getResource(cheminRepertoire);
            if (urPackage != null) {
                File directory = new File(urPackage.getFile());
                File[] fichiers = directory.listFiles();
                if (fichiers != null) {
                    for (File fichier : fichiers) {
                        if (fichier.isFile() && fichier.getName().endsWith(".class")) {
                            String nomClasse = fichier.getName().substring(0, fichier.getName().length() - 6);
                            String nomCompletClasse = chemin + "." + nomClasse;
                            Class<?> clazz = Class.forName(nomCompletClasse);
                            if (clazz.isAnnotationPresent(Controleur.class)) {
                                System.out.println("is annote");
                                liste.add(nomCompletClasse);
                            }
                        } else if (fichier.isDirectory()) {
                            List<String> li = scan(chemin + "." + fichier.getName());
                            liste.addAll(li);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return liste;
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String requestUrl = request.getRequestURI().substring(request.getContextPath().length());
            Mapping mapping = urlMappings.get(requestUrl);
            
            if (mapping != null) {
                Class<?> class1 = Class.forName(mapping.getClassName());
                // Obtenir le nom de la méthode
                String methodName = mapping.getMethodName();
                
                // Obtenir la méthode à partir de la classe et du nom de la méthode
                // Note: Cet exemple suppose que la méthode n'a pas de paramètres
                Method method = class1.getMethod(methodName);
                Object instance = class1.getDeclaredConstructor().newInstance();
            
                // Invoquer la méthode sur l'instance
    
