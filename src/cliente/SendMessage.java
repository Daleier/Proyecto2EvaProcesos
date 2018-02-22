/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

/**
 *
 * @author dam203
 */
public class SendMessage extends Observable implements Runnable  {

    String ip;
    int puerto;
    String frase;
    Socket socketCliente;

    SendMessage(String ip, int puerto, String frase) {
        this.ip = ip;
        this.puerto = puerto;
        this.frase = frase;
    }

    @Override
    public void run() {
        OutputStream flujoSalida;
        DataOutputStream flujo;
        byte[] mensaje;
        InputStream flujoLectura;
        DataInputStream flujoDI;
        try {
            socketCliente = new Socket(this.ip, this.puerto);
            flujoSalida = socketCliente.getOutputStream();
            flujo = new DataOutputStream(flujoSalida);
            flujo.writeInt(this.frase.length());
            flujo.writeBytes(this.frase);
            this.setChanged();
            this.notifyObservers();
            this.clearChanged();
            //Recepción del mensaje
            flujoLectura = socketCliente.getInputStream();
            flujoDI = new DataInputStream(flujoLectura);
            int i = flujoDI.readInt();
            mensaje = new byte[i];
            flujoDI.readFully(mensaje);
            
            //Mostrar recibido junto la fecha/hora de recepción
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            System.out.println(new String(mensaje)+" Fecha/hora de recepción: "+dateFormat.format(date) + " - 29BPDJ");
            this.setChanged();
            this.notifyObservers(dateFormat.format(date));
            this.clearChanged();
            socketCliente.close();
        } catch (UnknownHostException e) {
            System.out.println("Referencia a host no resuelta");
        } catch (IOException e) {
            System.out.println("Error en las comunicacines");
        } catch (SecurityException e) {
            System.out.println("Comunicacion no permitida");
        }
    }
}

