# JLuaParser | Lua Parser and AST Transformer

An experimental **Java 25(also experimental)** framework for lexing, parsing, generating source with AST or rewriting Lua source code.

## 🏗 Project Structure

* `main.ast`: The core node definitions (Statements, Expressions, Blocks).
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
