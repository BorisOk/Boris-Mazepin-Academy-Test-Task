public class Intervals {

    public static String intervalConstruction(String[] args) {
        checkingTheNumberOfItems(args);
        boolean ascTrueDscFalse = Note.sortOrder(args);
        Note startNote = Note.valueOf(String.valueOf(args[1].charAt(0)));
        Spacing spacing = Spacing.valueOf(args[0]);
        int spacingInDegrees = spacing.getDegrees();
        int spacingInSemitone = spacing.getSemitone();
        int indexInArray = startNote.getIndexInArray();
        Note incompleteNote = Note.incompleteNote(spacingInDegrees, startNote.getId(), ascTrueDscFalse);
        if (ascTrueDscFalse) {
            spacingInSemitone = Note.semitonesAtAsc(args[1], spacingInSemitone, indexInArray);
        } else {
            spacingInSemitone = Note.semitonesAtDsc(args[1], spacingInSemitone, indexInArray);
        }
        String resultNote;
        if (Note.getArrayForPassage()[spacingInSemitone].length() == 1) {
            resultNote = Note.resultNoteAssumingOneChar(incompleteNote, spacingInSemitone);
        } else {
            resultNote = Note.resultNoteNoAssumingOneChar(incompleteNote, spacingInSemitone);
        }
        return resultNote;
    }

    public static String intervalIdentification(String[] args) {
        checkingTheNumberOfItems(args);
        boolean ascTrueDscFalse = Note.sortOrder(args);
        Note startNote = Note.valueOf(String.valueOf(args[0].charAt(0)));
        Note endNote = Note.valueOf(String.valueOf(args[1].charAt(0)));
        int degree = Note.requiredDegree(startNote, endNote, ascTrueDscFalse);
        int startSemitone = Note.startSemitone(startNote, args[0]);
        int endSemitone = Note.endSemitone(endNote, args[1], ascTrueDscFalse);
        int semitone = Note.spacingSemitone(startSemitone, endSemitone, ascTrueDscFalse);
        return Spacing.nameInterval(degree, semitone);
    }

    private static void checkingTheNumberOfItems(String[] inputArray) {
        if (inputArray.length != 2) {
            if (inputArray.length != 3) {
                throw new RuntimeException("Недопустимое количество элементов во входном массиве");
            }
        }
    }
}

enum Spacing {
    m2("m2", 2, 1),
    M2("M2", 2, 2),
    m3("m3", 3, 3),
    M3("M3", 3, 4),
    P4("P4", 4, 5),
    P5("P5", 5, 7),
    m6("m6", 6, 8),
    M6("M6", 6, 9),
    m7("m7", 7, 10),
    M7("M7", 7, 11),
    P8("P8", 8, 12);

    private final String name;
    private final int degrees;
    private final int semitone;

    Spacing(String name, int degrees, int semitone) {
        this.name = name;
        this.degrees = degrees;
        this.semitone = semitone;
    }

    public String getName() {
        return name;
    }

    public int getDegrees() {
        return degrees;
    }

    public int getSemitone() {
        return semitone;
    }

    @Override
    public String toString() {
        return "Intervals{" +
                "name='" + name + '\'' +
                ", degrees=" + degrees +
                ", semitone=" + semitone +
                '}';
    }

    public static String nameInterval(int degrees, int semitone) {
        String intervalName = null;
        Spacing[] arraySpacing = Spacing.values();
        for (Spacing spacing : arraySpacing) {
            if (spacing.getDegrees() == degrees && spacing.getSemitone() == semitone) {
                intervalName = spacing.getName();
            }
        }
        return intervalName;
    }
}

enum Note {

    C(1, null, "C", "C#", 0),
    D(2, "Db", "D", "D#", 2),
    E(3, "Eb", "E", null, 4),
    F(4, null, "F", "F#", 5),
    G(5, "Gb", "G", "G#", 7),
    A(6, "Ab", "A", "A#", 9),
    B(7, "Bb", "B", null, 11);

    static final String[] arrayForPassage = {
            C.name,
            C.right + "|" + D.left,
            D.name,
            D.right + "|" + E.left,
            E.name,
            F.name,
            F.right + "|" + G.left,
            G.name,
            G.right + "|" + A.left,
            A.name,
            A.right + "|" + B.left,
            B.name
    };

    private final int id;
    private final String left;
    private final String name;
    private final String right;
    private final int indexInArray;

    Note(int id, String left, String name, String right, int indexInArray) {
        this.id = id;
        this.left = left;
        this.name = name;
        this.right = right;
        this.indexInArray = indexInArray;
    }

    public int getId() {
        return id;
    }

    public String getLeft() {
        return left;
    }

    public String getName() {
        return name;
    }

    public String getRight() {
        return right;
    }

    public int getIndexInArray() {
        return indexInArray;
    }

    public static String[] getArrayForPassage() {
        return arrayForPassage;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", left='" + left + '\'' +
                ", name='" + name + '\'' +
                ", right='" + right + '\'' +
                ", indexInArray=" + indexInArray +
                '}';
    }

    public static boolean sortOrder(String[] args) {
        return args.length != 3 || !args[2].equals("dsc");
    }

    public static Note incompleteNote(int spacingInDegrees, int idStartNote, boolean ascTrueDscFalse) {
        int numberDegrees;
        if (!ascTrueDscFalse) {
            numberDegrees = idStartNote - spacingInDegrees + 1;
            if (numberDegrees < 1) {
                numberDegrees += 7;
            }
        } else {
            numberDegrees = idStartNote + spacingInDegrees - 1;
            if (numberDegrees > 7) {
                numberDegrees -= 7;
            }
        }
        return noteByIndex(numberDegrees);
    }

    public static Note noteByIndex(int index) {
        Note[] notes = Note.values();
        for (Note note : notes) {
            if (note.getId() == index) {
                return note;
            }
        }
        return null;
    }

    public static int semitonesAtAsc(String note, int spacingInSemitone, int indexInArray) {
        if (note.length() > 1) {
            String valueTwo = String.valueOf(note.charAt(1));
            if (valueTwo.equals("#")) {
                spacingInSemitone += 1;
            } else {
                spacingInSemitone -= 1;
            }
        }
        spacingInSemitone = spacingInSemitone + indexInArray;
        if (spacingInSemitone > 11) {
            spacingInSemitone = spacingInSemitone - 12;
        }
        return spacingInSemitone;
    }

    public static int semitonesAtDsc(String note, int spacingInSemitone, int indexInArray) {
        int result = indexInArray;
        if (note.length() > 1) {
            String string2 = String.valueOf(note.charAt(1));
            if (string2.equals("#")) {
                result += 1;
            } else {
                result -= 1;
            }
        }
        result = result - spacingInSemitone;
        if (result < 0) {
            result += 12;
        }
        return result;
    }

    public static int requiredDegree(Note startNote, Note endNote, boolean ascTrueDscFalse) {
        if (ascTrueDscFalse) {
            if (startNote.getId() > endNote.getId()) {
                return 8 - startNote.getId() + endNote.getId();
            } else {
                return endNote.getId() - startNote.getId() + 1;
            }
        } else {
            if (startNote.getId() > endNote.getId()) {
                return startNote.getId() - endNote.getId() + 1;
            } else {
                return 8 - endNote.getId() + startNote.getId();
            }
        }
    }

    public static int startSemitone(Note startNote, String argStartNote) {
        int numberStartSemitone = startNote.getIndexInArray();
        if (argStartNote.length() == 2) {
            String string = String.valueOf(argStartNote.charAt(1));
            if (string.equals("#")) {
                numberStartSemitone += 1;
            } else {
                numberStartSemitone -= 1;
            }
        }
        return numberStartSemitone;
    }

    public static String resultNoteNoAssumingOneChar(Note incompleteNote, int spacingInSemitone) {
        String[] arrayTwoNotes = Note.getArrayForPassage()[spacingInSemitone].split("\\|");
        String resultNote = "";
        for (String noteArray : arrayTwoNotes) {
            String oneCleanNoteOfTwo = String.valueOf(noteArray.charAt(0));
            if (oneCleanNoteOfTwo.equals(incompleteNote.getName())) {
                resultNote = noteArray;
            }
        }
        return resultNote;
    }

    public static String resultNoteAssumingOneChar(Note incompleteNote, int spacingInSemitone) {
        int idForArrayClean = incompleteNote.getIndexInArray();
        if (spacingInSemitone < idForArrayClean) {
            return incompleteNote.getName() + "bb";
        }
        if (spacingInSemitone > idForArrayClean) {
            return incompleteNote.getName() + "##";
        }
        return incompleteNote.getName();
    }

    public static int endSemitone(Note endNote, String argEndNote, boolean ascTrueDscFalse) {
        int numberEndSemitone = endNote.getIndexInArray();
        if (argEndNote.length() > 1) {
            String charOne = String.valueOf(argEndNote.charAt(0));
            String charTwo = String.valueOf(argEndNote.charAt(1));
            if (argEndNote.length() == 2) {
                if (ascTrueDscFalse) {
                    if (charTwo.equals("#")) {
                        numberEndSemitone += 1;
                    } else {
                        numberEndSemitone -= 1;
                    }
                } else {
                    if (charTwo.equals("b")) {
                        numberEndSemitone -= 1;
                    } else {
                        numberEndSemitone += 1;
                    }
                }
            } else {
                if (charTwo.equals("#")) {
                    if (charOne.equals("B") || (charOne.equals("E"))) {
                        numberEndSemitone += 1;
                    } else {
                        numberEndSemitone += 2;
                    }
                } else {
                    if (charOne.equals("C") || (charOne.equals("F"))) {
                        numberEndSemitone -= 1;
                    } else {
                        numberEndSemitone -= 2;
                    }
                }
            }
        }
        return numberEndSemitone;
    }

    public static int spacingSemitone(int startSemitone, int endSemitone, boolean ascTrueDscFalse) {
        int result;
        if (ascTrueDscFalse) {
            if (startSemitone < endSemitone) {
                result = endSemitone - startSemitone;
            } else {
                result = 12 - startSemitone + endSemitone;
            }
        } else {
            if (startSemitone < endSemitone) {
                result = 12 - endSemitone + startSemitone;
            } else {
                result = startSemitone - endSemitone;
            }
        }
        return result;
    }
}
