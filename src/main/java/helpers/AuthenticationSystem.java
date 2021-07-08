package helpers;

import helpers.Database.db_crud;
import models.Usuario;

import java.io.*;
import java.util.Properties;

public class AuthenticationSystem {

    public static Usuario user;

    public static boolean loginWithUsernameAndPassword (String plainUsername, String plainPassword) {
        boolean state = false;
        Usuario usuario = db_crud.metodoRecupUsuario(plainUsername);
        System.out.println(usuario.getUsername());
        System.out.println(usuario.getPass());
        System.out.println(StringUtil.Decripto(usuario.getPass()));

        if(usuario != null){
            System.out.println(plainUsername);
            if(usuario.getUsername().toUpperCase().trim().equals(plainUsername.toUpperCase().trim())){
                System.out.println(plainPassword);
                System.out.println(plainPassword.length());
                System.out.println(StringUtil.Decripto(usuario.getPass()));
                System.out.println(StringUtil.Decripto(usuario.getPass()).toUpperCase().trim().length());
                if(0 == StringUtil.Decripto(usuario.getPass()).compareTo(plainPassword)){
                    user = usuario;
                    state = true;
                }else{
                    state = false;
                }
            }else{
                return false;
            }
        }else{
            state = false;
        }
        return state;
    }

    public static boolean signInWithUsernameAndPassword (Usuario user) {
        boolean state;
        String password = StringUtil.Cripto(user.getPass());
        System.out.println(password);
        state = db_crud.metodoInsertUsuario(user.getUsername().trim(), password, user.getPriv());
        return state;
    }

    public static boolean updateUsernameAndPassword (Usuario user) {
        boolean state;
        String password = StringUtil.Cripto(user.getPass());
        state = db_crud.metodoUpdateUsuario(user.getUsername().toUpperCase().trim(), password, user.getPriv(), user.getId());
        return state;
    }

    public static String getConfig(String path){
        File configFile = new File(path);
        String host = "";

        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            host = props.getProperty("host");

            System.out.print("Host name is: " + host);
            reader.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

        return host;
    }
    public static void setConfig(String path) throws IOException {
        Properties defaultProps = new Properties();
        //set default

        //create main


        File configFile = new File("config.properties");

        try {
            Properties props = new Properties();
            props.setProperty("host", "www.codejava.net");
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "host settings");
            writer.close();
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

        /*

        File configFile = new File(path);
        FileReader reader = new FileReader(configFile);
        Properties props = new Properties();

        props.load(reader);

        props.setProperty("host", "localhost");

        FileWriter writer = new FileWriter(configFile);
        props.store(writer, "config");

         */
    }


    public static Usuario getUser() {
        return user;
    }
}
