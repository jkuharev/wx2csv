import org.apache.commons.cli.Option;

import de.mz.jk.jsix.cli.SimpleCLI;

/** wx2csv_old, , Nov 3, 2017 */
/**
 * <h3>{@link Wx2CsvCLI}</h3>
 * @author jkuharev
 * @version Nov 3, 2017 9:57:10 AM
 */
public class Wx2CsvCLI extends SimpleCLI
{
	public Wx2CsvCLI()
	{
		super();
		setHelpHeader(
				"\nExtract database search results from PLGS Ion Accounting XML files \n"
						+ "to a single CSV table.\n\n" );
		setHelpFooter( "\nWx2Csv, build " + Wx2Csv.buildVersion + "\n (c) Dr. Joerg Kuharev" );
	}

	@Override public Option[] getDefaultOptions()
	{
		return new Option[] {
				Option.builder( "o" ).longOpt( "output" ).argName( "csv file" ).desc( "file path to a new CSV file" ).hasArg().required( false ).build(),
				Option.builder( "i" ).longOpt( "input" ).argName( "xml files" ).desc( "file paths to Workflow.xml files" ).hasArgs().required( false ).build(),
				Option.builder( "a" ).longOpt( "append" ).argName( "append mode" ).desc( "append to csv" ).hasArg( false ).required( false ).build(),
				Option.builder( "g" ).longOpt( "gui" ).argName( "graphical mode" ).desc( "show graphical interface" ).hasArg( false ).required( false ).build(),
				Option.builder( "b" ).longOpt( "batch" ).argName( "batch mode" ).desc( "close window after job end" ).hasArg( false ).required( false ).build(),
				getDefaultHelpOption(),
		};
	}

	@Override public String getExecutableJarFileName()
	{
		return "wx2csv";
	}
}
