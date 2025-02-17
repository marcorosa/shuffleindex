package client.command;

/**
 * Created by upara on 15/06/2016.
 */
public class ExecuteClientCommand {

    private DatabaseClientCommand databaseClientCommand;
    private CloseClientCommand closeClientCommand;

    public ExecuteClientCommand(CloseClientCommand closeClientCommand, DatabaseClientCommand databaseClientCommand  ) {
        this.databaseClientCommand = databaseClientCommand;
        this.closeClientCommand = closeClientCommand;
    }

    public void create() {databaseClientCommand.execute();}
    public void close() {closeClientCommand.execute();}
}
