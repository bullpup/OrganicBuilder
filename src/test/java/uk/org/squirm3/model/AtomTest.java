package uk.org.squirm3.model;

import java.util.LinkedList;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AtomTest {

    /**
     * Test of the constructor. *
     */
    @Test
    public void testAtom() {
        final int t = 4;
        final int s = 5;
        final Atom a = new Atom(new MobilePoint(), t, s);
        assertTrue(a.getType() == t);
        assertTrue(a.getState() == s);
    }

    /**
     * Test de la propriete stuck. *
     */
    @Test
    public void testStuck() {
        Atom a = new Atom(new MobilePoint(), 0, 0);
        assertFalse(a.isStuck());

        a = new Atom(new FixedPoint(0, 0), 0, 0);
        assertTrue(a.isStuck());
    }

    /**
     * Test de la propriete killer. *
     */
    @Test
    public void testKiller() {
        Atom a = new Atom(new MobilePoint(), 0, 0);
        assertFalse(a.isKiller());

        a = new Atom(new MobilePoint(), Atom.KILLER_TYPE, 0);
        assertTrue(a.isKiller());
    }

    /**
     * Test of basic bonding and breaking.
     */
    @Test
    public void testBondingAndBreaking() {
        final Atom a1 = new Atom(new MobilePoint(), 0, 1);
        final Atom a2 = new Atom(new MobilePoint(), 0, 2);
        assertFalse(a1.hasBondWith(a2));
        assertFalse(a2.hasBondWith(a1));

        a1.bondWith(a2);
        assertTrue(a1.hasBondWith(a2));
        assertTrue(a2.hasBondWith(a1));

        a1.breakBondWith(a2);
        assertFalse(a1.hasBondWith(a2));
        assertFalse(a2.hasBondWith(a1));
    }

    /**
     * Test of breakAllBonds() and getAllConnectedAtoms(). *
     */
    @Test
    public void testGeneralMethods() {
        final MobilePoint physicalPoint = new MobilePoint();
        final Atom a1 = new Atom(physicalPoint, 0, 1);
        final Atom a2 = new Atom(physicalPoint, 0, 2);
        final Atom a3 = new Atom(physicalPoint, 0, 3);
        final Atom a4 = new Atom(physicalPoint, 0, 4);
        final Atom a5 = new Atom(physicalPoint, 0, 3);
        final Atom a6 = new Atom(physicalPoint, 0, 4);

        a1.bondWith(a2);
        a1.bondWith(a3);
        a1.bondWith(a6);
        a3.bondWith(a4);
        a3.bondWith(a5);
        a5.bondWith(a6);

        final LinkedList<Atom> l = new LinkedList<Atom>();
        a1.getAllConnectedAtoms(l);

        assertTrue(l.contains(a1));
        assertTrue(l.contains(a2));
        assertTrue(l.contains(a3));
        assertTrue(l.contains(a4));
        assertTrue(l.contains(a5));
        assertTrue(l.contains(a6));

        a1.breakAllBonds();
        l.clear();
        a1.getAllConnectedAtoms(l);
        assertTrue(l.contains(a1));
        l.remove(a1);
        assertTrue(l.isEmpty());
    }

    /**
     * Test of the string representation. *
     */
    @Test
    public void testToString() {
        final Atom a = new Atom(new MobilePoint(), 1, 1);
        assertTrue("b1".equals(a.toString()));
    }
}
