# 21 · gRPC / gRPC

🇹🇷 [Ders notları](docs/tr/ders.md) ([PDF](docs/tr/ders.pdf)) · [Ödevler](docs/tr/odevler.md) ([PDF](docs/tr/odevler.pdf))
🇬🇧 [Lesson notes](docs/en/lesson.md) ([PDF](docs/en/lesson.pdf)) · [Exercises](docs/en/exercises.md) ([PDF](docs/en/exercises.pdf))

## 🇹🇷 Bu modülde

- `.proto` sözleşmesi (BookCatalog) ve `protobuf-maven-plugin` ile kod üretimi
- Dört RPC türü: unary, server streaming, client streaming, çift yönlü streaming (`@GrpcService`)
- Hata → `Status` eşleme (`@GrpcAdvice`), global interceptor (loglama), deadline
- `@ImportGrpcClients` ile client stub'ları, health ve reflection servisleri, in-process testler
- REST ile gRPC karşılaştırması

## 🇬🇧 In this module

- A `.proto` contract (BookCatalog) and code generation with `protobuf-maven-plugin`
- Four kinds of RPC: unary, server streaming, client streaming, bidirectional streaming (`@GrpcService`)
- Error → `Status` mapping (`@GrpcAdvice`), a global interceptor (logging), deadlines
- Client stubs with `@ImportGrpcClients`, health and reflection services, in-process tests
- REST compared with gRPC

## gRPC'yi denemek / Trying gRPC

[grpcurl](https://github.com/fullstorydev/grpcurl) ile (reflection açık / reflection is on):

```bash
grpcurl -plaintext localhost:9090 list
grpcurl -plaintext -d '{"isbn": "9780134685991"}' localhost:9090 bookstore.catalog.v1.BookCatalog/GetBook
grpcurl -plaintext -d '{"author": "Joshua Bloch"}' localhost:9090 bookstore.catalog.v1.BookCatalog/ListBooks
grpcurl -plaintext localhost:9090 grpc.health.v1.Health/Check
```

## Çalıştırma / How to run

Ön koşul / Prerequisite: JDK 27 (Docker gerekmez / no Docker needed) — `export JAVA_HOME=$(/usr/libexec/java_home -v 27)`

```bash
# Ders kodunu çalıştır: gRPC sunucusu 9090'da / Run the lesson: the gRPC server listens on 9090
./mvnw -pl modules/21-grpc/lesson spring-boot:run


# Testler / Tests (in-process gRPC)
./mvnw -pl modules/21-grpc/lesson verify

# Ödevler / Exercises (kırmızı başlar / start red)
./mvnw -Pexercises -pl modules/21-grpc/exercise test
```


## Yapı / Layout

| Klasör / Folder | İçerik / Content |
|---|---|
| `lesson/` | Çalışan örnekler + testler / Runnable examples + tests |
| `exercise/` | TODO'lu başlangıç kodu / Starter code with TODOs |
| `solution/` | Referans çözüm / Reference solution |
| `docs/` | Ders ve ödev dokümanları (MD + PDF) / Lesson & exercise docs |
