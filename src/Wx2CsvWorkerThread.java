import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import de.mz.jk.jsix.utilities.Bencher;
import de.mz.jk.jsix.utilities.CSVUtils;
import isoquant.kernel.plgs.obj.*;
import isoquant.kernel.plgs.reader.WorkflowReader;


/** wx2csv_old, , Nov 2, 2017*/
/**
 * <h3>{@link Wx2CsvWorkerThread}</h3>
 * @author jkuharev
 * @version Nov 2, 2017 4:04:38 PM
 */
public class Wx2CsvWorkerThread extends Thread
{
	public static int jobID = 0;
	private CSVUtils csv = null;
	private List<File> inputFiles = null;
	private PrintStream log = null;
	private boolean printHeader = false;

	public Wx2CsvWorkerThread(List<File> inFiles, PrintStream outStream, PrintStream logStream, boolean printHeader) throws Exception
	{
		this.log = logStream;
		this.csv = new CSVUtils( outStream );
		this.inputFiles = inFiles;
		this.printHeader = printHeader;
	}

	@Override public void run()
	{
		log.println( "processing " + inputFiles.size() + " files ..." );
		Bencher tAll = new Bencher( true );
		if (printHeader) printCsvHeader();
		for ( File xmlFile : inputFiles )
		{
			log.println( "----------" );
			log.println( "	file:	" + xmlFile.getAbsolutePath() + "" );
			try
			{
				Bencher t = new Bencher( true );
				Workflow w = processSearchResult( xmlFile );
				log.println( "		peptides:	" + w.peptides.size() );
				log.println( "		proteins:	" + w.proteins.size() );
				log.println( "		duration:	" + t.stop().getSecString() );
			}
			catch (Exception e)
			{
				e.printStackTrace( log );
			}
		}
		log.println( "----------" );
		log.println( "processing " + inputFiles.size() + " files ... done! [" + tAll.stop().getSecString() + "]" );
		csv.close();
	}

	private void printCsvHeader()
	{
		csv
				.txtCell( "result file" ).colSep()
				.txtCell( "acquired_name" ).colSep()
				.txtCell( "sample_description" ).colSep()
				.txtCell( "protein index" ).colSep()
				.txtCell( "sequence" ).colSep()
				.txtCell( "type" ).colSep()
				.txtCell( "modifier" ).colSep()
				.txtCell( "mass" ).colSep()
				.txtCell( "retention_time" ).colSep()
				.txtCell( "drift_time" ).colSep()
				.txtCell( "intensity" ).colSep()
				.txtCell( "charge" ).colSep()
				.txtCell( "peptide score" ).colSep()
				.txtCell( "peptide coverage" ).colSep()
				.txtCell( "mass_error" ).colSep()
				.txtCell( "start position" ).colSep()
				.txtCell( "end position" ).colSep()
				.txtCell( "entry" ).colSep()
				.txtCell( "accession" ).colSep()
				.txtCell( "protein length" ).colSep()
				.txtCell( "peptides" ).colSep()
				.txtCell( "products" ).colSep()
				.txtCell( "protein score" ).colSep()
				.txtCell( "protein coverage" )
				.endLine();
	}

	public Workflow processSearchResult(File xmlFile) throws Exception
	{
		Workflow w = WorkflowReader.getWorkflow( xmlFile, true );
		for ( Map.Entry<Integer, Peptide> pe : w.peptides.entrySet() )
		{
			Peptide pep = pe.getValue();
			Protein pro = w.proteins.get( pep.protein_id );
			QueryMass qm = w.queryMasses.get( pep.id );
			LowEnergy le = w.lowEnergies.get( qm.low_energy_id );
			csv
					.txtCell( xmlFile.getName() ).colSep()
					.txtCell( w.acquired_name ).colSep()
					.txtCell( w.sample_description ).colSep()
					.numCell( pe.getKey() ).colSep()
					.txtCell( pep.sequence ).colSep()
					.txtCell( pep.type ).colSep()
					.txtCell( pep.modifier ).colSep()
					.numCell( le.mass ).colSep()
					.numCell( le.retention_time ).colSep()
					.numCell( le.drift_time ).colSep()
					.numCell( qm.intensity ).colSep()
					.numCell( le.charge ).colSep()
					.numCell( pep.score ).colSep()
					.numCell( pep.coverage ).colSep()
					.numCell( pep.mass_error ).colSep()
					.numCell( pep.start ).colSep()
					.numCell( pep.end ).colSep()
					.numCell( pro.entry ).colSep()
					.numCell( pro.accession ).colSep()
					.numCell( pro.sequence.length() ).colSep()
					.numCell( pro.peptides ).colSep()
					.numCell( pro.products ).colSep()
					.numCell( pro.score ).colSep()
					.numCell( pro.coverage )
					.endLine();
		}
		return w;
	}
}
