package uk.org.squirm3.engine.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import uk.org.squirm3.model.Atom;
import uk.org.squirm3.model.Configuration;
import uk.org.squirm3.model.FixedPoint;
import uk.org.squirm3.model.IPhysicalPoint;
import uk.org.squirm3.model.MobilePoint;
import uk.org.squirm3.model.type.AtomType;
import uk.org.squirm3.model.type.BuilderType;
import uk.org.squirm3.springframework.converter.BuilderTypeToAtomTypeConverter;
import uk.org.squirm3.springframework.converter.CharacterToBuilderTypeConverter;

import com.google.common.collect.Lists;

public class AtomBuilder {
    private static final char[] NO_ATOM = "......".toCharArray();
    private static final int ATOM_LENGTH = NO_ATOM.length;

    private static final char MOBILE_ATOM_START = '(';
    private static final char MOBILE_ATOM_STOP = ')';
    private static final char FIXED_ATOM_START = '[';
    private static final char FIXED_ATOM_STOP = ']';
    private static final char NO_BOND = '_';
    private static final char HORIZONTAL_BOND = '⇠';
    private static final char VERTICAL_BOND = '⇡';

    private final Converter<Character, BuilderType> builderTypeConverter;

    public AtomBuilder() {
        builderTypeConverter = new CharacterToBuilderTypeConverter();
    }

    public Collection<Atom> build(final String levelDescription,
            final Configuration configuration) throws BuilderException {
        String randomizerConfiguration = "abcdef";
        final BufferedReader descriptionReader = new BufferedReader(
                new StringReader(levelDescription));

        try {
            String descriptionLine = descriptionReader.readLine();
            if (descriptionLine != null && descriptionLine.startsWith("#")) {
                randomizerConfiguration = descriptionLine.substring(1);
                descriptionLine = descriptionReader.readLine();
            }

            final Collection<Atom> atoms = Lists.newArrayList();
            ArrayList<Atom> previousAtomLine = Lists.newArrayList();
            ArrayList<Atom> currentAtomLine = Lists.newArrayList();

            final Converter<BuilderType, AtomType> atomTypeConverter = new BuilderTypeToAtomTypeConverter(
                    randomizerConfiguration);
            int x = 0;
            int y = 0;
            int maxX = 0;

            while (descriptionLine != null) {
                y++;
                x = 1;

                int index = 0;
                while (index < descriptionLine.length()) {
                    final char[] atomDescription = getAtomDescription(
                            descriptionLine, index);
                    if (isAtomStart(atomDescription)) {
                        final char atomStart = atomDescription[0];
                        final char atomType = atomDescription[2];
                        final char atomState = atomDescription[3];

                        final Atom atom = new Atom(getPhysicalPoint(x, y,
                                atomStart), getAtomType(atomType,
                                atomTypeConverter), getAtomState(atomState));

                        currentAtomLine.add(atom);

                        if (isVerticalBondActivated(atomDescription)) {
                            atom.bondWith(getUpperAtom(previousAtomLine, x));
                        }

                        if (isHorizontalBondActivated(atomDescription)) {
                            atom.bondWith(getPreviousAtom(currentAtomLine, x));
                        }

                    } else {
                        currentAtomLine.add(null);
                    }
                    index += ATOM_LENGTH;
                    x++;
                }
                atoms.addAll(currentAtomLine);
                previousAtomLine = currentAtomLine;
                currentAtomLine = Lists.newArrayList();
                descriptionLine = descriptionReader.readLine();
                if (x > maxX) {
                    maxX = x;
                }
            }
            atoms.removeAll(Collections.singleton(null));
            checkConfiguration(configuration, maxX, y);
            return atoms;

        } catch (final IOException e) {
            throw new BuilderException(
                    "Unexpected IOException while reading from level description.",
                    e);
        } finally {
            try {
                descriptionReader.close();
            } catch (final IOException e) {
            }
        }

    }

    private void checkConfiguration(Configuration configuration, int x, int y)
            throws BuilderException {
        final double horizontalSpace = Atom.getAtomSize() * (x * 2 + 1);
        if (horizontalSpace > configuration.getWidth()) {
            throw new BuilderException(
                    "Map horizontal space is greater than the configuration's width : "
                            + horizontalSpace + " > "
                            + configuration.getWidth());
        }
        final double verticalSpace = Atom.getAtomSize() * (y * 2 + 1);
        if (verticalSpace > configuration.getHeight()) {
            throw new BuilderException(
                    "Map vertical space is greater than the configuration's height : "
                            + verticalSpace + " > " + configuration.getHeight());
        }
    }
    private boolean isHorizontalBondActivated(final char[] atomDescription)
            throws BuilderException {
        if (atomDescription[1] == HORIZONTAL_BOND) {
            return true;
        }
        checkDefaultBondValue(atomDescription[1], atomDescription, "horizontal");
        return false;
    }

    private void checkDefaultBondValue(final char bond,
            final char[] atomDescription, final String qualifier)
            throws BuilderException {
        if (bond != NO_BOND) {
            throw new BuilderException("Incorrect setting for " + qualifier
                    + " bond : " + String.valueOf(atomDescription));
        }
    }

    private boolean isVerticalBondActivated(final char[] atomDescription)
            throws BuilderException {
        if (atomDescription[4] == VERTICAL_BOND) {
            return true;
        }
        checkDefaultBondValue(atomDescription[4], atomDescription, "vertical");
        return false;
    }

    private Atom getUpperAtom(final ArrayList<Atom> previousLine, final int x)
            throws BuilderException {
        try {
            final Atom atom = previousLine.get(x - 1);
            if (atom == null) {
                throw new BuilderException(
                        "Incorrect setting for vertical bond : there is no upper atom!");
            }
            return atom;
        } catch (final IndexOutOfBoundsException e) {
            throw new BuilderException(
                    "Incorrect setting for vertical bond : there is no upper atom!",
                    e);
        }
    }

    private Atom getPreviousAtom(final List<Atom> currentLine,
            final int currentIndex) throws BuilderException {
        try {
            final Atom atom = currentLine.get(currentIndex - 2);
            if (atom == null) {
                throw new BuilderException(
                        "Incorrect setting for horizontal bond : there is no previous atom!");
            }
            return atom;
        } catch (final IndexOutOfBoundsException e) {
            throw new BuilderException(
                    "Incorrect setting for horizontal bond : there is no previous atom!",
                    e);
        }
    }
    private IPhysicalPoint getPhysicalPoint(final int x, final int y,
            final char atomStart) {
        final float xCoordinate = 2 * x * Atom.getAtomSize();
        final float yCoordinate = 2 * y * Atom.getAtomSize();

        if (atomStart == MOBILE_ATOM_START) {
            final IPhysicalPoint mobilePoint = new MobilePoint(xCoordinate,
                    yCoordinate, 0, 0, 0, 0);
            final float ms = Atom.getAtomSize() / 3;
            mobilePoint.setSpeedX((float) (Math.random() * ms - ms / 2.0));
            mobilePoint.setSpeedY((float) (Math.random() * ms - ms / 2.0));
            return mobilePoint;
        } else if (atomStart == FIXED_ATOM_START) {
            return new FixedPoint(xCoordinate, yCoordinate);
        }
        return null;
    }

    private int getAtomType(final char atomType,
            final Converter<BuilderType, AtomType> atomTypeConverter)
            throws BuilderException {
        final BuilderType builderType = builderTypeConverter.convert(atomType);
        if (builderType == null) {
            throw new BuilderException("Incorrect BuilderType : " + atomType);
        }
        final AtomType type = atomTypeConverter.convert(builderType);
        if (type == null) {
            throw new BuilderException("No AtomType for : " + builderType);
        }
        return type.getIntegerIndentifier();
    }

    private int getAtomState(final char atomState) throws BuilderException {
        final int digitAtomState = Character.digit(atomState, 10);
        if (digitAtomState == -1) {
            throw new BuilderException("Incorrect state : " + atomState);
        }
        return digitAtomState;
    }

    private char[] getAtomDescription(final String levelDescription,
            final int index) throws BuilderException {
        if (index + ATOM_LENGTH > levelDescription.length()) {
            throw new BuilderException("Unexpected end of levelDescription : "
                    + levelDescription.substring(index));
        }
        return levelDescription.substring(index, index + ATOM_LENGTH)
                .toCharArray();
    }

    private boolean isAtomStart(final char[] atomDescription)
            throws BuilderException {
        if (atomDescription[0] == MOBILE_ATOM_START) {
            if (atomDescription[ATOM_LENGTH - 1] != MOBILE_ATOM_STOP) {
                throw new BuilderException("Illegal end of mobile atom : "
                        + String.valueOf(atomDescription));
            }
            return true;
        }
        if (atomDescription[0] == FIXED_ATOM_START) {
            if (atomDescription[ATOM_LENGTH - 1] != FIXED_ATOM_STOP) {
                throw new BuilderException("Illegal end of fixed atom : "
                        + String.valueOf(atomDescription));
            }
            return true;
        }
        if (atomDescription[0] == NO_ATOM[0]) {
            if (!Arrays.equals(atomDescription, NO_ATOM)) {
                throw new BuilderException("Illegal no atom : "
                        + String.valueOf(atomDescription));
            }
            return false;
        }
        throw new BuilderException("Illegal description of atom : "
                + String.valueOf(atomDescription));

    }

}
