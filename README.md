# JLuaParser | Lua Parser and AST Transformer

An experimental **Java 25(also experimental)** framework for lexing, parsing, generating source with AST or rewriting Lua source code.

## 🏗 Project Structure

* `main.ast`: The core node definitions (Statements, Expressions).
* `main.examples`: Examples built using JLuaParser.
* `main.lexer`: Lexer logic.
* `main.parser`: Parser logic.
* `main.util`: Utility classes like `ASTStringGenerator` for "un-parsing" the tree.
* `main.visit`: Contains the `BaseRewriter` and visitor implementations.

---

## 🛠 Usage Examples
**Please Check `main.examples` for examples.**

## 📝 Code Generation
The ASTStringGenerator (also an example) provides "un-parsing" capabilities. It includes a withIndent helper to ensure nested logic is properly tabbed.

---

## 🔧 Installation & Usage
**Requirements:** JDK 25 or higher.

* Ensure your AST nodes implement the `accept(NodeVisitor)` method.
* Inherit from BaseRewriter to create your rewriters.

## 🤝 Contributing
Contributions are welcome! Whether it's adding support for Lua 5.x+ (or other lua versions) features, feel free to open a Pull Request.

## 📬 Contact
If you have questions, suggestions, or want to report a bug, feel free to reach out:

- Email: atabit@protonmail.com

## Example Output
### Input:
```
function print_all(...)
	for i, v in ipairs ({...}) do 
		print(i, v);
	end
end

print_all(1, 2, 3, 4, 5, false, true, {}, "hello");
```
### **Following output has been generated with** `ASTPrettyPrinter`**.**
```
Chunk
├──Block
│ ├──FunctionStatement
│ ├──Identifier "print_all"
│ ├──Parameters: 
│ ├──VarArgExpression
│ │ ├──Chunk:
│ │ │ ├──Chunk
│ │ │ │ ├──Block
│ │ │ │ │ ├──ForGenericStatement
│ │ │ │ │ ├──Identifier "i"
│ │ │ │ │ ├──Identifier "v"
│ │ │ │ │ │ ├──Expression:
│ │ │ │ │ │ │ ├──FunctionCall
│ │ │ │ │ │ │ │ ├──Target:
│ │ │ │ │ │ │ │ │ ├──Identifier "ipairs"
│ │ │ │ │ │ │ │ ├──Args:
│ │ │ │ │ │ │ │ │ ├──TableConstructorExpression
│ │ │ │ │ │ │ │ │ │ ├──TableFieldExpression
│ │ │ │ │ │ │ │ │ │ │ ├──Value:
│ │ │ │ │ │ │ │ │ │ │ │ ├──VarArgExpression
│ │ │ │ │ │ ├──Body:
│ │ │ │ │ │ │ ├──Block
│ │ │ │ │ │ │ │ ├──ExpressionStatement
│ │ │ │ │ │ │ │ │ ├──FunctionCall
│ │ │ │ │ │ │ │ │ │ ├──Target:
│ │ │ │ │ │ │ │ │ │ │ ├──Identifier "print"
│ │ │ │ │ │ │ │ │ │ ├──Args:
│ │ │ │ │ │ │ │ │ │ │ ├──Identifier "i"
│ │ │ │ │ │ │ │ │ │ │ ├──Identifier "v"
│ ├──ExpressionStatement
│ │ ├──FunctionCall
│ │ │ ├──Target:
│ │ │ │ ├──Identifier "print_all"
│ │ │ ├──Args:
│ │ │ │ ├──Literal 1
│ │ │ │ ├──Literal 2
│ │ │ │ ├──Literal 3
│ │ │ │ ├──Literal 4
│ │ │ │ ├──Literal 5
│ │ │ │ ├──Literal false
│ │ │ │ ├──Literal true
│ │ │ │ ├──TableConstructorExpression
│ │ │ │ ├──Literal hello
```
### **Following output has been generaed with** `ASTStringGenerator`**.**
```
function print_all(...)
	for i, v in ipairs({ ... }) do
		print(i, v);
	end;
end;
print_all(1, 2, 3, 4, 5, false, true, {  }, "hello");
```

