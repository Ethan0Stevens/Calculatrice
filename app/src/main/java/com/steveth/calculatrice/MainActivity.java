package com.steveth.calculatrice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView TV_main_screen;
    private TextView TV_main_calcul;
    String op = "+";
    String nombre = "";
    boolean estUnNouvelOp = true;
    boolean estUnNouveauCalc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TV_main_screen = findViewById(R.id.tv_main_screen);
        TV_main_calcul = findViewById(R.id.tv_main_calcul);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    public void addNumber(View view) {
        if (estUnNouvelOp) {
            nombre = "";
        }
        if (estUnNouveauCalc) {
            TV_main_calcul.setText("");
        }
        estUnNouvelOp = false;
        estUnNouveauCalc = false;
        switch (view.getId()) {
            case R.id.bt_0:
                TV_main_calcul.append("0");
                nombre += "0";
                break;
            case R.id.bt_1:
                TV_main_calcul.append("1");
                nombre += "1";
                break;
            case R.id.bt_2:
                TV_main_calcul.append("2");
                nombre += "2";
                break;
            case R.id.bt_3:
                TV_main_calcul.append("3");
                nombre += "3";
                break;
            case R.id.bt_4:
                TV_main_calcul.append("4");
                nombre += "4";
                break;
            case R.id.bt_5:
                TV_main_calcul.append("5");
                nombre += "5";
                break;
            case R.id.bt_6:
                TV_main_calcul.append("6");
                nombre += "6";
                break;
            case R.id.bt_7:
                TV_main_calcul.append("7");
                nombre += "7";
                break;
            case R.id.bt_8:
                TV_main_calcul.append("8");
                nombre += "8";
                break;
            case R.id.bt_9:
                TV_main_calcul.append("9");
                nombre += "9";
                break;
            case R.id.bt_point:
                if (!nombre.contains(".")) {
                    if (nombre.equals("")) {
                        TV_main_calcul.append("0");
                        nombre += "0";
                    }
                    TV_main_calcul.append(".");
                    nombre += ".";
                }
                break;
            case R.id.bt_signe:
                TV_main_calcul.setText(TV_main_calcul.getText().toString().substring(0, nombre.length()+1));
                nombre = String.valueOf(Double.parseDouble(nombre) * -1);
                TV_main_calcul.append("(" + nombre);
                break;
            case R.id.bt_parenthese:
                nombre = "(" + nombre;
        }
        TV_main_screen.setText(nombre);
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    public void addOperateurs(View view) {
        if (!TV_main_calcul.getText().toString().isEmpty()) {
            estUnNouvelOp = true;
            estUnNouveauCalc = false;
            if (nombre.contains(op)) {
                TV_main_calcul.setText(TV_main_calcul.getText().toString().substring(0, TV_main_calcul.getText().toString().length() - 1));
            }
            switch (view.getId()) {
                case R.id.bt_divise:
                    op = "/";
                    break;
                case R.id.bt_fois:
                    op = "*";
                    break;
                case R.id.bt_plus:
                    op = "+";
                    break;
                case R.id.bt_moins:
                    op = "-";
                    break;

                case R.id.bt_pourcent:
                    op = "%";
                    break;
            }
            TV_main_calcul.append(op);
            nombre += op;
        }

    }

    public void calcule(View view) {
        try {
            Double resultat = eval(TV_main_calcul.getText().toString());
            TV_main_calcul.setText(String.valueOf(resultat));
            TV_main_screen.setText("");
            estUnNouveauCalc = true;
            estUnNouvelOp = true;
        } catch (Exception e){
            System.out.println("Error");
        }


    }

    public void supprimer(View view) {
        TV_main_screen.setText("");
        TV_main_calcul.setText("");
        estUnNouveauCalc = true;
        estUnNouvelOp = true;
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else if (eat('%')) x %= parseFactor(); // pourcent
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}