/**
 * Title: Force Field X.
 *
 * Description: Force Field X - Software for Molecular Biophysics.
 *
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2017.
 *
 * This file is part of Force Field X.
 *
 * Force Field X is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * Force Field X is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package ffx.potential.utils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Arrays.fill;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ffx.potential.ForceFieldEnergy;
import ffx.potential.MolecularAssembly;
import ffx.potential.PotentialComponent;
import ffx.potential.extended.ExtendedSystem;
import ffx.potential.extended.ExtendedSystem.ExtendedSystemConfig;
import ffx.potential.extended.SBLogger;
import ffx.potential.nonbonded.ParticleMeshEwald.Polarization;
import ffx.potential.nonbonded.ParticleMeshEwald.SCFAlgorithm;
import ffx.potential.nonbonded.ParticleMeshEwaldCart;
import ffx.potential.nonbonded.ParticleMeshEwaldQI;
import ffx.potential.parameters.ForceField.ForceFieldName;

import static ffx.potential.extended.ExtUtils.setProp;
import static ffx.potential.extended.SBLogger.SB;

/**
 * Test ExtendedSystem and its variables to verify that
 *	(1) Analytic derivatives match finite difference approximation to appropriate epsilon.
 *	(2) End state energies are consistent with manually titrated input files.
 *	(3) Lambda paths and gradients are sufficiently smooth.
 * @author slucore
 */
@RunWith(Parameterized.class)
public class ExtendedVariableTest {

    private static final Logger logMaster = Logger.getLogger("ffx");
	private static final PotentialsUtils utils = new PotentialsUtils();
	private static final SBLogger SB = new SBLogger();

	private static final String	  dilysine			= "lys-lys.pdb";
	private static final String[] dilysStates		= {"lyd-lyd.pdb","lyd-lys.pdb",
													   "lys-lyd.pdb","lys-lys.pdb"};
	private static final String	  dilysineCryst		= "lys-lys-cryst.pdb";
	private static final String[] dilysStatesCryst	= {"lyd-lyd-cryst.pdb","lyd-lys-cryst.pdb",
													   "lys-lyd-cryst.pdb","lys-lys-cryst.pdb"};
	private static final boolean yes	= true;
	private static final int lys2_nz	= 16;
	private static final int ala3_c		= 30;
	private static final int lys4_nz	= 48;
	private static final int[] allAtoms = (new IntRange(0,67)).toArray();

	private static boolean		resultsOnly		= false;
	private static final double tolerance		= 1e-6;
	private static final double errorThreshold	= 1e-9;
	private static final String resourcePrefix	= "ffx/potential/structures/";

	private static final boolean includeManualQiEndStates	= false;
	private static final boolean oneSidedFiniteAtExtremes	= false;	// TODO analyze bonded behavior
	private static final boolean smoothnessDecomposition	= false;
	private static final boolean singleThreaded				= false;
	private static final boolean assertions					= false;	// TODO enable
	private static double[] lambdaValuesToTest = {0.0, 0.25, 0.5, 0.75, 1.0};

	/** Arguments to the class constructor.
	 * EsvTest.END_STATES requires two ESVs and that 4x states + energies be defined.
	 * EsvTest.DERIVATIVES can take any system; use derivativeLambdas to control FD points.
	 * EsvTest.SMOOTHNESS is ad-hoc; TODO: use d2U_dL2 from FD to assess smoothness instead.
	 */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
//			{EsvTest.Deriv_OneEsv,	Interactions.All,	CellType.Aperiodic},
			{EsvTest.Deriv_OneEsv,	Interactions.OnePermOneInd,	CellType.Crystal},
//			{EsvTest.Deriv_TwoEsvs,	Interactions.All,	CellType.Aperiodic},
//			{EsvTest.Deriv_TwoEsvs,	Interactions.All,	CellType.Crystal},
//			{EsvTest.Deriv_Meta,	Interactions.All,	CellType.Crystal},
//			{EsvTest.EndStates,		Interactions.All,	CellType.Aperiodic},
			{EsvTest.EndStates,		Interactions.All,	CellType.Crystal},
//			{EsvTest.Smoothness,	Interactions.All,	CellType.Aperiodic},
//			{EsvTest.Smoothness,	Interactions.All,	CellType.Crystal},
		});
	}

	// Setup control of PME lambda derivative calculation.
	private ExtendedSystemConfig derivativeDebugParameters() {
		setProp("rotate-multipoles",		yes);	// global frame
		setProp("esv.allowMaskPerm",		yes);	// permanent energy masking
		setProp("esv.allowMaskPolarD",		yes);	// group-based masking
		setProp("esv.allowMaskPolarP",		yes);	// induced energy masking
		setProp("esv.allowSymOps",			yes);	// symmetry operators
		setProp("esv.allowScreening",		yes);	// SCREENED_COULOMB tensors
		setProp("esv.allowTholeDamping",	yes);	// THOLE_FIELD tensors
		setProp("esv.recipFieldEffects",	yes);	// reciprocal space contributions to field

		setProp("esv.scaleAlpha",			yes);	// polarizability scaling and associated derivative term
		setProp("polarization",		Polarization.MUTUAL);
		setProp("scf-algorithm",	SCFAlgorithm.CG);
		setProp("esv.cdqScales",	Arrays.asList(1.0, 1.0, 1.0));	// scale charges, dipoles, quads

		setProp("use-charges",				yes);
		setProp("use-dipoles",				yes);
		setProp("use-quadrupoles",			yes);

		ExtendedSystemConfig esvConfig = new ExtendedSystemConfig();
		// Alternatively,
		// activateAll();

		switch (interactions) {
			default:
			case All:
				esvConfig.setPermanentWhitelist(null);
				esvConfig.setInducedWhitelist(  null);
				esvConfig.useWhitelists = false;
				break;
			case OnePermOneInd:
				esvConfig.setPermanentWhitelist(new int[]{lys2_nz});
				esvConfig.setInducedWhitelist(	new int[]{ala3_c});
				esvConfig.useWhitelists = true;
				break;
			case TwoPermOneInd:
				esvConfig.setPermanentWhitelist(new int[]{lys2_nz, lys4_nz});
				esvConfig.setInducedWhitelist(	new int[]{ala3_c});
				esvConfig.useWhitelists = true;
				break;
			case OnePermTwoInd:
				esvConfig.setPermanentWhitelist(new int[]{lys2_nz});
				esvConfig.setInducedWhitelist(  new int[]{ala3_c, lys4_nz});
				esvConfig.useWhitelists = true;
				break;
			case OnePermAllInd:
				esvConfig.setPermanentWhitelist(new int[]{lys2_nz});
				esvConfig.setInducedWhitelist(	allAtoms);
				esvConfig.useWhitelists = true;
				break;
			case AllPermOneInd:
				esvConfig.setPermanentWhitelist(allAtoms);
				esvConfig.setInducedWhitelist(	new int[]{ala3_c});
				esvConfig.useWhitelists = true;
				break;
		}
		return esvConfig;
	}

	private static Properties originalSystemConfig;	// properties as they existed before class setup

	private Interactions interactions;
	public enum Interactions {
		All, OnePermOneInd, TwoPermOneInd, OnePermTwoInd, OnePermAllInd, AllPermOneInd;
	}
	private final EsvTest test;
	public enum EsvTest {
		EndStates, Derivatives, Smoothness, All,
		Deriv_OneEsv, Deriv_TwoEsvs, Deriv_Meta;
	}
	public enum CellType {
		Aperiodic, Crystal, TightCrystal;
	}

	private final Level origLevel;
	private final String esvFilename;
	private final String esvResidueIDs;
	private final String[] stateFilenames;
	private final double[] initialLambda;

	public ExtendedVariableTest(EsvTest test, Interactions interactions, CellType cell) {
		origLevel = logMaster.getLevel();
		this.test = test;
		this.interactions = interactions;
		switch (cell) {
			default:
			case Aperiodic:
				esvFilename = dilysine;
				stateFilenames = dilysStates;
				break;
			case Crystal:
				esvFilename = dilysineCryst;
				stateFilenames = dilysStatesCryst;
				break;
			case TightCrystal:
				esvFilename = "lys-lys-tight.pdb";
				stateFilenames = null;
				break;
		}
		switch (test) {
			case Deriv_OneEsv:
			case Deriv_Meta:
				esvResidueIDs = "A2";
				initialLambda = new double[]{1.0};
				break;
			default:
			
			case Deriv_TwoEsvs:
			case EndStates:
			case Smoothness:
			case All:
				esvResidueIDs = "A2,A4";
				initialLambda = new double[]{0.5, 0.5};
				break;
		}
	}

    /**
     * Set the system properties of which we are certain.
	 * This happens once, prior to class construction; the resulting set defines revertSystemProperties().
     */
	@BeforeClass
    public static void setUpClass() {
		// Save passed-in configuration so we can restore it at teardown (and not affect downstream tests).
		originalSystemConfig = (Properties) System.getProperties().clone();

		// Single-line test output without frills.
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
		// Potential Terms
        setProp("forcefield",		ForceFieldName.AMOEBA_PROTEIN_2013);
		setProp(true,	"esvterm", "lambdaterm", "bondterm", "angleterm", "strbendterm", "ureyterm",
					"opbendterm", "torsionterm", "pitorsterm", "tortorterm", "improperterm");
		setProp(false,	"gkterm", "restrainterm", "comrestrainterm", "lambda-torsions");

        // Potential Details
		setProp(true,	"vdwterm", "esv.vanDerWaals", "mpoleterm", "esv.electrostatics", "pme.qi");
		setProp(true,	"recipterm", "polarizeterm");
		setProp("polarization",		Polarization.MUTUAL);
		setProp("scf-algorithm",	SCFAlgorithm.SOR);
		setProp("polar-eps",		1e-12);
        setProp("vdw-cutoff",		10.0);
        setProp("ewald-cutoff",		10.0);
		setProp("pme-order",		8);
		setProp("pme-mesh-density", 2.0);

        // Potential Settings
        setProp("permanent-lambda-alpha",		2.0);
        setProp("permanent-lambda-exponent",	3.0);
        setProp("polarization-lambda-start",	0.0);	// polarize on the whole range [0,1]
        setProp("polarization-lambda-exponent", 0.0);	// polarization not softcored, only prefactored
        setProp("ligand-vapor-elec",			false);	// cancels when reference is solution phase
        setProp("no-ligand-condensed-scf",		false);	// don't need condensed phase polarization
        setProp("intramolecular-softcore",		false);
        setProp("intermolecular-softcore",		false);

        // ESV Settings
		// Include discr+pH biases; include bonded; hook up BG bonded to FG node; exclude BG atoms from potential.
		setProp(true,	"esv.biasTerm", "esv.bonded");
		// Use multipole scaling in all cases; don't allow ESV particle dynamics.
		setProp(false,	"esv.propagation");
		setProp("esv.verbose",				false);
		setProp("esv.allowLambdaSwitch",	false);	// if false, Lswith == L && dLswitch == 1.0
		setProp("esv.nonlinearMultipoles",	false);	// if false, disallow Lswitch for PME specifically
		setProp("esv.cloneXyzIndices",		yes);	// background atoms receive foreground indexes

        // System Settings
		// Fold bonded into one line; print components of PME: {real,recip}*{self,perm,ind}
		if (singleThreaded) setProp("pj.nt", 1);
        setProp(true, "ffe.combineBonded",	"ffe.decomposePme");
	}

    /**
     * Revert props to their state prior to construction.
     */
    @AfterClass
    public static void tearDownClass() {
        System.getProperties().clear();
		System.getProperties().putAll(originalSystemConfig);
	}

    public MolecularAssembly setup(String filename, boolean quietly) {
		if (quietly || resultsOnly) logMaster.setLevel(Level.WARNING);
        MolecularAssembly mola = openResource(filename, quietly);
        // Turn off checks for overlapping atoms, which is expected for lambda=0.
        mola.getPotentialEnergy().getCrystal().setSpecialPositionCutoff(0.0);
		logMaster.setLevel(origLevel);
		return mola;
    }

	public MolecularAssembly setupWithExtended(String filename, boolean quietly, ExtendedSystemConfig esvConfig) {
		if (quietly || resultsOnly) logMaster.setLevel(Level.WARNING);
		setProp("pme.qi", true);
        MolecularAssembly mola = openResource(filename, quietly);
        // Turn off checks for overlapping atoms, which is expected for lambda=0.
        mola.getPotentialEnergy().getCrystal().setSpecialPositionCutoff(0.0);
		// Create extended variables; add to system; hook up to assembly.
        ExtendedSystem esvSystem = (esvConfig != null)
				? new ExtendedSystem(mola, esvConfig) : new ExtendedSystem(mola);
		esvSystem.setConstantPh(7.4);
        esvSystem.populate(esvResidueIDs);
		mola.getPotentialEnergy().attachExtendedSystem(esvSystem);
		ExtendedSystemConfig.print(esvSystem.config);
		logMaster.setLevel(origLevel);
		return mola;
	}

    /**
     * Locates files packed into the uberjar by Maven.
     */
    private MolecularAssembly openResource(String filename, boolean quietly) {
		MolecularAssembly mola;
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			File structure = new File(cl.getResource(resourcePrefix + filename).getPath());
			mola = (quietly) ? utils.openQuietly(structure) : utils.open(structure);
		} catch (RuntimeException ex) {
			File structure = new File("examples//" + filename);
			mola = (quietly) ? utils.openQuietly(structure) : utils.open(structure);
		}
		return mola;
    }
    private MolecularAssembly openResource(String filename) {
        return openResource(filename, false);
    }
    private MolecularAssembly openResourceQuietly(String filename) {
        return openResource(filename, true);
    }

    private void assertEquals(String message, double expected, double actual, double delta) {
		if (assertions) org.junit.Assert.assertEquals(message, expected, actual, delta);
	}

	@org.junit.Test
	public void testLauncher() {
		switch (test) {
			case EndStates:
				testEndStates();
				break;
			case Derivatives:
				testDerivatives();
			case Deriv_OneEsv:
			case Deriv_TwoEsvs:
				derivativeDebugParameters();
				testDerivatives();
				break;
			case Deriv_Meta:
				testDerivativesMeta();
				break;
			case Smoothness:
				testSmoothness();
				break;
			default:
			case All:
				testEndStates();
				testDerivatives();
				testSmoothness();
		}
	}

	public void testDerivativesMeta() {
		setProp("use-charge",				yes);
		setProp("use-dipoles",				yes);
		setProp("use-quadrupoles",			yes);
		resultsOnly = true;
		fill(initialLambda, 1.0);
		lambdaValuesToTest = new double[]{0.0,0.25,0.5,0.75,1.0};

		SB.logf(" DIRECT/CG, --masking, --scaleAlpha");
		setProp("esv.allowMaskPerm",			false);
		setProp("esv.allowMaskPolarD",			false);
		setProp("esv.allowMaskPolarP",			false);
		setProp("polarization",			Polarization.DIRECT);
		setProp("scf-algorithm",		SCFAlgorithm.CG);
		setProp("esv.scaleAlpha",				false);
		testDerivatives(new ExtendedSystemConfig());

		SB.logf(" DIRECT/CG, --masking, ++scaleAlpha");
		setProp("esv.allowMaskPerm",			false);
		setProp("esv.allowMaskPolarD",			false);
		setProp("esv.allowMaskPolarP",			false);
		setProp("polarization",			Polarization.DIRECT);
		setProp("scf-algorithm",		SCFAlgorithm.CG);
		setProp("esv.scaleAlpha",				yes);
		testDerivatives(new ExtendedSystemConfig());

		SB.nlogfn(" DIRECT/CG, ++masking, --scaleAlpha");
		setProp("esv.allowMaskPerm",			yes);
		setProp("esv.allowMaskPolarD",			yes);
		setProp("esv.allowMaskPolarP",			yes);
		setProp("polarization",			Polarization.DIRECT);
		setProp("scf-algorithm",		SCFAlgorithm.CG);
		setProp("esv.scaleAlpha",				false);
		testDerivatives(new ExtendedSystemConfig());

		SB.nlogfn(" MUTUAL/CG, ++masking, ++scaleAlpha");
		setProp("esv.allowMaskPerm",			yes);
		setProp("esv.allowMaskPolarD",			yes);
		setProp("esv.allowMaskPolarP",			yes);
		setProp("polarization",			Polarization.MUTUAL);
		setProp("scf-algorithm",		SCFAlgorithm.CG);
		setProp("esv.scaleAlpha",				yes);
		testDerivatives(new ExtendedSystemConfig());
	}

	public void testDerivatives() {
		testDerivatives(null);
	}

    /**
     * Analytic Derivative vs Finite Difference: VdW,PermReal,PermRecip,Total
     */
    public void testDerivatives(ExtendedSystemConfig esvConfig) {
		if (esvConfig == null) {
			esvConfig = derivativeDebugParameters();
		}
		MolecularAssembly mola = setupWithExtended(esvFilename, false, esvConfig);
		ForceFieldEnergy ffe = mola.getPotentialEnergy();
		ExtendedSystem esvSystem = ffe.getExtendedSystem();
		if (resultsOnly) logMaster.setLevel(Level.WARNING);
        final double step = 1e-6;

		for (int i = 0; i < esvSystem.size(); i++) {
			final String esvName = esvSystem.getEsv(i).getName();
			if (!mola.getCrystal().aperiodic()) {
				SB.logfn(" Finite Diff: %5.5s (Crystal)", esvName);
			} else {
				SB.logfn(" Finite Diff: %5.5s (Aprodc.)", esvName);
			}
			// Reset lambdas.
			for (int k = 0; k < esvSystem.size(); k++) {
				esvSystem.setLambda(k, initialLambda[k]);
			}
			for (int l = 0; l < lambdaValuesToTest.length; l++) {
				final double center, low, high;
				if (oneSidedFiniteAtExtremes) {
					center = lambdaValuesToTest[l];
					low =  (center - step >= 0.0) ? center - step : center;
					high = (center + step <= 1.0) ? center + step : center;
				} else {
					center = (lambdaValuesToTest[l] == 0.0) ? lambdaValuesToTest[l] + step
										: (lambdaValuesToTest[l] == 1.0) ? lambdaValuesToTest[l] - step
										:  lambdaValuesToTest[l];
					low = center - step;
					high = center + step;
				}
				final double width = high - low;

				// Collect numeric derivative components.
				esvSystem.setLambda(i, low);
				ffe.energy(true, false);
				final double vdwLow       = ffe.getEnergyComponent(PotentialComponent.VanDerWaals);
				final double bondedLow    = ffe.getEnergyComponent(PotentialComponent.Bonded);
				final double biasLow      = ffe.getEnergyComponent(PotentialComponent.Bias);
				final double permRealLow  = ffe.getEnergyComponent(PotentialComponent.PermanentRealSpace);
				final double permSelfLow  = ffe.getEnergyComponent(PotentialComponent.PermanentSelf);
				final double permRecipLow = ffe.getEnergyComponent(PotentialComponent.PermanentReciprocal);
				final double indRealLow   = ffe.getEnergyComponent(PotentialComponent.InducedRealSpace);
				final double indSelfLow   = ffe.getEnergyComponent(PotentialComponent.InducedSelf);
				final double indRecipLow  = ffe.getEnergyComponent(PotentialComponent.InducedReciprocal);
				final double totalLow     = ffe.getEnergyComponent(PotentialComponent.ForceFieldEnergy);

				esvSystem.setLambda(i, high);
				ffe.energy(true, false);
				final double vdwHigh      = ffe.getEnergyComponent(PotentialComponent.VanDerWaals);
				final double bondedHigh   = ffe.getEnergyComponent(PotentialComponent.Bonded);
				final double biasHigh     = ffe.getEnergyComponent(PotentialComponent.Bias);
				final double permRealHigh = ffe.getEnergyComponent(PotentialComponent.PermanentRealSpace);
				final double permSelfHigh = ffe.getEnergyComponent(PotentialComponent.PermanentSelf);
				final double permRecipHigh= ffe.getEnergyComponent(PotentialComponent.PermanentReciprocal);
				final double indRealHigh  = ffe.getEnergyComponent(PotentialComponent.InducedRealSpace);
				final double indSelfHigh  = ffe.getEnergyComponent(PotentialComponent.InducedSelf);
				final double indRecipHigh = ffe.getEnergyComponent(PotentialComponent.InducedReciprocal);
				final double totalHigh    = ffe.getEnergyComponent(PotentialComponent.ForceFieldEnergy);

				// Get analytic derivatives from the center.
				esvSystem.setLambda(i, center);
				ffe.energy(true, false);
				final double vdwAna       = esvSystem.getDerivativeComponent(PotentialComponent.VanDerWaals, i);
				final double bondedAna    = esvSystem.getDerivativeComponent(PotentialComponent.Bonded, i);
				final double biasAna      = esvSystem.getDerivativeComponent(PotentialComponent.Bias, i);
				final double permRealAna  = esvSystem.getDerivativeComponent(PotentialComponent.PermanentRealSpace, i);
				final double permSelfAna  = esvSystem.getDerivativeComponent(PotentialComponent.PermanentSelf, i);
				final double permRecipAna = esvSystem.getDerivativeComponent(PotentialComponent.PermanentReciprocal, i);
				final double indRealAna   = esvSystem.getDerivativeComponent(PotentialComponent.InducedRealSpace, i);
				final double indSelfAna   = esvSystem.getDerivativeComponent(PotentialComponent.InducedSelf, i);
				final double indRecipAna  = esvSystem.getDerivativeComponent(PotentialComponent.InducedReciprocal, i);
				final double totalAna     = esvSystem.getDerivativeComponent(PotentialComponent.ForceFieldEnergy, i);

				// Calculate numeric derivatives and error.
				final double vdwNum         = (vdwHigh - vdwLow) / (width);
				final double vdwErr         = (vdwAna - vdwNum);
				final double bondedNum      = (bondedHigh - bondedLow) / (width);
				final double bondedErr      = (bondedAna - bondedNum);
				final double biasNum        = (biasHigh - biasLow) / (width);
				final double biasErr        = (biasAna - biasNum);
				final double permRealNum    = (permRealHigh - permRealLow) / (width);
				final double permRealErr    = (permRealAna - permRealNum);
				final double permSelfNum    = (permSelfHigh - permSelfLow) / (width);
				final double permSelfErr    = (permSelfAna - permSelfNum);
				final double permRecipNum   = (permRecipHigh - permRecipLow) / (width);
				final double permRecipErr   = (permRecipAna - permRecipNum);
				final double indRealNum     = (indRealHigh - indRealLow) / (width);
				final double indRealErr     = (indRealAna - indRealNum);
				final double indSelfNum     = (indSelfHigh - indSelfLow) / (width);
				final double indSelfErr     = (indSelfAna - indSelfNum);
				final double indRecipNum    = (indRecipHigh - indRecipLow) / (width);
				final double indRecipErr    = (indRecipAna - indRecipNum);
				final double totalNum       = (totalHigh - totalLow) / (width);
				final double totalErr       = (totalAna - totalNum);

				SB.logf(" %-28s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s",
						StringUtils.repeat("*", 28),
						"vdw ", "bonded", "bias", "permReal", "permSelf", "permRecip", "indReal", "indSelf", "indRecip", "total");
				SB.nlogf(" %-28s %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g",
						format("Numeric  @L=%s", esvSystem.getLambdaList()),
						vdwNum, bondedNum, biasNum, permRealNum, permSelfNum, permRecipNum, indRealNum, indSelfNum, indRecipNum, totalNum);
				SB.nlogf(" %-28s %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g %+12.6g",
						format("Analytic @L=%s", esvSystem.getLambdaList()),
						vdwAna, bondedAna, biasAna, permRealAna, permSelfAna, permRecipAna, indRealAna, indSelfAna, indRecipAna, totalAna);
				SB.nlogf(" %-28s %12s %12s %12s %12s %12s %12s %12s %12s %12s %12s",
						"Error:", err(vdwAna,vdwNum), err(bondedAna,bondedNum), err(biasAna,biasNum),
						err(permRealAna,permRealNum), err(permSelfAna,permSelfNum), err(permRecipAna,permRecipNum),
						err(indRealAna,indRealNum), err(indSelfAna,indSelfNum), err(indRecipAna,indRecipNum), err(totalAna,totalNum));
				logMaster.setLevel(origLevel);
				SB.print();

				if (assertions) {
					assertEquals("VanDerWaals Deriv Error", 0.0, vdwErr, tolerance);
					assertEquals("Bonded Deriv Error", 0.0, bondedErr, tolerance);
					assertEquals("Bias Deriv Error", 0.0, biasErr, tolerance);
					assertEquals("PermReal Deriv Error", 0.0, permRealErr, tolerance);
					assertEquals("PermSelf Deriv Error", 0.0, permSelfErr, tolerance);
					assertEquals("PermRecip Deriv Error", 0.0, permRecipErr, tolerance);
					assertEquals("IndReal Deriv Error", 0.0, indRealErr, tolerance);
					assertEquals("IndSelf Deriv Error", 0.0, indSelfErr, tolerance);
					assertEquals("IndRecip Deriv Error", 0.0, indRecipErr, tolerance);
					assertEquals("Total Deriv Error", 0.0, totalErr, tolerance);
				}
			}	// lambda loop
		}	// ESV loop
    }	// testDerivatives method

	private static String err(double analytic, double numeric) {
		return err(analytic,numeric,errorThreshold);
	}
	private static String err(double analytic, double numeric, double threshold) {
		double error = analytic - numeric;
		double absError = Math.abs(error);
		double percentError = Math.abs(error) / Math.abs(numeric) * 100;
		return (absError <= threshold && (percentError < 1.0 || numeric == 0.0)) ? "nil"
				: (absError < 1e-4) ? format("%12.1e", error) : format("%12.4f", error);
	}

	private static ExtendedSystemConfig activateAll() {
		setProp("esv.allowMaskPerm",		yes);
		setProp("esv.allowMaskPolarD",		yes);
		setProp("esv.allowMaskPolarP",		yes);

		setProp("polarization",		Polarization.MUTUAL);
		setProp("scf-algorithm",	SCFAlgorithm.CG);
		setProp("esv.scaleAlpha",			yes);	// scale polarizability of titrating H+
		setProp("rotate-multipoles",		yes);	// global frame
		setProp("esv.allowSymOps",			yes);	// symmetry operators
		setProp("esv.allowScreening",		yes);	// SCREENED_COULOMB tensors
		setProp("esv.allowTholeDamping",	yes);	// THOLE_FIELD tensors
		setProp("esv.recipFieldEffects",	yes);	// reciprocal space contributions to field

		setProp("esv.cdqScales",	Arrays.asList(1.0, 1.0, 1.0));
		setProp("esv.verbose",				false);
		setProp("esv.allowLambdaSwitch",	false);
		setProp("esv.nonlinearMultipoles",	false);
		setProp("esv.cloneXyzIndices",		yes);
		setProp("use-charges",				yes);
		setProp("use-dipoles",				yes);
		setProp("use-quadrupoles",			yes);
		return new ExtendedSystemConfig();
	}

    /**
     * Verify that a lys-lys system with two ESVs can exactly reproduce the energy
     * yielded by vanilla energy() calls on mutated PDB files.
     */
    public void testEndStates() {
		ExtendedSystemConfig esvConfig = activateAll();
		MolecularAssembly mola = setupWithExtended(esvFilename, true, esvConfig);
		ForceFieldEnergy ffe = mola.getPotentialEnergy();
		ExtendedSystem esvSystem = ffe.getExtendedSystem();
		if (resultsOnly) logMaster.setLevel(Level.WARNING);
		ParticleMeshEwaldQI esvPme = ffe.getPmeQiNode();
		final double[] totalEsv = new double[4];
		final double[] vdwEsv = new double[4];
		final double[] permEsv = new double[4],
				permRealEsv = new double[4], permSelfEsv = new double[4], permRecipEsv = new double[4];
		final double[] directEsv = new double[4];
		final double[] mutualEsv = new double[4],
				indRealEsv = new double[4], indSelfEsv = new double[4], indRecipEsv = new double[4];
		final String[] esvStateNames = new String[4];
		esvSystem.setLambda('A', 0.0); esvSystem.setLambda('B', 0.0);
		esvStateNames[0] = format("L=%1.0f,%1.0f", esvSystem.getLambda('A'), esvSystem.getLambda('B'));
		esvPme.setPolarization(Polarization.MUTUAL);
		ffe.energy(true, false);
        totalEsv[0] = ffe.getTotalEnergy() - esvSystem.getBiasEnergy();
        vdwEsv[0] = ffe.getVanDerWaalsEnergy();
        permEsv[0] = esvPme.getPermanentEnergy();
//		permRealEsv[0] = esvPme.getPermRealEnergy();
//		permSelfEsv[0] = esvPme.getPermSelfEnergy();
//		permRecipEsv[0] = esvPme.getPermRecipEnergy();
        mutualEsv[0] = esvPme.getPolarizationEnergy();
//		indRealEsv[0] = esvPme.getIndRealEnergy();
//		indSelfEsv[0] = esvPme.getIndSelfEnergy();
//		indRecipEsv[0] = esvPme.getIndRecipEnergy();
		ffe.getPmeNode().setPolarization(Polarization.DIRECT);
		ffe.energy(true, false);
		directEsv[0] = ffe.getPolarizationEnergy();
		ffe.getPmeNode().setPolarization(Polarization.MUTUAL);

        esvSystem.setLambda('A', 0.0); esvSystem.setLambda('B', 1.0);
		esvStateNames[1] = format("L=%1.0f,%1.0f", esvSystem.getLambda('A'), esvSystem.getLambda('B'));
		ffe.energy(true, false);
        totalEsv[1] = ffe.getTotalEnergy() - esvSystem.getBiasEnergy();
        vdwEsv[1] = ffe.getVanDerWaalsEnergy();
        permEsv[1] = esvPme.getPermanentEnergy();
//		permRealEsv[1] = esvPme.getPermRealEnergy();
//		permSelfEsv[1] = esvPme.getPermSelfEnergy();
//		permRecipEsv[1] = esvPme.getPermRecipEnergy();
        mutualEsv[1] = esvPme.getPolarizationEnergy();
//		indRealEsv[1] = esvPme.getIndRealEnergy();
//		indSelfEsv[1] = esvPme.getIndSelfEnergy();
//		indRecipEsv[1] = esvPme.getIndRecipEnergy();
		ffe.getPmeNode().setPolarization(Polarization.DIRECT);
		ffe.energy(true, false);
		directEsv[1] = ffe.getPolarizationEnergy();
		ffe.getPmeNode().setPolarization(Polarization.MUTUAL);

        esvSystem.setLambda('A', 1.0); esvSystem.setLambda('B', 0.0);
		esvStateNames[2] = format("L=%1.0f,%1.0f", esvSystem.getLambda('A'), esvSystem.getLambda('B'));
		ffe.energy(true, false);
        totalEsv[2] = ffe.getTotalEnergy() - esvSystem.getBiasEnergy();
        vdwEsv[2] = ffe.getVanDerWaalsEnergy();
        permEsv[2] = esvPme.getPermanentEnergy();
//		permRealEsv[2] = esvPme.getPermRealEnergy();
//		permSelfEsv[2] = esvPme.getPermSelfEnergy();
//		permRecipEsv[2] = esvPme.getPermRecipEnergy();
        mutualEsv[2] = esvPme.getPolarizationEnergy();
//		indRealEsv[2] = esvPme.getIndRealEnergy();
//		indSelfEsv[2] = esvPme.getIndSelfEnergy();
//		indRecipEsv[2] = esvPme.getIndRecipEnergy();
		ffe.getPmeNode().setPolarization(Polarization.DIRECT);
		ffe.energy(true, false);
		directEsv[2] = ffe.getPolarizationEnergy();
		ffe.getPmeNode().setPolarization(Polarization.MUTUAL);

        esvSystem.setLambda('A', 1.0); esvSystem.setLambda('B', 1.0);
		esvStateNames[3] = format("L=%1.0f,%1.0f", esvSystem.getLambda('A'), esvSystem.getLambda('B'));		ffe.energy(true, false);
        totalEsv[3] = ffe.getTotalEnergy() - esvSystem.getBiasEnergy();
        vdwEsv[3] = ffe.getVanDerWaalsEnergy();
        permEsv[3] = esvPme.getPermanentEnergy();
//		permRealEsv[3] = esvPme.getPermRealEnergy();
//		permSelfEsv[3] = esvPme.getPermSelfEnergy();
//		permRecipEsv[3] = esvPme.getPermRecipEnergy();
        mutualEsv[3] = esvPme.getPolarizationEnergy();
//		indRealEsv[3] = esvPme.getIndRealEnergy();
//		indSelfEsv[3] = esvPme.getIndSelfEnergy();
//		indRecipEsv[3] = esvPme.getIndRecipEnergy();
		ffe.getPmeNode().setPolarization(Polarization.DIRECT);
		ffe.energy(true, false);
		directEsv[3] = ffe.getPolarizationEnergy();
		ffe.getPmeNode().setPolarization(Polarization.MUTUAL);

        /* Open vanilla end states. */
        MolecularAssembly qi, cart;
        ForceFieldEnergy qiPot, cartPot;
		final int numStates = stateFilenames.length;
		final double[] totalQi	= new double[numStates], totalCart	= new double[numStates];
		final double[] vdwQi	= new double[numStates], vdwCart	= new double[numStates];
		final double[] permQi	= new double[numStates], permCart	= new double[numStates];
		final double[] permRealQi = new double[numStates], permRealCart = new double[numStates];
		final double[] permSelfQi = new double[numStates], permSelfCart = new double[numStates];
		final double[] permRecipQi = new double[numStates], permRecipCart = new double[numStates];
		final double[] mutualQi = new double[numStates], mutualCart = new double[numStates];
		final double[] directQi = new double[numStates], directCart = new double[numStates];
		final double[] indRealQi = new double[numStates], indRealCart = new double[numStates];
		final double[] indSelfQi = new double[numStates], indSelfCart = new double[numStates];
		final double[] indRecipQi = new double[numStates], indRecipCart = new double[numStates];

		// Get manual (no ESVs) end state energy components from both vanilla-qi and cartesian PME.
		for (int i = 0; i < stateFilenames.length; i++) {
			String state = stateFilenames[i];
			setProp("pme.qi",true);
			qi = openResource(stateFilenames[i], true);
			qiPot = qi.getPotentialEnergy();
			ParticleMeshEwaldQI qiPme = qiPot.getPmeQiNode();

			qiPot.energy(true, false);
			totalQi[i] = qiPot.getTotalEnergy();
			vdwQi[i] = qiPot.getVanDerWaalsEnergy();
			permQi[i] = qiPme.getPermanentEnergy();
//			permRealQi[i] = qiPme.getPermRealEnergy();
//			permSelfQi[i] = qiPme.getPermSelfEnergy();
//			permRecipQi[i] = qiPme.getPermRecipEnergy();
			mutualQi[i] = qiPme.getPolarizationEnergy();
//			indRealQi[i] = qiPme.getIndRealEnergy();
//			indSelfQi[i] = qiPme.getIndSelfEnergy();
//			indRecipQi[i] = qiPme.getIndRecipEnergy();
			qiPme.setPolarization(Polarization.DIRECT);
			qiPot.energy(true, false);
			directQi[i] = qiPme.getPolarizationEnergy();
			utils.close(qi);

			setProp("pme.qi",false);
			cart = openResource(stateFilenames[i], true);
			cartPot = cart.getPotentialEnergy();
			ParticleMeshEwaldCart cartPme = (ParticleMeshEwaldCart) cartPot.getPmeNode();

			cartPot.energy(true, false);
			totalCart[i] = cartPot.getTotalEnergy();
			vdwCart[i] = cartPot.getVanDerWaalsEnergy();
			permCart[i] = cartPme.getPermanentEnergy();
//			permRealCart[i] = cartPme.getPermRealEnergy();
//			permSelfCart[i] = cartPme.getPermSelfEnergy();
//			permRecipCart[i] = cartPme.getPermRecipEnergy();
			mutualCart[i] = cartPme.getPolarizationEnergy();
//			indRealCart[i] = cartPme.getIndRealEnergy();
//			indSelfCart[i] = cartPme.getIndSelfEnergy();
//			indRecipCart[i] = cartPme.getIndRecipEnergy();
			cartPme.setPolarization(Polarization.DIRECT);
			cartPot.energy(true, false);
			directCart[i] = cartPme.getPolarizationEnergy();
			utils.close(cart);

			if (assertions) {
				assertEquals("Total"+i, totalCart[i], totalEsv[i], tolerance);
				assertEquals("VanDerWaals"+i, vdwCart[i], vdwEsv[i], tolerance);
				assertEquals("Permanent"+i, permCart[i], permEsv[i], tolerance);
				assertEquals("Ind.Direct"+i, directCart[i], directEsv[i], tolerance);
				assertEquals("Ind.Mutual"+i, mutualCart[i], mutualEsv[i], tolerance);
			}
		}

        SB.nlogfn("  Two-site End State Analysis ");
        SB.logfn(" ***************************** ");
		if (includeManualQiEndStates)
			 SB.logfn(" %-27s    %-22s    %-22s    %-22s",
					  "Extended System (1 File, QI)",
					  "Manual (4 Files, QI)",
					  "Manual (4 Files, Cart)",
					  "Error (Cart-ESV)");
		else SB.logfn(" %-27s    %-22s    %-22s",
					  "Extended System (1 File, QI)",
					  "Manual (4 Files, Cart)",
					  "Error (Cart-ESV)");
		double[][] esvResult	= new double[][]{totalEsv, vdwEsv,
												 permEsv, directEsv, mutualEsv};
		double[][] qiResult		= new double[][]{totalQi, vdwQi,
												 permQi, directQi, mutualQi};
		double[][] cartResult	= new double[][]{totalCart, vdwCart,
												 permCart, directCart, mutualCart};
		String[] names			= new String[]{"Total", "VanWaals",
											   "Permanent", "Direct", "Mutual"};
        for (int component = 0; component < names.length; component++) {
			for (int state = 0; state < numStates; state++) {
				String name = (state == 0) ? names[component] : "";
				SB.logf(" %-27s", format("%-9s %-7.7s %10.5f", name, esvStateNames[state], esvResult[component][state]));
				if (includeManualQiEndStates)
					SB.logf("    %-22s", format("%-7.7s   %12.6f", stateFilenames[state], qiResult[component][state]));
				SB.logf("    %-22s", format("%-7.7s   %12.6f", stateFilenames[state], cartResult[component][state]));
				final double error = Math.abs(cartResult[component][state] - esvResult[component][state]);
				final String errorStr = (error < errorThreshold)
						? format("< %.1e", errorThreshold) : format("%+g", error);
				SB.logfn("    %16s", errorStr);
			}
		}
		logMaster.setLevel(origLevel);
		SB.print();
    }

    /**
     * Numerically ensure that the energy and lambda derivatives are smooth all
     * along both ESV coordinates in the dilysine system.
     */
    public void testSmoothness() {
		ExtendedSystemConfig esvConfig = activateAll();
		MolecularAssembly mola = setupWithExtended(esvFilename, true, esvConfig);
		ForceFieldEnergy ffe = mola.getPotentialEnergy();
		ExtendedSystem esvSystem = ffe.getExtendedSystem();
		if (resultsOnly) logMaster.setLevel(Level.WARNING);
        double[][] totalEnergies, totalDerivsA, totalDerivsB;
        double[][] vdwEnergies, vdwDerivsA, vdwDerivsB;
        double[][] permanentEnergies, permanentDerivsA, permanentDerivsB;
        double[][] inducedEnergies, inducedDerivsA, inducedDerivsB;
        totalEnergies = new double[11][11];
        totalDerivsA = new double[11][11];
        totalDerivsB = new double[11][11];
		if (smoothnessDecomposition) {
			vdwEnergies = new double[11][11];
			vdwDerivsA = new double[11][11];
			vdwDerivsB = new double[11][11];
			permanentEnergies = new double[11][11];
			permanentDerivsA = new double[11][11];
			permanentDerivsB = new double[11][11];
			inducedEnergies = new double[11][11];
			inducedDerivsA = new double[11][11];
			inducedDerivsB = new double[11][11];
		}
        for (int idxA = 0; idxA <= 10; idxA++) {
            double evA = idxA / 10.0;
            for (int idxB = 0; idxB <= 10; idxB++) {
                final double evB = idxB / 10.0;
                esvSystem.setLambda('A', evA);
                esvSystem.setLambda('B', evB);
                final double totalEnergy = ffe.energy(true, false);
                totalEnergies[idxA][idxB] = ffe.getEnergyComponent(PotentialComponent.ForceFieldEnergy);
                totalDerivsA[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.ForceFieldEnergy, 0);
                totalDerivsB[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.ForceFieldEnergy, 1);
				if (smoothnessDecomposition) {
					vdwEnergies[idxA][idxB] = ffe.getEnergyComponent(PotentialComponent.VanDerWaals);
					vdwDerivsA[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.VanDerWaals, 0);
					vdwDerivsB[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.VanDerWaals, 1);
					permanentEnergies[idxA][idxB] = ffe.getEnergyComponent(PotentialComponent.Permanent);
					permanentDerivsA[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.Permanent, 0);
					permanentDerivsB[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.Permanent, 1);
					inducedEnergies[idxA][idxB] = ffe.getEnergyComponent(PotentialComponent.Induced);
					inducedDerivsA[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.Induced, 0);
					inducedDerivsB[idxA][idxB] = esvSystem.getDerivativeComponent(PotentialComponent.Induced, 1);
				}
			}
        }

        /* TODO: improve upon the following arbitrary definition of approximate smoothness. */
        final int testRow = 2;
        final double min = Arrays.stream(totalEnergies[testRow]).min().getAsDouble();
        final double max = Arrays.stream(totalEnergies[testRow]).max().getAsDouble();
        final double maxChange = (max - min) * 0.5;
        final int maxDirectionSignChanges = 2;
        int directionSignChanges = 0;
        for (int idxB = 0; idxB < 10; idxB++) {
            final double here = totalEnergies[testRow][idxB];
            final double next = totalEnergies[testRow][idxB+1];
            final double absChange = Math.abs(next - here);
            if (idxB > 0) {
                final double prev = totalEnergies[testRow][idxB-1];
                if (Math.signum(next - here) != Math.signum(here - prev)) {
                    directionSignChanges++;
                }
            }
            if (absChange > maxChange && assertions) {
                org.junit.Assert.fail(format("Failed max_change smoothness criterion: %g > %g. (change:%g->%g, range:{%g,%g})",
                        absChange, maxChange, min, max,
                        totalEnergies[testRow][idxB], totalEnergies[testRow][idxB+1]));
            }
        }
        if (directionSignChanges > maxDirectionSignChanges && assertions) {
            org.junit.Assert.fail(format("Failed direction_changes smoothness criterion: %d > %d.",
                    directionSignChanges, maxDirectionSignChanges));
        }

        SB.nlogfn("  Smoothness Verification: Total ");
        SB.logfn(" ******************************** ");
        printAsTable(totalEnergies, "U_Total", SB);
        printAsTable(totalDerivsA, "dU_dEsvA", SB);
        printAsTable(totalDerivsB, "dU_dEsvB", SB);
        if (smoothnessDecomposition) {
			SB.nlogfn("  Smoothness Verification: VdW ");
			SB.logfn(" ****************************** ");
			printAsTable(vdwEnergies, "vanWaals", SB);
			printAsTable(vdwDerivsA, "dVdw_dA", SB);
			printAsTable(vdwDerivsB, "dVdw_dB", SB);
			SB.force();
			SB.nlogfn("  Smoothness Verification: PermReal ");
			SB.logfn(" *********************************** ");
			printAsTable(permanentEnergies, "permReal", SB);
			printAsTable(permanentDerivsA, "dPRealdA", SB);
			printAsTable(permanentDerivsB, "dPRealdB", SB);
			SB.force();
			SB.nlogfn("  Smoothness Verification: PermRecip ");
			SB.logfn(" ************************************ ");
			printAsTable(inducedEnergies, "permRcp", SB);
			printAsTable(inducedDerivsA, "dPRcp_dA", SB);
			printAsTable(inducedDerivsB, "dPRcp_dB", SB);
		}
		logMaster.setLevel(origLevel);
		SB.print();
	}

    private SBLogger printAsTable(double[][] values, String title, SBLogger sb) {
        if (SB == null) {
            sb = new SBLogger();
        }
        if (title == null) title = "Title?";
        sb.logf(" %8s %8.1f %8.1f %8.1f %8.1f %8s %8s %8s %8.1f %8.1f %8.1f %8.1f",
                title, 0.0, 0.1, 0.2, 0.3, "<   ", " EvA  ", ">   ", 0.7, 0.8, 0.9, 1.0);
        for (int idxA = 0; idxA <= 10; idxA++) {
            double evA = idxA / 10.0;
            switch (idxA) {
                case 4:
                    sb.nlogf(" %8s", " ^ ");
                    break;
                case 5:
                    sb.nlogf(" %8s", "EvB");
                    break;
                case 6:
                    sb.nlogf(" %8s", " v ");
                    break;
                default:
                    sb.nlogf(" %8.1f", evA);
            }
            for (int idxB = 0; idxB <= 10; idxB++) {
                double evB = idxB / 10.0;
                String value = format("%8.4f", values[idxA][idxB]);
                int max = (value.length() < 8) ? value.length() : 8;
                sb.logf(" %8s", value.substring(0,max));
            }
        }
        sb.nl();
        return sb;
    }
}