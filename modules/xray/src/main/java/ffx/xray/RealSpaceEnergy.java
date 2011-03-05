/**
 * Title: Force Field X
 * Description: Force Field X - Software for Molecular Biophysics.
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2009
 *
 * This file is part of Force Field X.
 *
 * Force Field X is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * Force Field X is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Force Field X; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 */
package ffx.xray;

import ffx.numerics.Potential;
import ffx.potential.bonded.Atom;
import ffx.xray.RefinementMinimize.RefinementMode;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Combine the Real Space target and chemical potential energy.
 *
 * @author Timothy D. Fenn and Michael J. Schnieders
 *
 * @since 1.0
 */
public class RealSpaceEnergy implements Potential {

    private static final Logger logger = Logger.getLogger(RealSpaceEnergy.class.getName());
    private final RealSpaceData realspacedata;
    private final RefinementModel refinementmodel;
    private final Atom atomarray[];
    private final int nAtoms;
    private int nxyz;
    private RefinementMode refinementMode;
    private boolean refinexyz = false;
    protected double[] optimizationScaling = null;

    /**
     * Diffraction data energy target
     *
     * @param diffractiondata {@link DiffractionData} object to associate with
     * the target
     * @param nxyz number of xyz parameters
     * @param nb number of b factor parameters
     * @param nocc number of occupancy parameters
     * @param refinementmode the {@link RefinementMinimize.RefinementMode} type
     * of refinement requested
     */
    public RealSpaceEnergy(RealSpaceData realspacedata, int nxyz, int nb, int nocc,
            RefinementMode refinementmode) {
        this.realspacedata = realspacedata;
        this.refinementmodel = realspacedata.getRefinementModel();
        this.refinementMode = refinementmode;
        this.atomarray = refinementmodel.atomarray;
        this.nAtoms = atomarray.length;
        this.nxyz = nxyz;

        setRefinementBooleans();
    }

    @Override
    public double energyAndGradient(double[] x, double[] g) {
        double e = 0.0;
        /**
         * Unscale the coordinates.
         */
        if (optimizationScaling != null) {
            int len = x.length;
            for (int i = 0; i < len; i++) {
                x[i] /= optimizationScaling[i];
            }
        }

        if (refinexyz) {
            for (Atom a : atomarray) {
                a.setXYZGradient(0.0, 0.0, 0.0);
            }
        }

        // target function for real space refinement
        if (refinexyz) {
            e = realspacedata.computeRealSpaceTarget();

            // pack gradients into gradient array
            getXYZGradients(g);
        }

        /**
         * Scale the coordinates and gradients.
         */
        if (optimizationScaling != null) {
            int len = x.length;
            for (int i = 0; i < len; i++) {
                x[i] *= optimizationScaling[i];
                g[i] /= optimizationScaling[i];
            }
        }
        return e;
    }

    public RefinementMode getRefinementMode() {
        return refinementMode;
    }

    public void setRefinementMode(RefinementMode refinementmode) {
        this.refinementMode = refinementmode;
        setRefinementBooleans();
    }

    /**
     * if the refinement mode has changed, this should be called to update which
     * parameters are being fit
     */
    private void setRefinementBooleans() {
        // reset, if previously set
        refinexyz = false;

        if (refinementMode == RefinementMode.COORDINATES
                || refinementMode == RefinementMode.COORDINATES_AND_BFACTORS
                || refinementMode == RefinementMode.COORDINATES_AND_OCCUPANCIES
                || refinementMode == RefinementMode.COORDINATES_AND_BFACTORS_AND_OCCUPANCIES) {
            refinexyz = true;
        }
    }

    /**
     * get the number of xyz parameters being fit
     * @return the number of xyz parameters
     */
    public int getNXYZ() {
        return nxyz;
    }

    /**
     * set the number of xyz parameters
     * @param nxyz requested number of xyz parameters
     */
    public void setNXYZ(int nxyz) {
        this.nxyz = nxyz;
    }

    /**
     * fill gradient array with xyz gradients
     *
     * @param g array to add gradients to
     */
    public void getXYZGradients(double g[]) {
        assert (g != null);
        double grad[] = new double[3];
        int index = 0;
        for (Atom a : atomarray) {
            a.getXYZGradient(grad);
            g[index++] = grad[0];
            g[index++] = grad[1];
            g[index++] = grad[2];
        }
    }

    @Override
    public double[] getCoordinates(double x[]) {
        assert (x != null);
        double xyz[] = new double[3];
        int index = 0;
        Arrays.fill(x, 0.0);

        if (refinexyz) {
            for (Atom a : atomarray) {
                a.getXYZ(xyz);
                x[index++] = xyz[0];
                x[index++] = xyz[1];
                x[index++] = xyz[2];
            }
        }

        return x;
    }

    /**
     * set atomic xyz coordinates based on current position
     *
     * @param x current parameters to set coordinates with
     */
    public void setCoordinates(double x[]) {
        int n = getNumberOfVariables();
        assert (x != null);
        double xyz[] = new double[3];
        int index = 0;
        for (Atom a : atomarray) {
            xyz[0] = x[index++];
            xyz[1] = x[index++];
            xyz[2] = x[index++];
            a.moveTo(xyz);
        }
    }

    @Override
    public void setScaling(double[] scaling) {
        optimizationScaling = scaling;
    }

    @Override
    public double[] getScaling() {
        return optimizationScaling;
    }

    @Override
    public double[] getMass() {
        double mass[] = new double[nxyz];
        int i = 0;
        if (refinexyz) {
            for (Atom a : atomarray) {
                double m = a.getMass();
                mass[i++] = m;
                mass[i++] = m;
                mass[i++] = m;
            }
        }

        return mass;
    }

    @Override
    public int getNumberOfVariables() {
        return nxyz;
    }
}