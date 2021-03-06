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
package ffx.algorithms.mc;

import java.util.Random;
import java.util.logging.Logger;
import static java.lang.Math.abs;

import ffx.algorithms.thermodynamics.AbstractOSRW;

/**
 * Define an MC move to update lambda.
 *
 * @author Mallory R. Tollefson
 */
public class LambdaMove implements MCMove {

    private static final Logger logger = Logger.getLogger(LambdaMove.class.getName());

    /**
     * Current value of lambda, which always refreshed from the OSRW instance.
     */
    private double currentLambda;

    /**
     * Apply the Lambda move to an OSRW instance.
     */
    private final AbstractOSRW osrw;

    /**
     * Random number generator.
     */
    private Random random;

    /**
     * Lambda move standard deviation.
     */
    private double stdDev = 0.1;

    /**
     * Starting lambda value.
     */
    // private double lambdaStart = 0.0;

    /**
     * <p>Constructor for LambdaMove.</p>
     *
     * @param osrw a {@link AbstractOSRW} object.
     */
    public LambdaMove(AbstractOSRW osrw) {
        this.osrw = osrw;
        random = new Random();
    }

    /**
     * <p>Constructor for LambdaMove.</p>
     *
     * @param randomSeed Random seed to use.
     * @param osrw       OSRW instance.
     */
    public LambdaMove(int randomSeed, AbstractOSRW osrw) {
        this.osrw = osrw;
        random = new Random(randomSeed);
    }

    /**
     * <p>Setter for the field <code>stdDev</code>.</p>
     *
     * @param stdDev a double.
     */
    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move() {
        currentLambda = osrw.getLambda();

        // Draw a trial move from the distribution.
        double dL = random.nextGaussian() * stdDev;
        double newLambda = currentLambda + dL;

        // Map values into the range 0.0 .. 1.0 using mirror boundary conditions.
        if (newLambda > 1.0) {
            newLambda = (2.0 - newLambda);
        } else if (newLambda < 0.0) {
            newLambda = abs(newLambda);
        }

        // Update the OSRW instance.
        osrw.setLambda(newLambda);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void revertMove() {
        osrw.setLambda(currentLambda);
    }

    /**
     * Get the Lambda move standard deviation.
     *
     * @return The standard deviation of the lambda move.
     */
    public double getStandardDeviation() {
        return stdDev;
    }
}
