//******************************************************************************
//
// Title:       Force Field X.
// Description: Force Field X - Software for Molecular Biophysics.
// Copyright:   Copyright (c) Michael J. Schnieders 2001-2019.
//
// This file is part of Force Field X.
//
// Force Field X is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License version 3 as published by
// the Free Software Foundation.
//
// Force Field X is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You should have received a copy of the GNU General Public License along with
// Force Field X; if not, write to the Free Software Foundation, Inc., 59 Temple
// Place, Suite 330, Boston, MA 02111-1307 USA
//
// Linking this library statically or dynamically with other modules is making a
// combined work based on this library. Thus, the terms and conditions of the
// GNU General Public License cover the whole combination.
//
// As a special exception, the copyright holders of this library give you
// permission to link this library with independent modules to produce an
// executable, regardless of the license terms of these independent modules, and
// to copy and distribute the resulting executable under terms of your choice,
// provided that you also meet, for each linked independent module, the terms
// and conditions of the license of that module. An independent module is a
// module which is not derived from or based on this library. If you modify this
// library, you may extend this exception to your version of the library, but
// you are not obligated to do so. If you do not wish to do so, delete this
// exception statement from your version.
//
//******************************************************************************
package ffx.potential.nonbonded;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.String.format;
import static java.util.Arrays.fill;

import static org.apache.commons.math3.util.FastMath.PI;
import static org.apache.commons.math3.util.FastMath.max;
import static org.apache.commons.math3.util.FastMath.pow;

import edu.rit.pj.ParallelTeam;

import ffx.crystal.Crystal;
import ffx.numerics.atomic.AtomicDoubleArray;
import ffx.numerics.atomic.AtomicDoubleArray.AtomicDoubleArrayImpl;
import ffx.numerics.atomic.AtomicDoubleArray3D;
import ffx.numerics.atomic.MultiDoubleArray;
import ffx.potential.bonded.Atom;
import ffx.potential.bonded.LambdaInterface;
import ffx.potential.nonbonded.ParticleMeshEwald.Polarization;
import ffx.potential.nonbonded.implicit.BornGradRegion;
import ffx.potential.nonbonded.implicit.BornRadiiRegion;
import ffx.potential.nonbonded.implicit.CavitationRegion;
import ffx.potential.nonbonded.implicit.DispersionRegion;
import ffx.potential.nonbonded.implicit.GKEnergyRegion;
import ffx.potential.nonbonded.implicit.InducedGKFieldRegion;
import ffx.potential.nonbonded.implicit.InitializationRegion;
import ffx.potential.nonbonded.implicit.PermanentGKFieldRegion;
import ffx.potential.nonbonded.implicit.VolumeRegion;
import ffx.potential.parameters.AtomType;
import ffx.potential.parameters.ForceField;
import ffx.potential.parameters.SoluteRadii;
import static ffx.numerics.atomic.AtomicDoubleArray.atomicDoubleArrayFactory;
import static ffx.potential.nonbonded.ParticleMeshEwald.DEFAULT_ELECTRIC;
import static ffx.potential.parameters.ForceField.ForceFieldString.ARRAY_REDUCTION;
import static ffx.potential.parameters.ForceField.toEnumForm;

/**
 * This Generalized Kirkwood class implements GK for the AMOEBA polarizable
 * atomic multipole force field in parallel using a {@link ffx.potential.nonbonded.NeighborList}.
 *
 * @author Michael J. Schnieders<br> derived from:<br> TINKER code by Michael J.
 * Schnieders and Jay W. Ponder<br>
 * @see <a href="http://dx.doi.org/10.1021/ct7001336" target="_blank">M. J.
 * Schnieders and J. W. Ponder, Polarizable atomic multipole solutes in a
 * generalized Kirkwood continuum, Journal of Chemical Theory and Computation
 * 2007, 3, (6), 2083-2097.</a><br>
 */
public class GeneralizedKirkwood implements LambdaInterface {

    private static final Logger logger = Logger.getLogger(GeneralizedKirkwood.class.getName());

    public enum NonPolar {
        CAV, CAV_DISP, GAUSS_DISP, HYDROPHOBIC_PMF, BORN_CAV_DISP, BORN_SOLV, NONE
    }

    /**
     * Permittivity of water at STP.
     */
    public static final double dWater = 78.3;
    /**
     * Default Bondi scale factor.
     */
    private static final double DEFAULT_BONDI_SCALE = 1.03;
    /**
     * Default overlap scale factor for the Hawkins, Cramer & Truhlar pairwise descreening algorithm.
     */
    private static final double DEFAULT_OVERLAP_SCALE = 0.69;
    /**
     * Default surface tension for apolar models without an explicit dispersion
     * term. This is lower than CAVDISP, since the favorable dispersion term is
     * implicitly included.
     */
    private static final double DEFAULT_CAV_SURFACE_TENSION = 0.0049;
    /**
     * Default surface tension for apolar models with an explicit dispersion term.
     * <p>
     * Experimental value: 0.103 kcal/mol/Ang^2
     * <p>
     * Originally set to 0.08 kcal/mol/Ang^2 since there will always be a slight curve in water
     * with a solute (ie: water will never be perfectly flat like it would be
     * during surface tension experiments with pure water)
     */
    private static final double DEFAULT_CAVDISP_SURFACE_TENSION = 0.103;
    /**
     * Default solvent pressure for apolar models with an explicit volume term.
     * <p>
     * Original value of 0.0327 kcal/mol/A^3 is based on using rigorous solvent
     * accessible volumes.
     * <p>
     * For use with GaussVol volumes (i.e. a vdW volume), a larger solvent pressure of 0.1266 is needed.
     */
    private static final double DEFAULT_SOLVENT_PRESSURE = 0.11337;
    /**
     * Default probe radius for use with Gaussian Volumes.
     */
    private static final double DEFAULT_GAUSSVOL_PROBE = 0.0;
    /**
     * Default dielectric offset
     **/
    private static final double DEFAULT_DIELECTRIC_OFFSET = 0.09;

    /**
     * Empirical scaling of the Bondi radii.
     */
    private double bondiScale;
    /**
     * Cavitation surface tension coefficient (kcal/mol/A^2).
     */
    private final double surfaceTension;
    /**
     * Cavitation solvent pressure coefficient (kcal/mol/A^3).
     */
    private final double solventPressue;
    /**
     * The requested permittivity.
     */
    private double epsilon;
    /**
     * Conversion from electron**2/Ang to kcal/mole.
     */
    public double electric;
    /**
     * Water probe radius.
     */
    public final double probe;
    /**
     * Dielectric offset from:
     * <p>
     * W. C. Still, A. Tempczyk, R. C. Hawley and T. Hendrickson, "A Semianalytical Treatment of Solvation for Molecular
     * Mechanics and Dynamics", J. Amer. Chem. Soc., 112, 6127-6129 (1990)
     */
    private final double dOffset = DEFAULT_DIELECTRIC_OFFSET;
    /**
     * Force field in use.
     */
    private final ForceField forceField;
    /**
     * Treatment of polarization.
     */
    private final Polarization polarization;
    /**
     * Treatment of non-polar interactions.
     */
    private final NonPolar nonPolar;
    /**
     * Array of Atoms being considered.
     */
    private Atom[] atoms;
    /**
     * Number of atoms.
     */
    private int nAtoms;
    /**
     * Cartesian coordinates of each atom.
     */
    private double[] x, y, z;
    /**
     * Base radius of each atom.
     */
    private double[] baseRadius;
    /**
     * Overlap scale factor for each atom, when using the Hawkins, Cramer & Truhlar pairwise descreening algorithm.
     * <p>
     * G. D. Hawkins, C. J. Cramer and D. G. Truhlar, "Parametrized Models of Aqueous Free Energies of Solvation Based on Pairwise
     * Descreening of Solute Atomic Charges from a Dielectric Medium", J. Phys. Chem., 100, 19824-19839 (1996).
     */
    private double[] overlapScale;
    /**
     * Over-ride the overlap scale factor for hydrogen atoms.
     */
    private final double heavyAtomOverlapScale;
    /**
     * Over-ride the overlap scale factor for hydrogen atoms.
     */
    private final double hydrogenOverlapScale;
    /**
     * Born radius of each atom.
     */
    private double[] born;
    /**
     * Flag to indicate if an atom should be included.
     */
    private boolean[] use = null;
    /**
     * Periodic boundary conditions and symmetry.
     */
    private Crystal crystal;
    /**
     * Particle mesh Ewald instance, which contains variables such as expanded coordinates and multipoles
     * in the global frame that GK uses.
     */
    private final ParticleMeshEwald particleMeshEwald;
    /**
     * Atomic coordinates for each symmetry operator.
     */
    private double[][][] sXYZ;
    /**
     * Multipole moments for each symmetry operator.
     */
    private double[][][] globalMultipole;
    /**
     * Induced dipoles for each symmetry operator.
     */
    private double[][][] inducedDipole;
    /**
     * Induced dipole chain rule terms for each symmetry operator.
     */
    private double[][][] inducedDipoleCR;

    /**
     * AtomicDoubleArray implementation to use.
     */
    private AtomicDoubleArrayImpl atomicDoubleArrayImpl;
    /**
     * Atomic Gradient array.
     */
    private AtomicDoubleArray3D grad;
    /**
     * Atomic Torque array.
     */
    private AtomicDoubleArray3D torque;
    /**
     * Atomic Born radii gradient array.
     */
    private AtomicDoubleArray sharedBornGrad;
    /**
     * Atomic GK field array.
     */
    public AtomicDoubleArray3D sharedGKField;
    /**
     * Atomic GK field chain-rule array.
     */
    public AtomicDoubleArray3D sharedGKFieldCR;

    /**
     * Neighbor lists for each atom and symmetry operator.
     */
    private int[][][] neighborLists;
    /**
     * This field is because re-initializing the force field resizes some arrays
     * but not others; that second category must, when called on, be resized not
     * to the current number of atoms but to the maximum number of atoms (and
     * thus to the size of the first category of arrays).
     */
    private int maxNumAtoms;
    /**
     * Parallel team object for shared memory parallelization.
     */
    private final ParallelTeam parallelTeam;
    /**
     * Initialize GK output variables.
     */
    private final InitializationRegion initializationRegion;
    /**
     * Parallel computation of Born Radii.
     */
    private final BornRadiiRegion bornRadiiRegion;
    /**
     * Parallel computation of the Permanent GK Field.
     */
    private final PermanentGKFieldRegion permanentGKFieldRegion;
    /**
     * Parallel computation of the Induced GK Field.
     */
    private final InducedGKFieldRegion inducedGKFieldRegion;
    /**
     * Parallel computation of the GK continuum electrostatics energy.
     */
    private final GKEnergyRegion gkEnergyRegion;
    /**
     * Parallel computation of Born radii chain rule term.
     */
    private final BornGradRegion bornGradRegion;
    /**
     * Parallel computation of Dispersion energy.
     */
    private final DispersionRegion dispersionRegion;
    /**
     * Parallel computation of Cavitation.
     */
    private final CavitationRegion cavitationRegion;
    /**
     * Parallel computation of Volume.
     */
    private final VolumeRegion volumeRegion;
    /**
     * Gaussian Based Volume and Surface Area
     */
    private final GaussVol gaussVol;

    /**
     * GK cut-off distance.
     */
    private double cutoff;
    /**
     * GK cut-off distance squared.
     */
    private double cut2;
    /**
     * Boolean flag to indicate GK will be scaled by the lambda state variable.
     */
    private boolean lambdaTerm;
    /**
     * The current value of the lambda state variable.
     */
    private double lambda = 1.0;
    /**
     * lPow equals lambda^polarizationLambdaExponent, where polarizationLambdaExponent also used by PME.
     */
    private double lPow = 1.0;
    /**
     * First derivative of lPow with respect to l.
     */
    private double dlPow = 0.0;
    /**
     * Second derivative of lPow with respect to l.
     */
    private double dl2Pow = 0.0;
    /**
     * Electrostatic Solvation Energy.
     */
    private double solvationEnergy = 0.0;
    /**
     * Dispersion Solvation Energy.
     */
    private double dispersionEnergy = 0.0;
    /**
     * Cavitation Solvation Energy.
     */
    private double cavitationEnergy = 0.0;
    /**
     * Time to compute GK electrostatics.
     */
    private long gkTime = 0;
    /**
     * Time to compute Dispersion energy.
     */
    private long dispersionTime = 0;
    /**
     * Time to compute Cavitation energy.
     */
    private long cavitationTime = 0;
    /**
     * If true, prevents Born radii from updating.
     */
    private boolean fixedRadii = false;
    /**
     * Forces all atoms to be considered during Born radius updates.
     */
    private boolean nativeEnvironmentApproximation;
    /**
     * Maps radii overrides (by AtomType) specified from the command line. e.g.
     * -DradiiOverride=134r1.20,135r1.20 sets atom types 134,135 to Bondi=1.20
     */
    private final HashMap<Integer, Double> radiiOverride = new HashMap<>();
    /**
     * Flag to indicate calculation of molecular volume.
     */
    private boolean doVolume;

    /**
     * <p>
     * Constructor for GeneralizedKirkwood.</p>
     *
     * @param forceField        a {@link ffx.potential.parameters.ForceField} object.
     * @param atoms             an array of {@link ffx.potential.bonded.Atom} objects.
     * @param particleMeshEwald a {@link ffx.potential.nonbonded.ParticleMeshEwald} object.
     * @param crystal           a {@link ffx.crystal.Crystal} object.
     * @param parallelTeam      a {@link edu.rit.pj.ParallelTeam} object.
     */
    public GeneralizedKirkwood(ForceField forceField, Atom[] atoms,
                               ParticleMeshEwald particleMeshEwald, Crystal crystal,
                               ParallelTeam parallelTeam) {

        this.forceField = forceField;
        this.atoms = atoms;
        this.particleMeshEwald = particleMeshEwald;
        this.crystal = crystal;
        this.parallelTeam = parallelTeam;
        nAtoms = atoms.length;
        maxNumAtoms = nAtoms;
        polarization = particleMeshEwald.polarization;

        // Set the conversion from electron**2/Ang to kcal/mole
        electric = forceField.getDouble(ForceField.ForceFieldDouble.ELECTRIC, DEFAULT_ELECTRIC);

        // Set the Kirkwood multipolar reaction field constants.
        epsilon = forceField.getDouble(ForceField.ForceFieldDouble.GK_EPSILON, dWater);

        // Define how force arrays will be accumulated.
        String value = forceField.getString(ARRAY_REDUCTION, "MULTI");
        try {
            atomicDoubleArrayImpl = AtomicDoubleArrayImpl.valueOf(toEnumForm(value));
        } catch (Exception e) {
            atomicDoubleArrayImpl = AtomicDoubleArrayImpl.MULTI;
            logger.info(format(" Unrecognized ARRAY-REDUCTION %s; defaulting to %s", value, atomicDoubleArrayImpl));
        }

        // Define default Bondi scale factor, and HCT overlap scale factors.
        bondiScale = forceField.getDouble(ForceField.ForceFieldDouble.GK_BONDIOVERRIDE, DEFAULT_BONDI_SCALE);
        heavyAtomOverlapScale = forceField.getDouble(ForceField.ForceFieldDouble.GK_OVERLAPSCALE, DEFAULT_OVERLAP_SCALE);
        hydrogenOverlapScale = forceField.getDouble(ForceField.ForceFieldDouble.GK_HYDROGEN_OVERLAPSCALE, heavyAtomOverlapScale);

        // Process any radii override values.
        String radiiProp = forceField.getString(ForceField.ForceFieldString.GK_RADIIOVERRIDE, null);
        if (radiiProp != null) {
            String[] tokens = radiiProp.split("A");
            for (String token : tokens) {
                if (!token.contains("r")) {
                    logger.severe("Invalid radius override.");
                }
                int separator = token.indexOf("r");
                int type = Integer.parseInt(token.substring(0, separator));
                double factor = Double.parseDouble(token.substring(separator + 1));
                logger.info(format(" (GK) Scaling AtomType %d with bondi factor %.2f", type, factor));
                radiiOverride.put(type, factor);
            }
        }

        NonPolar nonpolarModel;
        try {
            String cavModel = forceField.getString(ForceField.ForceFieldString.CAVMODEL, "CAV_DISP").toUpperCase();
            nonpolarModel = getNonPolarModel(cavModel);
        } catch (Exception ex) {
            nonpolarModel = NonPolar.NONE;
            logger.warning(format(" Error parsing non-polar model (set to NONE) %s", ex.toString()));
        }
        nonPolar = nonpolarModel;

        int threadCount = parallelTeam.getThreadCount();
        sharedGKField = new AtomicDoubleArray3D(atomicDoubleArrayImpl, nAtoms, threadCount);
        sharedGKFieldCR = new AtomicDoubleArray3D(atomicDoubleArrayImpl, nAtoms, threadCount);

        nativeEnvironmentApproximation = forceField.getBoolean(
                ForceField.ForceFieldBoolean.NATIVE_ENVIRONMENT_APPROXIMATION, false);
        double tempProbe = forceField.getDouble(ForceField.ForceFieldDouble.PROBE_RADIUS, 1.4);
        cutoff = forceField.getDouble(ForceField.ForceFieldDouble.GK_CUTOFF, particleMeshEwald.getEwaldCutoff());
        cut2 = cutoff * cutoff;
        lambdaTerm = forceField.getBoolean(ForceField.ForceFieldBoolean.GK_LAMBDATERM,
                forceField.getBoolean(ForceField.ForceFieldBoolean.LAMBDATERM, false));


        /*
          If polarization lambda exponent is set to 0.0, then we're running
          Dual-Topology and the GK energy will be scaled with the overall system lambda value.
         */
        double polLambdaExp = forceField.getDouble(ForceField.ForceFieldDouble.POLARIZATION_LAMBDA_EXPONENT, 3.0);
        if (polLambdaExp == 0.0) {
            lambdaTerm = false;
            logger.info(" GK lambda term set to false.");
        }

        // If PME includes polarization and is a function of lambda, GK must also.
        if (!lambdaTerm && particleMeshEwald.getPolarizationType() != Polarization.NONE) {
            if (forceField.getBoolean(ForceField.ForceFieldBoolean.ELEC_LAMBDATERM,
                    forceField.getBoolean(ForceField.ForceFieldBoolean.LAMBDATERM, false))) {
                logger.info(" If PME includes polarization and is a function of lambda, GK must also.");
                lambdaTerm = true;
            }
        }

        initAtomArrays();

        doVolume = forceField.getBoolean(ForceField.ForceFieldBoolean.VOLUME, false);

        double tensionDefault;
        switch (nonPolar) {
            case CAV:
                probe = tempProbe;
                tensionDefault = DEFAULT_CAV_SURFACE_TENSION;
                cavitationRegion = new CavitationRegion(atoms, x, y, z, use, neighborLists,
                        grad, threadCount, probe, tensionDefault);
                volumeRegion = new VolumeRegion(atoms, x, y, z, tensionDefault, threadCount);
                dispersionRegion = null;
                gaussVol = null;
                break;
            case CAV_DISP:
                probe = tempProbe;
                tensionDefault = DEFAULT_CAVDISP_SURFACE_TENSION;
                cavitationRegion = new CavitationRegion(atoms, x, y, z, use, neighborLists,
                        grad, threadCount, probe, tensionDefault);
                volumeRegion = new VolumeRegion(atoms, x, y, z, tensionDefault, threadCount);
                dispersionRegion = new DispersionRegion(threadCount, atoms);
                gaussVol = null;
                break;
            case GAUSS_DISP:
                dispersionRegion = new DispersionRegion(threadCount, atoms);
                cavitationRegion = null;
                volumeRegion = null;
                gaussVol = new GaussVol(nAtoms, null);
                tensionDefault = DEFAULT_CAVDISP_SURFACE_TENSION;

                boolean[] isHydrogen = new boolean[nAtoms];
                double[] radii = new double[nAtoms];
                double[] volume = new double[nAtoms];
                double[] gamma = new double[nAtoms];

                double fourThirdsPI = 4.0 / 3.0 * PI;
                double rminToSigma = 1.0 / pow(2.0, 1.0 / 6.0);

                probe = forceField.getDouble(ForceField.ForceFieldDouble.PROBE_RADIUS, DEFAULT_GAUSSVOL_PROBE);
                int index = 0;
                double hydrogenFactor = 0.035;
                int nCarbons = 0;

                // Count number of carbons
                for (Atom atom : atoms) {
                    if (atom.getAtomicNumber() == 12) {
                        nCarbons += 1;
                    }
                }

                for (Atom atom : atoms) {
                    isHydrogen[index] = atom.isHydrogen();
                    radii[index] = atom.getVDWType().radius / 2.0; //* rminToSigma;
                    if (atom.getNumberOfBondedHydrogen() == 3) {
                        hydrogenFactor = 0.036 - (0.001 * nCarbons);
                    } else if (atom.getNumberOfBondedHydrogen() <= 2) {
                        hydrogenFactor = 0.00;
                    }
                    //System.out.println("Hydrogen Factor: "+hydrogenFactor);
                    //radii[index] += probe + hydrogenFactor * atom.getNumberOfBondedHydrogen();
                    radii[index] += probe;
                    volume[index] = fourThirdsPI * pow(radii[index], 3);
                    gamma[index] = 1.0;
                    index++;
                }

                try {
                    gaussVol.setGammas(gamma);
                    gaussVol.setRadii(radii);
                    gaussVol.setVolumes(volume);
                    gaussVol.setIsHydrogen(isHydrogen);
                } catch (Exception e) {
                    logger.severe(" Exception creating GaussVol: " + e.toString());
                }

                break;
            case BORN_CAV_DISP:
                probe = tempProbe;
                tensionDefault = DEFAULT_CAVDISP_SURFACE_TENSION;
                cavitationRegion = null;
                volumeRegion = null;
                gaussVol = null;
                dispersionRegion = new DispersionRegion(threadCount, atoms);
                break;
            case HYDROPHOBIC_PMF:
            case BORN_SOLV:
            case NONE:
            default:
                probe = tempProbe;
                tensionDefault = DEFAULT_CAV_SURFACE_TENSION;
                cavitationRegion = null;
                volumeRegion = null;
                dispersionRegion = null;
                gaussVol = null;
                break;
        }

        surfaceTension = forceField.getDouble(ForceField.ForceFieldDouble.SURFACE_TENSION, tensionDefault);
        solventPressue = forceField.getDouble(ForceField.ForceFieldDouble.SOLVENT_PRESSURE, DEFAULT_SOLVENT_PRESSURE);

        if (gaussVol != null) {
            gaussVol.setSurfaceTension(surfaceTension);
            gaussVol.setSolventPressure(solventPressue);
        }

        initializationRegion = new InitializationRegion(threadCount);
        bornRadiiRegion = new BornRadiiRegion(threadCount);
        permanentGKFieldRegion = new PermanentGKFieldRegion(threadCount, forceField);
        inducedGKFieldRegion = new InducedGKFieldRegion(threadCount, forceField);
        bornGradRegion = new BornGradRegion(threadCount);
        gkEnergyRegion = new GKEnergyRegion(threadCount, forceField, polarization, nonPolar, surfaceTension, probe);

        logger.info("  Continuum Solvation ");
        logger.info(format("   Generalized Kirkwood Cut-Off:       %8.3f (A)", cutoff));
        logger.info(format("   Solvent Dielectric:                 %8.3f", epsilon));
        SoluteRadii.logRadiiSource(forceField);
        logger.info(format("   Non-Polar Model:                  %10s",
                nonPolar.toString().replace('_', '-')));

        if (cavitationRegion != null) {
            logger.info(format("   Cavitation Probe Radius:            %8.3f (A)", probe));
            logger.info(format("   Cavitation Surface Tension:         %8.3f (Kcal/mol/A^2)", surfaceTension));
        } else if (gaussVol != null) {
            logger.info(format("   Cavitation Probe Radius:            %8.3f (A)", probe));
            logger.info(format("   Cavitation Solvent Pressure:        %8.3f (Kcal/mol/A^3)", solventPressue));
            logger.info(format("   Cavitation Surface Tension:         %8.3f (Kcal/mol/A^2)", surfaceTension));
        }

        // Print out all Base Radii
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("   GK Base Radii");
            for (int i = 0; i < nAtoms; i++) {
                logger.info(format("   %s %8.6f %5.3f", atoms[i].toString(), baseRadius[i], overlapScale[i]));
            }
        }

    }

    public void init() {
        initializationRegion.init(atoms, lambdaTerm, grad, torque, sharedBornGrad);
        initializationRegion.executeWith(parallelTeam);
    }

    public AtomicDoubleArray3D getGrad() {
        return grad;
    }

    public AtomicDoubleArray3D getTorque() {
        return torque;
    }

    public void reduce(AtomicDoubleArray3D g, AtomicDoubleArray3D t,
                       AtomicDoubleArray3D lg, AtomicDoubleArray3D lt) {
        grad.reduce(parallelTeam, 0, nAtoms - 1);
        torque.reduce(parallelTeam, 0, nAtoms - 1);
        for (int i = 0; i < nAtoms; i++) {
            g.add(0, i, lPow * grad.getX(i), lPow * grad.getY(i), lPow * grad.getZ(i));
            t.add(0, i, lPow * torque.getX(i), lPow * torque.getY(i), lPow * torque.getZ(i));
            if (lambdaTerm) {
                lg.add(0, i, dlPow * grad.getX(i), dlPow * grad.getY(i), dlPow * grad.getZ(i));
                lt.add(0, i, dlPow * torque.getX(i), dlPow * torque.getY(i), dlPow * torque.getZ(i));
            }
        }
    }

    /**
     * <p>Getter for the field <code>overlapScale</code>.</p>
     *
     * @return an array of {@link double} objects.
     */
    public double[] getOverlapScale() {
        return overlapScale;
    }

    /**
     * <p>getBaseRadii.</p>
     *
     * @return an array of {@link double} objects.
     */
    public double[] getBaseRadii() {
        return baseRadius;
    }

    /**
     * <p>Getter for the field <code>surfaceTension</code>.</p>
     *
     * @return a double.
     */
    public double getSurfaceTension() {
        return surfaceTension;
    }

    /**
     * <p>getNonPolarModel.</p>
     *
     * @return a {@link ffx.potential.nonbonded.GeneralizedKirkwood.NonPolar} object.
     */
    public NonPolar getNonPolarModel() {
        return nonPolar;
    }

    /**
     * Returns the dielectric offset (in Angstroms).
     *
     * @return Currently: 0.09 Angstroms.
     */
    public double getDielecOffset() {
        return dOffset;
    }

    /**
     * Returns the solvent relative permittivity (typically 78.3).
     *
     * @return Relative permittivity of solvent.
     */
    public double getSolventPermittivity() {
        return epsilon;
    }

    /**
     * Returns the probe radius (typically 1.4 Angstroms).
     *
     * @return Radius of the solvent probe.
     */
    public double getProbeRadius() {
        return probe;
    }

    /**
     * <p>Setter for the field <code>cutoff</code>.</p>
     *
     * @param cutoff a double.
     */
    public void setCutoff(double cutoff) {
        this.cutoff = cutoff;
        this.cut2 = cutoff * cutoff;
    }

    /**
     * <p>Getter for the field <code>cutoff</code>.</p>
     *
     * @return a double.
     */
    public double getCutoff() {
        return cutoff;
    }

    /**
     * <p>Setter for the field <code>crystal</code>.</p>
     *
     * @param crystal a {@link ffx.crystal.Crystal} object.
     */
    public void setCrystal(Crystal crystal) {
        this.crystal = crystal;
    }

    /**
     * <p>setNeighborList.</p>
     *
     * @param neighbors an array of {@link int} objects.
     */
    public void setNeighborList(int[][][] neighbors) {
        this.neighborLists = neighbors;
    }

    /**
     * <p>Setter for the field <code>atoms</code>.</p>
     *
     * @param atoms an array of {@link ffx.potential.bonded.Atom} objects.
     */
    public void setAtoms(Atom[] atoms) {
        this.atoms = atoms;
        nAtoms = atoms.length;
        maxNumAtoms = max(nAtoms, maxNumAtoms);
        initAtomArrays();
    }

    /**
     * <p>
     * Checks whether GK uses the Native Environment Approximation.
     * </p>
     * <p>
     * This (previously known as born-use-all) is useful for rotamer
     * optimization under continuum solvent. If a large number of sidechain
     * atoms are completely removed from the GK/GB calculation, the remaining
     * sidechains are overly solvated. The NEA says "we will keep all sidechains
     * not under optimization in some default state and let them contribute to
     * Born radii calculations, but still exclude their solvation energy
     * components."
     * </p>
     *
     * @return Whether the NEA is in use.
     */
    public boolean getNativeEnvironmentApproximation() {
        return nativeEnvironmentApproximation;
    }

    private void initAtomArrays() {
        if (fixedRadii) {
            fixedRadii = false;
        }

        sXYZ = particleMeshEwald.coordinates;

        x = sXYZ[0][0];
        y = sXYZ[0][1];
        z = sXYZ[0][2];

        globalMultipole = particleMeshEwald.globalMultipole;
        inducedDipole = particleMeshEwald.inducedDipole;
        inducedDipoleCR = particleMeshEwald.inducedDipoleCR;
        neighborLists = particleMeshEwald.neighborLists;

        if (grad == null) {
            int threadCount = parallelTeam.getThreadCount();
            grad = new AtomicDoubleArray3D(atomicDoubleArrayImpl, nAtoms, threadCount);
            torque = new AtomicDoubleArray3D(atomicDoubleArrayImpl, nAtoms, threadCount);
            sharedBornGrad = atomicDoubleArrayFactory(atomicDoubleArrayImpl, threadCount, nAtoms);
        } else {
            grad.alloc(nAtoms);
            torque.alloc(nAtoms);
            sharedBornGrad.alloc(nAtoms);
        }

        sharedGKField.alloc(nAtoms);
        sharedGKFieldCR.alloc(nAtoms);

        if (baseRadius == null || baseRadius.length < nAtoms) {
            baseRadius = new double[nAtoms];
            overlapScale = new double[nAtoms];
            born = new double[nAtoms];
            use = new boolean[nAtoms];
        }

        fill(use, true);

        bondiScale = SoluteRadii.applyGKRadii(forceField, bondiScale, atoms, baseRadius);

        // Set up HCT overlap scale factors and any requested radii overrides.
        for (int i = 0; i < nAtoms; i++) {
            overlapScale[i] = heavyAtomOverlapScale;
            int atomicNumber = atoms[i].getAtomicNumber();
            if (atomicNumber == 1) {
                overlapScale[i] = hydrogenOverlapScale;
            }

            AtomType atomType = atoms[i].getAtomType();
            if (radiiOverride.containsKey(atomType.type)) {
                double override = radiiOverride.get(atomType.type);
                // Remove default bondiFactor, and apply override.
                baseRadius[i] = baseRadius[i] * override / bondiScale;
                logger.info(format(" Scaling %s (atom type %d) to %7.4f (Bondi factor %7.4f)",
                        atoms[i], atomType.type, baseRadius[i], override));
            }
        }

        if (dispersionRegion != null) {
            dispersionRegion.allocate(atoms);
        }

        if (cavitationRegion != null) {
            cavitationRegion.init();
        }

        if (gaussVol != null) {
            boolean[] isHydrogen = new boolean[nAtoms];
            double[] radii = new double[nAtoms];
            double[] volume = new double[nAtoms];
            double[] gamma = new double[nAtoms];

            double fourThirdsPI = 4.0 / 3.0 * PI;
            double rminToSigma = 1.0 / pow(2.0, 1.0 / 6.0);

            int index = 0;
            for (Atom atom : atoms) {
                isHydrogen[index] = atom.isHydrogen();
                radii[index] = atom.getVDWType().radius / 2.0 * rminToSigma;
                radii[index] += probe;
                volume[index] = fourThirdsPI * pow(radii[index], 3);
                gamma[index] = 1.0;
                index++;
            }

            try {
                gaussVol.setGammas(gamma);
                gaussVol.setRadii(radii);
                gaussVol.setVolumes(volume);
                gaussVol.setIsHydrogen(isHydrogen);
            } catch (Exception e) {
                logger.severe(" Exception creating GaussVol: " + e.toString());
            }

        }

    }

    /**
     * <p>Setter for the field <code>use</code>.</p>
     *
     * @param use an array of {@link boolean} objects.
     */
    public void setUse(boolean[] use) {
        this.use = use;
    }

    /**
     * <p>
     * computeBornRadii</p>
     */
    public void computeBornRadii() {
        // Born radii are fixed.
        if (fixedRadii) {
            return;
        }
        try {
            bornRadiiRegion.init(atoms, crystal, sXYZ, neighborLists, baseRadius, overlapScale, use,
                    cut2, nativeEnvironmentApproximation, born);
            parallelTeam.execute(bornRadiiRegion);
        } catch (Exception e) {
            String message = "Fatal exception computing Born radii.";
            logger.log(Level.SEVERE, message, e);
        }
    }

    /**
     * <p>
     * computePermanentGKField</p>
     */
    public void computePermanentGKField() {
        try {
            sharedGKField.reset(parallelTeam, 0, nAtoms - 1);
            permanentGKFieldRegion.init(atoms, globalMultipole, crystal, sXYZ, neighborLists,
                    use, cut2, born, sharedGKField);
            parallelTeam.execute(permanentGKFieldRegion);
            sharedGKField.reduce(parallelTeam, 0, nAtoms - 1);
        } catch (Exception e) {
            String message = "Fatal exception computing permanent GK field.";
            logger.log(Level.SEVERE, message, e);
        }
    }

    /**
     * <p>
     * computeInducedGKField</p>
     */
    public void computeInducedGKField() {
        try {
            sharedGKField.reset(parallelTeam, 0, nAtoms - 1);
            sharedGKFieldCR.reset(parallelTeam, 0, nAtoms - 1);
            inducedGKFieldRegion.init(atoms, inducedDipole, inducedDipoleCR, crystal, sXYZ, neighborLists,
                    use, cut2, born, sharedGKField, sharedGKFieldCR);
            parallelTeam.execute(inducedGKFieldRegion);
            sharedGKField.reduce(parallelTeam, 0, nAtoms - 1);
            sharedGKFieldCR.reduce(parallelTeam, 0, nAtoms - 1);
        } catch (Exception e) {
            String message = "Fatal exception computing induced GK field.";
            logger.log(Level.SEVERE, message, e);
        }
    }

    /**
     * <p>
     * solvationEnergy</p>
     *
     * @param gradient a boolean.
     * @param print    a boolean.
     * @return a double.
     */
    public double solvationEnergy(boolean gradient, boolean print) {
        return solvationEnergy(0.0, gradient, print);
    }

    /**
     * <p>
     * solvationEnergy</p>
     *
     * @param gkPolarizationEnergy GK vacuum to SCRF polarization energy cost.
     * @param gradient             a boolean.
     * @param print                a boolean.
     * @return a double.
     */
    public double solvationEnergy(double gkPolarizationEnergy, boolean gradient, boolean print) {

        try {
            // Find the GK energy.
            gkTime = -System.nanoTime();
            gkEnergyRegion.init(atoms, globalMultipole, inducedDipole, inducedDipoleCR,
                    crystal, sXYZ, neighborLists, use, cut2, baseRadius, born,
                    gradient, grad, torque, sharedBornGrad);
            parallelTeam.execute(gkEnergyRegion);
            gkTime += System.nanoTime();

            // Find the nonpolar energy.
            switch (nonPolar) {
                case CAV:
                    cavitationTime = -System.nanoTime();
                    parallelTeam.execute(cavitationRegion);
                    if (doVolume) {
                        ParallelTeam serialTeam = new ParallelTeam(1);
                        serialTeam.execute(volumeRegion);
                    }
                    cavitationTime += System.nanoTime();
                    break;
                case CAV_DISP:
                    dispersionTime = -System.nanoTime();
                    dispersionRegion.init(atoms, crystal, use, neighborLists, x, y, z, cut2, gradient, grad);
                    parallelTeam.execute(dispersionRegion);
                    dispersionTime += System.nanoTime();
                    cavitationTime = -System.nanoTime();
                    parallelTeam.execute(cavitationRegion);
                    if (doVolume) {
                        ParallelTeam serialTeam = new ParallelTeam(1);
                        serialTeam.execute(volumeRegion);
                    }
                    cavitationTime += System.nanoTime();
                    break;
                case GAUSS_DISP:
                    dispersionTime = -System.nanoTime();
                    dispersionRegion.init(atoms, crystal, use, neighborLists, x, y, z, cut2, gradient, grad);
                    parallelTeam.execute(dispersionRegion);
                    dispersionTime += System.nanoTime();

                    cavitationTime = -System.nanoTime();
                    double[][] positions = new double[nAtoms][3];
                    for (int i = 0; i < nAtoms; i++) {
                        positions[i][0] = atoms[i].getX();
                        positions[i][1] = atoms[i].getY();
                        positions[i][2] = atoms[i].getZ();
                    }
                    gaussVol.energyAndGradient(positions, grad);
                    cavitationTime += System.nanoTime();
                    break;
                case BORN_CAV_DISP:
                    dispersionTime = -System.nanoTime();
                    dispersionRegion.init(atoms, crystal, use, neighborLists, x, y, z, cut2, gradient, grad);
                    parallelTeam.execute(dispersionRegion);
                    dispersionTime += System.nanoTime();
                    break;
                case HYDROPHOBIC_PMF:
                case BORN_SOLV:
                case NONE:
                default:
                    break;
            }
        } catch (Exception e) {
            String message = "Fatal exception computing the continuum solvation energy.";
            logger.log(Level.SEVERE, message, e);
        }

        // Compute the Born radii chain rule term.
        if (gradient) {
            try {
                gkTime -= System.nanoTime();
                bornGradRegion.init(atoms, crystal, sXYZ, neighborLists, baseRadius,
                        overlapScale, use, cut2, nativeEnvironmentApproximation,
                        born, grad, sharedBornGrad);
                bornGradRegion.executeWith(parallelTeam);
                gkTime += System.nanoTime();
            } catch (Exception e) {
                String message = "Fatal exception computing Born radii chain rule term.";
                logger.log(Level.SEVERE, message, e);
            }
        }

        solvationEnergy = gkEnergyRegion.getEnergy() + gkPolarizationEnergy;

        if (print) {
            logger.info(format(" Generalized Kirkwood%16.8f %10.3f", solvationEnergy, gkTime * 1e-9));
            switch (nonPolar) {
                case CAV:
                    if (doVolume) {
                        cavitationEnergy = volumeRegion.getEnergy();
                    } else {
                        cavitationEnergy = cavitationRegion.getEnergy();
                    }
                    logger.info(format(" Cavitation          %16.8f %10.3f",
                            cavitationEnergy, cavitationTime * 1e-9));
                    break;
                case CAV_DISP:
                    if (doVolume) {
                        cavitationEnergy = volumeRegion.getEnergy();
                    } else {
                        cavitationEnergy = cavitationRegion.getEnergy();
                    }
                    dispersionEnergy = dispersionRegion.getEnergy();
                    logger.info(format(" Cavitation          %16.8f %10.3f",
                            cavitationEnergy, cavitationTime * 1e-9));
                    logger.info(format(" Dispersion          %16.8f %10.3f",
                            dispersionEnergy, dispersionTime * 1e-9));
                    break;
                case GAUSS_DISP:
                    cavitationEnergy = gaussVol.getEnergy();
                    dispersionEnergy = dispersionRegion.getEnergy();
                    logger.info(format(" Cavitation          %16.8f %10.3f",
                            cavitationEnergy, cavitationTime * 1e-9));
                    logger.info(format(" Dispersion          %16.8f %10.3f",
                            dispersionEnergy, dispersionTime * 1e-9));
                    break;
                case BORN_CAV_DISP:
                    dispersionEnergy = dispersionRegion.getEnergy();
                    logger.info(format(" Dispersion          %16.8f %10.3f",
                            dispersionEnergy, dispersionTime * 1e-9));
                    break;
                case HYDROPHOBIC_PMF:
                case BORN_SOLV:
                case NONE:
                default:
                    break;
            }
        }

        switch (nonPolar) {
            case CAV:
                if (doVolume) {
                    solvationEnergy += volumeRegion.getEnergy();
                } else {
                    solvationEnergy += cavitationRegion.getEnergy();
                }
                break;
            case CAV_DISP:
                if (doVolume) {
                    solvationEnergy += dispersionRegion.getEnergy() + volumeRegion.getEnergy();
                } else {
                    solvationEnergy += dispersionRegion.getEnergy() + cavitationRegion.getEnergy();
                }
                break;
            case GAUSS_DISP:
                solvationEnergy += dispersionRegion.getEnergy() + gaussVol.getEnergy();
                break;
            case BORN_CAV_DISP:
                solvationEnergy += dispersionRegion.getEnergy();
                break;
            case HYDROPHOBIC_PMF:
            case BORN_SOLV:
            case NONE:
            default:
                break;
        }

        if (lambdaTerm) {
            return lPow * solvationEnergy;
        } else {
            return solvationEnergy;
        }
    }

    /**
     * Returns the cavitation component (if applicable) of GK energy. If this GK
     * is operating without a cavitation term, it either returns 0, or throws an
     * error if throwError is true.
     *
     * @param throwError a boolean.
     * @return Cavitation energy
     */
    public double getCavitationEnergy(boolean throwError) {
        switch (nonPolar) {
            case CAV:
            case CAV_DISP:
                return cavitationEnergy;
            default:
                if (throwError) {
                    throw new IllegalArgumentException(" GK is operating without a cavitation term");
                } else {
                    return 0.0;
                }
        }
    }

    /**
     * Returns the dispersion component (if applicable) of GK energy. If this GK
     * is operating without a dispersion term, it either returns 0, or throws an
     * error if throwError is true.
     *
     * @param throwError a boolean.
     * @return Cavitation energy
     */
    public double getDispersionEnergy(boolean throwError) {
        switch (nonPolar) {
            case CAV_DISP:
            case BORN_CAV_DISP:
                return dispersionEnergy;
            default:
                if (throwError) {
                    throw new IllegalArgumentException(" GK is operating without a dispersion term");
                } else {
                    return 0.0;
                }
        }
    }

    /**
     * <p>
     * getInteractions</p>
     *
     * @return a int.
     */
    public int getInteractions() {
        return gkEnergyRegion.getInteractions();
    }

    void setLambdaFunction(double lPow, double dlPow, double dl2Pow) {
        if (lambdaTerm) {
            this.lPow = lPow;
            this.dlPow = dlPow;
            this.dl2Pow = dl2Pow;
        } else {
            // If the lambdaTerm flag is false, lambda must be set to one.
            this.lambda = 1.0;
            this.lPow = 1.0;
            this.dlPow = 0.0;
            this.dl2Pow = 0.0;
        }
    }

    /**
     * <p>getNonPolarModel.</p>
     *
     * @param nonpolarModel a {@link java.lang.String} object.
     * @return a {@link ffx.potential.nonbonded.GeneralizedKirkwood.NonPolar} object.
     */
    public static NonPolar getNonPolarModel(String nonpolarModel) {
        try {
            return NonPolar.valueOf(toEnumForm(nonpolarModel));
        } catch (IllegalArgumentException ex) {
            logger.warning(" Unrecognized nonpolar model requested; defaulting to NONE.");
            return NonPolar.NONE;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the value of lPow.
     */
    @Override
    public void setLambda(double lambda) {
        if (lambdaTerm) {
            this.lambda = lambda;
        } else {
            // If the lambdaTerm flag is false, lambda is set to one.
            this.lambda = 1.0;
            lPow = 1.0;
            dlPow = 0.0;
            dl2Pow = 0.0;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLambda() {
        return lambda;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getdEdL() {
        if (lambdaTerm) {
            return dlPow * solvationEnergy;
        }
        return 0.0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The 2nd derivative is 0.0. (U=Lambda*Egk, dU/dL=Egk, d2U/dL2=0.0)
     */
    @Override
    public double getd2EdL2() {
        if (lambdaTerm) {
            return dl2Pow * solvationEnergy;
        } else {
            return 0.0;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * These contributions are already aggregated into the arrays used by PME.
     */
    @Override
    public void getdEdXdL(double[] gradient) {
        // This contribution is collected by GeneralizedKirkwood.reduce
    }

}
