package com.springbootedu.grpc.exercise1;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.springbootedu.grpc.exercises.old.OldBook;
import com.springbootedu.grpc.exercises.v1.Book;
import com.springbootedu.grpc.exercises.v1.BookServiceGrpc;
import com.springbootedu.grpc.exercises.v1.SearchBooksRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.grpc.test.autoconfigure.AutoConfigureTestGrpcTransport;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestGrpcTransport
class Exercise1Test {

    @Autowired
    BookServiceGrpc.BookServiceBlockingStub books;

    @Test
    void theNewRpcSearchesTitles() {
        var result = books.searchBooks(SearchBooksRequest.newBuilder().setTitleContains("java").build());

        assertThat(result.getBooksList()).extracting(Book::getTitle).containsExactly("Effective Java", "Java Puzzlers");
    }

    @Test
    void theNewFieldHasItsOwnNumber() {
        FieldDescriptor year = Book.getDescriptor().findFieldByName("year");   // descriptor: compiles before the field exists

        assertThat(year).as("Book has a field 'year'").isNotNull();
        assertThat(year.getNumber()).isNotIn(1, 2, 3);
        assertThat(year.getJavaType()).isEqualTo(FieldDescriptor.JavaType.INT);
    }

    @Test
    void anOldClientReadsTheNewMessage() throws Exception {
        FieldDescriptor year = Book.getDescriptor().findFieldByName("year");
        assertThat(year).isNotNull();
        byte[] newBytes = Book.newBuilder().setIsbn("9780134685991").setTitle("Effective Java")
                .setPriceCents(8990).setField(year, 2018).build().toByteArray();

        OldBook seenByOldClient = OldBook.parseFrom(newBytes);

        assertThat(seenByOldClient.getTitle()).isEqualTo("Effective Java");
        assertThat(seenByOldClient.getPriceCents()).isEqualTo(8990);           // unchanged by the new field
    }

    @Test
    void aNewServerReadsAnOldMessage() throws Exception {
        byte[] oldBytes = OldBook.newBuilder().setIsbn("9780321336781").setTitle("Java Puzzlers")
                .setPriceCents(5500).build().toByteArray();

        Book seenByNewServer = Book.parseFrom(oldBytes);

        assertThat(seenByNewServer.getPriceCents()).isEqualTo(5500);
        FieldDescriptor year = Book.getDescriptor().findFieldByName("year");
        assertThat(year).isNotNull();
        assertThat(seenByNewServer.getField(year)).isEqualTo(0);              // missing → default value
    }
}
