
package com.quizchamp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("!admin-reset")
public class QuizDataSeeder implements CommandLineRunner {

    private final QuizCategoryRepository categoryRepository;
    private final QuizTopicRepository topicRepository;
    private final QuizQuestionRepository questionRepository;

    public QuizDataSeeder(
            QuizCategoryRepository categoryRepository,
            QuizTopicRepository topicRepository,
            QuizQuestionRepository questionRepository) {
        this.categoryRepository = categoryRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
    }

    private record TopicData(String name, String description) {}
    private record QuestionData(
            String question, String a, String b, String c,
            String d, String answer) {}

    @Override
    @Transactional
    public void run(String... args) {
        seedCategory("Data Structures and Algorithms", List.of(
                "Arrays", "Linked Lists", "Stacks", "Queues",
                "Searching", "Sorting"));

        seedCategory("Java Programming", List.of(
                "Java Basics", "OOP", "Inheritance",
                "Polymorphism", "Exception Handling"));

        seedCategory("Python Programming", List.of(
                "Python Basics", "Data Types", "Conditions",
                "Loops", "Functions"));

        seedCategory("Computer Networks", List.of(
                "Network Basics", "OSI Model", "TCP/IP",
                "Network Devices"));

        seedCategory("Database Management", List.of(
                "DBMS Basics", "SQL", "Keys",
                "Normalization", "Joins"));
    }

    private void seedCategory(String categoryName, List<String> topics) {
        QuizCategory category = categoryRepository
                .findByNameIgnoreCase(categoryName)
                .orElseGet(() -> categoryRepository.save(
                        new QuizCategory(categoryName,
                                "Quiz questions about " + categoryName)));

        for (String topicName : topics) {
            seedTopic(category, topicName);
        }
    }

    private void seedTopic(QuizCategory category, String topicName) {
        // Reuse existing topics, including "array" versus "Arrays".
        QuizTopic topic = topicRepository.findAll().stream()
                .filter(t -> t.getCategory().getId()
                        .equals(category.getId()))
                .filter(t -> sameTopic(t.getName(), topicName))
                .findFirst()
                .orElseGet(() -> topicRepository.save(
                        new QuizTopic(topicName,
                                "Questions about " + topicName, category)));

        List<QuestionData> bank = questionsFor(topicName);

        for (QuestionData q : bank) {
            boolean exists = questionRepository
                    .existsByQuestionTextIgnoreCaseAndTopicId(
                            q.question(), topic.getId());

            if (!exists) {
                questionRepository.save(new QuizQuestion(
                        q.question(), q.a(), q.b(), q.c(), q.d(),
                        q.answer(), topic));
            }
        }
    }

    private boolean sameTopic(String existing, String requested) {
        if (existing.equalsIgnoreCase(requested)) return true;
        return requested.equalsIgnoreCase("Arrays")
                && existing.equalsIgnoreCase("array");
    }

    /*
     * Ten starter MCQs for each topic.
     * Questions are tailored to each topic's subject area.
     */
    private List<QuestionData> questionsFor(String topic) {
        return switch (topic.toLowerCase()) {
            case "arrays", "array" -> List.of(
                q("Java array indexing starts at?", "0", "1", "-1", "2", "A"),
                q("Which property gives array length?", "size()", "count", "length", "length()", "C"),
                q("Last index of an array of length 5?", "5", "4", "3", "6", "B"),
                q("Which keyword creates an array?", "class", "array", "new", "create", "C"),
                q("Correct integer array declaration?", "int a = new int;", "int[] a = new int[5];", "array a;", "int a = 5;", "B"),
                q("Default value of an int array element?", "null", "1", "0", "-1", "C"),
                q("Invalid array index causes?", "IOException", "ArrayIndexOutOfBoundsException", "ArithmeticException", "ClassNotFoundException", "B"),
                q("For int[] a={4,7,9}, what is a[1]?", "4", "7", "9", "1", "B"),
                q("Can a Java array grow automatically?", "Always", "Using length++", "No, its length is fixed", "Only for integers", "C"),
                q("How do you access the third element?", "a[3]", "a[2]", "a(3)", "a{2}", "B")
            );

            case "linked lists" -> List.of(
                q("A linked list consists of?", "Nodes", "Only indexes", "Rows", "Columns", "A"),
                q("A typical node stores?", "Data and link", "Only a loop", "Only an index", "Only a class name", "A"),
                q("Which list has links in both directions?", "Singly", "Doubly", "Circular singly only", "Static array", "B"),
                q("The first node is commonly called?", "Tail", "Root", "Head", "Leaf", "C"),
                q("In a singly linked list, the last link is usually?", "Head", "Previous", "Null", "First", "C"),
                q("Which structure supports dynamic node insertion?", "Linked list", "Fixed-size array only", "Integer", "Boolean", "A"),
                q("Accessing the nth node generally requires?", "Direct indexing", "Traversal", "Sorting", "Hashing only", "B"),
                q("A circular linked list links the last node to?", "Null always", "The first node", "A random node", "The middle only", "B"),
                q("Deleting a node often requires updating?", "Links", "Variable names", "Comments", "Package name", "A"),
                q("A disadvantage of linked lists is?", "No dynamic size", "Extra memory for links", "No insertion", "No deletion", "B")
            );

            case "stacks" -> List.of(
                q("Which principle does a stack follow?", "FIFO", "LIFO", "Random order", "Sorted order", "B"),
                q("Adding an item to a stack is called?", "Pop", "Push", "Peek", "Dequeue", "B"),
                q("Removing the top item is called?", "Push", "Insert", "Pop", "Enqueue", "C"),
                q("Which operation views the top without removing it?", "Peek", "Pop", "Push", "Clear", "A"),
                q("Removing from an empty stack causes?", "Overflow", "Underflow", "Sorting", "Traversal", "B"),
                q("A stack is useful for?", "Function calls", "Only printing", "Only networking", "Only database tables", "A"),
                q("Which structure is used to check balanced brackets?", "Stack", "Queue only", "Graph only", "Heap only", "A"),
                q("The top pointer indicates?", "Bottom element", "Most recently added element", "Total memory", "Array length only", "B"),
                q("Stack overflow can occur when?", "Capacity is exceeded", "Stack is empty", "An item is viewed", "A pop succeeds", "A"),
                q("Undo operations commonly use a?", "Stack", "Queue only", "Tree only", "Network switch", "A")
            );

            case "queues" -> List.of(
                q("Which principle does a queue follow?", "LIFO", "FIFO", "Random order", "Reverse order", "B"),
                q("Adding an item to a queue is called?", "Push", "Pop", "Enqueue", "Peek", "C"),
                q("Removing an item is called?", "Dequeue", "Enqueue", "Push", "Traverse", "A"),
                q("A queue removes items from the?", "Rear", "Front", "Middle only", "Top", "B"),
                q("A circular queue helps reduce?", "Memory waste from unused slots", "All processing", "All errors", "Data types", "A"),
                q("Which queue serves items by priority?", "Simple queue", "Priority queue", "Stack", "Array only", "B"),
                q("A deque permits insertion and removal at?", "One end only", "Both ends", "No ends", "The middle only", "B"),
                q("A queue is useful for?", "Task scheduling", "Only recursion", "Only sorting", "Only inheritance", "A"),
                q("In a typical queue, new items enter at the?", "Front", "Rear", "Top", "Center", "B"),
                q("An empty queue has no?", "Elements", "Operations", "Variables", "Methods", "A")
            );

            case "searching" -> List.of(
                q("Linear search examines elements?", "One by one", "Only in pairs", "Only from the middle", "By sorting first always", "A"),
                q("Binary search requires data to be?", "Random", "Sorted", "Duplicated", "Reversed always", "B"),
                q("Binary search repeatedly halves the?", "Array search interval", "Data type", "Memory address", "Program file", "A"),
                q("Worst-case linear search time is?", "O(1)", "O(log n)", "O(n)", "O(n log n)", "C"),
                q("Binary search worst-case time is?", "O(n²)", "O(log n)", "O(n)", "O(2ⁿ)", "B"),
                q("Best-case search finds the target?", "Immediately", "Never", "After sorting", "Only at the end", "A"),
                q("What does a search algorithm find?", "A target item", "A compiler", "A network cable", "A database schema only", "A"),
                q("Binary search checks the?", "Middle element", "Last element only", "First element only", "Every element at once", "A"),
                q("If the target is smaller than the middle value, search?", "The right half", "The left half", "Both halves forever", "No half", "B"),
                q("Searching an empty collection finds an item?", "Always", "Only if sorted", "No", "Only with recursion", "C")
            );

            case "sorting" -> List.of(
                q("Sorting arranges data in a chosen?", "Order", "Language", "File type", "Network", "A"),
                q("Which sort repeatedly selects a minimum?", "Selection sort", "Binary search", "Linear search", "BFS", "A"),
                q("Which sort builds a sorted prefix by insertion?", "Insertion sort", "Merge sort", "Heap sort", "Radix search", "A"),
                q("Merge sort uses which strategy?", "Divide and conquer", "Only random swaps", "Database joins", "Network routing", "A"),
                q("Quicksort commonly uses a?", "Pivot", "Foreign key", "Listener", "Socket only", "A"),
                q("Heap sort uses a?", "Heap", "Queue only", "Linked list only", "Compiler", "A"),
                q("Bubble sort compares typically?", "Adjacent elements", "Only first and last", "Random files", "Class names", "A"),
                q("Which algorithm sorts digits by position?", "Radix sort", "Binary search", "DFS", "SQL", "A"),
                q("Average merge sort time is?", "O(n²)", "O(n log n)", "O(log n)", "O(1)", "B"),
                q("Sorting can make which search efficient?", "Binary search", "Linear search only", "No search", "File search only", "A")
            );

            case "java basics" -> List.of(
                q("Java source files commonly use which extension?", ".java", ".class", ".html", ".sql", "A"),
                q("Which method is the usual Java entry point?", "start()", "main()", "runProgram()", "begin()", "B"),
                q("Which keyword declares a class?", "define", "class", "object", "struct", "B"),
                q("Which type stores true or false?", "int", "char", "boolean", "double", "C"),
                q("Which keyword creates an object?", "new", "make", "object", "create", "A"),
                q("Java bytecode runs on the?", "JVM", "HTML", "SQL server only", "Router", "A"),
                q("Which symbol ends a Java statement?", ":", ";", ",", "#", "B"),
                q("Which is a single-line comment?", "//", "<!--", "**", "##", "A"),
                q("Which keyword declares a constant-like variable?", "final", "fixed", "constant", "lock", "A"),
                q("Which method prints text to the console?", "System.out.println()", "print.console()", "echo()", "Console.writeLine()", "A")
            );

            case "oop" -> List.of(
                q("OOP stands for?", "Object-Oriented Programming", "Order of Operations", "Open Output Program", "Object Order Process", "A"),
                q("A class is a blueprint for?", "Objects", "Packages only", "Comments", "Loops only", "A"),
                q("An object is an instance of a?", "Class", "Loop", "Comment", "Keyword", "A"),
                q("Encapsulation combines data and?", "Methods", "Only files", "Only packages", "Only loops", "A"),
                q("Hiding implementation details is called?", "Abstraction", "Sorting", "Compilation only", "Iteration", "A"),
                q("Which is an OOP principle?", "Inheritance", "Compilation error", "File extension", "Networking only", "A"),
                q("Polymorphism means?", "Many forms", "One variable only", "No methods", "No objects", "A"),
                q("A constructor initializes an?", "Object", "Import statement", "Comment", "Package", "A"),
                q("Which access modifier is most restrictive?", "private", "public", "protected", "No modifier", "A"),
                q("Which keyword refers to the current object?", "this", "super", "static", "void", "A")
            );

            case "inheritance" -> List.of(
                q("Inheritance lets a class acquire features from a?", "Superclass", "Comment", "Loop", "Package only", "A"),
                q("Which Java keyword declares class inheritance?", "extends", "inherits", "implements", "superclass", "A"),
                q("The class being inherited from is the?", "Superclass", "Subclass", "Object variable", "Method argument", "A"),
                q("The class that inherits is the?", "Subclass", "Superclass", "Package", "Interface method", "A"),
                q("Which keyword calls a superclass constructor?", "super", "this", "base()", "parent", "A"),
                q("Java classes directly extend how many classes?", "One", "Two", "Unlimited", "None", "A"),
                q("Inheritance supports code?", "Reuse", "Duplication only", "Deletion only", "Formatting only", "A"),
                q("Can a private member be directly accessed by a subclass?", "No", "Always", "Only through import", "Only in main", "A"),
                q("Which relationship is often described by inheritance?", "Is-a", "Has-a only", "Runs-a", "Prints-a", "A"),
                q("Which keyword is used to prevent class inheritance?", "final", "static", "void", "package", "A")
            );

            case "polymorphism" -> List.of(
                q("Polymorphism means?", "Many forms", "One file", "No objects", "Only data storage", "A"),
                q("Method overloading uses the same name with different?", "Parameter lists", "Class files", "Packages only", "Comments", "A"),
                q("Method overriding redefines an inherited?", "Method", "Package", "Variable type only", "Import", "A"),
                q("Overloading is commonly resolved at?", "Compile time", "Only shutdown", "Network time", "Database backup", "A"),
                q("Overriding supports which kind of polymorphism?", "Runtime", "File-based", "Network-only", "None", "A"),
                q("An overridden method generally keeps the same?", "Method signature", "Local variable names", "Body", "Comments", "A"),
                q("Which annotation indicates an intended override?", "@Override", "@Overload", "@Method", "@Runtime", "A"),
                q("Can static methods be overridden in the usual runtime sense?", "No", "Always", "Only by interfaces", "Only by constructors", "A"),
                q("Which feature allows one interface to have multiple implementations?", "Polymorphism", "Commenting", "Sorting", "Casting files", "A"),
                q("A superclass reference may refer to a subclass object?", "Yes", "No", "Only for int", "Only in Python", "A")
            );

            case "exception handling" -> List.of(
                q("Which block handles an exception?", "catch", "try only", "import", "package", "A"),
                q("Which block contains code that may throw an exception?", "try", "catch only", "final", "class", "A"),
                q("Which block commonly runs whether an exception occurs or not?", "finally", "throws", "extends", "main only", "A"),
                q("Which keyword explicitly throws an exception?", "throw", "throws", "catch", "try", "A"),
                q("Which keyword declares possible exceptions?", "throws", "throw", "catch", "final", "A"),
                q("ArithmeticException can occur during?", "Integer division by zero", "A normal comment", "Importing a package", "Declaring a class", "A"),
                q("Which is a checked exception?", "IOException", "NullPointerException", "ArithmeticException", "IndexOutOfBoundsException", "A"),
                q("Can one try block have multiple catch blocks?", "Yes", "No", "Only in Python", "Only without exceptions", "A"),
                q("An exception represents an abnormal?", "Program event", "Variable name", "Comment", "Class filename", "A"),
                q("Which parent class is the root of Java exception types?", "Throwable", "String", "Scanner", "System", "A")
            );

            case "python basics" -> List.of(
                q("Python files commonly use which extension?", ".py", ".java", ".sql", ".html", "A"),
                q("Which function displays output?", "print()", "echo()", "showText()", "writeConsole()", "A"),
                q("Which symbol starts a single-line comment?", "#", "//", "<!--", "**", "A"),
                q("Python uses indentation to indicate?", "Code blocks", "Variable types only", "File names", "Imports only", "A"),
                q("Which keyword defines a function?", "def", "function", "fun", "method", "A"),
                q("Which function reads keyboard input?", "input()", "readlineOnly()", "scan()", "getText()", "A"),
                q("Which value represents no value?", "None", "null", "void", "empty", "A"),
                q("Which keyword starts a conditional?", "if", "when", "check", "caseOnly", "A"),
                q("Which operator tests equality?", "==", "=", "!=", "=>", "A"),
                q("Python is commonly described as?", "High-level language", "Markup language only", "Database only", "Operating system", "A")
            );

            case "data types" -> List.of(
                q("Which type stores whole numbers?", "int", "str", "bool", "list", "A"),
                q("Which type stores text?", "str", "int", "bool", "float", "A"),
                q("Which type stores decimal numbers?", "float", "str", "bool", "dict", "A"),
                q("Which type represents true or false?", "bool", "list", "str", "tuple", "A"),
                q("Which collection is mutable?", "list", "tuple", "str", "int", "A"),
                q("Which collection is immutable?", "tuple", "list", "dict", "set", "A"),
                q("A dictionary stores?", "Key-value pairs", "Only ordered digits", "Only comments", "Only functions", "A"),
                q("Which collection stores unique elements?", "set", "list", "tuple", "str", "A"),
                q("Which function reports an object's type?", "type()", "kind()", "typeof()", "classof()", "A"),
                q("Which value is the Boolean true value in Python?", "True", "true", "TRUE()", "1true", "A")
            );

            case "conditions" -> List.of(
                q("Which keyword tests a condition?", "if", "for", "def", "import", "A"),
                q("Which keyword handles an alternative condition?", "elif", "repeat", "loop", "switch", "A"),
                q("Which keyword handles the remaining alternative?", "else", "then", "defaultOnly", "otherwise()", "A"),
                q("Which operator means logical AND?", "and", "&&", "&!", "plus", "A"),
                q("Which operator means logical OR?", "or", "||", "either()", "add", "A"),
                q("Which operator negates a Boolean condition?", "not", "no", "!", "invert()", "A"),
                q("Which operator means not equal?", "!=", "==", "=", "<>", "A"),
                q("What does an if statement control?", "Conditional execution", "File extension", "Class inheritance", "Database indexing", "A"),
                q("What is the result of bool(0)?", "False", "True", "0.0", "None", "A"),
                q("A condition usually evaluates to?", "True or False", "A file", "A class", "A package", "A")
            );

            case "loops" -> List.of(
                q("Which loop iterates over a sequence?", "for", "class", "def", "import", "A"),
                q("Which loop repeats while a condition is true?", "while", "if", "try", "return", "A"),
                q("Which statement exits a loop?", "break", "continue", "pass", "next", "A"),
                q("Which statement skips to the next iteration?", "continue", "break", "stop", "exit", "A"),
                q("Which function commonly creates a number sequence?", "range()", "sequence()", "numbers()", "loop()", "A"),
                q("What does range(3) produce?", "0, 1, 2", "1, 2, 3", "0, 1, 2, 3", "3 only", "A"),
                q("A loop inside another loop is a?", "Nested loop", "Conditional", "Function call", "Comment", "A"),
                q("An infinite loop repeats?", "Without end unless interrupted", "Exactly once", "Zero times always", "Only twice", "A"),
                q("Which statement does nothing when executed?", "pass", "break", "return", "raise", "A"),
                q("Loops help reduce repeated?", "Code", "Memory addresses", "Imports", "File extensions", "A")
            );

            case "functions" -> List.of(
                q("Which keyword defines a Python function?", "def", "func", "function", "method", "A"),
                q("Which keyword sends a result back?", "return", "yieldOnly", "send", "print", "A"),
                q("A function can accept?", "Parameters", "Only comments", "Only files", "Only classes", "A"),
                q("A value passed to a function is an?", "Argument", "Import", "Loop", "Package", "A"),
                q("What does print() do?", "Displays output", "Returns every input", "Defines a class", "Imports a module", "A"),
                q("A function without an explicit return usually returns?", "None", "False always", "0 always", "An empty list", "A"),
                q("What is recursion?", "A function calling itself", "A function importing a file", "A loop condition only", "A data type", "A"),
                q("Default parameters provide?", "Fallback argument values", "Only errors", "File paths", "Class names", "A"),
                q("A lambda is commonly a?", "Small anonymous function", "Database table", "Network protocol", "Loop keyword", "A"),
                q("Why use functions?", "Reuse code", "Prevent all bugs", "Remove all variables", "Avoid all input", "A")
            );

            case "network basics" -> List.of(
                q("A computer network connects devices to?", "Communicate", "Compile Java only", "Sort arrays only", "Format documents only", "A"),
                q("LAN stands for?", "Local Area Network", "Large Access Node", "Linked Array Network", "Local App Name", "A"),
                q("WAN stands for?", "Wide Area Network", "Wireless App Number", "Web Array Name", "Wide Access Node", "A"),
                q("Which is a network protocol?", "HTTP", "JPEG", "PDF", "PNG", "A"),
                q("An IP address identifies a?", "Network interface", "Keyboard key", "Java method", "Database column only", "A"),
                q("Which protocol is used for web traffic encryption?", "HTTPS", "FTP only", "ARP only", "DHCP only", "A"),
                q("Bandwidth describes a link's data-carrying?", "Capacity", "Color", "File extension", "Password", "A"),
                q("Latency measures?", "Delay", "Storage size only", "Screen resolution", "Code indentation", "A"),
                q("A protocol defines communication?", "Rules", "Hardware color", "Variable names", "Usernames only", "A"),
                q("The internet is a network of?", "Networks", "Only one computer", "Only printers", "Only databases", "A")
            );

            case "osi model" -> List.of(
                q("How many layers are in the OSI model?", "4", "5", "7", "8", "C"),
                q("Which is the lowest OSI layer?", "Physical", "Network", "Session", "Application", "A"),
                q("Which layer routes packets?", "Network", "Presentation", "Session", "Physical", "A"),
                q("Which layer provides end-to-end transport?", "Transport", "Physical", "Presentation", "Data Link", "A"),
                q("Which layer handles frames?", "Data Link", "Application", "Session", "Transport", "A"),
                q("Which layer is closest to the end user?", "Application", "Physical", "Data Link", "Network", "A"),
                q("Which layer can handle data encryption and formatting?", "Presentation", "Physical", "Network", "Data Link", "A"),
                q("Which layer manages sessions?", "Session", "Physical", "Network", "Transport", "A"),
                q("The Physical layer transmits?", "Bits", "SQL rows", "Java objects", "Web pages only", "A"),
                q("OSI is a networking reference?", "Model", "Programming language", "Database engine", "Web browser", "A")
            );

            case "tcp/ip" -> List.of(
                q("TCP stands for?", "Transmission Control Protocol", "Transfer Computer Process", "Total Connection Path", "Transport Code Program", "A"),
                q("IP stands for?", "Internet Protocol", "Internal Process", "Input Program", "Internet Port", "A"),
                q("TCP provides reliable, ordered delivery?", "Yes", "No", "Only for images", "Only offline", "A"),
                q("UDP is generally?", "Connectionless", "Always connection-oriented", "A database", "A physical cable", "A"),
                q("Which protocol resolves domain names?", "DNS", "FTP", "SMTP", "SSH", "A"),
                q("Which protocol transfers web pages?", "HTTP", "ARP only", "DHCP only", "ICMP only", "A"),
                q("Which protocol assigns IP configuration automatically?", "DHCP", "HTML", "SQL", "CSS", "A"),
                q("TCP uses which mechanism to support reliability?", "Acknowledgements and retransmission", "Screen refresh", "Database joins", "File comments", "A"),
                q("Which protocol commonly sends email between servers?", "SMTP", "HTTP", "ARP", "DHCP", "A"),
                q("TCP/IP is a networking protocol?", "Suite", "Keyboard", "Database table", "Programming loop", "A")
            );

            case "network devices" -> List.of(
                q("Which device forwards frames within a LAN?", "Switch", "Keyboard", "Scanner", "Compiler", "A"),
                q("Which device routes packets between networks?", "Router", "Monitor", "Printer", "Repeater only", "A"),
                q("Which device regenerates signals?", "Repeater", "Database", "Compiler", "Browser", "A"),
                q("A modem modulates and demodulates?", "Signals", "SQL queries", "Java objects", "HTML tags", "A"),
                q("Which device provides wireless network access?", "Access point", "Keyboard", "CPU cooler", "Scanner", "A"),
                q("A hub sends incoming data to?", "All its ports", "Only the intended port", "A database", "A web server only", "A"),
                q("A firewall helps control?", "Network traffic", "Screen brightness", "Code indentation", "Disk formatting only", "A"),
                q("Which device can connect different networks?", "Router", "Mouse", "Monitor", "Keyboard", "A"),
                q("A network interface card connects a device to a?", "Network", "Spreadsheet", "Java package", "Database query only", "A"),
                q("Which device learns MAC addresses to forward frames?", "Switch", "Hub", "Modem only", "Printer", "A")
            );

            case "dbms basics" -> List.of(
                q("DBMS stands for?", "Database Management System", "Data Backup Main Service", "Digital Base Memory Store", "Database Method Syntax", "A"),
                q("A database stores organized?", "Data", "Only programs", "Only network cables", "Only images", "A"),
                q("Which is a relational database concept?", "Table", "Loop", "Class inheritance", "Network frame", "A"),
                q("A row in a table is also called a?", "Record", "Field", "Protocol", "Function", "A"),
                q("A column describes an?", "Attribute", "Entire network", "Loop", "Exception", "A"),
                q("Which language commonly queries relational databases?", "SQL", "HTML", "CSS", "XML only", "A"),
                q("Which constraint prevents duplicate values in a key?", "UNIQUE", "DEFAULT", "CHECK only", "COMMENT", "A"),
                q("Which command retrieves data?", "SELECT", "DELETE", "DROP", "ALTER only", "A"),
                q("A transaction should preserve database?", "Consistency", "Screen resolution", "Class names", "Network cables", "A"),
                q("A backup helps with data?", "Recovery", "Sorting code only", "Changing fonts", "Compilation only", "A")
            );

            case "sql" -> List.of(
                q("Which SQL command retrieves rows?", "SELECT", "INSERT", "DROP", "UPDATE", "A"),
                q("Which command adds rows?", "INSERT", "SELECT", "DROP", "GRANT only", "A"),
                q("Which command modifies existing rows?", "UPDATE", "CREATE", "SELECT", "COMMIT only", "A"),
                q("Which command removes selected rows?", "DELETE", "SELECT", "CREATE", "GRANT", "A"),
                q("Which clause filters rows?", "WHERE", "ORDER BY", "GROUP BY only", "FROM only", "A"),
                q("Which clause sorts query results?", "ORDER BY", "WHERE", "VALUES", "SET", "A"),
                q("Which function counts rows?", "COUNT()", "SUMTEXT()", "ROWS()", "NUMBEROF()", "A"),
                q("Which keyword removes duplicate result rows?", "DISTINCT", "UNIQUEONLY", "DIFFERENT", "SINGLE", "A"),
                q("Which command creates a table?", "CREATE TABLE", "SELECT TABLE", "SHOW ROW", "FETCH TABLE", "A"),
                q("Which clause groups rows for aggregates?", "GROUP BY", "ORDER BY", "VALUES", "INTO", "A")
            );

            case "keys" -> List.of(
                q("A primary key uniquely identifies a?", "Row", "Database server", "SQL command", "Network packet", "A"),
                q("A foreign key references a key in another or the same table?", "Yes", "No", "Only in spreadsheets", "Only in Java", "A"),
                q("Can a table have multiple primary keys defined separately?", "No, one primary key constraint", "Always", "Only in SQL Server", "Only for text columns", "A"),
                q("A candidate key can uniquely identify a?", "Row", "Database file only", "Network route", "SQL keyword", "A"),
                q("A superkey uniquely identifies a row and may contain?", "Extra attributes", "No attributes", "Only nulls", "Only duplicate values", "A"),
                q("A composite key uses?", "Multiple columns", "One table only", "No columns", "Only a password", "A"),
                q("A foreign key supports?", "Referential integrity", "Screen formatting", "Code compilation", "Network speed", "A"),
                q("Can a primary key contain NULL?", "No", "Always", "Only text keys", "Only composite keys", "A"),
                q("A UNIQUE constraint prevents duplicate?", "Values", "Tables", "Databases", "Queries", "A"),
                q("Which key is selected from candidate keys?", "Primary key", "Foreign key only", "Random key", "Temporary key", "A")
            );

            case "normalization" -> List.of(
                q("Database normalization helps reduce?", "Redundancy", "Data accuracy", "All tables", "All keys", "A"),
                q("First Normal Form requires values to be?", "Atomic", "Always duplicated", "Nested lists in cells", "Encrypted", "A"),
                q("Second Normal Form removes partial dependency on a?", "Candidate key", "Network address", "Function name", "File extension", "A"),
                q("Third Normal Form addresses?", "Transitive dependencies", "Screen layouts", "Network latency", "Java constructors", "A"),
                q("Redundant data can cause update?", "Anomalies", "Compilers", "Packets", "Loops", "A"),
                q("Normalization organizes relational tables to improve?", "Data integrity", "Screen brightness", "CPU fan speed", "Code comments", "A"),
                q("An insertion anomaly is a problem with?", "Adding data", "Sorting arrays", "Network routing", "Compiling Java", "A"),
                q("A deletion anomaly may cause unintended loss of?", "Information", "Keyboard input", "Network cables", "Source code comments", "A"),
                q("Functional dependency relates attributes in a?", "Relation", "Router", "Loop", "Class file", "A"),
                q("Normalization is primarily a database design technique?", "Yes", "No", "Only for HTML", "Only for networks", "A")
            );

            case "joins" -> List.of(
                q("A JOIN combines related rows from?", "Tables", "Java methods", "Network cables", "Source files", "A"),
                q("Which join returns matching rows from both tables?", "INNER JOIN", "CROSS JOIN only", "DROP JOIN", "DELETE JOIN", "A"),
                q("Which join preserves all left-table rows?", "LEFT JOIN", "INNER JOIN only", "RIGHT JOIN only", "CROSS JOIN", "A"),
                q("Which join preserves all right-table rows?", "RIGHT JOIN", "LEFT JOIN only", "INNER JOIN only", "DROP JOIN", "A"),
                q("Which join produces combinations of rows?", "CROSS JOIN", "INNER JOIN only", "LEFT JOIN only", "SELF JOIN only", "A"),
                q("A self join joins a table to?", "Itself", "A Java class", "A router", "A text file only", "A"),
                q("Join conditions commonly use?", "ON", "PRINT", "LOOP", "PACKAGE", "A"),
                q("An unmatched LEFT JOIN row has NULLs for?", "Right-side columns", "All left-side values", "Every database", "The table name", "A"),
                q("Which join can return only matching pairs?", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN", "A"),
                q("Why use joins?", "Combine related table data", "Delete every table", "Compile code", "Create network hardware", "A")
            );

            default -> genericQuestions(topic);
        };
    }

    private QuestionData q(String question, String a, String b,
                           String c, String d, String answer) {
        return new QuestionData(question, a, b, c, d, answer);
    }

    private List<QuestionData> genericQuestions(String topic) {
        return List.of(
            q("Which best describes " + topic + "?", "A computing topic", "A monitor brand", "A keyboard key", "A file extension", "A"),
            q("Why study " + topic + "?", "To understand computing concepts", "To remove all code", "To avoid learning", "To replace electricity", "A"),
            q("Which is important when learning " + topic + "?", "Understand its core concepts", "Ignore examples", "Avoid practice", "Delete all notes", "A"),
            q("Which activity helps learn " + topic + "?", "Practice and review", "Never test", "Skip definitions", "Avoid examples", "A"),
            q("A good way to check knowledge of " + topic + " is?", "Answer practice questions", "Close the book", "Delete the notes", "Skip exercises", "A"),
            q("When using " + topic + ", documentation helps you?", "Understand usage", "Remove all errors automatically", "Replace every program", "Avoid testing", "A"),
            q("What should you do when an answer about " + topic + " is uncertain?", "Verify with a reliable source", "Guess every time", "Ignore the question", "Delete the project", "A"),
            q("Examples of " + topic + " are useful because they?", "Illustrate concepts", "Replace all theory", "Guarantee no errors", "Remove the need to learn", "A"),
            q("Practice with " + topic + " should include?", "Testing understanding", "Avoiding feedback", "Never reviewing", "Skipping examples", "A"),
            q("What supports progress in " + topic + "?", "Consistent practice", "Never revising", "Avoiding questions", "Ignoring mistakes", "A")
        );
    }
}