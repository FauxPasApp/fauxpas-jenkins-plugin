package jenkins.plugins.fauxpasapp.commands;

public interface Command
{
    public int execute(BuildContext context) throws Exception;
}
