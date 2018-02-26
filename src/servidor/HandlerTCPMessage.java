/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author dam203
 */
public class HandlerTCPMessage extends Observable implements Runnable {

    private final Socket comunicaCliente;
    byte[] mensaje;
    InputStream flujoLectura;
    DataInputStream flujo;

    OutputStream flujoSalida;
    DataOutputStream flujoDO;

    HandlerTCPMessage(Socket comunicaCliente) {
        this.comunicaCliente = comunicaCliente;
    }

    @Override
    public void run() {
        try {
            flujoLectura = comunicaCliente.getInputStream();
            flujo = new DataInputStream(flujoLectura);
            int i = flujo.readInt();
            mensaje = new byte[i];
            flujo.readFully(mensaje);
            //Se muestra también la fecha/hora de recepción
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date dateRecepcion = new Date();
            
            System.out.println(new String(mensaje)+ " Fecha/hora de recepción: " + dateFormat.format(dateRecepcion));
            this.setChanged();
            notifyObservers(new String(mensaje)+ " - " + dateFormat.format(dateRecepcion));
            this.clearChanged();
            //Simulación de tiempo de procesamiento
            Thread.sleep(5000);
            
            Date dateRespuesta = new Date();
            generarInformacion(dateFormat.format(dateRecepcion), dateFormat.format(dateRespuesta), new String(mensaje));
            flujoSalida = comunicaCliente.getOutputStream();
            flujoDO = new DataOutputStream(flujoSalida);
            String frase = "Recibido!";
            flujoDO.writeInt(frase.length());
            flujoDO.writeBytes(frase);
            comunicaCliente.close();
        } catch (IOException e) {
            System.out.println("Error en las comunicaciones");
        } catch (SecurityException e) {
            System.out.println("Comunicacion no permitida por razones de seguridad");
        } catch (InterruptedException e) {
            System.out.println("Recepción de mensaje interrumpida");
        }
    }
    
    public void generarInformacion(String dateRecepcion, String dateRespuesta, String mensaje){
        Object data[] = {
                mensaje,
                dateRecepcion, 
                comunicaCliente.getInetAddress().getHostAddress(), 
                comunicaCliente.getPort(),
//                comunicaCliente.getInetAddress().getCanonicalHostName(), 
//                comunicaCliente.getInetAddress().getHostName(),
//                comunicaCliente.getLocalAddress().getHostAddress(), 
//                comunicaCliente.getLocalPort(),
//                comunicaCliente.getLocalAddress().getCanonicalHostName(), 
//                comunicaCliente.getLocalAddress().getHostName(), 
                dateRespuesta
        };
        this.setChanged();
        notifyObservers(data);
        this.clearChanged();
    }
}
