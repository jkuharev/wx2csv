import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;

/** wx2csv, , Nov 3, 2017*/
/**
 * <h3>{@link Wx2Csv}</h3>
 * @author jkuharev
 * @version Nov 3, 2017 10:08:09 AM
 */
public class Wx2Csv
{
	public static final String buildVersion = "20180216";

	private List<File> xmlFiles = null;
	private File csvFile = null;
	private boolean guiMode = false;
	private boolean batchMode = false;
	private boolean appendMode = false;

	private Wx2CsvCLI appCLI = new Wx2CsvCLI();

	public static void main(String[] args)
	{
		new Wx2Csv( args );
	}
	
	public Wx2Csv(String[] args)
	{
		initCli( args );
		if (guiMode)
			runGUI();
		else
			runCLI();
	}

	private void runCLI()
	{
		if (csvFile != null && xmlFiles != null)
		{
			try
			{
				PrintStream outStream = new PrintStream( new FileOutputStream( csvFile, appendMode ) );
				Thread job = new Wx2CsvWorkerThread( xmlFiles, outStream, System.out, !appendMode );
				job.start();
				csvFile = null;
				xmlFiles = null;
				job.join();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void runGUI()
	{
		Wx2CsvUI ui = new Wx2CsvUI();
		if (csvFile != null) ui.setCsvFile( csvFile );
		if (xmlFiles != null) ui.doMultiFileAction( ui.filterTargetFiles( xmlFiles ) );
		if (batchMode)
		{
			try
			{
				Thread job = ui.startProcessingThread();
				job.join();
				ui.dispose();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void initCli(String[] args)
	{
		if (args.length < 1) 
		{ 
			guiMode = true;
			return;
		}

		try
		{
			CommandLine cli = appCLI.parseCommandLine( args );
			
			if (cli.hasOption( "a" ))
			{
				appendMode = true;
			}

			if (cli.hasOption( "g" ))
			{
				guiMode = true;
			}

			if (cli.hasOption( "b" ))
			{
				batchMode = true;
			}
			
			if (cli.hasOption( "o" ))
			{
				csvFile = new File( cli.getOptionValue( "o" ) );
			}

			if (cli.hasOption( "i" ))
			{
				String[] iArgs = cli.getOptionValues( "i" );
				xmlFiles = new ArrayList<File>( iArgs.length );
				for ( String i : iArgs )
				{
					xmlFiles.add( new File( i ) );
				}
			}

			if (cli.hasOption( "h" ))
			{
				appCLI.showHelp();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			appCLI.showHelp();
		}
	}
	

}
