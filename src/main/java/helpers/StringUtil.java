package helpers;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class StringUtil {
    // Classe com funções para trabalho com strings.

    public static String Cripto(String senha)
    {
        //Criptografa a String passada por parâmetro
        int contador, tamanho,codigoASCII;
        String senhaCriptografada = "";
        tamanho = senha.length();
        contador = 0;

        while(contador <tamanho)
        {
            codigoASCII = senha.charAt(contador)+130;
            senhaCriptografada = senhaCriptografada +(char) codigoASCII;
            contador++;
        }

        return senhaCriptografada;
    }

    public static String Decripto(String senha)
    {
        //Descriptografa a String passada por parâmetro
        int contador, tamanho,codigoASCII;
        String senhaCriptografada = "";
        tamanho = senha.length();
        contador = 0;

        while(contador <tamanho)
        {
            codigoASCII = senha.charAt(contador)-130;
            senhaCriptografada = senhaCriptografada +(char) codigoASCII;
            contador++;
        }

        return senhaCriptografada;
    }
}
