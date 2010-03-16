/**
 * Title: Force Field X
 * Description: Force Field X - Software for Molecular Biophysics.
 * Copyright: Copyright (c) Michael J. Schnieders 2001-2010
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

import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import static ffx.numerics.VectorMath.determinant3;
import static ffx.numerics.VectorMath.diff;
import static ffx.numerics.VectorMath.dot;
import static ffx.numerics.VectorMath.mat3mat3;
import static ffx.numerics.VectorMath.scalarmat3mat3;
import static ffx.numerics.VectorMath.vec3mat3;
import static ffx.numerics.VectorMath.rsq;

import ffx.crystal.Crystal;
import ffx.crystal.HKL;
import ffx.potential.bonded.Atom;

import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.EigenDecompositionImpl;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

/**
 *
 * @author Tim Fenn<br>
 *
 * This implementation uses the coefficients from Su and Coppens:<br>
 *
 * @see <a href="http://dx.doi.org/10.1107/S0108767397004558" target="_blank">
 * Z. Su and P. Coppens, Acta Cryst. (1997). A53, 749-762
 *
 * @see <a href="http://dx.doi.org/10.1107/S010876739800124X" target="_blank">
 * Z. Su and P. Coppens, Acta Cryst. (1998). A54, 357
 *
 * Source data:<br>
 * @see <a href="http://harker.chem.buffalo.edu/group/groupindex.html" target="_blank">
 *
 * and form factor equations from:
 *
 * @see <a href="http://dx.doi.org/10.1107/S0907444909022707" target="_blank">
 * M. J. Schnieders, T. D. Fenn, V. S. Pande and A. T. Brunger,
 * Acta Cryst. (2009). D65 952-965.
 */
public class FormFactor {

    private static final Logger logger = Logger.getLogger(ffx.xray.FormFactor.class.getName());
    private static final double twopi2 = 2.0 * PI * PI;
    private static final double eightpi2 = 8.0 * PI * PI;
    private static final double twopi32 = pow(2.0 * PI, -1.5);
    private static final double vx[] = {1.0, 0.0, 0.0};
    private static final double vy[] = {0.0, 1.0, 0.0};
    private static final double vz[] = {0.0, 0.0, 1.0};
    private static final double u11[][] = {{1.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
    private static final double u22[][] = {{0.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 0.0}};
    private static final double u33[][] = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 1.0}};
    private static final double u12[][] = {{0.0, 1.0, 0.0}, {1.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
    private static final double u13[][] = {{0.0, 0.0, 1.0}, {0.0, 0.0, 0.0}, {1.0, 0.0, 0.0}};
    private static final double u23[][] = {{0.0, 0.0, 0.0}, {0.0, 0.0, 1.0}, {0.0, 1.0, 0.0}};
    private static final HashMap formfactors = new HashMap();
    private static final String[] atoms = {"H", "He", "Li", "Be", "B", "C", "N", "O",
        "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca",
        "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge",
        "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru",
        "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Li+",
        "Be2+", "Cv", "O-", "F-", "Na+", "Mg2+", "Al3+", "Si0", "Si4+", "Cl-",
        "K+", "Ca2+", "Sc3+", "Ti2+", "Ti3+", "Ti4+", "V2+", "V3+", "V5+",
        "Cr2+", "Cr3+", "Mn2+", "Mn3+", "Mn4+", "Fe2+", "Fe3+", "Co2+", "Co3+",
        "Ni2+", "Ni3+", "Cu+", "Cu2+", "Zn2+", "Ga3+", "Ge4+", "Br-", "Rb+",
        "Sr2+", "Y3+", "Zr4+", "Nb3+", "Nb5+", "Mo3+", "Mo6+", "Ru3+", "Ru4+",
        "Rh3+", "Rh4+", "Pd2+", "Pd4+", "Ag+", "Ag2+", "Cd2+", "In3+", "Sn2+",
        "Sn4+", "Sb3+", "Sb5+", "I-"};
    private static final String[] atomsi = {"1", "2", "3", "4", "5", "6", "7", "8",
        "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32",
        "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44",
        "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "3_1",
        "4_2", "6_5", "8_-1", "9_-1", "11_1", "12_2", "13_3", "14_0", "14_4", "17_-1",
        "19_1", "20_2", "21_3", "22_2", "22_3", "22_4", "23_2", "23_3", "23_5",
        "24_2", "24_3", "25_2", "25_3", "25_4", "26_2", "26_3", "27_2", "27_3",
        "28_2", "28_3", "29_1", "29_2", "30_2", "31_3", "32_4", "35_-1", "37_1",
        "38_2", "39_3", "40_4", "41_3", "41_5", "42_3", "42_6", "44_3", "44_4",
        "45_3", "45_4", "46_2", "46_4", "47_1", "47_2", "48_2", "49_3", "50_2",
        "50_4", "51_3", "51_5", "53_-1"};
    private static final double[][][] ffactors = {
        {{0.43028, 0.28537, 0.17134, 0.09451, 0.01725, 0.00114},
            {23.02312, 10.20138, 51.25444, 4.13511, 1.35427, 0.24269}},
        {{0.69475, 0.62068, 0.38661, 0.15223, 0.12661, 0.01907},
            {5.83366, 12.87682, 2.53296, 28.16171, 0.97507, 0.25308}},
        {{0.84645, 0.81146, 0.81096, 0.26115, 0.26055, 0.00930},
            {4.63253, 1.71862, 97.87364, 0.50620, 200.00000, 0.00010}},
        {{1.59261, 1.12768, 0.70296, 0.53815, 0.03863, 0.00010},
            {43.67397, 1.86275, 0.54243, 103.44910, 0.00010, 0.34975}},
        {{2.07418, 1.20577, 1.07592, 0.52023, 0.12280, 0.00010},
            {23.39543, 1.07672, 60.93249, 0.27132, 0.27192, 0.11361}},
        {{2.09921, 1.80832, 1.26159, 0.56775, 0.26303, 0.00010},
            {13.18997, 30.37956, 0.69255, 0.16381, 68.42774, 0.44083}},
        {{2.45424, 2.15782, 1.05782, 0.57557, 0.44959, 0.30480},
            {18.66694, 8.31271, 0.46989, 42.44646, 0.08747, 0.47126}},
        {{2.34752, 1.83006, 1.61538, 1.52402, 0.41423, 0.26867},
            {9.69710, 18.59876, 5.19879, 0.32408, 39.79099, 0.01150}},
        {{2.96981, 2.04536, 1.78123, 1.52086, 0.42253, 0.26008},
            {7.52365, 15.41441, 3.79721, 0.25209, 33.76478, 0.00488}},
        {{3.56413, 2.72559, 1.67359, 1.58884, 0.25468, 0.19320},
            {7.30559, 3.34491, 15.93226, 0.13859, 0.69111, 35.26368}},
        {{4.16491, 2.38097, 1.70484, 1.59622, 0.66291, 0.48971},
            {4.23096, 9.48502, 0.12559, 1.98358, 172.13327, 82.23091}},
        {{3.90882, 2.62159, 1.69157, 1.52610, 1.47907, 0.77262},
            {3.06041, 6.12146, 0.10357, 58.65022, 1.56940, 125.49980}},
        {{4.25474, 3.58301, 2.37351, 1.72366, 0.99400, 0.07031},
            {3.76670, 1.69151, 45.27810, 0.09238, 113.96978, 17.47922}},
        {{4.94976, 3.25403, 2.84957, 1.66053, 1.22949, 0.05611},
            {2.70254, 34.45314, 1.24059, 0.07201, 84.53648, 56.34208}},
        {{6.48197, 4.31666, 1.73759, 1.35793, 1.10559, 0.00010},
            {1.89537, 27.61455, 0.50991, 66.28296, 0.00010, 12.05652}},
        {{6.90565, 5.24410, 1.54516, 1.42922, 0.87564, 0.00010},
            {1.46764, 22.31576, 56.06328, 0.25588, 0.00010, 26.96892}},
        {{7.13381, 6.26972, 1.82658, 1.62579, 0.14431, 0.00010},
            {1.17455, 18.57626, 0.07869, 48.08203, 0.07871, 23.23894}},
        {{7.28551, 7.24549, 1.74775, 1.72174, 0.00010, 0.00010},
            {15.63295, 0.95562, 0.04456, 41.07550, 0.00617, 20.09628}},
        {{8.13161, 7.43972, 1.42159, 1.12030, 0.88342, 0.00010},
            {12.73675, 0.77443, 0.00010, 200.00000, 36.18711, 82.98380}},
        {{8.62965, 7.38765, 1.63044, 1.37681, 0.97538, 0.00010},
            {10.45238, 0.66036, 87.06258, 0.00010, 181.27760, 28.57890}},
        {{9.18894, 7.36727, 1.60214, 1.33655, 0.78386, 0.72047},
            {9.02948, 0.57364, 137.40503, 0.00010, 51.53615, 53.74395}},
        {{9.75861, 7.35354, 1.46842, 1.40591, 1.28669, 0.72609},
            {7.86172, 0.50107, 32.75146, 90.95131, 0.00010, 149.02872}},
        {{10.25443, 7.34699, 1.84039, 1.72148, 1.22611, 0.61000},
            {6.86177, 0.43939, 23.70259, 79.72053, 0.00010, 149.36488}},
        {{10.67225, 4.62093, 3.33159, 2.72784, 1.45281, 1.19090},
            {6.12143, 0.39293, 20.15470, 0.39293, 92.01317, 0.00010}},
        {{10.98576, 7.35617, 2.92091, 1.65707, 1.08018, 0.99906},
            {5.27951, 0.34199, 14.55791, 54.87900, 0.00010, 118.26511}},
        {{11.18858, 7.37206, 3.55141, 1.68125, 1.20893, 0.99652},
            {4.64599, 0.30327, 12.07655, 44.15316, 104.11866, 0.00010}},
        {{11.41624, 7.38902, 4.21351, 1.80189, 1.26103, 0.91710},
            {4.12258, 0.27069, 10.36636, 38.32442, 97.14970, 0.00010}},
        {{11.76300, 7.39888, 4.85491, 1.98079, 1.14857, 0.85325},
            {3.69729, 0.24374, 9.30593, 36.58880, 96.02875, 0.00010}},
        {{11.87211, 7.37491, 6.08548, 1.94337, 0.86475, 0.85837},
            {3.34773, 0.22522, 8.46165, 27.95010, 98.02165, 0.00012}},
        {{12.53020, 6.57092, 5.84880, 2.07610, 1.65893, 1.31388},
            {3.05828, 0.14326, 7.58930, 28.50706, 0.38369, 82.22092}},
        {{10.69865, 7.89127, 4.74778, 3.83120, 2.59218, 1.23712},
            {3.44787, 0.15426, 2.07387, 8.38441, 34.93356, 99.34732}},
        {{9.56335, 7.86994, 7.64215, 3.31296, 2.13351, 1.47704},
            {2.21494, 0.14284, 3.86490, 32.69417, 8.94286, 82.15827}},
        {{10.86205, 7.83248, 5.48862, 4.21250, 2.56904, 2.03413},
            {2.10046, 0.13209, 3.33631, 26.38254, 5.81992, 63.74567}},
        {{12.63843, 7.77316, 5.80645, 4.44296, 1.82898, 1.50938},
            {1.97006, 0.12167, 3.57609, 28.84348, 15.15766, 64.03025}},
        {{12.56835, 7.70669, 5.76243, 4.78093, 2.48412, 1.69674},
            {1.79826, 0.11204, 2.98848, 25.62856, 14.95420, 55.44329}},
        {{13.32373, 7.64645, 5.71351, 4.95009, 2.80427, 1.56038},
            {1.67399, 0.10346, 17.43646, 2.62566, 42.87908, 19.80281}},
        {{17.73932, 7.70415, 5.33484, 4.92829, 1.28671, 0.00010},
            {1.68298, 0.09944, 12.80739, 23.59343, 200.00000, 77.16806}},
        {{11.77920, 9.53489, 7.57120, 6.03047, 2.02653, 1.05652},
            {1.52266, 13.50271, 0.08995, 1.52251, 162.86971, 53.07068}},
        {{17.89478, 9.91124, 7.40424, 2.14475, 1.64266, 0.00010},
            {1.37779, 12.18084, 0.08009, 137.73235, 49.81442, 0.42187}},
        {{18.00877, 10.47108, 7.22234, 2.43263, 1.86405, 0.00010},
            {1.25042, 11.25972, 0.07050, 49.09408, 131.67513, 1.76480}},
        {{18.18722, 11.07349, 7.02786, 3.35224, 1.35250, 0.00606},
            {1.13993, 10.82683, 0.06116, 38.71734, 115.18009, 1.19550}},
        {{18.36000, 6.75320, 6.25470, 5.52972, 3.76774, 1.33338},
            {1.03291, 0.05000, 10.10135, 10.12179, 34.16693, 104.10497}},
        {{18.53113, 12.72135, 6.39681, 2.88811, 1.72002, 0.74148},
            {0.93112, 9.26800, 0.03703, 31.91681, 110.11821, 44.07274}},
        {{18.82022, 13.49636, 6.01136, 3.54102, 1.19962, 0.93207},
            {0.84363, 8.84277, 0.02355, 27.02179, 44.09284, 113.68484}},
        {{19.15093, 14.43898, 4.66972, 4.66263, 1.22522, 0.85125},
            {0.75936, 8.27523, 26.67965, 0.00694, 97.04210, 0.00695}},
        {{19.32300, 15.30162, 5.26970, 5.12338, 0.98021, 0.00010},
            {0.69750, 7.93132, 0.00010, 23.54133, 60.82499, 1.28291}},
        {{19.28330, 16.71519, 5.18450, 4.77793, 1.03807, 0.00010},
            {0.64519, 7.48785, 0.00010, 24.79225, 100.31405, 2.33951}},
        {{19.22320, 17.67107, 5.07851, 4.43017, 1.59588, 0.00010},
            {0.59542, 6.92490, 0.00010, 24.85505, 87.61222, 31.90172}},
        {{19.16300, 18.59170, 4.95237, 4.27994, 2.00969, 0.00010},
            {0.54868, 6.39500, 0.00010, 26.18224, 93.70112, 8.23922}},
        {{19.22704, 19.09869, 4.79841, 4.37320, 2.50037, 0.00010},
            {5.84698, 0.50421, 0.00010, 27.22571, 81.57248, 31.56814}},
        {{19.04077, 13.05412, 6.63670, 4.95963, 4.60941, 2.69795},
            {0.46176, 5.31900, 5.31953, 28.54198, 0.00010, 72.65174}},
        {{19.96327, 18.99686, 6.19148, 4.38583, 2.46194, 0.00010},
            {4.81879, 0.42169, 28.79858, 0.00010, 70.63864, 12.77096}},
        {{18.97925, 15.69578, 7.06433, 4.42489, 4.10018, 2.73271},
            {0.38267, 4.34879, 26.93604, 4.35210, 0.00010, 61.59836}},
        {{20.29787, 19.00556, 9.04165, 3.76022, 1.89561, 0.00010},
            {3.93838, 0.34588, 26.70066, 0.00010, 65.34476, 20.30305}},
        {{0.79375, 0.54736, 0.46161, 0.13918, 0.05800, 0.00010},
            {2.88678, 1.16905, 6.18250, 0.31715, 12.60983, 28.15927}},
        {{0.82577, 0.73691, 0.23557, 0.20135, 0.00034, 0.00010},
            {2.04212, 0.80252, 4.60157, 0.21162, 43.68258, 103.45510}},
        {{2.03492, 1.64286, 0.68060, 0.67022, 0.51650, 0.45488},
            {25.99675, 11.77809, 0.51013, 0.97866, 0.16915, 57.91874}},
        {{3.56378, 2.14950, 1.52760, 1.47980, 0.27065, 0.00010},
            {14.10561, 5.60491, 0.32801, 46.88862, 0.00980, 10.98084}},
        {{3.22684, 2.47111, 1.59839, 1.28490, 1.11335, 0.30182},
            {4.95997, 14.45952, 0.17267, 11.39653, 43.30817, 0.96703}},
        {{3.69529, 3.30459, 1.68333, 0.69149, 0.62431, 0.00088},
            {3.24183, 7.07179, 0.12279, 15.45334, 1.43664, 35.26383}},
        {{4.30385, 2.58390, 1.71397, 1.39368, 0.00470, 0.00010},
            {4.02045, 1.85304, 0.10693, 8.78523, 58.58712, 125.50050}},
        {{4.19367, 3.00032, 1.71590, 1.08840, 0.00167, 0.00010},
            {3.37134, 1.58637, 0.09158, 6.99679, 45.26456, 113.97270}},
        {{5.49488, 3.33770, 2.38765, 1.59864, 1.17986, 0.00010},
            {2.60802, 37.46289, 1.09647, 0.06439, 80.52337, 56.27056}},
        {{3.98392, 3.53675, 1.72808, 0.75103, 0.00013, 0.00010},
            {2.94648, 1.39488, 0.08069, 5.91604, 56.23176, 79.76580}},
        {{7.13932, 6.34213, 2.29801, 1.97826, 0.22854, 0.00983},
            {1.18073, 19.52901, 61.04850, 0.08057, 23.18225, 0.09759}},
        {{8.00372, 7.44077, 1.42217, 1.13491, 0.00010, 0.00010},
            {12.70476, 0.77473, 0.00010, 32.44270, 199.99900, 82.98298}},
        {{8.66803, 7.39747, 1.38325, 0.55348, 0.00010, 0.00010},
            {10.62955, 0.66306, 0.00010, 30.98476, 199.99880, 82.97898}},
        {{9.01395, 7.36477, 1.32160, 0.30179, 0.00010, 0.00010},
            {8.86658, 0.56771, 0.00010, 29.98133, 137.40030, 53.69811}},
        {{9.67607, 7.35874, 1.66775, 1.29681, 0.00010, 0.00010},
            {7.92858, 0.50388, 23.88214, 0.00010, 92.10388, 145.58810}},
        {{9.56376, 7.35320, 1.26997, 0.81496, 0.00010, 0.00010},
            {7.72729, 0.49604, 0.00010, 22.37931, 92.10560, 145.58920}},
        {{9.22395, 7.35117, 1.23367, 0.19305, 0.00010, 0.00010},
            {7.44634, 0.48595, 0.00010, 28.20512, 92.10930, 145.59010}},
        {{10.14209, 7.35015, 2.25361, 1.23887, 0.01533, 0.00010},
            {6.90615, 0.44224, 20.14575, 0.00010, 120.21700, 55.09812}},
        {{10.05723, 7.34875, 1.38759, 1.20752, 0.00010, 0.00010},
            {6.75290, 0.43509, 18.25122, 0.00010, 120.22150, 55.11062}},
        {{9.37695, 7.36389, 1.11621, 0.14450, 0.00010, 0.00010},
            {6.31625, 0.41568, 0.00010, 25.36044, 199.99870, 82.97847}},
        {{10.54130, 4.41457, 2.93436, 2.87024, 1.17229, 0.06743},
            {6.04009, 0.38967, 0.38966, 16.94938, 0.00010, 59.98400}},
        {{10.45597, 4.43683, 2.92505, 2.06149, 1.11981, 0.00120},
            {5.90641, 0.38863, 0.37041, 15.34221, 0.00010, 59.68271}},
        {{10.86580, 7.35401, 3.49267, 1.09987, 0.18537, 0.00249},
            {5.30450, 0.34487, 14.15718, 0.00010, 38.60730, 100.13560}},
        {{11.04414, 4.43611, 4.06737, 2.44502, 0.00559, 0.00189},
            {5.32462, 0.15971, 0.47488, 13.90108, 100.14020, 38.59723}},
        {{10.80739, 7.37819, 1.80548, 1.00948, 0.00010, 0.00010},
            {5.12031, 0.33181, 12.46589, 0.00010, 100.14660, 38.60185}},
        {{11.32394, 7.35828, 4.08542, 1.03934, 0.19438, 0.00010},
            {4.71611, 0.30793, 12.87900, 0.00024, 43.73118, 103.91920}},
        {{11.27641, 7.37595, 3.32058, 0.98461, 0.04263, 0.00010},
            {4.63894, 0.30169, 11.63908, 0.00010, 44.10289, 103.92070}},
        {{11.59539, 7.37601, 4.75131, 0.95818, 0.31843, 0.00010},
            {4.18474, 0.27510, 11.19206, 0.00010, 36.27509, 93.95933}},
        {{11.58135, 7.38964, 4.01201, 0.91419, 0.10353, 0.00010},
            {4.13155, 0.27012, 10.32693, 0.00010, 35.20369, 93.95908}},
        {{11.83838, 5.16446, 4.59215, 3.72826, 0.67719, 0.00010},
            {3.76040, 9.57707, 0.31557, 0.11646, 25.17286, 96.76703}},
        {{12.08932, 7.37051, 4.53328, 0.89389, 0.11440, 0.00010},
            {3.73486, 0.24588, 9.52524, 0.00100, 36.54998, 96.77110}},
        {{11.74994, 6.77249, 6.21229, 1.75552, 1.47560, 0.03461},
            {3.34714, 0.23831, 8.32820, 23.58346, 0.04331, 98.01738}},
        {{11.83187, 5.78192, 5.77531, 2.46041, 1.14698, 0.00353},
            {3.33965, 0.25530, 8.03031, 0.08201, 19.99327, 98.02090}},
        {{12.49609, 7.88148, 4.99190, 2.05602, 0.57505, 0.00010},
            {3.52509, 0.16619, 9.20541, 1.71372, 24.20427, 82.21923}},
        {{10.80193, 7.89470, 5.30620, 3.91136, 0.08693, 0.00010},
            {3.67800, 0.15468, 2.08510, 9.11568, 34.76155, 99.34953}},
        {{8.64238, 8.44015, 7.88210, 2.99985, 0.03590, 0.00010},
            {3.75852, 2.14595, 0.14366, 8.16207, 30.93576, 72.31449}},
        {{14.72809, 7.73340, 4.08153, 3.89920, 2.84995, 2.70412},
            {1.87781, 0.11285, 23.45650, 3.65207, 21.50646, 68.50430}},
        {{17.72736, 7.70846, 6.22707, 4.23320, 0.10456, 0.00010},
            {1.68258, 0.09962, 13.34713, 25.64859, 76.90928, 199.99860}},
        {{13.56253, 9.15282, 7.57461, 4.23621, 1.47524, 0.00010},
            {1.52639, 13.37893, 0.09009, 1.50827, 28.97999, 162.86130}},
        {{17.83594, 10.00061, 7.34299, 0.76995, 0.05161, 0.00010},
            {1.37290, 11.94201, 0.07979, 27.59179, 0.08311, 137.72530}},
        {{17.88797, 10.57832, 7.18725, 0.34750, 0.00010, 0.00010},
            {1.24006, 10.60035, 0.06944, 29.00543, 131.45550, 1.67829}},
        {{17.94269, 11.64938, 7.03542, 1.17571, 0.20353, 0.00010},
            {1.13911, 10.82291, 0.06147, 34.40293, 1.15832, 134.27490}},
        {{17.35713, 10.99074, 7.04050, 0.57079, 0.04542, 0.00010},
            {1.13181, 9.52278, 0.06199, 1.11378, 134.27980, 38.40765}},
        {{16.70847, 11.98967, 6.70451, 1.98553, 1.61267, 0.00010},
            {1.02628, 9.86398, 0.04848, 26.23584, 1.02613, 83.38388}},
        {{16.84671, 11.18317, 6.67150, 1.21668, 0.08306, 0.00010},
            {1.01489, 8.31776, 0.04772, 1.01511, 36.37142, 83.39908}},
        {{16.20121, 13.68489, 5.92693, 2.62037, 2.56751, 0.00010},
            {0.83651, 8.66621, 0.02083, 0.83653, 22.32915, 67.41669}},
        {{15.97671, 13.58921, 5.91839, 2.79182, 1.72564, 0.00010},
            {0.83452, 8.38679, 0.02066, 0.83387, 21.20783, 67.42265}},
        {{14.55243, 14.36520, 5.43109, 3.60085, 2.86567, 1.18601},
            {8.09600, 0.75250, 0.00422, 0.75381, 21.00325, 0.75895}},
        {{14.57165, 14.10996, 5.40851, 3.65768, 1.90013, 1.35484},
            {7.90759, 0.75012, 0.00354, 0.75338, 19.97214, 0.75124}},
        {{19.27390, 15.67787, 5.26036, 3.78685, 0.00010, 0.00010},
            {0.69511, 7.84482, 0.00010, 22.21775, 60.82368, 1.12994}},
        {{19.16608, 15.58248, 5.24991, 1.97949, 0.02452, 0.00010},
            {0.69220, 7.50980, 0.00010, 19.35021, 0.69139, 60.83056}},
        {{19.29333, 16.76786, 5.18419, 4.69146, 0.06334, 0.00010},
            {0.64534, 7.54710, 0.00010, 23.16034, 100.32570, 2.35114}},
        {{19.26038, 16.76118, 5.17728, 3.80102, 0.00010, 0.00010},
            {0.64383, 7.44215, 0.00010, 21.24567, 100.31430, 2.43992}},
        {{19.24328, 17.81622, 5.07556, 3.86538, 0.00010, 0.00010},
            {0.59548, 7.03822, 0.00010, 20.12238, 87.60555, 31.88584}},
        {{19.15099, 19.02664, 5.11556, 1.72846, 1.00259, 0.00010},
            {0.55860, 6.79490, 0.00370, 25.60539, 8.23095, 93.69624}},
        {{19.14517, 19.11002, 4.80720, 4.48861, 0.25075, 0.20103},
            {5.86776, 0.50516, 0.00010, 24.33452, 87.00222, 31.41846}},
        {{19.71431, 19.14550, 4.79767, 2.34645, 0.00010, 0.00010},
            {6.04052, 0.50506, 0.00010, 16.17828, 87.05909, 31.49791}},
        {{19.06093, 12.90928, 6.64901, 4.63278, 4.60732, 0.14140},
            {0.46390, 5.35884, 5.35853, 0.00010, 21.75129, 70.66362}},
        {{19.55274, 19.11016, 4.62585, 1.75378, 0.96170, 0.00010},
            {5.57560, 0.46433, 0.00010, 15.08594, 5.57571, 70.66860}},
        {{18.97534, 15.68841, 6.74714, 4.42194, 4.08431, 4.06854},
            {0.38165, 4.33217, 26.51128, 4.35007, 0.00013, 70.73529}}
    };

    {
        for (int i = 0; i < atoms.length; i++) {
            formfactors.put(atomsi[i].intern(), ffactors[i]);
        }
    }
    private final Atom atom;
    private double xyz[] = new double[3];
    private final double biso;
    private final double uadd;
    private final double uaniso[];
    private final double occ;
    private final double a[] = new double[6];
    private final double ainv[] = new double[6];
    private final double b[] = new double[6];
    private final double binv[] = new double[6];
    private final double u[][][] = new double[6][3][3];
    private final double uinv[][][] = new double[6][3][3];
    private final int n;

    public FormFactor(Atom atom, double badd, double xyz[]) {
        this.atom = atom;
        this.uadd = badd / eightpi2;
        double ffactor[][] = new double[2][6];
        ffactor = getFormFactor("" + atom.getAtomType().atomicNumber);
        int i;
        for (i = 0; i < 6; i++) {
            if (ffactor[0][i] < 0.01) {
                break;
            }
            a[i] = ffactor[0][i];
            b[i] = ffactor[1][i];
        }
        n = i;
        assert (n > 0);
        this.xyz[0] = xyz[0];
        this.xyz[1] = xyz[1];
        this.xyz[2] = xyz[2];
        occ = atom.getOccupancy();
        biso = atom.getTempFactor();

        if (atom.getAnisou() != null) {
            // first check the ANISOU is valid
            uaniso = atom.getAnisou();
            u[0][0][0] = uaniso[0];
            u[0][1][1] = uaniso[1];
            u[0][2][2] = uaniso[2];
            u[0][0][1] = u[0][1][0] = uaniso[3];
            u[0][0][2] = u[0][2][0] = uaniso[4];
            u[0][1][2] = u[0][2][1] = uaniso[5];


            RealMatrix m = new Array2DRowRealMatrix(u[0], true);
            EigenDecompositionImpl evd = new EigenDecompositionImpl(m, 0.01);
            if (evd.getRealEigenvalue(0) <= 0.0
                    || evd.getRealEigenvalue(1) <= 0.0
                    || evd.getRealEigenvalue(2) <= 0.0) {
                StringBuffer sb = new StringBuffer();
                sb.append("non-positive definite ANISOU for atom: " + atom.toString() + "\n");
                sb.append("resetting ANISOU based on isotropic B: (" + biso + ")\n");
                logger.warning(sb.toString());

                uaniso[0] = uaniso[1] = uaniso[2] = biso / eightpi2;
                uaniso[3] = uaniso[4] = uaniso[5] = 0.0;
                atom.setAnisou(uaniso);
            }
        } else {
            uaniso = new double[6];
            uaniso[0] = uaniso[1] = uaniso[2] = biso / eightpi2;
            uaniso[3] = uaniso[4] = uaniso[5] = 0.0;
        }

        for (i = 0; i < n; i++) {
            u[i][0][0] = uaniso[0] + (b[i] / eightpi2) + uadd;
            u[i][1][1] = uaniso[1] + (b[i] / eightpi2) + uadd;
            u[i][2][2] = uaniso[2] + (b[i] / eightpi2) + uadd;
            u[i][0][1] = u[i][1][0] = uaniso[3];
            u[i][0][2] = u[i][2][0] = uaniso[4];
            u[i][1][2] = u[i][2][1] = uaniso[5];

            RealMatrix m = new Array2DRowRealMatrix(u[i], true);
            m = new LUDecompositionImpl(m).getSolver().getInverse();
            uinv[i] = m.getData();

            double det = determinant3(u[i]);
            ainv[i] = a[i] / sqrt(det);
            b[i] = pow(det, 0.33333333333);
            det = determinant3(uinv[i]);
            binv[i] = pow(det, 0.3333333333);
        }
    }

    public FormFactor(Atom atom) {
        this(atom, 0.0);
    }

    public FormFactor(Atom atom, double badd) {
        this(atom, badd, atom.getXYZ());
    }

    public static double[] getFormFactorA(String atom) {
        double ffactor[][] = null;

        if (formfactors.containsKey(atom)) {
            ffactor = (double[][]) formfactors.get(atom);
        } else {
            String message = "Form factor for atom: " + atom
                    + " not found!";
            logger.severe(message);
        }

        return ffactor[0];
    }

    public static double[] getFormFactorB(String atom) {
        double ffactor[][] = null;

        if (formfactors.containsKey(atom)) {
            ffactor = (double[][]) formfactors.get(atom);
        } else {
            String message = "Form factor for atom: " + atom
                    + " not found!";
            logger.severe(message);
        }

        return ffactor[1];
    }

    public static double[][] getFormFactor(String atom) {
        double ffactor[][] = null;

        if (formfactors.containsKey(atom)) {
            ffactor = (double[][]) formfactors.get(atom);
        } else {
            String message = "Form factor for atom: " + atom
                    + " not found!";
            logger.severe(message);
        }

        return ffactor;
    }

    public double f(HKL hkl) {
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            sum += a[i] * exp(-twopi2 * Crystal.quad_form(hkl, u[i]));
        }
        return occ * sum;
    }

    public double rho(double xyz[]) {
        double dxyz[] = new double[3];
        diff(xyz, this.xyz, dxyz);
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            sum += ainv[i] * exp(-0.5 * Crystal.quad_form(dxyz, uinv[i]));
        }
        return occ * twopi32 * sum;
    }

    public double rho_gauss(double xyz[], double sd) {
        double dxyz[] = new double[3];
        diff(xyz, this.xyz, dxyz);
        return rho_gauss(rsq(dxyz), sd);
    }

    public double rho_gauss(double rsq, double sd) {
        double sd2 = sd * sd;
        return exp(-rsq / (2.0 * sd2));
    }

    public void rho_grad(double xyz[]) {
        double dxyz[] = new double[3];
        diff(xyz, this.xyz, dxyz);
        double r2 = rsq(dxyz);
        double aex;
        double jmat[][][] = new double[6][3][3];
        double gradp[] = {0.0, 0.0, 0.0, 0.0, 0.0};
        double gradu[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        double rho;
        double g[];

        for (int i = 0; i < n; i++) {
            aex = ainv[i] * exp(-0.5 * Crystal.quad_form(dxyz, uinv[i]));

            gradp[0] += aex * dot(vec3mat3(dxyz, uinv[i]), vx);
            gradp[1] += aex * dot(vec3mat3(dxyz, uinv[i]), vy);
            gradp[2] += aex * dot(vec3mat3(dxyz, uinv[i]), vz);
            gradp[3] += aex;
            gradp[4] += aex * 0.5 * (r2 * binv[i] * binv[i] - 3.0 * binv[i]);

            jmat[0] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u11), uinv[i]);
            jmat[1] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u22), uinv[i]);
            jmat[2] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u33), uinv[i]);
            jmat[3] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u12), uinv[i]);
            jmat[4] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u13), uinv[i]);
            jmat[5] = mat3mat3(scalarmat3mat3(-1.0, uinv[i], u23), uinv[i]);

            gradu[0] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[0]) - uinv[i][0][0]);
            gradu[1] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[1]) - uinv[i][1][1]);
            gradu[2] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[2]) - uinv[i][2][2]);
            gradu[3] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[3]) - uinv[i][0][1] * 2.0);
            gradu[4] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[4]) - uinv[i][0][2] * 2.0);
            gradu[5] += aex * 0.5 * (-Crystal.quad_form(dxyz, jmat[5]) - uinv[i][1][2] * 2.0);
        }
        rho = occ * twopi32 * gradp[3];

        // x, y, z
        atom.setXYZGradient(
                occ * twopi32 * gradp[0],
                occ * twopi32 * gradp[1],
                occ * twopi32 * gradp[2]);
        // occ
        atom.setOccupancyGradient(twopi32 * gradp[3]);
        // Biso
        atom.setTempFactorGradient(occ * twopi32 * gradp[4]);
        // Uaniso
        if (atom.getAnisou() != null) {
            g = atom.getAnisouGradient();
            g[0] = occ * twopi32 * gradu[0];
            g[1] = occ * twopi32 * gradu[1];
            g[2] = occ * twopi32 * gradu[2];
            g[3] = occ * twopi32 * gradu[3];
            g[4] = occ * twopi32 * gradu[4];
            g[5] = occ * twopi32 * gradu[5];
        }
    }

    public void rho_gauss_grad(double xyz[], double sd) {
        double dxyz[] = new double[3];
        diff(xyz, this.xyz, dxyz);
        double r2 = rsq(dxyz);

        double rho = exp(-0.5 * r2 / sd);

        double g[] = new double[3];
        g[0] = rho * dxyz[0] / sd;
        g[1] = rho * dxyz[1] / sd;
        g[2] = rho * dxyz[2] / sd;

        atom.setXYZGradient(g[0], g[1], g[2]);
    }

    public void setXYZ(double xyz[]) {
        this.xyz[0] = xyz[0];
        this.xyz[1] = xyz[1];
        this.xyz[2] = xyz[2];
    }
}
