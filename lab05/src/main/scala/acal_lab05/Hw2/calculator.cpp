#include <iostream>
#include <stack>
#include <string>
#include <sstream>
#include <cctype>
#include <cmath>

using namespace std;

// Function to determine precedence of operators
int precedence(char op) {
    if (op == '+' || op == '-') return 1;
    if (op == '*' || op == '/') return 2;
    if (op == '^') return 3; // Highest precedence for exponentiation
    return 0;
}

// Function to check if an operator is right associative (only exponentiation '^' is right associative)
bool isRightAssociative(char op) {
    return op == '^';
}

// Function to check if character is an operator
bool isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

// Function to check if the token is a number (including negatives)
bool isNumber(const string& token) {
    return !token.empty() && (isdigit(token[0]) || (token[0] == '-' && token.length() > 1));
}

// Function to convert infix expression to postfix
string infixToPostfix(const string &expression) {
    stack<char> operators;
    string postfix;
    bool expectNumber = true; // To distinguish between negation and subtraction

    for (size_t i = 0; i < expression.size(); ++i) {
        char token = expression[i];
        
        // Skip whitespaces
        if (isspace(token)) continue;

        // If the token is a number or part of a number
        if (isdigit(token) || (token == '-' && expectNumber)) {
            if (token == '-' && expectNumber) {
                // Handle negative numbers
                postfix += token;
                ++i;
            }
            // Collect the whole number (multi-digit)
            while (i < expression.size() && (isdigit(expression[i]) || expression[i] == '.')) {
                postfix += expression[i];
                ++i;
            }
            postfix += ' '; // Separate numbers
            --i;
            expectNumber = false; // After a number, expect an operator
        }
        // If the token is an opening parenthesis
        else if (token == '(') {
            operators.push(token);
            expectNumber = true; // After '(', expect a number or negative sign
        }
        // If the token is a closing parenthesis
        else if (token == ')') {
            while (!operators.empty() && operators.top() != '(') {
                postfix += operators.top();
                postfix += ' ';
                operators.pop();
            }
            operators.pop(); // Pop '('
            expectNumber = false; // After ')', expect an operator
        }
        // If the token is an operator
        else if (isOperator(token)) {
            while (!operators.empty() &&
                   ((isRightAssociative(token) && precedence(operators.top()) > precedence(token)) || // Right associative (e.g., ^)
                    (!isRightAssociative(token) && precedence(operators.top()) >= precedence(token)))) { // Left associative (e.g., +, -, *, /)
                cout << "pop\n";
                postfix += operators.top();
                postfix += ' ';
                operators.pop();
            }
            cout << "push\n";
            operators.push(token);
            expectNumber = true; // After an operator, expect a number or negative sign
        }
    }

    // Pop all remaining operators
    while (!operators.empty()) {
        postfix += operators.top();
        postfix += ' ';
        operators.pop();
    }

    return postfix;
}

// Function to perform arithmetic operations
double performOperation(char operator_, double operand1, double operand2) {
    switch (operator_) {
        case '+': return operand1 + operand2;
        case '-': return operand1 - operand2;
        case '*': return operand1 * operand2;
        case '/': return operand1 / operand2;
        case '^': return pow(operand1, operand2);
        default:
            cerr << "Unknown operator: " << operator_ << endl;
            exit(EXIT_FAILURE);
    }
}

// Function to evaluate postfix expression
double evaluatePostfix(const string &postfix) {
    stack<double> values;
    istringstream stream(postfix);
    string token;
    
    while (stream >> token) {
        if (isNumber(token)) {
            // Token is a number (may include negative numbers)
            values.push(stod(token));
        } else if (isOperator(token[0]) && token.length() == 1) {
            // Token is an operator
            double operand2 = values.top(); values.pop();
            double operand1 = values.top(); values.pop();
            double result = performOperation(token[0], operand1, operand2);
            values.push(result);
        }
    }

    return values.top();
}

int main() {
    string infixExpression;
    
    cout << "Enter infix expression: ";
    getline(cin, infixExpression);

    // Convert infix to postfix
    string postfixExpression = infixToPostfix(infixExpression);
    cout << "Postfix expression: " << postfixExpression << endl;

    // Evaluate the postfix expression
    double result = evaluatePostfix(postfixExpression);
    cout << "Result: " << result << endl;

    return 0;
}
