package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0";

    private double latestValue;

    private String latestOperation = "";
    private boolean isNewInput = false; // <<< Änderung: Flag für neue Eingabe
    private double lastOperand = 0;     // <<< Änderung: Letzter Operand für wiederholtes Gleich

    public String readScreen() {
        // <<< Änderung: Komma statt Punkt
        return screen.replace('.', ','); // Für deutsche Formatierung
    }

    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException();

        if(screen.equals("0") || isNewInput) screen = ""; // <<< Änderung: isNewInput nutzen

        screen = screen + digit;
        isNewInput = false; // <<< Änderung
    }

    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
        lastOperand = 0.0; // <<< Änderung
        isNewInput = false; // <<< Änderung
    }

    public void pressBinaryOperationKey(String operation)  {
        if (!latestOperation.equals("")) { // <<< Änderung: vorherige Operation ausführen, wenn nötig
            pressEqualsKey();
        }

        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        isNewInput = true; // <<< Änderung
    }

    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        var result = switch(operation) {
            case "√" -> Math.sqrt(Double.parseDouble(screen));
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error";
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }

    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }

    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    public void pressEqualsKey() {
        double currentValue = Double.parseDouble(screen);
        double result = 0.0;

        if (latestOperation.equals("")) return;

        if (isNewInput) { // <<< Änderung: Bei mehrfach "=" drücken
            currentValue = lastOperand;
        } else {
            lastOperand = currentValue; // <<< Änderung
        }

        result = switch(latestOperation) {
            case "+" -> latestValue + currentValue;
            case "-" -> latestValue - currentValue;
            case "x" -> latestValue * currentValue;
            case "/" -> currentValue == 0 ? Double.POSITIVE_INFINITY : latestValue / currentValue;
            default -> throw new IllegalArgumentException();
        };

        if(Double.isInfinite(result) || Double.isNaN(result)) {
            screen = "Error";
        } else {
            screen = Double.toString(result);
            if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2);
            if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
        }

        latestValue = result; // <<< Änderung: Für weitere "=" drücken
        isNewInput = true; // <<< Änderung
    }
}
