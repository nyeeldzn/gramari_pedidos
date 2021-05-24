package helpers;

import models.Usuario;

public class UserPrivilegiesVerify {

    private static Usuario user;

    /////////////////////////
    // priv 1 - entregador //
    // priv 2 - operador   //
    // priv 3 - admin      //
    /////////////////////////

    public static boolean permissaoVerBotao(Usuario user, int buttonPriv){
        boolean state = false;

        switch (buttonPriv){
            case 1:
                if(user.getPriv() >= 1){
                    state = true;
                }else{
                    state = false;
                }
                break;
            case 2:
                if(user.getPriv() >= 2){
                    state = true;
                }else{
                    state = false;
                }
                break;
            case 3:
                if(user.getPriv() == 3){
                    state = true;
                }else{
                    state = false;
                }
                break;
        }

        return state;
    }

}
