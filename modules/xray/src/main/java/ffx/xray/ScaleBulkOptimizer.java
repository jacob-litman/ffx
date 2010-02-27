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

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.PI;
import static java.lang.Math.pow;

import java.util.logging.Logger;
import java.util.Vector;

import ffx.crystal.Crystal;
import ffx.crystal.HKL;
import ffx.crystal.ReflectionList;
import ffx.crystal.SymOp;
import ffx.numerics.ComplexNumber;
import ffx.numerics.Optimizable;
import ffx.numerics.VectorMath;

/**
 *
 * Fit bulk solvent and aniso B scaling terms to correct calculated structure
 * factors against data
 *
 * @author Tim Fenn<br>
 *
 * @see <a href="http://dx.doi.org/10.1107/S0907444905007894" target="_blank">
 * P. V. Afonine, R. W. Grosse-Kunstleve and P. D. Adams,
 * Acta Cryst. (2005). D61, 850-855
 *
 */
public class ScaleBulkOptimizer implements Optimizable {

    private static final Logger logger = Logger.getLogger(ScaleBulkOptimizer.class.getName());
    private static final double twopi2 = 2.0 * PI * PI;
    private static final double eightpi2 = 8.0 * PI * PI;
    private final ReflectionList reflectionlist;
    private final Crystal crystal;
    private final RefinementData refinementdata;
    private final double fc[][];
    private final double fs[][];
    private final double fctot[][];
    private final double fo[][];
    private final int freer[];
    private final int n;
    protected double[] optimizationScaling = null;

    public ScaleBulkOptimizer(ReflectionList reflectionlist, RefinementData refinementdata, int n) {
        this.reflectionlist = reflectionlist;
        this.crystal = reflectionlist.crystal;
        this.refinementdata = refinementdata;
        this.fc = refinementdata.fc;
        this.fs = refinementdata.fs;
        this.fctot = refinementdata.fctot;
        this.fo = refinementdata.fsigf;
        this.freer = refinementdata.freer;
        this.n = n;
    }

    public double target(double x[], double g[],
            boolean gradient, boolean print) {
        double r, rf, rfree, rfreef, sum, sumfo;

        // zero out the gradient
        if (gradient) {
            for (int i = 0; i < g.length; i++) {
                g[i] = 0.0;
            }
        }

        int scale_n = crystal.scale_n;
        double model_k = x[0];
        double solvent_k = 0.0;
        double solvent_ueq = 0.0;
        if (refinementdata.solvent_n > 1) {
            solvent_k = x[1];
            solvent_ueq = x[2];
        }
        double model_b[] = new double[6];
        for (int i = 0; i < 6; i++) {
            if (crystal.scale_b[i] >= 0) {
                model_b[i] = x[refinementdata.solvent_n + crystal.scale_b[i]];
            }
        }

        r = rf = rfree = rfreef = sum = sumfo = 0.0;
        for (HKL ih : reflectionlist.hkllist) {
            int i = ih.index();
            if (Double.isNaN(fc[i][0])
                    || Double.isNaN(fo[i][0])
                    || fo[i][1] <= 0.0) {
                continue;
            }

            //  constants
            double ihc[] = {ih.h(), ih.k(), ih.l()};
            double ihf[] = VectorMath.mat3vec3(ihc, crystal.recip);
            double u = model_k
                    - pow(ihf[0], 2.0) * model_b[0]
                    - pow(ihf[1], 2.0) * model_b[1]
                    - pow(ihf[2], 2.0) * model_b[2]
                    - 2.0 * ihf[0] * ihf[1] * model_b[3]
                    - 2.0 * ihf[0] * ihf[2] * model_b[4]
                    - 2.0 * ihf[1] * ihf[2] * model_b[5];
            double s = Crystal.invressq(crystal, ih);
            double ebs = exp(-twopi2 * solvent_ueq * s);
            double ksebs = solvent_k * ebs;
            double kmems = exp(0.5 * u);

            // structure factors
            ComplexNumber fcc = new ComplexNumber(fc[i][0], fc[i][1]);
            ComplexNumber fsc = new ComplexNumber(fs[i][0], fs[i][1]);
            ComplexNumber fct = refinementdata.solvent_n > 1
                    ? fcc.plus(fsc.times(ksebs)) : fcc;
            ComplexNumber kfct = fct.times(kmems);

            // total structure factor (for refinement)
            fctot[i][0] = kfct.re();
            fctot[i][1] = kfct.im();

            // target
            double f1 = fo[i][0];
            double f2 = kfct.abs();
            double d = f1 - f2;
            double d2 = d * d;
            double dr = -2.0 * d;
            /*
            double eps = ih.epsilon();
            double f1 = pow(fo[i][0], 2.0) / eps;
            double f2 = pow(fct.abs(), 2.0) / eps;
            double d = u + log(f2) - log(f1);
            double d2 = d * d;
            double dr = 2.0 * d;
             */

            sum += d2;
            sumfo += f1 * f1;

            if (freer[i] == refinementdata.rfreeflag) {
                rfree += abs(abs(fo[i][0]) - abs(kfct.abs()));
                rfreef += abs(fo[i][0]);
            } else {
                r += abs(abs(fo[i][0]) - abs(kfct.abs()));
                rf += abs(fo[i][0]);
            }

            if (gradient) {
                // common derivative element
                double dfp = ebs * (fcc.re() * fsc.re()
                        + fcc.im() * fsc.im()
                        + ksebs * pow(fsc.abs(), 2.0));

                // model_k derivative
                g[0] += 0.5 * kfct.abs() * dr;
                // g[0] += dr;
                if (refinementdata.solvent_n > 1) {
                    // solvent_k derivative
                    g[1] += kmems * dfp * dr / fct.abs();
                    // g[1] += dfp * dr / pow(fct.abs(), 2.0);
                    // solvent_ueq derivative
                    g[2] += kmems * -twopi2 * s * solvent_k * dfp * dr / fct.abs();
                    // g[2] += -twopi2 * s * solvent_k * dfp * dr / pow(fct.abs(), 2.0);
                }

                int sn = refinementdata.solvent_n;
                for (int k = 0; k < 6; k++) {
                    if (crystal.scale_b[k] >= 0) {
                        switch (k) {
                            case (0):
                                // B11
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -pow(ihf[0], 2.0) * dr;
                                break;
                            case (1):
                                // B22
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -pow(ihf[1], 2.0) * dr;
                                break;
                            case (2):
                                // B33
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -pow(ihf[2], 2.0) * dr;
                                break;
                            case (3):
                                // B12
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -2.0 * ihf[0] * ihf[1] * dr;
                                break;
                            case (4):
                                // B13
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -2.0 * ihf[0] * ihf[2] * dr;
                                break;
                            case (5):
                                // B23
                                g[sn + crystal.scale_b[k]] += 0.5 * kfct.abs() * -2.0 * ihf[1] * ihf[2] * dr;
                                break;
                        }
                    }
                }
            }
        }

        if (gradient) {
            for (int i = 0; i < g.length; i++) {
                g[i] /= sumfo;
            }
        }

        if (print) {
            StringBuffer sb = new StringBuffer("\n");
            sb.append(" Bulk solvent and scale fit\n");
            sb.append(String.format("   residual:  %8.3f\n", sum / sumfo));
            sb.append(String.format("   R:  %8.3f  Rfree:  %8.3f\n",
                    (r / rf) * 100.0, (rfree / rfreef) * 100.0));
            sb.append("x: ");
            for (int i = 0; i < x.length; i++) {
                sb.append(String.format("%8g ", x[i]));
            }
            sb.append("\ng: ");
            for (int i = 0; i < g.length; i++) {
                sb.append(String.format("%8g ", g[i]));
            }
            sb.append("\n");
            logger.info(sb.toString());
        }

        return sum / sumfo;
    }

    @Override
    public double energyAndGradient(double[] x, double[] g) {
        if (optimizationScaling != null) {
            int len = x.length;
            for (int i = 0; i < len; i++) {
                x[i] /= optimizationScaling[i];
            }
        }

        double sum = target(x, g, true, false);

        if (optimizationScaling != null) {
            int len = x.length;
            for (int i = 0; i < len; i++) {
                x[i] *= optimizationScaling[i];
                g[i] /= optimizationScaling[i];
            }
        }

        return sum;
    }

    @Override
    public void setOptimizationScaling(double[] scaling) {
        if (scaling != null && scaling.length == n) {
            optimizationScaling = scaling;
        } else {
            optimizationScaling = null;
        }
    }

    @Override
    public double[] getOptimizationScaling() {
        return optimizationScaling;
    }
}
