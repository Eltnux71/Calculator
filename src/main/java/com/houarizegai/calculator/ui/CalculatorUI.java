package com.houarizegai.calculator.ui;

import com.houarizegai.calculator.theme.properties.Theme;
import com.houarizegai.calculator.theme.ThemeLoader;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.regex.Pattern;
import java.awt.Color;
import javax.swing.*;

import static com.houarizegai.calculator.util.ColorUtil.hex2Color;

@SuppressWarnings("unused")
public class CalculatorUI {

    private static final String FONT_NAME = "Comic Sans MS";
    private static final String DOUBLE_OR_NUMBER_REGEX = "([-]?\\d+[.]\\d*)|(\\d+)|(-\\d+)";
    private static final String DOUBLE_REGEX = "[-]?[\\d]+[.][0]*";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH = 410;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 70;
    private static final int MARGIN_X = 20;
    private static final int MARGIN_Y = 60;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JButton btnC;
    private JButton btnBack;
    private JButton btnMod;
    private JButton btnDiv;
    private JButton btnMul;
    private JButton btnSub;
    private JButton btnAdd;
    private JButton btn0;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JButton btn7;
    private JButton btn8;
    private JButton btn9;
    private JButton btnPoint;
    private JButton btnEqual;
    private JButton btnRoot;
    private JButton btnPower;
    private JButton btnLog;

    private char selectedOperator = ' ';
    private boolean go = true; // For calculate with Opt != (=)
    private boolean addToDisplay = true; // Connect numbers in display
    private double typedValue = 0;

    private final Map<String, Theme> themesMap;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);

        int[] columns = {MARGIN_X, MARGIN_X + 90, MARGIN_X + 90 * 2, MARGIN_X + 90 * 3, MARGIN_X + 90 * 4};
        int[] rows = {MARGIN_Y, MARGIN_Y + 100, MARGIN_Y + 100 + 80, MARGIN_Y + 100 + 80 * 2, MARGIN_Y + 100 + 80 * 3, MARGIN_Y + 100 + 80 * 4};

        initInputScreen(columns, rows);
        initButtons(columns, rows);
        initCalculatorTypeSelector();

        initThemeSelector();

        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public double calculate(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return firstNumber + secondNumber;
            case '-':
                return firstNumber - secondNumber;
            case '*':
                return firstNumber * secondNumber;
            case '/':
                return firstNumber / secondNumber;
            case '%':
                return firstNumber % secondNumber;
            case '^':
                return Math.pow(firstNumber, secondNumber);
            default:
                return secondNumber;
        }
    }

    private void initThemeSelector() {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), 230, 30, "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });

        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initInputScreen(int[] columns, int[] rows) {
        inputScreen = new JTextField("0");
        inputScreen.setBounds(columns[0], rows[0], 350, 70);
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        window.add(inputScreen);
    }

    private void initCalculatorTypeSelector() {
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific"}, 20, 30, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedItem = (String) event.getItem();
            if ("Scientific".equals(selectedItem)) {
                window.setSize(WINDOW_WIDTH + 80, WINDOW_HEIGHT);
                btnRoot.setVisible(true);
                btnPower.setVisible(true);
                btnLog.setVisible(true);
            } else {
                window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                btnRoot.setVisible(false);
                btnPower.setVisible(false);
                btnLog.setVisible(false);
            }
        });
    }

    private void initButtons(int[] columns, int[] rows) {
        btnC = createButton("C", columns[0], rows[1]);
        btnC.addActionListener(event -> {
            inputScreen.setText("0");
            selectedOperator = ' ';
            typedValue = 0;
        });

        btnBack = createButton("<-", columns[1], rows[1]);
        btnBack.addActionListener(event -> {
            String str = inputScreen.getText();
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < (str.length() - 1); i++) {
                str2.append(str.charAt(i));
            }
            if (str2.toString().equals("")) {
                inputScreen.setText("0");
            } else {
                inputScreen.setText(str2.toString());
            }
        });

        btnMod = createButton("%", columns[2], rows[1]);
        btnMod.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()) || !go)
                return;

            typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
            if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                inputScreen.setText(String.valueOf((int) typedValue));
            } else {
                inputScreen.setText(String.valueOf(typedValue));
            }
            selectedOperator = '%';
            go = false;
            addToDisplay = false;
        });

        btnDiv = createButton("/", columns[3], rows[1]);
        btnDiv.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '/';
                go = false;
                addToDisplay = false;
            } else {
                selectedOperator = '/';
            }
        });

        createNumberButtons(columns, rows);

        btnMul = createButton("*", columns[3], rows[2]);
        btnMul.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '*';
                go = false;
                addToDisplay = false;
            } else {
                selectedOperator = '*';
            }
        });

        btnSub = createButton("-", columns[3], rows[3]);
        btnSub.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }

                selectedOperator = '-';
                go = false;
                addToDisplay = false;
            } else {
                selectedOperator = '-';
            }
        });

        btnAdd = createButton("+", columns[3], rows[4]);
        btnAdd.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '+';
                go = false;
                addToDisplay = false;
            } else {
                selectedOperator = '+';
            }
        });

        btnPoint = createButton(".", columns[0], rows[5]);
        btnPoint.addActionListener(event -> {
            if (addToDisplay) {
                if (!inputScreen.getText().contains(".")) {
                    inputScreen.setText(inputScreen.getText() + ".");
                }
            } else {
                inputScreen.setText("0.");
                addToDisplay = true;
            }
            go = true;
        });

        btnEqual = createButton("=", columns[2], rows[5]);
        btnEqual.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '=';
                addToDisplay = false;
            }
        });
        btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);

        btnRoot = createButton("√", columns[4], rows[1]);
        btnRoot.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = Math.sqrt(Double.parseDouble(inputScreen.getText()));
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '√';
                addToDisplay = false;
            }
        });
        btnRoot.setVisible(false);

        btnPower = createButton("pow", columns[4], rows[2]);
        btnPower.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '^';
                go = false;
                addToDisplay = false;
            } else {
                selectedOperator = '^';
            }
        });
        btnPower.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        btnPower.setVisible(false);

        btnLog = createButton("ln", columns[4], rows[3]);
        btnLog.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (go) {
                typedValue = Math.log(Double.parseDouble(inputScreen.getText()));
                if (Pattern.matches(DOUBLE_REGEX, String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = 'l';
                addToDisplay = false;
            }
        });
        btnLog.setVisible(false);
    }

    private void createNumberButtons(int[] columns, int[] rows) {

        btn0 = createButton("0", columns[1], rows[5]);
        btn0.addActionListener(event -> 
            addButton("0")
        );

        btn1 = createButton("1", columns[0], rows[4]);
        btn1.addActionListener(event -> 
            addButton("1")
        );

        btn2 = createButton("2", columns[1], rows[4]);
        btn2.addActionListener(event -> 
            addButton("2")
        );

        btn3 = createButton("3", columns[2], rows[4]);
        btn3.addActionListener(event -> 
            addButton("3")
        );
        
        btn4 = createButton("4", columns[0], rows[3]);
        btn4.addActionListener(event -> 
            addButton("4")
        );

        btn5 = createButton("5", columns[1], rows[3]);
        btn5.addActionListener(event -> 
            addButton("5")
        );

        btn6 = createButton("6", columns[2], rows[3]);
        btn6.addActionListener(event -> 
            addButton("6")
        );
        
        btn7 = createButton("7", columns[0], rows[2]);
        btn7.addActionListener(event -> 
            addButton("7")
        );

        btn8 = createButton("8", columns[1], rows[2]);
        btn8.addActionListener(event -> 
            addButton("8")
        );

        btn9 = createButton("9", columns[2], rows[2]);
        btn9.addActionListener(event -> 
            addButton("9")
        );
    }

    private void addButton(String buttonValue) {
        if (addToDisplay) {
            if (Pattern.matches(buttonValue, inputScreen.getText())) {
                inputScreen.setText(buttonValue); 
            } else {
                inputScreen.setText(inputScreen.getText() + buttonValue); 
            }
        } else {
            inputScreen.setText(buttonValue); 
            addToDisplay = true; 
        }
        go = true; 
    }

    private JComboBox<String> createComboBox(String[] items, int x, int y, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBounds(x, y, 140, 25);
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        window.add(combo);

        return combo;
    }

    private JButton createButton(String label, int x, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        btn.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);

        return btn;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));

        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));
        btn0.setForeground(hex2Color(theme.getTextColor()));
        btn1.setForeground(hex2Color(theme.getTextColor()));
        btn2.setForeground(hex2Color(theme.getTextColor()));
        btn3.setForeground(hex2Color(theme.getTextColor()));
        btn4.setForeground(hex2Color(theme.getTextColor()));
        btn5.setForeground(hex2Color(theme.getTextColor()));
        btn6.setForeground(hex2Color(theme.getTextColor()));
        btn7.setForeground(hex2Color(theme.getTextColor()));
        btn8.setForeground(hex2Color(theme.getTextColor()));
        btn9.setForeground(hex2Color(theme.getTextColor()));
        btnPoint.setForeground(hex2Color(theme.getTextColor()));
        btnC.setForeground(hex2Color(theme.getTextColor()));
        btnBack.setForeground(hex2Color(theme.getTextColor()));
        btnMod.setForeground(hex2Color(theme.getTextColor()));
        btnDiv.setForeground(hex2Color(theme.getTextColor()));
        btnMul.setForeground(hex2Color(theme.getTextColor()));
        btnSub.setForeground(hex2Color(theme.getTextColor()));
        btnAdd.setForeground(hex2Color(theme.getTextColor()));
        btnRoot.setForeground(hex2Color(theme.getTextColor()));
        btnLog.setForeground(hex2Color(theme.getTextColor()));
        btnPower.setForeground(hex2Color(theme.getTextColor()));
        btnEqual.setForeground(hex2Color(theme.getBtnEqualTextColor()));

        comboCalculatorType.setBackground(hex2Color(theme.getApplicationBackground()));
        comboTheme.setBackground(hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(hex2Color(theme.getApplicationBackground()));
        btn0.setBackground(hex2Color(theme.getNumbersBackground()));
        btn1.setBackground(hex2Color(theme.getNumbersBackground()));
        btn2.setBackground(hex2Color(theme.getNumbersBackground()));
        btn3.setBackground(hex2Color(theme.getNumbersBackground()));
        btn4.setBackground(hex2Color(theme.getNumbersBackground()));
        btn5.setBackground(hex2Color(theme.getNumbersBackground()));
        btn6.setBackground(hex2Color(theme.getNumbersBackground()));
        btn7.setBackground(hex2Color(theme.getNumbersBackground()));
        btn8.setBackground(hex2Color(theme.getNumbersBackground()));
        btn9.setBackground(hex2Color(theme.getNumbersBackground()));
        btnPoint.setBackground(hex2Color(theme.getNumbersBackground()));
        btnC.setBackground(hex2Color(theme.getOperatorBackground()));
        btnBack.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMod.setBackground(hex2Color(theme.getOperatorBackground()));
        btnDiv.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMul.setBackground(hex2Color(theme.getOperatorBackground()));
        btnSub.setBackground(hex2Color(theme.getOperatorBackground()));
        btnAdd.setBackground(hex2Color(theme.getOperatorBackground()));
        btnRoot.setBackground(hex2Color(theme.getOperatorBackground()));
        btnLog.setBackground(hex2Color(theme.getOperatorBackground()));
        btnPower.setBackground(hex2Color(theme.getOperatorBackground()));
        btnEqual.setBackground(hex2Color(theme.getBtnEqualBackground()));
    }
}