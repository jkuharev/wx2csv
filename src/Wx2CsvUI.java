import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import de.mz.jk.jsix.libs.XFiles;
import de.mz.jk.jsix.libs.XJava;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI;
import de.mz.jk.jsix.ui.TextWindowDragAndDropUI.FileActionListener;

/** wx2csv_old, , Nov 2, 2017*/
/**
 * <h3>{@link Wx2CsvUI}</h3>
 * @author jkuharev
 * @version Nov 2, 2017 12:05:22 PM
 */
public class Wx2CsvUI implements ActionListener, FileActionListener
{
	private final String actionRun = "run", actionFileChoose = "fc", actionAppendCsv = "append";

	public static void main(String[] args)
	{
		new Wx2CsvUI();
	}

	private static final String welcomeMessage =
			"-------------------------------\n" + 
			"This application converts PLGS Ion Accounting results\n" + 
			"from proprietary XML into human readable CSV format.\n" + 
			"\n" + 
			"Usage:\n" + 
			"	1. select CSV file\n" + 
			"	2. drag and drop Workflow XML files over here\n" + 
			"	3. click 'run'\n" + 
			"	\n" + 
			"Have a nice day!\n" + 
			"(c) Dr. Joerg Kuharev\n" + 
			"-------------------------------";

	private TextWindowDragAndDropUI ui = null;

	private List<File> xmlFiles = null;

	private File defaultCsvFile = new File( "combined_result_file.csv" );
	private File csvFile = null; // defaultCsvFile;
	private JButton runButton = new JButton( "run" );
	private JButton fcButton = new JButton( "choose result file" );
	private JCheckBox appendCheckBox = new JCheckBox( "append csv" );
	private boolean appendMode = false;

	public Wx2CsvUI()
	{
		ui = new TextWindowDragAndDropUI( "PLGS workflow XML to CSV converter", 600, 400, welcomeMessage );
		ui.addFileActionListener( this );
		JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		btnPanel.add( appendCheckBox );
		btnPanel.add( fcButton );
		btnPanel.add( runButton );
		ui.getWin().add( btnPanel, BorderLayout.SOUTH );
		ui.getWin().setVisible( true ); // update GUI

		runButton.setActionCommand( actionRun );
		runButton.addActionListener( this );
		fcButton.setActionCommand( actionFileChoose );
		fcButton.addActionListener( this );
		appendCheckBox.setActionCommand( actionAppendCsv );
		appendCheckBox.addActionListener( this );
	}

	public void setCsvFile(File resultFile)
	{
		System.out.println( "setting result file:\n\t" + resultFile.getAbsolutePath() );
		this.csvFile = resultFile;
	}

	@Override public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		switch (cmd)
		{
			case actionRun:
				startProcessingThread();
				break;
			case actionFileChoose:
				selectOutputFile();
				break;
			case actionAppendCsv:
				appendMode = appendCheckBox.isSelected();
				break;
			default:
				System.out.println( "undefined command" );
				break;
		}
	}

	private void selectOutputFile()
	{
		File file = XFiles.chooseFile( "select result file", true, csvFile, csvFile.getParentFile(), "csv", ui.getWin() );
		if (file == null)
		{
			System.out.println( "File selection aborted." );
		}
		else
		{
			setCsvFile( file );
		}
	}

	public Thread startProcessingThread()
	{
		Thread job = null;
		try
		{
			if (csvFile == null)
			{
				File firstXml = xmlFiles.get( 0 );
				String timeStamp = XJava.timeStamp();
				String baseName = XFiles.getBaseName( firstXml );
				csvFile = new File( firstXml.getParentFile(), baseName + "-" + timeStamp + ".csv" );
			}
			PrintStream outStream = new PrintStream( new FileOutputStream( csvFile, appendMode ) );
			job = new Wx2CsvWorkerThread( xmlFiles, outStream, System.out, !appendMode );
			job.start();
			// csvFile = null;
			xmlFiles = null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return job;
	}

	@Override 
	public List<File> filterTargetFiles(List<File> files)
	{
		List<File> res = new ArrayList<File>();
		for ( File file : files )
		{
			String fn = file.getName().toLowerCase();
			String ext = fn.substring( fn.lastIndexOf( "." ) + 1 );
			
			// System.out.println( "filtering file " + file );
			// System.out.println( "extension: " + ext );

			if (file.exists())
			{
				switch (ext)
				{
					case "xml":
						// System.out.println( "adding to input file list" );
						res.add( file );
						break;
					case "csv":
						setCsvFile( file );
						break;
					default:
						// skip unknown file type
				}
			}
		}
		return res;
	}

	public void dispose()
	{
		if (ui != null) ui.getWin().dispose();
	}

	@Override 
	public void doMultiFileAction(List<File> files)
	{
		xmlFiles = files;
		if (files == null || files.size() < 1) return;
		System.out.println( "--------------------------------------------------------------------------------" );
		System.out.println( "input files:" );
		System.out.println( "\t" + XJava.joinList( files, "\n\t" ) );
		System.out.println( "output file:" );
		System.out.println( "\t" + csvFile.getAbsolutePath() );
		System.out.println( "please click 'run' to start file processing." );
		System.out.println( "--------------------------------------------------------------------------------" );

	}

	@Override 
	public void doSingleFileAction(File file)
	{
		// nothing
	}
}

