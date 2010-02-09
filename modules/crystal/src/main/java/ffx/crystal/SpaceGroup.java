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
package ffx.crystal;

import static ffx.crystal.SpaceGroup.CrystalSystem.CUBIC;
import static ffx.crystal.SpaceGroup.CrystalSystem.HEXAGONAL;
import static ffx.crystal.SpaceGroup.CrystalSystem.MONOCLINIC;
import static ffx.crystal.SpaceGroup.CrystalSystem.ORTHORHOMBIC;
import static ffx.crystal.SpaceGroup.CrystalSystem.TETRAGONAL;
import static ffx.crystal.SpaceGroup.CrystalSystem.TRICLINIC;
import static ffx.crystal.SpaceGroup.CrystalSystem.TRIGONAL;

import static ffx.crystal.SpaceGroup.LaueSystem.L111;
import static ffx.crystal.SpaceGroup.LaueSystem.L121;
import static ffx.crystal.SpaceGroup.LaueSystem.L222;
import static ffx.crystal.SpaceGroup.LaueSystem.L114;
import static ffx.crystal.SpaceGroup.LaueSystem.L224;
import static ffx.crystal.SpaceGroup.LaueSystem.L113;
import static ffx.crystal.SpaceGroup.LaueSystem.L223;
import static ffx.crystal.SpaceGroup.LaueSystem.L32U;
import static ffx.crystal.SpaceGroup.LaueSystem.LM3B;
import static ffx.crystal.SpaceGroup.LaueSystem.LM3M;

import java.util.Vector;

/**
 * The Spacegroup class defines the symmetry of a crystal. There are 230
 * distinct space groups in three dimensions.
 *
 * @author Michael J. Schnieders
 * @see <a href="http://it.iucr.org/Ab/">International Tables for
 *      Crystallography Volume A: Space-group symmetry</a>
 * @since 1.0
 */
public class SpaceGroup {

    /**
     * Enumeration of the 7 crystal systems.
     *
     * @author Michael J. Schnieders
     * @since 1.0
     */
    public enum CrystalSystem {

        TRICLINIC, MONOCLINIC, ORTHORHOMBIC, TETRAGONAL, TRIGONAL, HEXAGONAL, CUBIC
    }

    /**
     * Enumeration of the different ASU limits.
     * Some are only used for nonstandard cells.
     */
    public enum LaueSystem {

        L111, L112, L121, L211, L21U, L21V, L21W, L21X, L21Y, L21Z, L222, L22U,
        L22V, L22W, L114, L141, L411, L224, L242, L422, L113, L131, L311, L11T,
        L1T1, LT11, L31A, L31B, L31C, L31D, L223, L232, L322, L32A, L32B, L32C,
        L32D, L32U, L32V, L32W, L32X, L32Y, L32Z, LM3B, LM3M
    }
    /**
     * Space group number.
     */
    public final int number;
    /**
     * Number of symmetry equivalents.
     */
    private final int numSymEquiv;
    /**
     * Number of primitive symmetry equivalents.
     */
    private final int numPrimitiveSymEquiv;
    /**
     * Space group name.
     */
    public final String shortName;
    /**
     * Point group name. There are 32 distinct points groups, or crystal classes
     * in three dimensions.
     */
    public final String pointGroupName;
    /**
     * Crystal system.
     */
    public final CrystalSystem crystalSystem;
    /**
     * Laue group
     */
    public final LaueSystem laueSystem;
    /**
     * Space group name under the PDB convention.
     */
    public final String pdbName;
    /**
     * A Vector of SymOp instances.
     */
    public final Vector<SymOp> symOps;
    /**
     * Names of the 230 three dimensional space groups.
     * @since 1.0
     */
    public static String[] spaceGroupNames = {"P1", "P-1", "P2", "P21", "C2",
        "Pm", "Pc", "Cm", "Cc", "P2/m", "P21/m", "C2/m", "P2/c", "P21/c",
        "C2/c", "P222", "P2221", "P21212", "P212121", "C2221", "C222",
        "F222", "I222", "I212121", "Pmm2", "Pmc21", "Pcc2", "Pma2",
        "Pca21", "Pnc2", "Pmn21", "Pba2", "Pna21", "Pnn2", "Cmm2", "Cmc21",
        "Ccc2", "Amm2", "Abm2", "Ama2", "Aba2", "Fmm2", "Fdd2", "Imm2",
        "Iba2", "Ima2", "Pmmm", "Pnnn", "Pccm", "Pban", "Pmma", "Pnna",
        "Pmna", "Pcca", "Pbam", "Pccn", "Pbcm", "Pnnm", "Pmmn", "Pbcn",
        "Pbca", "Pnma", "Cmcm", "Cmca", "Cmmm", "Cccm", "Cmma", "Ccca",
        "Fmmm", "Fddd", "Immm", "Ibam", "Ibca", "Imma", "P4", "P41", "P42",
        "P43", "I4", "I41", "P-4", "I-4", "P4/m", "P42/m", "P4/n", "P42/n",
        "I4/m", "I41/a", "P422", "P4212", "P4122", "P41212", "P4222",
        "P42212", "P4322", "P43212", "I422", "I4122", "P4mm", "P4bm",
        "P42cm", "P42nm", "P4cc", "P4nc", "P42mc", "P42bc", "I4mm", "I4cm",
        "I41md", "I41cd", "P-42m", "P-42c", "P-421m", "P-421c", "P-4m2",
        "P-4c2", "P-4b2", "P-4n2", "I-4m2", "I-4c2", "I-42m", "I-42d",
        "P4/mmm", "P4/mcc", "P4/nbm", "P4/nnc", "P4/mbm", "P4/mnc",
        "P4/nmm", "P4/ncc", "P42/mmc", "P42/mcm", "P42/nbc", "P42/nnm",
        "P42/mbc", "P42/mnm", "P42/nmc", "P42/ncm", "I4/mmm", "I4/mcm",
        "I41/amd", "I41/acd", "P3", "P31", "P32", "H3", "P-3", "H-3",
        "P312", "P321", "P3112", "P3121", "P3212", "P3221", "H32", "P3m1",
        "P31m", "P3c1", "P31c", "H3m", "H3c", "P-31m", "P-31c", "P-3m1",
        "P-3c1", "H-3m", "H-3c", "P6", "P61", "P65", "P62", "P64", "P63",
        "P-6", "P6/m", "P63/m", "P622", "P6122", "P6522", "P6222", "P6422",
        "P6322", "P6mm", "P6cc", "P63cm", "P63mc", "P-6m2", "P-6c2",
        "P-62m", "P-62c", "P6/mmm", "P6/mcc", "P63/mcm", "P63/mmc", "P23",
        "F23", "I23", "P213", "I213", "Pm-3", "Pn-3", "Fm-3", "Fd-3",
        "Im-3", "Pa-3", "Ia-3", "P432", "P4232", "F432", "F4132", "I432",
        "P4332", "P4132", "I4132", "P-43m", "F-43m", "I-43m", "P-43n",
        "F-43c", "I-43d", "Pm-3m", "Pn-3n", "Pm-3n", "Pn-3m", "Fm-3m",
        "Fm-3c", "Fd-3m", "Fd-3c", "Im-3m", "Ia-3d"};

    public static String pdbSpaceGroupNames[] = {"P 1", "P -1", "P 1 2 1",
    "P 1 21 1", "C 1 2 1", "P 1 m 1", "P 1 c 1", "C 1 m 1", "C 1 c 1",
    "P 1 2/m 1", "P 1 21/m 1", "C 1 2/m 1", "P 1 2/c 1", "P 1 21/c 1",
    "C 1 2/c 1", "P 2 2 2", "P 2 2 21", "P 21 21 2", "P 21 21 21",
    "C 2 2 21", "C 2 2 2", "F 2 2 2", "I 2 2 2", "I 21 21 21", "P m m 2",
    "P m c 21", "P c c 2", "P m a 2", "P c a 21", "P n c 2", "P m n 21",
    "P b a 2", "P n a 21", "P n n 2", "C m m 2", "C m c 21", "C c c 2",
    "A m m 2", "A b m 2", "A m a 2", "A b a 2", "F m m 2", "F d d 2",
    "I m m 2", "I b a 2", "I m a 2", "P 2/m 2/m 2/m", "P 2/n 2/n 2/n",
    "P 2/c 2/c 2/m", "P 2/b 2/a 2/n", "P 21/m 2/m 2/a", "P 2/n 21/n 2/a",
    "P 2/m 2/n 21/a", "P 21/c 2/c 2/a", "P 21/b 21/a 2/m", "P 21/c 21/c 2/n",
    "P 2/b 21/c 21/m", "P 21/n 21/n 2/m", "P 21/m 21/m 2/n", "P 21/b 2/c 21/n",
    "P 21/b 21/c 21/a", "P 21/n 21/m 21/a", "C 2/m 2/c 21/m", "C 2/m 2/c 21/a",
    "C 2/m 2/m 2/m", "C 2/c 2/c 2/m", "C 2/m 2/m 2/a", "C 2/c 2/c 2/a",
    "F 2/m 2/m 2/m", "F 2/d 2/d 2/d", "I 2/m 2/m 2/m", "I 2/b 2/a 2/m",
    "I 21/b 21/c 21/a", "I 21/m 21/m 21/a", "P 4", "P 41", "P 42", "P 43",
    "I 4", "I 41", "P -4", "I -4", "P 4/m", "P 42/m", "P 4/n", "P 42/n",
    "I 4/m", "I 41/a", "P 4 2 2", "P 4 21 2", "P 41 2 2", "P 41 21 2",
    "P 42 2 2", "P 42 21 2", "P 43 2 2", "P 43 21 2", "I 4 2 2", "I 41 2 2",
    "P 4 m m", "P 4 b m", "P 42 c m", "P 42 n m", "P 4 c c", "P 4 n c",
    "P 42 m c", "P 42 b c", "I 4 m m", "I 4 c m", "I 41 m d", "I 41 c d",
    "P -4 2 m", "P -4 2 c", "P -4 21 m", "P -4 21 c", "P -4 m 2", "P -4 c 2",
    "P -4 b 2", "P -4 n 2", "I -4 m 2", "I -4 c 2", "I -4 2 m", "I -4 2 d",
    "P 4/m 2/m 2/m", "P 4/m 2/c 2/c", "P 4/n 2/b 2/m", "P 4/n 2/n 2/c",
    "P 4/m 21/b 2/m", "P 4/m 21/n 2/c", "P 4/n 21/m 2/m", "P 4/n 2/c 2/c",
    "P 42/m 2/m 2/c", "P 42/m 2/c 2/m", "P 42/n 2/b 2/c", "P 42/n 2/n 2/m",
    "P 42/m 21/b 2/c", "P 42/m 21/n 2/m", "P 42/n 21/m 2/c", "P 42/n 21/c 2/m",
    "I 4/m 2/m 2/m", "I 4/m 2/c 2/m", "I 41/a 2/m 2/d", "I 41/a 2/c 2/d", "P 3",
    "P 31", "P 32", "H 3", "P -3", "H -3", "P 3 1 2", "P 3 2 1", "P 31 1 2",
    "P 31 2 1", "P 32 1 2", "P 32 2 1", "H 3 2", "P 3 m 1", "P 3 1 m",
    "P 3 c 1", "P 3 1 c", "H 3 m", "H 3 c", "P -3 1 2/m", "P -3 1 2/c",
    "P -3 2/m 1", "P -3 2/c 1", "H -3 2/m", "H -3 2/c", "P 6", "P 61", "P 65",
    "P 62", "P 64", "P 63", "P -6", "P 6/m", "P 63/m", "P 6 2 2", "P 61 2 2",
    "P 65 2 2", "P 62 2 2", "P 64 2 2", "P 63 2 2", "P 6 m m", "P 6 c c",
    "P 63 c m", "P 63 m c", "P -6 m 2", "P -6 c 2", "P -6 2 m", "P -6 2 c",
    "P 6/m 2/m 2/m", "P 6/m 2/c 2/c", "P 63/m 2/c 2/m", "P 63/m 2/m 2/c",
    "P 2 3", "F 2 3", "I 2 3", "P 21 3", "I 21 3", "P 2/m -3", "P 2/n -3",
    "F 2/m -3", "F 2/d -3", "I 2/m -3", "P 21/a -3", "I 21/a -3", "P 4 3 2",
    "P 42 3 2", "F 4 3 2", "F 41 3 2", "I 4 3 2", "P 43 3 2", "P 41 3 2",
    "I 41 3 2", "P -4 3 m", "F -4 3 m", "I -4 3 m", "P -4 3 n", "F -4 3 c",
    "I -4 3 d", "P 4/m -3 2/m", "P 4/n -3 2/n", "P 42/m -3 2/n",
    "P 42/n -3 2/m", "F 4/m -3 2/m", "F 4/m -3 2/c", "F 41/d -3 2/m",
    "F 41/d -3 2/c", "I 4/m -3 2/m", "I 41/a -3 2/d"};

    /**
     * Immutable SpaceGroup instances are made available only through the
     * factory method so this constructor is private.
     *
     * @param number
     * @param numSymEquiv
     * @param numPrimitiveSymEquiv
     * @param shortName
     * @param pointGroupName
     * @param crystalSystem
     * @param laueSystem
     * @param pdbName
     * @param symOps
     * @since 1.0
     */
    private SpaceGroup(int number, int numSymEquiv, int numPrimitiveSymEquiv,
            String shortName, String pointGroupName,
            CrystalSystem crystalSystem, LaueSystem laueSystem, String pdbName,
            SymOp... symOps) {
        this.number = number;
        this.numSymEquiv = numSymEquiv;
        this.numPrimitiveSymEquiv = numPrimitiveSymEquiv;
        this.shortName = shortName;
        this.pointGroupName = pointGroupName;
        this.crystalSystem = crystalSystem;
        this.laueSystem = laueSystem;
        this.pdbName = pdbName;
        this.symOps = new Vector<SymOp>(symOps.length);
        for (SymOp op : symOps) {
            this.symOps.add(op);
        }
    }

    /**
     * Check the given HKL is valid given the crystal/Laue system
     * @param laueSystem
     * @param h
     * @param k
     * @param l
     * @return True if the reflection is valid, false otherwise
     */
    public static boolean checkLaueRestrictions(LaueSystem laueSystem,
            int h, int k, int l) {
        switch (laueSystem) {
            case L111:
                return (l > 0 || (l == 0 && (h > 0 || (h == 0 && k >= 0))));
            case L112:
                return (l >= 0 && (h > 0 || (h == 0 && k >= 0)));
            case L121:
                return (k >= 0 && (l > 0 || (l == 0 && h >= 0)));
            case L211:
                return (h >= 0 && (k > 0 || (k == 0 && l >= 0)));
            case L21U:
                return (h + k >= 0 && (l > 0 || (l == 0 && h - k >= 0)));
            case L21V:
                return (l + h >= 0 && (k > 0 || (k == 0 && l - h >= 0)));
            case L21W:
                return (k + l >= 0 && (h > 0 || (h == 0 && k - l >= 0)));
            case L21X:
                return (h - k >= 0 && (l > 0 || (l == 0 && h + k >= 0)));
            case L21Y:
                return (l - h >= 0 && (k > 0 || (k == 0 && l + h >= 0)));
            case L21Z:
                return (k - l >= 0 && (h > 0 || (h == 0 && k + l >= 0)));
            case L222:
                return (h >= 0 && k >= 0 && l >= 0);
            case L22U:
                return (h <= k && h >= -k && l >= 0);
            case L22V:
                return (l <= h && l >= -h && k >= 0);
            case L22W:
                return (k <= l && k >= -l && h >= 0);
            case L114:
                return (l >= 0 && ((h >= 0 && k > 0) || (h == 0 && k == 0)));
            case L141:
                return (k >= 0 && ((l >= 0 && h > 0) || (l == 0 && h == 0)));
            case L411:
                return (h >= 0 && ((k >= 0 && l > 0) || (k == 0 && l == 0)));
            case L224:
                return (h >= k && k >= 0 && l >= 0);
            case L242:
                return (l >= h && h >= 0 && k >= 0);
            case L422:
                return (k >= l && l >= 0 && h >= 0);
            case L113:
                return (h >= 0 && k > 0) || (h == 0 && k == 0 && l >= 0);
            case L131:
                return (l >= 0 && h > 0) || (l == 0 && h == 0 && k >= 0);
            case L311:
                return (k >= 0 && l > 0) || (k == 0 && l == 0 && h >= 0);
            case L11T:
                return (h <= 0 && k > 0) || (h == 0 && k == 0 && l >= 0);
            case L1T1:
                return (l <= 0 && h > 0) || (l == 0 && h == 0 && k >= 0);
            case LT11:
                return (k <= 0 && l > 0) || (k == 0 && l == 0 && h >= 0);
            case L31A:
                return (k - l >= 0 && l - h > 0) || (h == l && k == l && h + k + l >= 0);
            case L31B:
                return (k - l >= 0 && l + h > 0) || (-h == l && k == l && -h + k + l >= 0);
            case L31C:
                return (-k - l >= 0 && l - h > 0) || (h == l && -k == l && h - k + l >= 0);
            case L31D:
                return (k + l >= 0 && -l - h > 0) || (h == -l && k == -l && h + k - l >= 0);
            case L223:
                return (h >= k && k >= 0 && (k > 0 || l >= 0));
            case L232:
                return (l >= h && h >= 0 && (h > 0 || k >= 0));
            case L322:
                return (k >= l && l >= 0 && (l > 0 || h >= 0));
            case L32A:
                return (h >= k && k + l >= h + h && (k + l > h + h || h + k + l >= 0));
            case L32B:
                return (-h >= k && k + l >= -h - h && (k + l > -h - h || -h + k + l >= 0));
            case L32C:
                return (h >= -k && -k + l >= h + h && (-k + l > h + h || h - k + l >= 0));
            case L32D:
                return (h >= k && k - l >= h + h && (k - l > h + h || h + k - l >= 0));
            case L32U:
                return (h >= k && k >= 0 && (h > k || l >= 0));
            case L32V:
                return (k >= l && l >= 0 && (k > l || h >= 0));
            case L32W:
                return (l >= h && h >= 0 && (l > h || k >= 0));
            case L32X:
                return (-h >= k && k >= 0 && (-h > k || l >= 0));
            case L32Y:
                return (-k >= l && l >= 0 && (-k > l || h >= 0));
            case L32Z:
                return (-l >= h && h >= 0 && (-l > h || k >= 0));
            case LM3B:
                return (h >= 0 && ((l >= h && k > h) || (l == h && k == h)));
            case LM3M:
                return (k >= l && l >= h && h >= 0);
            default:
                assert (2 != 2);
                return false;
        }
    }

    /**
     * Check that the lattice parameters satisfy the restrictions of the
     * crystal systems.
     *
     * @param crystalSystem
     * @param a
     * @param b
     * @param c
     * @param alpha
     * @param beta
     * @param gamma
     * @return True if the restrictions are satisfied, false otherwise.
     */
    public static boolean checkRestrictions(CrystalSystem crystalSystem,
            double a, double b, double c,
            double alpha, double beta, double gamma) {
        switch (crystalSystem) {
            case TRICLINIC:
                return true;
            case MONOCLINIC:
                return (alpha == beta || alpha == gamma);
            case ORTHORHOMBIC:
                return (alpha == 90.0 && beta == 90.0 && gamma == 90.0);
            case TETRAGONAL:
                return (a == b && alpha == 90.0 && beta == 90.0 && gamma == 90.0);
            case TRIGONAL:
                return (// Rombohedral axes, primitive cell.
                        (a == b && b == c && alpha == beta && beta == gamma)
                        || // Hexagonal axes, triple obverse cell.
                        (a == b && alpha == 90.0 && beta == 90.0 && gamma == 120.0));
            case HEXAGONAL:
                return (a == b && alpha == 90.0 && beta == 90.0 && gamma == 120.0);
            case CUBIC:
                return (a == b && b == c && alpha == 90.0 && beta == 90.0 && gamma == 90.0);
            default:
                assert (2 != 2);
                return false;
        }

    }

    /**
     * Return the ith symmetry operator.
     * @param i the symmetry operator number.
     * @return the SymOp
     * @since 1.0
     */
    public SymOp getSymOp(int i) {
        return symOps.get(i);
    }

    /**
     * Return the number of symmetry operators.
     * @return the number of symmetry operators.
     * @since 1.0
     */
    public int getNumberOfSymOps() {
        return symOps.size();
    }

    /**
     * Returns a SpaceGroup instance corresponding to the number parameter.
     * If number is not between 1-230 inclusive then null is returned.
     *
     * @param number All 230 3D spacegroups are available.
     * @return The space group corresponding to the given number.
     * @since 1.0
     */
    public static SpaceGroup spaceGroupFactory(int number) {
        if (number > 0 && number <= 100) {
            return getSpaceGroup1(number);
        } else if (number > 100 && number <= 200) {
            return getSpaceGroup2(number);
        } else if (number > 200 && number <= 230) {
            return getSpaceGroup3(number);
        }
        return null;
    }

    /**
     * Return a SpaceGroup based on its name.
     * @param pdbName Available SpaceGroup names are given in
     *              the "spaceGroupName" array.
     * @return The space group corresponding to the given number.
     * @since 1.0
     */
    public static SpaceGroup spaceGroupFactory(String name) {
        return spaceGroupFactory(spaceGroupNumber(name));
    }

    /**
     * There are 3 private "getSpaceGroupX" routines because if they are not
     * split up, a Java specification limit on method size is exceeded.
     *
     * @param num
     * @return
     */
    private static SpaceGroup getSpaceGroup1(int num) {
        SpaceGroup spaceGroup = null;
        switch (num) {
            case 1:
                spaceGroup = new SpaceGroup(1, 1, 1, "P1", "PG1", TRICLINIC,
                        L111, "P 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0));
                break;
            case 2:
                spaceGroup = new SpaceGroup(2, 2, 2, "P-1", "PG1bar", TRICLINIC,
                        L111, "P -1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0));
                break;
            case 3:
                spaceGroup = new SpaceGroup(3, 2, 2, "P2", "PG2", MONOCLINIC,
                        L121, "P 1 2 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0));
                break;
            case 4:
                spaceGroup = new SpaceGroup(4, 2, 2, "P21", "PG2", MONOCLINIC,
                        L121, "P 1 21 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_0));
                break;
            case 5:
                spaceGroup = new SpaceGroup(5, 4, 2, "C2", "PG2", MONOCLINIC,
                        L121, "C 1 2 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0));
                break;
            case 6:
                spaceGroup = new SpaceGroup(6, 2, 2, "Pm", "PGm", MONOCLINIC,
                        L121, "P 1 m 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0));
                break;
            case 7:
                spaceGroup = new SpaceGroup(7, 2, 2, "Pc", "PGm", MONOCLINIC,
                        L121, "P 1 c 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_12));
                break;
            case 8:
                spaceGroup = new SpaceGroup(8, 4, 2, "Cm", "PGm", MONOCLINIC,
                        L121, "C 1 m 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_0));
                break;
            case 9:
                spaceGroup = new SpaceGroup(9, 4, 2, "Cc", "PGm", MONOCLINIC,
                        L121, "C 1 c 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_12));
                break;
            case 10:
                spaceGroup = new SpaceGroup(10, 4, 4, "P2/m", "PG2/m", MONOCLINIC,
                        L121, "P 1 2/m 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0));
                break;
            case 11:
                spaceGroup = new SpaceGroup(11, 4, 4, "P21/m", "PG2/m", MONOCLINIC,
                        L121, "P 1 21/m 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_0));
                break;
            case 12:
                spaceGroup = new SpaceGroup(12, 8, 4, "C2/m", "PG2/m", MONOCLINIC,
                        L121, "C 1 2/m 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_0));
                break;
            case 13:
                spaceGroup = new SpaceGroup(13, 4, 4, "P2/c", "PG2/m", MONOCLINIC,
                        L121, "P 1 2/c 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_12));
                break;
            case 14:
                spaceGroup = new SpaceGroup(14, 4, 4, "P21/c", "PG2/m", MONOCLINIC,
                        L121, "P 1 21/c 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12));
                break;
            case 15:
                spaceGroup = new SpaceGroup(15, 8, 4, "C2/c", "PG2/m", MONOCLINIC,
                        L121, "C 1 2/c 1",
                        new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_12));
                break;
            case 16:
                spaceGroup = new SpaceGroup(16, 4, 4, "P222", "PG222",
                        ORTHORHOMBIC, L222, "P 2 2 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 17:
                spaceGroup = new SpaceGroup(17, 4, 4, "P2221", "PG222",
                        ORTHORHOMBIC, L222, "P 2 2 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 18:
                spaceGroup = new SpaceGroup(18, 4, 4, "P21212", "PG222",
                        ORTHORHOMBIC, L222, "P 21 21 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 19:
                spaceGroup = new SpaceGroup(19, 4, 4, "P212121", "PG222",
                        ORTHORHOMBIC, L222, "P 21 21 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 20:
                spaceGroup = new SpaceGroup(20, 8, 4, "C2221", "PG222",
                        ORTHORHOMBIC, L222, "C 2 2 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 21:
                spaceGroup = new SpaceGroup(21, 8, 4, "C222", "PG222",
                        ORTHORHOMBIC, L222, "C 2 2 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 22:
                spaceGroup = new SpaceGroup(22, 16, 4, "F222", "PG222",
                        ORTHORHOMBIC, L222, "F 2 2 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 23:
                spaceGroup = new SpaceGroup(23, 8, 4, "I222", "PG222",
                        ORTHORHOMBIC, L222, "I 2 2 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12));
                break;
            case 24:
                spaceGroup = new SpaceGroup(24, 8, 4, "I212121", "PG222",
                        ORTHORHOMBIC, L222, "I 21 21 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 25:
                spaceGroup = new SpaceGroup(25, 4, 4, "Pmm2", "PGmm2",
                        ORTHORHOMBIC, L222, "P m m 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 26:
                spaceGroup = new SpaceGroup(26, 4, 4, "Pmc21", "PGmm2",
                        ORTHORHOMBIC, L222, "P m c 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 27:
                spaceGroup = new SpaceGroup(27, 4, 4, "Pcc2", "PGmm2",
                        ORTHORHOMBIC, L222, "P c c 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 28:
                spaceGroup = new SpaceGroup(28, 4, 4, "Pma2", "PGmm2",
                        ORTHORHOMBIC, L222, "P m a 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_0));
                break;
            case 29:
                spaceGroup = new SpaceGroup(29, 4, 4, "Pca21", "PGmm2",
                        ORTHORHOMBIC, L222, "P c a 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12));
                break;
            case 30:
                spaceGroup = new SpaceGroup(30, 4, 4, "Pnc2", "PGmm2",
                        ORTHORHOMBIC, L222, "P n c 2",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12));
                break;
            case 31:
                spaceGroup = new SpaceGroup(31, 4, 4, "Pmn21", "PGmm2",
                        ORTHORHOMBIC, L222, "P m n 21",
                        new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 32:
                spaceGroup = new SpaceGroup(32, 4, 4, "Pba2", "PGmm2",
                        ORTHORHOMBIC, L222, "P b a 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 33:
                spaceGroup = new SpaceGroup(33, 4, 4, "Pna21", "PGmm2",
                        ORTHORHOMBIC, L222, "P n a 21", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 34:
                spaceGroup = new SpaceGroup(34, 4, 4, "Pnn2", "PGmm2",
                        ORTHORHOMBIC, L222, "P n n 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 35:
                spaceGroup = new SpaceGroup(35, 8, 4, "Cmm2", "PGmm2",
                        ORTHORHOMBIC, L222, "C m m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 36:
                spaceGroup = new SpaceGroup(36, 8, 4, "Cmc21", "PGmm2",
                        ORTHORHOMBIC, L222, "C m c 21", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 37:
                spaceGroup = new SpaceGroup(37, 8, 4, "Ccc2", "PGmm2",
                        ORTHORHOMBIC, L222, "C c c 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 38:
                spaceGroup = new SpaceGroup(38, 8, 4, "Amm2", "PGmm2",
                        ORTHORHOMBIC, L222, "A m m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12));
                break;
            case 39:
                spaceGroup = new SpaceGroup(39, 8, 4, "Abm2", "PGmm2",
                        ORTHORHOMBIC, L222, "A b m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 40:
                spaceGroup = new SpaceGroup(40, 8, 4, "Ama2", "PGmm2",
                        ORTHORHOMBIC, L222, "A m a 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 41:
                spaceGroup = new SpaceGroup(41, 8, 4, "Aba2", "PGmm2",
                        ORTHORHOMBIC, L222, "A b a 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12));
                break;
            case 42:
                spaceGroup = new SpaceGroup(42, 16, 4, "Fmm2", "PGmm2",
                        ORTHORHOMBIC, L222, "F m m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 43:
                spaceGroup = new SpaceGroup(43, 16, 4, "Fdd2", "PGmm2",
                        ORTHORHOMBIC, L222, "F d d 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_34_14));
                break;
            case 44:
                spaceGroup = new SpaceGroup(44, 8, 4, "Imm2", "PGmm2",
                        ORTHORHOMBIC, L222, "I m m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 45:
                spaceGroup = new SpaceGroup(45, 8, 4, "Iba2", "PGmm2",
                        ORTHORHOMBIC, L222, "I b a 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 46:
                spaceGroup = new SpaceGroup(46, 8, 4, "Ima2", "PGmm2",
                        ORTHORHOMBIC, L222, "I m a 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12));
                break;
            case 47:
                spaceGroup = new SpaceGroup(47, 8, 8, "Pmmm", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 48:
                spaceGroup = new SpaceGroup(48, 8, 8, "Pnnn", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/n 2/n 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 49:
                spaceGroup = new SpaceGroup(49, 8, 8, "Pccm", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/c 2/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 50:
                spaceGroup = new SpaceGroup(50, 8, 8, "Pban", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/b 2/a 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 51:
                spaceGroup = new SpaceGroup(51, 8, 8, "Pmma", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/m 2/m 2/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_0));
                break;
            case 52:
                spaceGroup = new SpaceGroup(52, 8, 8, "Pnna", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/n 21/n 2/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12));
                break;
            case 53:
                spaceGroup = new SpaceGroup(53, 8, 8, "Pmna", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/m 2/n 21/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 54:
                spaceGroup = new SpaceGroup(54, 8, 8, "Pcca", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/c 2/c 2/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12));
                break;
            case 55:
                spaceGroup = new SpaceGroup(55, 8, 8, "Pbam", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/b 21/a 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 56:
                spaceGroup = new SpaceGroup(56, 8, 8, "Pccn", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/c 21/c 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12));
                break;
            case 57:
                spaceGroup = new SpaceGroup(57, 8, 8, "Pbcm", "PGmmm",
                        ORTHORHOMBIC, L222, "P 2/b 21/c 21/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_0));
                break;
            case 58:
                spaceGroup = new SpaceGroup(58, 8, 8, "Pnnm", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/n 21/n 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 59:
                spaceGroup = new SpaceGroup(59, 8, 8, "Pmmn", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/m 21/m 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 60:
                spaceGroup = new SpaceGroup(60, 8, 8, "Pbcn", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/b 2/c 21/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 61:
                spaceGroup = new SpaceGroup(61, 8, 8, "Pbca", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/b 21/c 21/a", new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0));
                break;
            case 62:
                spaceGroup = new SpaceGroup(62, 8, 8, "Pnma", "PGmmm",
                        ORTHORHOMBIC, L222, "P 21/n 21/m 21/a", new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_12));
                break;
            case 63:
                spaceGroup = new SpaceGroup(63, 16, 8, "Cmcm", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/m 2/c 21/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 64:
                spaceGroup = new SpaceGroup(64, 16, 8, "Cmca", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/m 2/c 21/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 65:
                spaceGroup = new SpaceGroup(65, 16, 8, "Cmmm", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 66:
                spaceGroup = new SpaceGroup(66, 16, 8, "Cccm", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/c 2/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 67:
                spaceGroup = new SpaceGroup(67, 16, 8, "Cmma", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/m 2/m 2/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 68:
                spaceGroup = new SpaceGroup(68, 16, 8, "Ccca", "PGmmm",
                        ORTHORHOMBIC, L222, "C 2/c 2/c 2/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12));
                break;
            case 69:
                spaceGroup = new SpaceGroup(69, 32, 8, "Fmmm", "PGmmm",
                        ORTHORHOMBIC, L222, "F 2/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 70:
                spaceGroup = new SpaceGroup(70, 32, 8, "Fddd", "PGmmm",
                        ORTHORHOMBIC, L222, "F 2/d 2/d 2/d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_34_14));
                break;
            case 71:
                spaceGroup = new SpaceGroup(71, 16, 8, "Immm", "PGmmm",
                        ORTHORHOMBIC, L222, "I 2/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 72:
                spaceGroup = new SpaceGroup(72, 16, 8, "Ibam", "PGmmm",
                        ORTHORHOMBIC, L222, "I 2/b 2/a 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 73:
                spaceGroup = new SpaceGroup(73, 16, 8, "Ibca", "PGmmm",
                        ORTHORHOMBIC, L222, "I 21/b 21/c 21/a", new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_12));
                break;
            case 74:
                spaceGroup = new SpaceGroup(74, 16, 8, "Imma", "PGmmm",
                        ORTHORHOMBIC, L222, "I 21/m 21/m 21/a", new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_12));
                break;
            case 75:
                spaceGroup = new SpaceGroup(75, 4, 4, "P4", "PG4", TETRAGONAL,
                        L114, "P 4", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0));
                break;
            case 76:
                spaceGroup = new SpaceGroup(76, 4, 4, "P41", "PG4", TETRAGONAL,
                        L114, "P 41", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_34));
                break;
            case 77:
                spaceGroup = new SpaceGroup(77, 4, 4, "P42", "PG4", TETRAGONAL,
                        L114, "P 42", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_12));
                break;
            case 78:
                spaceGroup = new SpaceGroup(78, 4, 4, "P43", "PG4", TETRAGONAL,
                        L114, "P 43", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_14));
                break;
            case 79:
                spaceGroup = new SpaceGroup(79, 8, 4, "I4", "PG4", TETRAGONAL,
                        L114, "I 4", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12));
                break;
            case 80:
                spaceGroup = new SpaceGroup(80, 8, 4, "I41", "PG4", TETRAGONAL,
                        L114, "I 41", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_12_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_0_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_0_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_12_14));
                break;
            case 81:
                spaceGroup = new SpaceGroup(81, 4, 4, "P-4", "PG4bar", TETRAGONAL,
                        L114, "P -4", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0));
                break;
            case 82:
                spaceGroup = new SpaceGroup(82, 8, 4, "I-4", "PG4bar", TETRAGONAL,
                        L114, "I -4", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_12_12_12));
                break;
            case 83:
                spaceGroup = new SpaceGroup(83, 8, 8, "P4/m", "PG4/m", TETRAGONAL,
                        L114, "P 4/m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0));
                break;
            case 84:
                spaceGroup = new SpaceGroup(84, 8, 8, "P42/m", "PG4/m", TETRAGONAL,
                        L114, "P 42/m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_12));
                break;
            case 85:
                spaceGroup = new SpaceGroup(85, 8, 8, "P4/n", "PG4/m", TETRAGONAL,
                        L114, "P 4/n", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0));
                break;
            case 86:
                spaceGroup = new SpaceGroup(86, 8, 8, "P42/n", "PG4/m", TETRAGONAL,
                        L114, "P 42/n", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0));
                break;
            case 87:
                spaceGroup = new SpaceGroup(87, 16, 8, "I4/m", "PG4/m", TETRAGONAL,
                        L114, "I 4/m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_mZ, SymOp.Tr_12_12_12));
                break;
            case 88:
                spaceGroup = new SpaceGroup(88, 16, 8, "I41/a", "PG4/m",
                        TETRAGONAL, L114, "I 41/a", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 89:
                spaceGroup = new SpaceGroup(89, 8, 8, "P422", "PG422", TETRAGONAL,
                        L224, "P 4 2 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0));
                break;
            case 90:
                spaceGroup = new SpaceGroup(90, 8, 8, "P4212", "PG422", TETRAGONAL,
                        L224, "P 4 21 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0));
                break;
            case 91:
                spaceGroup = new SpaceGroup(91, 8, 8, "P4122", "PG422", TETRAGONAL,
                        L224, "P 41 2 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_34), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_34), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_14));
                break;
            case 92:
                spaceGroup = new SpaceGroup(92, 8, 8, "P41212", "PG422",
                        TETRAGONAL, L224, "P 41 21 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_34), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_14), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_34), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 93:
                spaceGroup = new SpaceGroup(93, 8, 8, "P4222", "PG422", TETRAGONAL,
                        L224, "P 42 2 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_12));
                break;
            case 94:
                spaceGroup = new SpaceGroup(94, 8, 8, "P42212", "PG422",
                        TETRAGONAL, L224, "P 42 21 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 95:
                spaceGroup = new SpaceGroup(95, 8, 8, "P4322", "PG422", TETRAGONAL,
                        L224, "P 43 2 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_14), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_14), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_34));
                break;
            case 96:
                spaceGroup = new SpaceGroup(96, 8, 8, "P43212", "PG422",
                        TETRAGONAL, L224, "P 43 21 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_14), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_34), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_14), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 97:
                spaceGroup = new SpaceGroup(97, 16, 8, "I422", "PG422", TETRAGONAL,
                        L224, "I 4 2 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_12_12_12));
                break;
            case 98:
                spaceGroup = new SpaceGroup(98, 16, 8, "I4122", "PG422",
                        TETRAGONAL, L224, "I 41 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12));
                break;
            case 99:
                spaceGroup = new SpaceGroup(99, 8, 8, "P4mm", "PG4mm", TETRAGONAL,
                        L224, "P 4 m m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_0_0_0));
                break;
            case 100:
                spaceGroup = new SpaceGroup(100, 8, 8, "P4bm", "PG4mm", TETRAGONAL,
                        L224, "P 4 b m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_12_12_0));
                break;
            default:
        }
        return spaceGroup;
    }

    private static SpaceGroup getSpaceGroup2(int num) {
        SpaceGroup spaceGroup = null;
        switch (num) {
            case 101:
                spaceGroup = new SpaceGroup(101, 8, 8, "P42cm", "PG4mm",
                        TETRAGONAL, L224, "P 42 c m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 102:
                spaceGroup = new SpaceGroup(102, 8, 8, "P42nm", "PG4mm",
                        TETRAGONAL, L224, "P 42 n m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 103:
                spaceGroup = new SpaceGroup(103, 8, 8, "P4cc", "PG4mm", TETRAGONAL,
                        L224, "P 4 c c", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_0_0_12));
                break;
            case 104:
                spaceGroup = new SpaceGroup(104, 8, 8, "P4nc", "PG4mm", TETRAGONAL,
                        L224, "P 4 n c", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_12_12_12));
                break;
            case 105:
                spaceGroup = new SpaceGroup(105, 8, 8, "P42mc", "PG4mm",
                        TETRAGONAL, L224, "P 42 m c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 106:
                spaceGroup = new SpaceGroup(106, 8, 8, "P42bc", "PG4mm",
                        TETRAGONAL, L224, "P 42 b c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 107:
                spaceGroup = new SpaceGroup(107, 16, 8, "I4mm", "PG4mm",
                        TETRAGONAL, L224, "I 4 m m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 108:
                spaceGroup = new SpaceGroup(108, 16, 8, "I4cm", "PG4mm",
                        TETRAGONAL, L224, "I 4 c m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 109:
                spaceGroup = new SpaceGroup(109, 16, 8, "I41md", "PG4mm",
                        TETRAGONAL, L224, "I 41 m d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_14));
                break;
            case 110:
                spaceGroup = new SpaceGroup(110, 16, 8, "I41cd", "PG4mm",
                        TETRAGONAL, L224, "I 41 c d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_14), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_34));
                break;
            case 111:
                spaceGroup = new SpaceGroup(111, 8, 8, "P-42m", "PG4bar2m",
                        TETRAGONAL, L224, "P -4 2 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 112:
                spaceGroup = new SpaceGroup(112, 8, 8, "P-42c", "PG4bar2m",
                        TETRAGONAL, L224, "P -4 2 c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 113:
                spaceGroup = new SpaceGroup(113, 8, 8, "P-421m", "PG4bar2m",
                        TETRAGONAL, L224, "P -4 21 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 114:
                spaceGroup = new SpaceGroup(114, 8, 8, "P-421c", "PG4bar2m",
                        TETRAGONAL, L224, "P -4 21 c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 115:
                spaceGroup = new SpaceGroup(115, 8, 8, "P-4m2", "PG4barm2",
                        TETRAGONAL, L224, "P -4 m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 116:
                spaceGroup = new SpaceGroup(116, 8, 8, "P-4c2", "PG4barm2",
                        TETRAGONAL, L224, "P -4 c 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 117:
                spaceGroup = new SpaceGroup(117, 8, 8, "P-4b2", "PG4barm2",
                        TETRAGONAL, L224, "P -4 b 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 118:
                spaceGroup = new SpaceGroup(118, 8, 8, "P-4n2", "PG4barm2",
                        TETRAGONAL, L224, "P -4 n 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12));
                break;
            case 119:
                spaceGroup = new SpaceGroup(119, 16, 8, "I-4m2", "PG4barm2",
                        TETRAGONAL, L224, "I -4 m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12));
                break;
            case 120:
                spaceGroup = new SpaceGroup(120, 16, 8, "I-4c2", "PG4barm2",
                        TETRAGONAL, L224, "I -4 c 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0));
                break;
            case 121:
                spaceGroup = new SpaceGroup(121, 16, 8, "I-42m", "PG4bar2m",
                        TETRAGONAL, L224, "I -4 2 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 122:
                spaceGroup = new SpaceGroup(122, 16, 8, "I-42d", "PG4bar2m",
                        TETRAGONAL, L224, "I -4 2 d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_14));
                break;
            case 123:
                spaceGroup = new SpaceGroup(123, 16, 16, "P4/mmm", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 124:
                spaceGroup = new SpaceGroup(124, 16, 16, "P4/mcc", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/m 2/c 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 125:
                spaceGroup = new SpaceGroup(125, 16, 16, "P4/nbm", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/n 2/b 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 126:
                spaceGroup = new SpaceGroup(126, 16, 16, "P4/nnc", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/n 2/n 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 127:
                spaceGroup = new SpaceGroup(127, 16, 16, "P4/mbm", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/m 21/b 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 128:
                spaceGroup = new SpaceGroup(128, 16, 16, "P4/mnc", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/m 21/n 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 129:
                spaceGroup = new SpaceGroup(129, 16, 16, "P4/nmm", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/n 21/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 130:
                spaceGroup = new SpaceGroup(130, 16, 16, "P4/ncc", "PG4/mmm",
                        TETRAGONAL, L224, "P 4/n 2/c 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 131:
                spaceGroup = new SpaceGroup(131, 16, 16, "P42/mmc", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/m 2/m 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 132:
                spaceGroup = new SpaceGroup(132, 16, 16, "P42/mcm", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/m 2/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 133:
                spaceGroup = new SpaceGroup(133, 16, 16, "P42/nbc", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/n 2/b 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 134:
                spaceGroup = new SpaceGroup(134, 16, 16, "P42/nnm", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/n 2/n 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 135:
                spaceGroup = new SpaceGroup(135, 16, 16, "P42/mbc", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/m 21/b 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 136:
                spaceGroup = new SpaceGroup(136, 16, 16, "P42/mnm", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/m 21/n 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 137:
                spaceGroup = new SpaceGroup(137, 16, 16, "P42/nmc", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/n 21/m 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 138:
                spaceGroup = new SpaceGroup(138, 16, 16, "P42/ncm", "PG4/mmm",
                        TETRAGONAL, L224, "P 42/n 21/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 139:
                spaceGroup = new SpaceGroup(139, 32, 16, "I4/mmm", "PG4/mmm",
                        TETRAGONAL, L224, "I 4/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12));
                break;
            case 140:
                spaceGroup = new SpaceGroup(140, 32, 16, "I4/mcm", "PG4/mmm",
                        TETRAGONAL, L224, "I 4/m 2/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0));
                break;
            case 141:
                spaceGroup = new SpaceGroup(141, 32, 16, "I41/amd", "PG4/mmm",
                        TETRAGONAL, L224, "I 41/a 2/m 2/d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_34));
                break;
            case 142:
                spaceGroup = new SpaceGroup(142, 32, 16, "I41/acd", "PG4/mmm",
                        TETRAGONAL, L224, "I 41/a 2/c 2/d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_14), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_34), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_34), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_14), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_14), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_14));
                break;
            case 143:
                spaceGroup = new SpaceGroup(143, 3, 3, "P3", "PG3", TRIGONAL,
                        L113, "P 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0));
                break;
            case 144:
                spaceGroup = new SpaceGroup(144, 3, 3, "P31", "PG3", TRIGONAL,
                        L113, "P 31", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_23));
                break;
            case 145:
                spaceGroup = new SpaceGroup(145, 3, 3, "P32", "PG3", TRIGONAL,
                        L113, "P 32", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_13));
                break;
            case 146:
                spaceGroup = new SpaceGroup(146, 9, 3, "H3", "PG3", TRIGONAL,
                        L113, "H 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_13_23_23));
                break;
            case 147:
                spaceGroup = new SpaceGroup(147, 6, 6, "P-3", "PG3bar", TRIGONAL,
                        L113, "P -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mXY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_X_mZ, SymOp.Tr_0_0_0));
                break;
            case 148:
                spaceGroup = new SpaceGroup(148, 18, 6, "H-3", "PG3bar", TRIGONAL,
                        L113, "H -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mXY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_Y_mXY_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_XmY_X_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_Y_mXY_mZ, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_XmY_X_mZ, SymOp.Tr_13_23_23));
                break;
            case 149:
                spaceGroup = new SpaceGroup(149, 6, 6, "P312", "PG312", TRIGONAL,
                        L223, "P 3 1 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_XmY_mZ, SymOp.Tr_0_0_0));
                break;
            case 150:
                spaceGroup = new SpaceGroup(150, 6, 6, "P321", "PG321", TRIGONAL,
                        L32U, "P 3 2 1", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_0_0_0));
                break;
            case 151:
                spaceGroup = new SpaceGroup(151, 6, 6, "P3112", "PG312", TRIGONAL,
                        L223, "P 31 1 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_Y_mZ, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_X_XmY_mZ, SymOp.Tr_0_0_0));
                break;
            case 152:
                spaceGroup = new SpaceGroup(152, 6, 6, "P3121", "PG321", TRIGONAL,
                        L32U, "P 31 2 1", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_0_0_13));
                break;
            case 153:
                spaceGroup = new SpaceGroup(153, 6, 6, "P3212", "PG312", TRIGONAL,
                        L223, "P 32 1 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_Y_mZ, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_X_XmY_mZ, SymOp.Tr_0_0_0));
                break;
            case 154:
                spaceGroup = new SpaceGroup(154, 6, 6, "P3221", "PG321", TRIGONAL,
                        L32U, "P 32 2 1", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_0_0_23));
                break;
            case 155:
                spaceGroup = new SpaceGroup(155, 18, 6, "H32", "PG321", TRIGONAL,
                        L32U, "H 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_XmY_mY_mZ, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mX_mXY_mZ, SymOp.Tr_13_23_23));
                break;
            case 156:
                spaceGroup = new SpaceGroup(156, 6, 6, "P3m1", "PG3m1", TRIGONAL,
                        L32U, "P 3 m 1", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_0_0_0));
                break;
            case 157:
                spaceGroup = new SpaceGroup(157, 6, 6, "P31m", "PG31m", TRIGONAL,
                        L223, "P 3 1 m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mXY_Z, SymOp.Tr_0_0_0));
                break;
            case 158:
                spaceGroup = new SpaceGroup(158, 6, 6, "P3c1", "PG3m1", TRIGONAL,
                        L32U, "P 3 c 1", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_0_0_12));
                break;
            case 159:
                spaceGroup = new SpaceGroup(159, 6, 6, "P31c", "PG31m", TRIGONAL,
                        L223, "P 3 1 c", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_XmY_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mX_mXY_Z, SymOp.Tr_0_0_12));
                break;
            case 160:
                spaceGroup = new SpaceGroup(160, 18, 6, "H3m", "PG3m", TRIGONAL,
                        L32U, "H 3 m", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_13_23_23));
                break;
            case 161:
                spaceGroup = new SpaceGroup(161, 18, 6, "H3c", "PG3m", TRIGONAL,
                        L32U, "H 3 c", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_23_13_13), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_23_13_56), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_23_13_56), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_23_13_56), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_XmY_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_13_23_23), new SymOp(
                        SymOp.Rot_mY_mX_Z, SymOp.Tr_13_23_16), new SymOp(
                        SymOp.Rot_mXY_Y_Z, SymOp.Tr_13_23_16), new SymOp(
                        SymOp.Rot_X_XmY_Z, SymOp.Tr_13_23_16));
                break;
            case 162:
                spaceGroup = new SpaceGroup(162, 12, 12, "P-31m", "PG3bar1m",
                        TRIGONAL, L223, "P -3 1 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 163:
                spaceGroup = new SpaceGroup(163, 12, 12, "P-31c", "PG3bar1m",
                        TRIGONAL, L223, "P -3 1 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 164:
                spaceGroup = new SpaceGroup(164, 12, 12, "P-3m1", "PG3barm1",
                        TRIGONAL, L32U, "P -3 2/m 1", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 165:
                spaceGroup = new SpaceGroup(165, 12, 12, "P-3c1", "PG3barm1",
                        TRIGONAL, L32U, "P -3 2/c 1", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 166:
                spaceGroup = new SpaceGroup(166, 36, 12, "H-3m", "PG3barm",
                        TRIGONAL, L32U, "H -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_13_23_23));
                break;
            case 167:
                spaceGroup = new SpaceGroup(167, 36, 12, "H-3c", "PG3barm",
                        TRIGONAL, L32U, "H -3 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_23_13_13), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_23_13_56), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_13_23_16), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_13_23_16), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_13_23_16), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_13_23_23), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_13_23_16), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_13_23_16), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_13_23_16));
                break;
            case 168:
                spaceGroup = new SpaceGroup(168, 6, 6, "P6", "PG6", HEXAGONAL,
                        L114, "P 6", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_0));
                break;
            case 169:
                spaceGroup = new SpaceGroup(169, 6, 6, "P61", "PG6", HEXAGONAL,
                        L114, "P 61", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_56), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_16));
                break;
            case 170:
                spaceGroup = new SpaceGroup(170, 6, 6, "P65", "PG6", HEXAGONAL,
                        L114, "P 65", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_16), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_56));
                break;
            case 171:
                spaceGroup = new SpaceGroup(171, 6, 6, "P62", "PG6", HEXAGONAL,
                        L114, "P 62", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_13));
                break;
            case 172:
                spaceGroup = new SpaceGroup(172, 6, 6, "P64", "PG6", HEXAGONAL,
                        L114, "P 64", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_23), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_13), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_23));
                break;
            case 173:
                spaceGroup = new SpaceGroup(173, 6, 6, "P63", "PG6", HEXAGONAL,
                        L114, "P 63", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_mXY_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_XmY_X_Z, SymOp.Tr_0_0_12));
                break;
            case 174:
                spaceGroup = new SpaceGroup(174, 6, 6, "P-6", "PG6bar", HEXAGONAL,
                        L114, "P -6", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mY_XmY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_XmY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mXY_mX_mZ, SymOp.Tr_0_0_0));
                break;
            case 175:
                spaceGroup = new SpaceGroup(175, 12, 12, "P6/m", "PG6/m",
                        HEXAGONAL, L114, "P 6/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 176:
                spaceGroup = new SpaceGroup(176, 12, 12, "P63/m", "PG6/m",
                        HEXAGONAL, L114, "P 63/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 177:
                spaceGroup = new SpaceGroup(177, 12, 12, "P622", "PG622",
                        HEXAGONAL, L224, "P 6 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 178:
                spaceGroup = new SpaceGroup(178, 12, 12, "P6122", "PG622",
                        HEXAGONAL, L224, "P 61 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_56), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_16), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_56), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_16));
                break;
            case 179:
                spaceGroup = new SpaceGroup(179, 12, 12, "P6522", "PG622",
                        HEXAGONAL, L224, "P 65 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_16), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_56), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_16), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_56));
                break;
            case 180:
                spaceGroup = new SpaceGroup(180, 12, 12, "P6222", "PG622",
                        HEXAGONAL, L224, "P 62 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_13));
                break;
            case 181:
                spaceGroup = new SpaceGroup(181, 12, 12, "P6422", "PG622",
                        HEXAGONAL, L224, "P 64 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_23), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_13), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_23));
                break;
            case 182:
                spaceGroup = new SpaceGroup(182, 12, 12, "P6322", "PG622",
                        HEXAGONAL, L224, "P 63 2 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_12));
                break;
            case 183:
                spaceGroup = new SpaceGroup(183, 12, 12, "P6mm", "PG6mm",
                        HEXAGONAL, L224, "P 6 m m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 184:
                spaceGroup = new SpaceGroup(184, 12, 12, "P6cc", "PG6mm",
                        HEXAGONAL, L224, "P 6 c c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 185:
                spaceGroup = new SpaceGroup(185, 12, 12, "P63cm", "PG6mm",
                        HEXAGONAL, L224, "P 63 c m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 186:
                spaceGroup = new SpaceGroup(186, 12, 12, "P63mc", "PG6mm",
                        HEXAGONAL, L224, "P 63 m c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 187:
                spaceGroup = new SpaceGroup(187, 12, 12, "P-6m2", "PG6barm2",
                        HEXAGONAL, L224, "P -6 m 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 188:
                spaceGroup = new SpaceGroup(188, 12, 12, "P-6c2", "PG6barm2",
                        HEXAGONAL, L224, "P -6 c 2", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0));
                break;
            case 189:
                spaceGroup = new SpaceGroup(189, 12, 12, "P-62m", "PG6bar2m",
                        HEXAGONAL, L224, "P -6 2 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 190:
                spaceGroup = new SpaceGroup(190, 12, 12, "P-62c", "PG6bar2m",
                        HEXAGONAL, L224, "P -6 2 c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 191:
                spaceGroup = new SpaceGroup(191, 24, 24, "P6/mmm", "PG6/mmm",
                        HEXAGONAL, L224, "P 6/m 2/m 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 192:
                spaceGroup = new SpaceGroup(192, 24, 24, "P6/mcc", "PG6/mmm",
                        HEXAGONAL, L224, "P 6/m 2/c 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 193:
                spaceGroup = new SpaceGroup(193, 24, 24, "P63/mcm", "PG6/mmm",
                        HEXAGONAL, L224, "P 63/m 2/c 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_0));
                break;
            case 194:
                spaceGroup = new SpaceGroup(194, 24, 24, "P63/mmc", "PG6/mmm",
                        HEXAGONAL, L224, "P 63/m 2/m 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mXY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mXY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_XmY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mXY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_XmY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mXY_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_XmY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_XmY_mY_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mXY_Z,
                        SymOp.Tr_0_0_12));
                break;
            case 195:
                spaceGroup = new SpaceGroup(195, 12, 12, "P23", "PG23", CUBIC,
                        LM3B, "P 2 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0));
                break;
            case 196:
                spaceGroup = new SpaceGroup(196, 48, 12, "F23", "PG23", CUBIC,
                        LM3B, "F 2 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_0));
                break;
            case 197:
                spaceGroup = new SpaceGroup(197, 24, 12, "I23", "PG23", CUBIC,
                        LM3B, "I 2 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_12));
                break;
            case 198:
                spaceGroup = new SpaceGroup(198, 12, 12, "P213", "PG23", CUBIC,
                        LM3B, "P 21 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12));
                break;
            case 199:
                spaceGroup = new SpaceGroup(199, 24, 12, "I213", "PG23", CUBIC,
                        LM3B, "I 21 3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_0));
                break;
            case 200:
                spaceGroup = new SpaceGroup(200, 24, 24, "Pm-3", "PGm3bar", CUBIC,
                        LM3B, "P 2/m -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_0_0_0));
                break;
            default:
        }
        return spaceGroup;
    }

    private static SpaceGroup getSpaceGroup3(int num) {
        SpaceGroup spaceGroup = null;
        switch (num) {
            case 201:
                spaceGroup = new SpaceGroup(201, 24, 24, "Pn-3", "PGm3bar", CUBIC,
                        LM3B, "P 2/n -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_12_12));
                break;
            case 202:
                spaceGroup = new SpaceGroup(202, 96, 24, "Fm-3", "PGm3bar", CUBIC,
                        LM3B, "F 2/m -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_12_0));
                break;
            case 203:
                spaceGroup = new SpaceGroup(203, 96, 24, "Fd-3", "PGm3bar", CUBIC,
                        LM3B, "F 2/d -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_34_34_14));
                break;
            case 204:
                spaceGroup = new SpaceGroup(204, 48, 24, "Im-3", "PGm3bar", CUBIC,
                        LM3B, "I 2/m -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_12_12));
                break;
            case 205:
                spaceGroup = new SpaceGroup(205, 24, 24, "Pa-3", "PGm3bar", CUBIC,
                        LM3B, "P 21/a -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_0_12));
                break;
            case 206:
                spaceGroup = new SpaceGroup(206, 48, 24, "Ia-3", "PGm3bar", CUBIC,
                        LM3B, "I 21/a -3", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Y_mZ, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_X_mY_Z, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_mX_Y_Z, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_Y, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Z_X_mY, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_Z_mX_Y, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_X, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_mY_Z_X, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Y_Z_mX, SymOp.Tr_0_12_0));
                break;
            case 207:
                spaceGroup = new SpaceGroup(207, 24, 24, "P432", "PG432", CUBIC,
                        LM3M, "P 4 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_0_0_0));
                break;
            case 208:
                spaceGroup = new SpaceGroup(208, 24, 24, "P4232", "PG432", CUBIC,
                        LM3M, "P 42 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_12_12_12));
                break;
            case 209:
                spaceGroup = new SpaceGroup(209, 96, 24, "F432", "PG432", CUBIC,
                        LM3M, "F 4 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_12_12_0));
                break;
            case 210:
                spaceGroup = new SpaceGroup(210, 96, 24, "F4132", "PG432", CUBIC,
                        LM3M, "F 41 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_34_34_14));
                break;
            case 211:
                spaceGroup = new SpaceGroup(211, 48, 24, "I432", "PG432", CUBIC,
                        LM3M, "I 4 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_12_12_12));
                break;
            case 212:
                spaceGroup = new SpaceGroup(212, 24, 24, "P4332", "PG432", CUBIC,
                        LM3M, "P 43 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_14_14_14));
                break;
            case 213:
                spaceGroup = new SpaceGroup(213, 24, 24, "P4132", "PG432", CUBIC,
                        LM3M, "P 41 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_34_34_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_34_34_34), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_34_34_34));
                break;
            case 214:
                spaceGroup = new SpaceGroup(214, 48, 24, "I4132", "PG432", CUBIC,
                        LM3M, "I 41 3 2", new SymOp(SymOp.Rot_X_Y_Z, SymOp.Tr_0_0_0),
                        new SymOp(SymOp.Rot_mX_mY_Z, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_0_0_0), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_0_12_12), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_12_12_0), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_12_0_12), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_34_34_34), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_34_34_34), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_34_14_14), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_14_14_34), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_14_34_14), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_34_34_34), new SymOp(
                        SymOp.Rot_X_Y_Z, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mX_mY_Z, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mX_Y_mZ, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_X_mY_mZ, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_Z_X_Y, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_Z_mX_mY, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mZ_mX_Y, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_mZ_X_mY, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_Z_X, SymOp.Tr_12_12_12), new SymOp(
                        SymOp.Rot_mY_Z_mX, SymOp.Tr_12_0_0), new SymOp(
                        SymOp.Rot_Y_mZ_mX, SymOp.Tr_0_0_12), new SymOp(
                        SymOp.Rot_mY_mZ_X, SymOp.Tr_0_12_0), new SymOp(
                        SymOp.Rot_Y_X_mZ, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mY_mX_mZ, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_Y_mX_Z, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mY_X_Z, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_X_Z_mY, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_mX_Z_Y, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mX_mZ_mY, SymOp.Tr_14_14_14), new SymOp(
                        SymOp.Rot_X_mZ_Y, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_Z_Y_mX, SymOp.Tr_14_34_34), new SymOp(
                        SymOp.Rot_Z_mY_X, SymOp.Tr_34_34_14), new SymOp(
                        SymOp.Rot_mZ_Y_X, SymOp.Tr_34_14_34), new SymOp(
                        SymOp.Rot_mZ_mY_mX, SymOp.Tr_14_14_14));
                break;
            case 215:
                spaceGroup = new SpaceGroup(215, 24, 24, "P-43m", "PG4bar3m",
                        CUBIC, LM3M, "P -4 3 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0));
                break;
            case 216:
                spaceGroup = new SpaceGroup(216, 96, 24, "F-43m", "PG4bar3m",
                        CUBIC, LM3M, "F -4 3 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_0));
                break;
            case 217:
                spaceGroup = new SpaceGroup(217, 48, 24, "I-43m", "PG4bar3m",
                        CUBIC, LM3M, "I -4 3 m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12));
                break;
            case 218:
                spaceGroup = new SpaceGroup(218, 24, 24, "P-43n", "PG4bar3m",
                        CUBIC, LM3M, "P -4 3 n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12));
                break;
            case 219:
                spaceGroup = new SpaceGroup(219, 96, 24, "F-43c", "PG4bar3m",
                        CUBIC, LM3M, "F -4 3 c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_12));
                break;
            case 220:
                spaceGroup = new SpaceGroup(220, 48, 24, "I-43d", "PG4bar3m",
                        CUBIC, LM3M, "I -4 3 d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_34_14_14));
                break;
            case 221:
                spaceGroup = new SpaceGroup(221, 48, 48, "Pm-3m", "PGm3barm",
                        CUBIC, LM3M, "P 4/m -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0));
                break;
            case 222:
                spaceGroup = new SpaceGroup(222, 48, 48, "Pn-3n", "PGm3barm",
                        CUBIC, LM3M, "P 4/n -3 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12));
                break;
            case 223:
                spaceGroup = new SpaceGroup(223, 48, 48, "Pm-3n", "PGm3barm",
                        CUBIC, LM3M, "P 42/m -3 2/n", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12));
                break;
            case 224:
                spaceGroup = new SpaceGroup(224, 48, 48, "Pn-3m", "PGm3barm",
                        CUBIC, LM3M, "P 42/n -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0));
                break;
            case 225:
                spaceGroup = new SpaceGroup(225, 192, 48, "Fm-3m", "PGm3barm",
                        CUBIC, LM3M, "F 4/m -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_0));
                break;
            case 226:
                spaceGroup = new SpaceGroup(226, 192, 48, "Fm-3c", "PGm3barm",
                        CUBIC, LM3M, "F 4/m -3 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_12));
                break;
            case 227:
                spaceGroup = new SpaceGroup(227, 192, 48, "Fd-3m", "PGm3barm",
                        CUBIC, LM3M, "F 41/d -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_0));
                break;
            case 228:
                spaceGroup = new SpaceGroup(228, 192, 48, "Fd-3c", "PGm3barm",
                        CUBIC, LM3M, "F 41/d -3 2/c", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_12));
                break;
            case 229:
                spaceGroup = new SpaceGroup(229, 96, 48, "Im-3m", "PGm3barm",
                        CUBIC, LM3M, "I 4/m -3 2/m", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_12_12_12));
                break;
            case 230:
                spaceGroup = new SpaceGroup(230, 96, 48, "Ia-3d", "PGm3barm",
                        CUBIC, LM3M, "I 41/a -3 2/d", new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_0_0_0), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_0_12_12), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_12_12_0), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_12_0_12), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_Y_Z,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mX_mY_Z,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mX_Y_mZ,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_X_mY_mZ,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_X_Y,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Z_mX_mY,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mX_Y,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mZ_X_mY,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_Z_X,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mY_Z_mX,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_Y_mZ_mX,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mY_mZ_X,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Y_X_mZ,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mY_mX_mZ,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_Y_mX_Z,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mY_X_Z,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_X_Z_mY,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_mX_Z_Y,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mX_mZ_mY,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_X_mZ_Y,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_Z_Y_mX,
                        SymOp.Tr_14_34_34), new SymOp(SymOp.Rot_Z_mY_X,
                        SymOp.Tr_34_34_14), new SymOp(SymOp.Rot_mZ_Y_X,
                        SymOp.Tr_34_14_34), new SymOp(SymOp.Rot_mZ_mY_mX,
                        SymOp.Tr_14_14_14), new SymOp(SymOp.Rot_mX_mY_mZ,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_X_Y_mZ,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_X_mY_Z,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mX_Y_Z,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_mZ_mX_mY,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_mZ_X_Y,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Z_X_mY,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_Z_mX_Y,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_mZ_mX,
                        SymOp.Tr_12_12_12), new SymOp(SymOp.Rot_Y_mZ_X,
                        SymOp.Tr_12_0_0), new SymOp(SymOp.Rot_mY_Z_X,
                        SymOp.Tr_0_0_12), new SymOp(SymOp.Rot_Y_Z_mX,
                        SymOp.Tr_0_12_0), new SymOp(SymOp.Rot_mY_mX_Z,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_Y_X_Z,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mY_X_mZ,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Y_mX_mZ,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_mX_mZ_Y,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_X_mZ_mY,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_X_Z_Y,
                        SymOp.Tr_34_34_34), new SymOp(SymOp.Rot_mX_Z_mY,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_mZ_mY_X,
                        SymOp.Tr_34_14_14), new SymOp(SymOp.Rot_mZ_Y_mX,
                        SymOp.Tr_14_14_34), new SymOp(SymOp.Rot_Z_mY_mX,
                        SymOp.Tr_14_34_14), new SymOp(SymOp.Rot_Z_Y_X,
                        SymOp.Tr_34_34_34));
                break;
            default:
        }
        return spaceGroup;
    }

    /**
     * Returns the space group number for a given space group name.
     * @param pdbName The space group name.
     * @return The space group number.
     * @since 1.0
     */
    private static int spaceGroupNumber(String name) {
        if (name == null) {
            return -1;
        }
        String n = name.trim();
        int num = spaceGroupNames.length;
        for (int i = 0; i < num; i++) {
            String s = spaceGroupNames[i];
            if (s.equalsIgnoreCase(n)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Returns the space group name for the given PDB name.
     * @param pdbName PDB space group name.
     * @return A short space group name.
     * @since 1.0
     */
    public static String pdb2ShortName(String pdbName) {
        if (pdbName == null) {
            return null;
        }
        String n = pdbName.trim();
        int num = pdbSpaceGroupNames.length;
        for (int i = 0; i < num; i++) {
            if (pdbSpaceGroupNames[i].equalsIgnoreCase(n)) {
                return spaceGroupNames[i];
            }
        }
        return pdbName;
    }

    /**
     * Print out info for each space group.
     */
    public static void main(String[] args) throws Exception {
        SpaceGroup sg = SpaceGroup.spaceGroupFactory(1);
        System.out.print("\"" + sg.pdbName + "\"");
        for (int i = 1; i < spaceGroupNames.length; i++) {
            sg = SpaceGroup.spaceGroupFactory(i+1);
            System.out.print(", \"" + sg.pdbName + "\"");
        }
    }
}
