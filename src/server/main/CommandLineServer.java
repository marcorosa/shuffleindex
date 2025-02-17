package server.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import server.command.CloseServerCommand;
import server.command.ExecuteServerCommand;
import server.command.StartServerCommand;
import server.command.StopServerCommand;
import server.pir.Server;

public class CommandLineServer{

	private static Server server; 
	private static StartServerCommand startServerCommand;
	private static StopServerCommand stopServerCommand;
	private static CloseServerCommand closeServerCommand;
	private static ExecuteServerCommand executeServerCommand;
	
	public static void main(String[] args) {
		
		InputStreamReader inputStreamReader = new InputStreamReader (System.in);
		BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
		server = new Server(null);

		startServerCommand = new StartServerCommand(server);
		stopServerCommand = new StopServerCommand(server);
		closeServerCommand = new CloseServerCommand(server);
        executeServerCommand = new ExecuteServerCommand(startServerCommand, stopServerCommand, closeServerCommand);
		
        String command = "";
        
        do {
        
        	try { command = bufferedReader.readLine(); } catch(Exception e) { e.printStackTrace(); }
        	
        	if(command.equals("start")) { 
        		executeServerCommand.start();
        	} else if(command.equals("stop")) {
        		executeServerCommand.stop();
        	} else if(command.equals("close")) {
        		executeServerCommand.stop();
                executeServerCommand.close();
        	} else {System.out.println("Command not recognized by the server");}
        	
        } while(true);

	}
}