import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Intervals {

    public static String intervalConstruction(String[] args) {
        checkingTheNumberOfItems(args);
        boolean ascTrueDscFalse = Note.sortOrder(args);
        Note startNote = Note.valueOf(String.valueOf(args[1].charAt(0)));
        Spacing spacing = Spacing.valueOf(args[0]);
        int spacingInDegrees = spacing.getDegrees();
        int spacingInSemitone = spacing.getSemitone();
        int indexInArray = startNote.getIndexPassage();
        Note incompleteNote = Note.incompleteNote(spacingInDegrees, startNote.getId(), ascTrueDscFalse);
        if (ascTrueDscFalse) {
            spacingInSemitone = Note.semitonesAtAsc(args[1], spacingInSemitone, indexInArray);
        } else {
            spacingInSemitone = Note.semitonesAtDsc(args[1], spacingInSemitone, indexInArray);
        }
        String resultNote;
        if (Note.listForPassage.get(spacingInSemitone).length() == 1) {
            resultNote = Note.resultNoteAssumingOneChar(incompleteNote, spacingInSemitone);
        } else {
            resultNote = Note.resultNoteAssumingNoOneChar(incompleteNote, spacingInSemitone);
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
        return Spacing.nameSpacing(degree, semitone);
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

    public static String nameSpacing(int degrees, int semitone) {
        String nameSpacing = null;
        Spacing[] arraySpacing = Spacing.values();
        for (Spacing spacing : arraySpacing) {
            if (spacing.getDegrees() == degrees && spacing.getSemitone() == semitone) {
                nameSpacing = spacing.getName();
            }
        }
        if (nameSpacing == null) {
            throw new RuntimeException("Невозможно опреденить интервал");
        }
        return nameSpacing;
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

    static String temporaryStorage;

    static final List<String> listForPassage = arrayStream();

    private final int id;
    private final String left;
    private final String centre;
    private final String right;
    private final int indexPassage;

    Note(int id, String left, String centre, String right, int indexPassage) {
        this.id = id;
        this.left = left;
        this.centre = centre;
        this.right = right;
        this.indexPassage = indexPassage;
    }

    public int getId() {
        return id;
    }

    public String getLeft() {
        return left;
    }

    public String getCentre() {
        return centre;
    }

    public String getRight() {
        return right;
    }

    public static String getTemporaryStorage() {
        return temporaryStorage;
    }

    public int getIndexPassage() {
        return indexPassage;
    }

    static List<String> arrayStream() {
        LinkedList<String> listNotesPassage = new LinkedList<>();
        Arrays.stream(values()).sorted().forEach(note -> addInList(note, listNotesPassage));
        return listNotesPassage;
    }

    static void addInList(Note note, List<String> list) {
        filterList(note.getLeft(), list);
        filterList(note.getCentre(), list);
        filterList(note.getRight(), list);
    }

    static void filterList(String name, List<String> list) {
        if (name != null && name.length() == 2 && String.valueOf(name.charAt(1)).equals("b")) {
            list.add(getTemporaryStorage() + "|" + name);
        }
        if (name != null && name.length() == 1) {
            list.add(name);
        }
        if (name != null && name.length() == 2 && String.valueOf(name.charAt(1)).equals("#")) {
            temporaryStorage = name;
        }
    }

    public static boolean sortOrder(String[] args) {
        return args.length != 3 || !args[2].equals("dsc");
    }

    public static Note incompleteNote(int spacingInDegrees, int idStartNote, boolean ascTrueDscFalse) {
        int numberDegrees;
        if (ascTrueDscFalse) {
            numberDegrees = idStartNote + spacingInDegrees - 2;
            if (numberDegrees > 6) {
                numberDegrees -= 7;
            }
        } else {
            numberDegrees = idStartNote - spacingInDegrees;
            if (numberDegrees < 0) {
                numberDegrees += 7;
            }
        }
        List<Note> notesList = Arrays.asList(Note.values());
        return notesList.get(numberDegrees);
    }

    public static int semitonesAtAsc(String note, int spacingInSemitone, int indexPassage) {
        if (note.length() > 1) {
            String valueTwo = String.valueOf(note.charAt(1));
            if (valueTwo.equals("#")) {
                spacingInSemitone += 1;
            } else {
                spacingInSemitone -= 1;
            }
        }
        spacingInSemitone = spacingInSemitone + indexPassage;
        if (spacingInSemitone > 11) {
            spacingInSemitone = spacingInSemitone - 12;
        }
        return spacingInSemitone;
    }

    public static int semitonesAtDsc(String note, int spacingInSemitone, int indexPassage) {
        int result = indexPassage;
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

    public static int startSemitone(Note startNote, String wholeStartingNote) {
        int numberStartSemitone = startNote.getIndexPassage();
        if (wholeStartingNote.length() == 2) {
            String string = String.valueOf(wholeStartingNote.charAt(1));
            if (string.equals("#")) {
                numberStartSemitone += 1;
            } else {
                numberStartSemitone -= 1;
            }
        }
        return numberStartSemitone;
    }

    public static String resultNoteAssumingNoOneChar(Note incompleteNote, int spacingInSemitone) {
        String[] arrayTwoNotes = Note.listForPassage.get(spacingInSemitone).split("\\|");
        String resultNote = "";
        for (String noteArray : arrayTwoNotes) {
            String oneCleanNoteOfTwo = String.valueOf(noteArray.charAt(0));
            if (oneCleanNoteOfTwo.equals(incompleteNote.getCentre())) {
                resultNote = noteArray;
            }
        }
        return resultNote;
    }

    public static String resultNoteAssumingOneChar(Note incompleteNote, int spacingInSemitone) {
        if (spacingInSemitone < incompleteNote.getIndexPassage()) {
            return incompleteNote.getCentre() + "bb";
        }
        if (spacingInSemitone > incompleteNote.getIndexPassage()) {
            return incompleteNote.getCentre() + "##";
        }
        return incompleteNote.getCentre();
    }

    public static int endSemitone(Note endNote, String argEndNote, boolean ascTrueDscFalse) {
        int numberEndSemitone = endNote.getIndexPassage();
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