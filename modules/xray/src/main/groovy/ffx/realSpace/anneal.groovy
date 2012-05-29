// REALSPACE SIMULATED ANNEALING

// Apache Imports
import org.apache.commons.io.FilenameUtils;

// Groovy Imports
import groovy.util.CliBuilder;

// Force Field X Imports
import ffx.algorithms.SimulatedAnnealing;
import ffx.xray.CrystalReciprocalSpace.SolventModel;
import ffx.xray.RealSpaceData;
import ffx.xray.RealSpaceFile;
import ffx.xray.RefinementEnergy;
import ffx.xray.RefinementMinimize.RefinementMode;

// suffix to append to output data
String suffix = "_anneal";

// High temperature starting point.
double high = 1000.0;

// Low temperature end point.
double low = 100.0;

// Number of annealing steps.
int windows = 10;

// Number of molecular dynamics steps at each temperature.
int steps = 1000;

// Reset velocities (ignored if a restart file is given)
boolean initVelocities = true;


// Things below this line normally do not need to be changed.
// ===============================================================================================

// Create the command line parser.
def cli = new CliBuilder(usage:' ffxc realspace.anneal [options] <pdbfilename> [datafilename]');
cli.h(longOpt:'help', 'Print this help message.');
cli.d(longOpt:'data', args:2, valueSeparator:',', argName:'data.map,1.0', 'specify input data filename (or simply provide the datafilename argument after the PDB file) and weight to apply to the data (wA)');
cli.p(longOpt:'polarization', args:1, argName:'mutual', 'polarization model: [none / direct / mutual]');
cli.s(longOpt:'suffix', args:1, argName:'_anneal', 'output suffix');
cli.l(longOpt:'low', args:1, argName:'100.0', 'Low temperature limit in degrees Kelvin.');
cli.t(longOpt:'high', args:1, argName:'1000.0', 'High temperature limit in degrees Kelvin.');
cli.w(longOpt:'windows', args:1, argName:'10', 'Number of annealing windows.');
cli.n(longOpt:'steps', args:1, argName:'1000', 'Number of molecular dynamics steps at each temperature.');
def options = cli.parse(args);
List<String> arguments = options.arguments();
if (options.h || arguments == null || arguments.size() < 1) {
    return cli.usage();
}

// Name of the file (PDB or XYZ).
String modelfilename = arguments.get(0);

// set up real space map data (can be multiple files)
List mapfiles = new ArrayList();
if (arguments.size() > 1) {
    RealSpaceFile realspacefile = new RealSpaceFile(arguments.get(1), 1.0);
    mapfiles.add(realspacefile);
}
if (options.d) {
    for (int i=0; i<options.ds.size(); i+=2) {
	double wA = Double.parseDouble(options.ds[i+1]);
	RealSpaceFile realspacefile = new RealSpaceFile(options.ds[i], wA);
	mapfiles.add(realspacefile);
    }
}

if (options.s) {
    suffix = options.s;
}

if (options.p) {
    System.setProperty("polarization", options.p);
}

if (options.t) {
    high = Double.parseDouble(options.t);
}

if (options.l) {
    low = Double.parseDouble(options.l);
}

if (options.w) {
    windows = Integer.parseInteger(options.w);
}

if (options.n) {
    steps = Integer.parseInteger(options.n);
}

logger.info("\n Running simulated annealing on " + modelfilename);
open(modelfilename);

if (mapfiles.size() == 0) {
    RealSpaceFile realspacefile = new RealSpaceFile(active, 1.0);
    mapfiles.add(realspacefile);
}

RealSpaceData realspacedata = new RealSpaceData(active, active.getProperties(), mapfiles.toArray(new RealSpaceFile[mapfiles.size()]));

energy();

RefinementEnergy refinementEnergy = new RefinementEnergy(realspacedata, RefinementMode.COORDINATES);
SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(active, refinementEnergy, active.getProperties(), refinementEnergy);
simulatedAnnealing.anneal(high, low, windows, steps);
energy();

saveAsPDB(systems, new File(FilenameUtils.removeExtension(modelfilename) + suffix + ".pdb"));