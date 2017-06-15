## Understanding JSymbol

JSymbol is a Java library to extract symbol definitions from source code of different programming languages. It is used by [GitPlex](https://www.pmease.com/gitplex) to show [source outline](https://www.pmease.com/gitplex#source-outline) and do [symbol search](https://www.pmease.com/gitplex#code-search) and [cross reference](https://www.pmease.com/gitplex#jump-to-definition).
 
For each supported language, there is a class implementing interface [SymbolExtractor](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/SymbolExtractor.java). The implementation parses source of target language and return a list of symbols representing static structure of the source. GitPlex then uses this list to do below things:

1. Construct source outline. Root nodes of the outline are those symbols with a _null_ [parent](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/Symbol.java?mark=30.0-31.0). To determine children of a particular symbol, GitPlex loops the list to find symbols with [parent](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/Symbol.java?mark=30.0-31.0) set to that symbol
1. Index names of [non-local](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/Symbol.java?mark=86.0-87.0) to do symbol search and cross reference later

Let's check how it works in detail by looking at a simple Java source below:
```java
package com.example.logger;
import java.util.Date;

public class Logger {
    private String name;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void log(String message) {
        Date date = new Date();
        System.out.println(date + ":" + name + ":" + message);
    }
}
```
The [JavaExtractor](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/JavaExtractor.java) will extract below list of symbols:

1. A [compilation unit symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/CompilationUnitSymbol.java) representing package _com.example.logger_
1. A [type symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/TypeSymbol.java) representing class _Logger_, with parent symbol set to compilation unit _com.example.logger_
1. A [field symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/FieldSymbol.java) representing field _name_, with parent symbol set to type definition _Logger_
1. A [method symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/MethodSymbol.java) representing method _getName()_, with parent symbol set to type definition _Logger_
1. A [method symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/MethodSymbol.java) representing method _setName(String)_, with parent symbol set to type definition _Logger_
1. A [method symbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/java/symbols/MethodSymbol.java) representing method _log(String)_, with parent symbol set to type definitio*n _Logger_
   
**Note that** symbols declared in method body (_date_ here) will not be extracted as they are not part of source static structure. Based on these extracted symbols, GitPlex can then do below things:

1. Displays source outline as:
> ![simple_example](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/java-symbols.png)
1. Indexes _Logger_, _getName_, _setName_ and _log_ for symbol search and cross reference. Package symbol _com.example.logger_ will not be indexed as we intentionally set the symbol name as _null_. The field symbol _name_ will also not be indexed as it is a local symbol (private field) 

## Set up development environment

1. Make sure you have JDK 8 installed
1. Install **Eclipse Neon for Java**, and open an empty workspace, for instance _d:\myworkspace_
1. Configure JDK 1.8 as default JRE:
![jdk](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/eclipse-jdk.png)
1. Install ANTLR plugin for Eclipse. To do it, open menu item _Help/Eclipse Marketplace_, and search for _antlr_:
![antlr](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/eclipse-antlr-install.png)
1. Since we only need to use grammar navigation feature of this plugin, we deactivate parser auto-generating as below (GitPlex pom file is already configured to auto-generate parsers):
![antlr](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/eclipse-antlr-deactivate-tool.png)
1. Fork this repository to your own account, and clone the forked repository to be under the workspace, for instance:
```
git clone https://www.gitplex.com/<your account name>/jsymbol d:\myworkspace\jsymbol
```
1. Import the cloned project as Maven project into Eclipse
![maven](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/eclipse-import-maven.png)
1. <a name="updateproject"></a> Eclipse will build the project upon import. Wait for a while for it to download all necessary libraries. If the build eventually fails, try to update the project as below:
![maven](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/eclipse-update-project.png) 
1. After finishing your work, push the changes and send a pull request to branch **dev** of this repository

## Support new languages

To add support for a new language, create a package of that language in JSymbol. For instance, the package [com.gitplex.jsymbol.c](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c) contains everything needed to extract symbols from C language:

1. Class [CExtractor](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/CExtractor.java) is entry point of symbol extraction for C language. It is extend from [AbstractSymbolExtractor](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/AbstractSymbolExtractor.java?mark=7.22-7.45). All extractor instance should be **thread safe**.
1. The package [com.gitplex.jsymbol.c.symbols](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/symbols) contains symbols defined for C language. They all extended from the base class [CSymbol](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/symbols/CSymbol.java) containing some common logic/data for C symbols
1. The package [com.gitplex.jsymbol.c.symbols.ui](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/symbols/ui) contains some [Wicket](http://wicket.apache.org/) component created to render the symbols. You do not need to be familiar with Wicket, as these components are very simple and straightforward. Just create yours in the same way if necessary. 
1. The package [com.gitplex.jsymbol.c.symbols.ui.icon](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/symbols/ui/icon) contains icons used by the symbols. 
1. Class [CExtractorTest](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/test/java/com/gitplex/jsymbol/c/CExtractorTest.java) to verify correctness of the extractor.

Creating an extractor is a quite involved task. You first need a parser to parse the source into AST. Then you can visit the AST to extract necessary symbols. 

### Find/create appropriate parser

Some languages have existing parsers written in Java (such as Java, JavaScript, C/C++), but most others do not. For those without existing parsers, we can use [ANTLR](http://www.antlr.org/) to generate parser from the grammar. Here is a list of languages to be supported in JSymbol with suggested parsers

- [x] Java -- Already implemented with [this parser](http://javaparser.org/)
- [x] JavaScript -- Already implemented with [this parser](https://github.com/SonarSource/sonar-javascript/tree/master/javascript-frontend)
- [ ] C  -- Currently implemented with ANTLR parser. To be replaced with [Eclipse CDT parser](https://github.com/ricardojlrufino/eclipse-cdt-standalone-astparser) for error tolerance reason
- [ ] C++  -- To be implemented with [Eclipse CDT parser](https://github.com/ricardojlrufino/eclipse-cdt-standalone-astparser)
- [ ] Python -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/python3)
- [ ] PHP -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/php)
- [ ] CSharp -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/csharp)
- [ ] GO -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/golang)
- [ ] Swift -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/swift)
- [ ] ObjectiveC -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/objc)
- [ ] R -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/r)
- [ ] SCSS -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/scss)
- [ ] LESS -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/less)
- [ ] Visual Basic 6 -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/vb6)
- [ ] Visual Basic for Application -- To be implemented with ANTLR generated parser with [this grammar](https://github.com/antlr/grammars-v4/tree/master/vba)

For languages using ANTLR generated parsers, you may put the grammar file under the language package. For instance, package [com.gitplex.jsymbol.c](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c) contains [CDeclarationLexer.g4](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/CDeclarationLexer.g4) and [CDeclarationParser.g4](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/CDeclarationParser.g4). Eclipse will automatically generate lexer and parser classes under source folder _target/generated-source/antlr4_ when grammar file is added or updated. Sometimes Eclipse has issues compiling the grammar and may complain some odd errors, which can normally be fixed by [updating the project](https://www.gitplex.com/gitplex/jsymbol/blob/master/readme.md#updateproject). Often you need to adjust the grammar for below reasons:

1. Some grammars may contain non-Java action code, and it will not compile in JSymbol. You either need to translate these code to Java counterpart, or write your own Java specific action code
1. Some languages may contain pre-processor directives, and you will need to adjust the grammar to handle these directives. For instance, [this block of grammar](https://www.gitplex.com/gitplex/jsymbol/blob/38e30876e3f85c4adb1e2e5ae01e3340ed1f2b78/src/main/java/com/gitplex/jsymbol/c/CDeclarationLexer.g4?mark=414.0-487.2) is added to extract C macro definition names into a separate channel and ignore other directives. It uses lexer mode instead of semantic predicates for performance reason (refer to part 3 of ANTLR book). Then macro names in the channel will be [extracted as macro symbols](https://www.gitplex.com/gitplex/jsymbol/blob/38e30876e3f85c4adb1e2e5ae01e3340ed1f2b78/src/main/java/com/gitplex/jsymbol/c/CExtractor.java?mark=97.0-105.16). 
1. Since we only need to extract symbol definitions, it might be possible that some block of code can be skipped if they does not contribute to symbol definitions. For instance, we skip parsing method body in C grammar by [quickly matching balanced braces](https://www.gitplex.com/gitplex/jsymbol/blob/38e30876e3f85c4adb1e2e5ae01e3340ed1f2b78/src/main/java/com/gitplex/jsymbol/c/CDeclarationParser.g4?mark=478.0-482.0)

While adjusting the grammar, you may run ANTLR TestRig from within Eclipse to check it in action:

![testrig1](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/testrig1.png)

![testrig2](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/testrig2.png)

![testrig3](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/testrig3.png)

![testrig4](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/testrig4.png)


### Extract symbols from AST

With the AST, you can then visit it to extract defined symbols. For static languages, the procedure is quite straightforward as symbol definitions are normally static and they can not be altered at runtime. For instance, all class members (methods, fields, etc.) in Java is available in AST, and the runtime code can not add additional members to a class. However for dynamic languages, there are more to consider as structure can be altered dynamically. For instance, a JavaScript statement may add a property to an existing object. While it is impossible to get the accurate structure in this case, we can do some simple static analysis to make the structure more complete. Here is some examples of [JavaScript symbol extraction](https://www.gitplex.com/gitplex/jsymbol/blob/master/doc/jsexamples.md).

While extracting symbols, make sure to extract supplementary information besides the symbol name to render the symbol informatively. For instance, most logic of [CExtractor](https://www.gitplex.com/gitplex/jsymbol/blob/master/src/main/java/com/gitplex/jsymbol/c/CExtractor.java?mark=65.13-65.23) extracts type and parameter information of fields/functions, and they will be displayed together with the symbol name while being searched:

![supplementary](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/supplementary.png)

It is suggested to refer to typical IDE of the language being worked to see what additional information it displays in source outline. 


### Debug your extractor

You may debug logic of your extractor by creating some unit tests. You may also run the shipped web server to examine extracted symbols from web page:
![web server](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/debug-webserver.png)
Then you can access url http://localhost:8080 to examine the result:
![web server](https://www.gitplex.com/gitplex/jsymbol/raw/master/doc/img/debug-webpage.png)
Note that the result will be refreshed automatically as long as the source changes