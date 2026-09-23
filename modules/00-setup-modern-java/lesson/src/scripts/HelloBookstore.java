// Lesson 3.9 — a compact source file: no class declaration, no public static, no build tool.
// Run it directly:  java src/scripts/HelloBookstore.java Ayşe
// tag::compact-source[]
void main(String[] args) {
    String name = args.length > 0 ? args[0] : "Java 27";
    var books = List.of("Effective Java", "Java Puzzlers", "Modern Java in Action");   // java.base is imported

    IO.println("Merhaba %s! / Hello %s!".formatted(name, name));
    IO.println(books.size() + " kitap / books: " + books);
}
// end::compact-source[]
