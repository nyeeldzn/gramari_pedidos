package helpers;

import models.Usuario;

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

    public static Usuario getUser() {
        return user;
    }
}
