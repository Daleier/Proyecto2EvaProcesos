/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author dam203
 */
public class TCPMessage implements Runnable {
    boolean fin;
    public final ServerSocket socketServidor;
    public final ExecutorService pool;
    public final Observer o;
    
    public TCPMessage(int port, int poolSize, Observer o) throws IOException {
        fin = false;
        this.socketServidor = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
	this.o=o;
    }
    
    @Override
    public void run() {
        try {
            do {
		HandlerTCPMessage h= new HandlerTCPMessage(socketServidor.accept());
                h.addObserver(o);
		pool.execute(h);
            } while (!fin);
            socketServidor.close();
        } catch (IOException e) {
            System.out.println("Error en las comunicaciones");
        }
    }
}

